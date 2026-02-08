package com.botplanilla.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad_vendida", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadVendida; // Puede ser bultos completos o fracciones (0.5, 0.25, etc.)

    @Column(name = "precio_unitario_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioVenta;

    @Column(name = "total_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVenta;

    @Column(name = "ganancia_obtenida", precision = 10, scale = 2)
    private BigDecimal gananciaObtenida;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta;

    @Column(name = "mes_venta")
    private Integer mesVenta;

    @Column(name = "anio_venta")
    private Integer anioVenta;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (fechaVenta != null) {
            mesVenta = fechaVenta.getMonthValue();
            anioVenta = fechaVenta.getYear();
        }
        calcularTotales();
    }

    @PreUpdate
    protected void onUpdate() {
        if (fechaVenta != null) {
            mesVenta = fechaVenta.getMonthValue();
            anioVenta = fechaVenta.getYear();
        }
        calcularTotales();
    }

    private void calcularTotales() {
        if (cantidadVendida != null && precioUnitarioVenta != null && producto != null) {
            // Total venta = cantidadVendida * precioUnitarioVenta
            totalVenta = cantidadVendida.multiply(precioUnitarioVenta);

            // Ganancia obtenida = cantidadVendida * (precioUnitarioVenta - precioBase)
            BigDecimal gananciaUnitaria = precioUnitarioVenta.subtract(producto.getPrecioBase());
            gananciaObtenida = cantidadVendida.multiply(gananciaUnitaria);
        }
    }
}

