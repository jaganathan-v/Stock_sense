package com.stocksense.repository;

import com.stocksense.model.MoveType;
import com.stocksense.model.StockMove;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMoveRepository extends JpaRepository<StockMove, Long> {

    /**
     * Compute the current stock for a single product by summing all quantity changes
     * in the immutable StockMove ledger.
     */
    @Query("SELECT COALESCE(SUM(sm.quantityChange), 0) FROM StockMove sm WHERE sm.product.id = :productId")
    Integer computeCurrentStockByProductId(@Param("productId") Long productId);

    /**
     * Efficiently compute the current stock for all products in a single aggregated query.
     */
    @Query("SELECT sm.product.id AS productId, CAST(COALESCE(SUM(sm.quantityChange), 0) AS integer) AS currentStock FROM StockMove sm GROUP BY sm.product.id")
    List<ProductStockProjection> computeAllProductStocks();

    @Query("SELECT sm FROM StockMove sm JOIN FETCH sm.product JOIN FETCH sm.location ORDER BY sm.timestamp DESC")
    List<StockMove> findAllWithDetails();

    @Query("SELECT sm FROM StockMove sm JOIN FETCH sm.product JOIN FETCH sm.location ORDER BY sm.timestamp DESC")
    List<StockMove> findRecentWithDetails(Pageable pageable);

    @Query("SELECT sm FROM StockMove sm JOIN FETCH sm.product JOIN FETCH sm.location WHERE sm.moveType = :moveType ORDER BY sm.timestamp DESC")
    List<StockMove> findRecentWithDetailsByMoveType(@Param("moveType") MoveType moveType, Pageable pageable);
}
