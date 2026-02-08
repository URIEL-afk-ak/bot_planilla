package com.botplanilla.controller;

import com.botplanilla.dto.PlanillaDTO;
import com.botplanilla.dto.VentaDTO;
import com.botplanilla.model.Producto;
import com.botplanilla.service.ProductoService;
import com.botplanilla.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/planilla")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PlanillaController {
    private final ProductoService productoService;
    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<PlanillaDTO>> obtenerPlanilla(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        
        // Si no se especifica mes/año, usar el mes/año actual
        final Integer mesFinal;
        final Integer anioFinal;
        if (mes == null || anio == null) {
            LocalDate ahora = LocalDate.now();
            mesFinal = ahora.getMonthValue();
            anioFinal = ahora.getYear();
        } else {
            mesFinal = mes;
            anioFinal = anio;
        }

        List<Producto> productos = productoService.obtenerTodos();
        
        List<PlanillaDTO> planilla = productos.stream()
                .map(producto -> {
                    PlanillaDTO dto = new PlanillaDTO();
                    dto.setProductoId(producto.getId());
                    dto.setNombreProducto(producto.getNombre());
                    dto.setTipoProducto(producto.getTipo());
                    dto.setPrecioBase(producto.getPrecioBase());
                    dto.setPrecioVenta(producto.getPrecioVenta());
                    dto.setCantidadBulto(producto.getCantidadBulto());
                    dto.setMargenGanancia(producto.getMargenGanancia());
                    dto.setGananciaPorProducto(producto.getGananciaPorProducto());
                    dto.setGananciaPorBulto(producto.getGananciaPorBulto());
                    
                    // Obtener cantidad vendida del mes
                    BigDecimal cantidadVendida = ventaService.obtenerCantidadVendidaPorMes(
                            producto.getId(), mesFinal, anioFinal);
                    dto.setCantidadVendidaMes(cantidadVendida);
                    
                    // Obtener total acumulado de ventas
                    BigDecimal totalVentas = ventaService.obtenerTotalVentasPorProducto(producto.getId());
                    dto.setTotalVentasAcumulado(totalVentas);
                    
                    // Obtener ventas del producto del mes seleccionado
                    List<VentaDTO> ventas = ventaService.obtenerPorMesYAnio(mesFinal, anioFinal).stream()
                            .filter(venta -> venta.getProducto().getId().equals(producto.getId()))
                            .map(venta -> {
                                VentaDTO ventaDTO = new VentaDTO();
                                ventaDTO.setId(venta.getId());
                                ventaDTO.setProductoId(venta.getProducto().getId());
                                ventaDTO.setNombreProducto(venta.getProducto().getNombre());
                                ventaDTO.setTipoProducto(venta.getProducto().getTipo());
                                ventaDTO.setCantidadVendida(venta.getCantidadVendida());
                                ventaDTO.setPrecioUnitarioVenta(venta.getPrecioUnitarioVenta());
                                ventaDTO.setTotalVenta(venta.getTotalVenta());
                                ventaDTO.setGananciaObtenida(venta.getGananciaObtenida());
                                ventaDTO.setFechaVenta(venta.getFechaVenta());
                                ventaDTO.setMesVenta(venta.getMesVenta());
                                ventaDTO.setAnioVenta(venta.getAnioVenta());
                                return ventaDTO;
                            })
                            .collect(Collectors.toList());
                    dto.setVentas(ventas);
                    
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(planilla);
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> obtenerTotalVentas(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        
        // Si no se especifica mes/año, usar el mes/año actual
        final Integer mesFinal;
        final Integer anioFinal;
        if (mes == null || anio == null) {
            LocalDate ahora = LocalDate.now();
            mesFinal = ahora.getMonthValue();
            anioFinal = ahora.getYear();
        } else {
            mesFinal = mes;
            anioFinal = anio;
        }
        
        return ResponseEntity.ok(ventaService.obtenerTotalVentasPorMes(mesFinal, anioFinal));
    }
}

