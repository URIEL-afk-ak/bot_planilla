package com.botplanilla.service;

import com.botplanilla.model.Producto;
import com.botplanilla.model.Venta;
import com.botplanilla.repository.ProductoRepository;
import com.botplanilla.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VentaService {
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> obtenerPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public List<Venta> obtenerPorProducto(Long productoId) {
        return ventaRepository.findByProductoId(productoId);
    }

    public List<Venta> obtenerPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return ventaRepository.findByFechaVentaBetween(fechaInicio, fechaFin);
    }

    public List<Venta> obtenerPorMesYAnio(Integer mes, Integer anio) {
        return ventaRepository.findByMesVentaAndAnioVenta(mes, anio);
    }

    public List<Venta> buscarPorNombreProducto(String nombre) {
        return ventaRepository.findByProductoNombreContaining(nombre);
    }

    @Transactional
    public Venta crear(Venta venta) {
        // Verificar que el producto existe
        Producto producto = productoRepository.findById(venta.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + venta.getProducto().getId()));

        venta.setProducto(producto);
        
        // Si no se especifica precio unitario, usar el precio de venta del producto
        if (venta.getPrecioUnitarioVenta() == null) {
            venta.setPrecioUnitarioVenta(producto.getPrecioVenta());
        }

        // Si no se especifica fecha, usar la fecha actual
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDate.now());
        }

        return ventaRepository.save(venta);
    }

    @Transactional
    public Venta actualizar(Long id, Venta ventaActualizada) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + id));

        // Verificar que el producto existe si se actualiza
        if (ventaActualizada.getProducto() != null && ventaActualizada.getProducto().getId() != null) {
            Producto producto = productoRepository.findById(ventaActualizada.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + ventaActualizada.getProducto().getId()));
            venta.setProducto(producto);
        }

        if (ventaActualizada.getCantidadVendida() != null) {
            venta.setCantidadVendida(ventaActualizada.getCantidadVendida());
        }

        if (ventaActualizada.getPrecioUnitarioVenta() != null) {
            venta.setPrecioUnitarioVenta(ventaActualizada.getPrecioUnitarioVenta());
        }

        if (ventaActualizada.getFechaVenta() != null) {
            venta.setFechaVenta(ventaActualizada.getFechaVenta());
        }

        return ventaRepository.save(venta);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada con id: " + id);
        }
        ventaRepository.deleteById(id);
    }

    public BigDecimal obtenerCantidadVendidaPorMes(Long productoId, Integer mes, Integer anio) {
        BigDecimal cantidad = ventaRepository.sumCantidadVendidaByProductoAndMes(productoId, mes, anio);
        return cantidad != null ? cantidad : BigDecimal.ZERO;
    }

    public BigDecimal obtenerTotalVentasPorProducto(Long productoId) {
        BigDecimal total = ventaRepository.sumTotalVentaByProducto(productoId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal obtenerTotalVentas() {
        BigDecimal total = ventaRepository.sumTotalVentas();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal obtenerTotalVentasPorMes(Integer mes, Integer anio) {
        BigDecimal total = ventaRepository.sumTotalVentasPorMes(mes, anio);
        return total != null ? total : BigDecimal.ZERO;
    }
}

