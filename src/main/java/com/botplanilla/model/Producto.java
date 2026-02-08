package com.botplanilla.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    @JsonDeserialize(using = TipoProductoDeserializer.class)
    private TipoProducto tipo;

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "cantidad_bulto", nullable = false)
    private Integer cantidadBulto;

    @Column(name = "margen_ganancia", precision = 10, scale = 2)
    private BigDecimal margenGanancia;

    @Column(name = "ganancia_por_producto", precision = 10, scale = 2)
    private BigDecimal gananciaPorProducto;

    @Column(name = "ganancia_por_bulto", precision = 10, scale = 2)
    private BigDecimal gananciaPorBulto;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        calcularGanancias();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
        calcularGanancias();
    }

    private void calcularGanancias() {
        if (precioBase != null && precioVenta != null && cantidadBulto != null && cantidadBulto > 0) {
            // Margen de ganancia = ((precioVenta - precioBase) / precioBase) * 100
            BigDecimal diferencia = precioVenta.subtract(precioBase);
            margenGanancia = diferencia.divide(precioBase, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            // Ganancia por producto = precioVenta - precioBase
            gananciaPorProducto = diferencia;

            // Ganancia por bulto = gananciaPorProducto * cantidadBulto
            gananciaPorBulto = gananciaPorProducto.multiply(BigDecimal.valueOf(cantidadBulto));
        }
    }
}

