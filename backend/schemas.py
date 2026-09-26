"""
schemas.py — Pydantic v2 request/response schemas for StockSense.
"""

from datetime import datetime
from typing import Optional
from pydantic import BaseModel, Field, field_validator


# ─── Product ────────────────────────────────────────────────────────────────

class ProductCreate(BaseModel):
    name: str = Field(..., min_length=1, max_length=120)
    sku: str = Field(..., min_length=1, max_length=60)
    category: str = Field(default="General", max_length=80)
    unit_of_measure: str = Field(default="units", max_length=30)

    @field_validator("name", "sku")
    @classmethod
    def strip_whitespace(cls, v: str) -> str:
        return v.strip()


class ProductOut(BaseModel):
    id: int
    name: str
    sku: str
    category: str
    unit_of_measure: str
    current_stock: int

    model_config = {"from_attributes": True}


# ─── StockMove ───────────────────────────────────────────────────────────────

class ReceiptIn(BaseModel):
    product_id: int
    quantity: int = Field(..., gt=0, description="Must be a positive integer")
    note: Optional[str] = Field(default="", max_length=255)


class DeliveryIn(BaseModel):
    product_id: int
    quantity: int = Field(..., gt=0, description="Must be a positive integer")
    note: Optional[str] = Field(default="", max_length=255)


class StockMoveOut(BaseModel):
    id: int
    product_id: int
    location_id: int
    quantity_change: int
    move_type: str
    timestamp: datetime
    note: Optional[str]

    model_config = {"from_attributes": True}


# ─── Dashboard ───────────────────────────────────────────────────────────────

class LowStockItem(BaseModel):
    id: int
    name: str
    sku: str
    current_stock: int
    unit_of_measure: str


class DashboardOut(BaseModel):
    total_products: int
    low_stock_threshold: int
    low_stock_items: list[LowStockItem]
