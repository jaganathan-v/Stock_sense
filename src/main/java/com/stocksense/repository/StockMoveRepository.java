package com.stocksense.repository;

import com.stocksense.model.MoveType;
import com.stocksense.model.StockMove;
import com.stocksense.model.MoveStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMoveRepository extends JpaRepository<StockMove, Long> {

    /**
     * Compute current stock for a single product across all locations.
     */
    @Query("SELECT COALESCE(SUM(sm.quantityChange), 0) FROM StockMove sm WHERE sm.product.id = :productId AND sm.status = com.stocksense.model.MoveStatus.CONFIRMED")
    Integer computeCurrentStockByProductId(@Param("productId") Long productId);

    /**
     * Compute current stock for a single product at a specific location.
     */
    @Query("SELECT COALESCE(SUM(sm.quantityChange), 0) FROM StockMove sm WHERE sm.product.id = :productId AND sm.location.id = :locationId AND sm.status = com.stocksense.model.MoveStatus.CONFIRMED")
    Integer computeStockByProductAndLocation(@Param("productId") Long productId, @Param("locationId") Long locationId);

    /**
     * Efficiently compute the current stock for all products across all locations.
     */
    @Query("SELECT sm.product.id AS productId, CAST(COALESCE(SUM(sm.quantityChange), 0) AS integer) AS currentStock FROM StockMove sm WHERE sm.status = com.stocksense.model.MoveStatus.CONFIRMED GROUP BY sm.product.id")
    List<ProductStockProjection> computeAllProductStocks();

    /**
     * Compute stock broken down by product and location.
     */
    @Query("SELECT sm.product.id AS productId, sm.location.id AS locationId, sm.location.name AS locationName, CAST(COALESCE(SUM(sm.quantityChange), 0) AS integer) AS currentStock FROM StockMove sm WHERE sm.status = com.stocksense.model.MoveStatus.CONFIRMED GROUP BY sm.product.id, sm.location.id, sm.location.name")
    List<ProductLocationStockProjection> computeAllProductLocationStocks();

    @Query("SELECT sm FROM StockMove sm JOIN FETCH sm.product JOIN FETCH sm.location ORDER BY sm.timestamp DESC")
    List<StockMove> findAllWithDetails();

    @Query("SELECT sm FROM StockMove sm JOIN FETCH sm.product JOIN FETCH sm.location ORDER BY sm.timestamp DESC")
    List<StockMove> findRecentWithDetails(Pageable pageable);

    @Query("SELECT sm FROM StockMove sm JOIN FETCH sm.product JOIN FETCH sm.location WHERE sm.moveType = :moveType ORDER BY sm.timestamp DESC")
    List<StockMove> findRecentWithDetailsByMoveType(@Param("moveType") MoveType moveType, Pageable pageable);

    @Query("SELECT sm FROM StockMove sm JOIN FETCH sm.product JOIN FETCH sm.location WHERE sm.status = :status ORDER BY sm.timestamp DESC")
    List<StockMove> findByStatusWithDetails(@Param("status") MoveStatus status);

    long countByStatusAndMoveType(MoveStatus status, MoveType moveType);

    boolean existsByProductId(Long productId);

    long countByProductId(Long productId);

    boolean existsByLocationId(Long locationId);
}
