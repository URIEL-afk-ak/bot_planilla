package com.botplanilla.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanillaDTO {
    private Long productoId;
    private String nombreProducto;
    private String tipoProducto;
    private BigDecimal precioBase;
    private BigDecimal precioVenta;
    private Integer cantidadBulto;
    private BigDecimal margenGanancia;
    private BigDecimal gananciaPorProducto;
    private BigDecimal gananciaPorBulto;
    private BigDecimal cantidadVendidaMes; // Cantidad vendida en el mes actual
    private BigDecimal totalVentasAcumulado; // Total en dinero acumulado por producto
    private List<VentaDTO> ventas;
}

