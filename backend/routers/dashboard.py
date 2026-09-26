"""
routers/dashboard.py — Dashboard summary endpoint.
"""

from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from sqlalchemy import func

from backend.database import get_db
from backend import models, schemas

router = APIRouter(prefix="/dashboard", tags=["Dashboard"])

LOW_STOCK_THRESHOLD = 10


@router.get("", response_model=schemas.DashboardOut)
def dashboard(db: Session = Depends(get_db)):
    """
    Returns:
      - total product count
      - list of all products whose computed stock < LOW_STOCK_THRESHOLD
    Stock is ALWAYS computed from StockMove rows — never stored on Product.
    """
    products = db.query(models.Product).all()
    total = len(products)

    # Compute stock for each product using a single aggregation query per product.
    # For a hackathon scale this is perfectly fine; in production you'd use
    # a subquery GROUP BY for efficiency.
    low_stock: list[schemas.LowStockItem] = []
    for p in products:
        stock = _compute_stock(p.id, db)
        if stock < LOW_STOCK_THRESHOLD:
            low_stock.append(
                schemas.LowStockItem(
                    id=p.id,
                    name=p.name,
                    sku=p.sku,
                    current_stock=stock,
                    unit_of_measure=p.unit_of_measure,
                )
            )

    # Sort low-stock list: lowest stock first (most urgent at top)
    low_stock.sort(key=lambda x: x.current_stock)

    return schemas.DashboardOut(
        total_products=total,
        low_stock_threshold=LOW_STOCK_THRESHOLD,
        low_stock_items=low_stock,
    )


def _compute_stock(product_id: int, db: Session) -> int:
    result = db.query(
        func.coalesce(func.sum(models.StockMove.quantity_change), 0)
    ).filter(models.StockMove.product_id == product_id).scalar()
    return int(result)
