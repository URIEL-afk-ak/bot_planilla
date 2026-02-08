package com.botplanilla.service;

import com.botplanilla.dto.GananciaMensualDTO;
import com.botplanilla.dto.TipoVendidoDTO;
import com.botplanilla.model.TipoProducto;
import com.botplanilla.model.Venta;
import com.botplanilla.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {
    private final VentaRepository ventaRepository;

    public List<TipoVendidoDTO> obtenerTiposMasVendidos(Integer anio) {
        List<Venta> ventas;
        
        if (anio != null) {
            ventas = ventaRepository.findAll().stream()
                    .filter(v -> v.getAnioVenta() != null && v.getAnioVenta().equals(anio))
                    .collect(Collectors.toList());
        } else {
            ventas = ventaRepository.findAll();
        }

        Map<String, TipoVendidoDTO> tipoMap = new HashMap<>();

        for (Venta venta : ventas) {
            TipoProducto tipo = venta.getProducto().getTipo();
            if (tipo == null) continue;

            String tipoNombre = tipo.getNombre();
            TipoVendidoDTO dto = tipoMap.getOrDefault(tipoNombre, 
                    new TipoVendidoDTO(tipoNombre, BigDecimal.ZERO, BigDecimal.ZERO, 0));

            dto.setCantidadVendida(dto.getCantidadVendida().add(venta.getCantidadVendida()));
            dto.setTotalVentas(dto.getTotalVentas().add(venta.getTotalVenta()));
            dto.setCantidadVentas(dto.getCantidadVentas() + 1);

            tipoMap.put(tipoNombre, dto);
        }

        return tipoMap.values().stream()
                .sorted((a, b) -> b.getCantidadVendida().compareTo(a.getCantidadVendida()))
                .collect(Collectors.toList());
    }

    public List<GananciaMensualDTO> obtenerGananciasPorMes(Integer anio) {
        List<Venta> ventas;
        
        if (anio != null) {
            ventas = ventaRepository.findAll().stream()
                    .filter(v -> v.getAnioVenta() != null && v.getAnioVenta().equals(anio))
                    .collect(Collectors.toList());
        } else {
            ventas = ventaRepository.findAll();
        }

        Map<String, GananciaMensualDTO> mesMap = new HashMap<>();

        for (Venta venta : ventas) {
            if (venta.getMesVenta() == null || venta.getAnioVenta() == null) continue;

            Integer mes = venta.getMesVenta();
            Integer anioVenta = venta.getAnioVenta();
            String key = anioVenta + "-" + mes;

            String mesNombre = LocalDate.of(anioVenta, mes, 1)
                    .getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"));
            mesNombre = mesNombre.substring(0, 1).toUpperCase() + mesNombre.substring(1);
            
            GananciaMensualDTO dto = mesMap.getOrDefault(key,
                    new GananciaMensualDTO(mes, anioVenta, mesNombre,
                            BigDecimal.ZERO, BigDecimal.ZERO, 0));

            dto.setTotalVentas(dto.getTotalVentas().add(venta.getTotalVenta()));
            if (venta.getGananciaObtenida() != null) {
                dto.setTotalGanancia(dto.getTotalGanancia().add(venta.getGananciaObtenida()));
            }
            dto.setCantidadVentas(dto.getCantidadVentas() + 1);

            mesMap.put(key, dto);
        }

        return mesMap.values().stream()
                .sorted((a, b) -> {
                    int anioCompare = a.getAnio().compareTo(b.getAnio());
                    if (anioCompare != 0) return anioCompare;
                    return a.getMes().compareTo(b.getMes());
                })
                .collect(Collectors.toList());
    }
}

