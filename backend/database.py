"""
database.py — SQLAlchemy engine, session factory, and DB initializer.
Seeds the default Location ("Main Warehouse") on first run.
"""

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, DeclarativeBase

DATABASE_URL = "sqlite:///./stocksense.db"

engine = create_engine(
    DATABASE_URL,
    connect_args={"check_same_thread": False},  # needed for SQLite + FastAPI
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


class Base(DeclarativeBase):
    pass


def get_db():
    """FastAPI dependency — yields a DB session and always closes it."""
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def init_db():
    """Create all tables and seed the default Location row."""
    from backend.models import Location  # local import avoids circular deps

    Base.metadata.create_all(bind=engine)

    db = SessionLocal()
    try:
        if db.query(Location).count() == 0:
            db.add(Location(name="Main Warehouse"))
            db.commit()
    finally:
        db.close()
