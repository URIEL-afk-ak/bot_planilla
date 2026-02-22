package com.botplanilla.service;

import com.botplanilla.model.Producto;
import com.botplanilla.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final TipoProductoService tipoProductoService;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Optional<Producto> obtenerPorNombre(String nombre) {
        return productoRepository.findByNombre(nombre);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> buscarPorTipo(String tipo) {
        return productoRepository.findByTipo(tipo);
    }

    public List<Producto> buscarPorTipoYNombre(String tipo, String nombre) {
        return productoRepository.findByTipoAndNombreContainingIgnoreCase(tipo, nombre);
    }

    @Transactional
    public Producto crear(Producto producto) {
        // Verificar si ya existe un producto con el mismo nombre
        if (productoRepository.findByNombre(producto.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un producto con el nombre: " + producto.getNombre());
        }
        
        // Normalizar y asegurar que el tipo existe, si no, crearlo
        if (producto.getTipo() != null && !producto.getTipo().trim().isEmpty()) {
            String tipoNormalizado = tipoProductoService.normalizarYObtenerNombre(producto.getTipo());
            producto.setTipo(tipoNormalizado);
        }
        
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(Long id, Producto productoActualizado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        // Verificar si el nuevo nombre ya existe en otro producto
        if (!producto.getNombre().equals(productoActualizado.getNombre())) {
            if (productoRepository.findByNombre(productoActualizado.getNombre()).isPresent()) {
                throw new RuntimeException("Ya existe un producto con el nombre: " + productoActualizado.getNombre());
            }
        }

        producto.setNombre(productoActualizado.getNombre());
        if (productoActualizado.getTipo() != null && !productoActualizado.getTipo().trim().isEmpty()) {
            // Normalizar y asegurar que el tipo existe, si no, crearlo
            String tipoNormalizado = tipoProductoService.normalizarYObtenerNombre(productoActualizado.getTipo());
            producto.setTipo(tipoNormalizado);
        }
        producto.setPrecioBase(productoActualizado.getPrecioBase());
        producto.setPrecioVenta(productoActualizado.getPrecioVenta());
        producto.setCantidadBulto(productoActualizado.getCantidadBulto());

        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }
}

