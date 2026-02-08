package com.botplanilla.controller;

import com.botplanilla.dto.VentaDTO;
import com.botplanilla.model.Venta;
import com.botplanilla.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VentaController {
    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaDTO>> obtenerTodas() {
        List<Venta> ventas = ventaService.obtenerTodas();
        return ResponseEntity.ok(convertirAVentaDTO(ventas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> obtenerPorId(@PathVariable Long id) {
        return ventaService.obtenerPorId(id)
                .map(venta -> ResponseEntity.ok(convertirAVentaDTO(venta)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<VentaDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        List<Venta> ventas = ventaService.obtenerPorProducto(productoId);
        return ResponseEntity.ok(convertirAVentaDTO(ventas));
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<VentaDTO>> filtrar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) String nombreProducto) {
        
        List<Venta> ventas;
        
        if (nombreProducto != null && !nombreProducto.isEmpty()) {
            ventas = ventaService.buscarPorNombreProducto(nombreProducto);
        } else if (mes != null && anio != null) {
            ventas = ventaService.obtenerPorMesYAnio(mes, anio);
        } else if (fechaInicio != null && fechaFin != null) {
            ventas = ventaService.obtenerPorRangoFechas(fechaInicio, fechaFin);
        } else {
            ventas = ventaService.obtenerTodas();
        }
        
        return ResponseEntity.ok(convertirAVentaDTO(ventas));
    }

    @PostMapping
    public ResponseEntity<VentaDTO> crear(@RequestBody Venta venta) {
        try {
            Venta ventaCreada = ventaService.crear(venta);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(convertirAVentaDTO(ventaCreada));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaDTO> actualizar(@PathVariable Long id, @RequestBody Venta venta) {
        try {
            Venta ventaActualizada = ventaService.actualizar(id, venta);
            return ResponseEntity.ok(convertirAVentaDTO(ventaActualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            ventaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private VentaDTO convertirAVentaDTO(Venta venta) {
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setProductoId(venta.getProducto().getId());
        dto.setNombreProducto(venta.getProducto().getNombre());
        dto.setTipoProducto(venta.getProducto().getTipo());
        dto.setCantidadVendida(venta.getCantidadVendida());
        dto.setPrecioUnitarioVenta(venta.getPrecioUnitarioVenta());
        dto.setTotalVenta(venta.getTotalVenta());
        dto.setGananciaObtenida(venta.getGananciaObtenida());
        dto.setFechaVenta(venta.getFechaVenta());
        dto.setMesVenta(venta.getMesVenta());
        dto.setAnioVenta(venta.getAnioVenta());
        return dto;
    }

    private List<VentaDTO> convertirAVentaDTO(List<Venta> ventas) {
        return ventas.stream()
                .map(this::convertirAVentaDTO)
                .collect(Collectors.toList());
    }
}

