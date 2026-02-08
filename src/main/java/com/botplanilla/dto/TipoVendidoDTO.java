package com.botplanilla.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoVendidoDTO {
    private String tipo;
    private BigDecimal cantidadVendida;
    private BigDecimal totalVentas;
    private Integer cantidadVentas;
}

