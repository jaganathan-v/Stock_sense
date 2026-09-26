"""
models.py — SQLAlchemy ORM models for StockSense.

Design rule: Product has NO stock quantity field.
Stock is always calculated as SUM(StockMove.quantity_change) for a product.
"""

from datetime import datetime, timezone
from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Enum as SAEnum
from sqlalchemy.orm import relationship
import enum

from backend.database import Base


class MoveType(str, enum.Enum):
    receipt = "receipt"
    delivery = "delivery"
    transfer = "transfer"
    adjustment = "adjustment"


class Product(Base):
    __tablename__ = "products"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(120), nullable=False)
    sku = Column(String(60), unique=True, nullable=False, index=True)
    category = Column(String(80), nullable=False, default="General")
    unit_of_measure = Column(String(30), nullable=False, default="units")

    moves = relationship("StockMove", back_populates="product")


class Location(Base):
    __tablename__ = "locations"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(120), nullable=False, unique=True)

    moves = relationship("StockMove", back_populates="location")


class StockMove(Base):
    __tablename__ = "stock_moves"

    id = Column(Integer, primary_key=True, index=True)
    product_id = Column(Integer, ForeignKey("products.id"), nullable=False, index=True)
    location_id = Column(Integer, ForeignKey("locations.id"), nullable=False)
    quantity_change = Column(Integer, nullable=False)  # positive = in, negative = out
    move_type = Column(SAEnum(MoveType), nullable=False)
    timestamp = Column(DateTime, nullable=False, default=lambda: datetime.now(timezone.utc))
    note = Column(String(255), nullable=True, default="")

    product = relationship("Product", back_populates="moves")
    location = relationship("Location", back_populates="moves")
