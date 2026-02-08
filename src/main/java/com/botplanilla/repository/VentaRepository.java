package com.botplanilla.repository;

import com.botplanilla.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByProductoId(Long productoId);
    
    List<Venta> findByFechaVentaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    List<Venta> findByMesVentaAndAnioVenta(Integer mes, Integer anio);
    
    @Query("SELECT v FROM Venta v WHERE v.producto.nombre LIKE %:nombre%")
    List<Venta> findByProductoNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT SUM(v.cantidadVendida) FROM Venta v WHERE v.producto.id = :productoId AND v.mesVenta = :mes AND v.anioVenta = :anio")
    java.math.BigDecimal sumCantidadVendidaByProductoAndMes(@Param("productoId") Long productoId, 
                                                              @Param("mes") Integer mes, 
                                                              @Param("anio") Integer anio);
    
    @Query("SELECT SUM(v.totalVenta) FROM Venta v WHERE v.producto.id = :productoId")
    java.math.BigDecimal sumTotalVentaByProducto(@Param("productoId") Long productoId);
    
    @Query("SELECT SUM(v.totalVenta) FROM Venta v")
    java.math.BigDecimal sumTotalVentas();
    
    @Query("SELECT SUM(v.totalVenta) FROM Venta v WHERE (:mes IS NULL OR v.mesVenta = :mes) AND (:anio IS NULL OR v.anioVenta = :anio)")
    java.math.BigDecimal sumTotalVentasPorMes(@Param("mes") Integer mes, @Param("anio") Integer anio);
}

