"""
routers/moves.py — StockMove endpoints (receipt & delivery).
"""

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from sqlalchemy import func

from backend.database import get_db
from backend import models, schemas

router = APIRouter(prefix="/moves", tags=["Stock Moves"])

LOW_STOCK_THRESHOLD = 10  # also used by dashboard


# ─── helpers ─────────────────────────────────────────────────────────────────

def _get_product_or_404(product_id: int, db: Session) -> models.Product:
    product = db.query(models.Product).filter(models.Product.id == product_id).first()
    if not product:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Product with id={product_id} not found.",
        )
    return product


def _compute_stock(product_id: int, db: Session) -> int:
    result = db.query(
        func.coalesce(func.sum(models.StockMove.quantity_change), 0)
    ).filter(models.StockMove.product_id == product_id).scalar()
    return int(result)


def _default_location(db: Session) -> models.Location:
    location = db.query(models.Location).first()
    if not location:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="No warehouse location found. Database may not be initialised.",
        )
    return location


# ─── receipt ─────────────────────────────────────────────────────────────────

@router.post("/receipt", response_model=schemas.StockMoveOut, status_code=status.HTTP_201_CREATED)
def receipt(payload: schemas.ReceiptIn, db: Session = Depends(get_db)):
    """
    Record a stock receipt (goods arriving into the warehouse).
    quantity must be > 0 — enforced by Pydantic schema.
    """
    _get_product_or_404(payload.product_id, db)
    location = _default_location(db)

    move = models.StockMove(
        product_id=payload.product_id,
        location_id=location.id,
        quantity_change=payload.quantity,          # positive
        move_type=models.MoveType.receipt,
        note=payload.note or "Stock receipt",
    )
    db.add(move)
    db.commit()
    db.refresh(move)
    return move


# ─── delivery ────────────────────────────────────────────────────────────────

@router.post("/delivery", response_model=schemas.StockMoveOut, status_code=status.HTTP_201_CREATED)
def delivery(payload: schemas.DeliveryIn, db: Session = Depends(get_db)):
    """
    Record a stock delivery (goods leaving the warehouse).
    Rejects the operation if current stock < requested quantity.
    """
    product = _get_product_or_404(payload.product_id, db)
    current = _compute_stock(payload.product_id, db)

    if payload.quantity > current:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=(
                f"Insufficient stock for '{product.name}'. "
                f"Available: {current} {product.unit_of_measure}, "
                f"Requested: {payload.quantity} {product.unit_of_measure}."
            ),
        )

    location = _default_location(db)

    move = models.StockMove(
        product_id=payload.product_id,
        location_id=location.id,
        quantity_change=-payload.quantity,         # negative — ledger pattern
        move_type=models.MoveType.delivery,
        note=payload.note or "Stock delivery",
    )
    db.add(move)
    db.commit()
    db.refresh(move)
    return move
