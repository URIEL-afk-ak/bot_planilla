package com.botplanilla.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private String tipoProducto;
    private BigDecimal cantidadVendida;
    private BigDecimal precioUnitarioVenta;
    private BigDecimal totalVenta;
    private BigDecimal gananciaObtenida;
    private LocalDate fechaVenta;
    private Integer mesVenta;
    private Integer anioVenta;
}

