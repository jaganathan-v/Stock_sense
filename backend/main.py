"""
main.py — FastAPI application entry point for StockSense.

Run with:
    uvicorn backend.main:app --reload --host 0.0.0.0 --port 8000
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import FileResponse
import pathlib

from backend.database import init_db
from backend.routers import products, moves, dashboard, reports

# ─── App setup ───────────────────────────────────────────────────────────────

app = FastAPI(
    title="StockSense API",
    description=(
        "Inventory Management System using an append-only ledger pattern. "
        "Current stock is ALWAYS computed from StockMove rows — never stored directly."
    ),
    version="1.0.0",
    docs_url="/api/docs",
    redoc_url="/api/redoc",
)

# Allow the frontend (served from the same origin or file://) to call the API
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],      # tighten in production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ─── DB init on startup ───────────────────────────────────────────────────────

@app.on_event("startup")
def startup_event():
    init_db()


# ─── API Routers ─────────────────────────────────────────────────────────────

app.include_router(products.router)
app.include_router(moves.router)
app.include_router(dashboard.router)
app.include_router(reports.router)


# ─── Serve frontend static files ─────────────────────────────────────────────

FRONTEND_DIR = pathlib.Path(__file__).parent.parent / "frontend"

if FRONTEND_DIR.exists():
    app.mount("/static", StaticFiles(directory=str(FRONTEND_DIR)), name="static")

    @app.get("/", include_in_schema=False)
    def root():
        return FileResponse(str(FRONTEND_DIR / "index.html"))

    @app.get("/{page}.html", include_in_schema=False)
    def serve_page(page: str):
        file_path = FRONTEND_DIR / f"{page}.html"
        if file_path.exists():
            return FileResponse(str(file_path))
        return FileResponse(str(FRONTEND_DIR / "index.html"))


# ─── Health check ────────────────────────────────────────────────────────────

@app.get("/health", tags=["Health"])
def health():
    return {"status": "ok", "service": "StockSense API"}
