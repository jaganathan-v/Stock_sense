"""
routers/products.py — CRUD endpoints for Products.
"""

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from sqlalchemy import func

from backend.database import get_db
from backend import models, schemas

router = APIRouter(prefix="/products", tags=["Products"])


@router.post("", response_model=schemas.ProductOut, status_code=status.HTTP_201_CREATED)
def create_product(payload: schemas.ProductCreate, db: Session = Depends(get_db)):
    """Create a new product. SKU must be unique."""
    existing = db.query(models.Product).filter(models.Product.sku == payload.sku).first()
    if existing:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=f"A product with SKU '{payload.sku}' already exists.",
        )
    product = models.Product(**payload.model_dump())
    db.add(product)
    db.commit()
    db.refresh(product)
    return _enrich(product, db)


@router.get("", response_model=list[schemas.ProductOut])
def list_products(db: Session = Depends(get_db)):
    """Return all products with their computed current stock."""
    products = db.query(models.Product).order_by(models.Product.name).all()
    return [_enrich(p, db) for p in products]


# ─── helpers ─────────────────────────────────────────────────────────────────

def _compute_stock(product_id: int, db: Session) -> int:
    result = db.query(
        func.coalesce(func.sum(models.StockMove.quantity_change), 0)
    ).filter(models.StockMove.product_id == product_id).scalar()
    return int(result)


def _enrich(product: models.Product, db: Session) -> schemas.ProductOut:
    """Build a ProductOut DTO with the computed stock."""
    return schemas.ProductOut(
        id=product.id,
        name=product.name,
        sku=product.sku,
        category=product.category,
        unit_of_measure=product.unit_of_measure,
        current_stock=_compute_stock(product.id, db),
    )
