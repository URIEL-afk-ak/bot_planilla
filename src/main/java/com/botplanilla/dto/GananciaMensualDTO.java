package com.botplanilla.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GananciaMensualDTO {
    private Integer mes;
    private Integer anio;
    private String mesNombre;
    private BigDecimal totalVentas;
    private BigDecimal totalGanancia;
    private Integer cantidadVentas;
}

