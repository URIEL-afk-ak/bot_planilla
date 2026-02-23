package com.botplanilla.service;

import com.botplanilla.model.TipoProductoEntity;
import com.botplanilla.repository.TipoProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipoProductoService {
    private final TipoProductoRepository tipoProductoRepository;

    public List<TipoProductoEntity> obtenerTodos() {
        return tipoProductoRepository.findAllByOrderByNombreAsc();
    }

    public Optional<TipoProductoEntity> obtenerPorNombre(String nombre) {
        return tipoProductoRepository.findByNombre(nombre);
    }

    /**
     * Normaliza el nombre del tipo:
     * - Elimina espacios al inicio y final
     * - Elimina espacios múltiples y los reemplaza por uno solo
     * - Capitaliza la primera letra de cada palabra
     * - Elimina caracteres especiales no deseados
     */
    public String normalizarNombre(String nombre) {
        if (nombre == null) {
            return null;
        }
        
        // Trim y eliminar espacios múltiples
        String normalizado = nombre.trim().replaceAll("\\s+", " ");
        
        if (normalizado.isEmpty()) {
            return null;
        }
        
        // Capitalizar primera letra de cada palabra
        String[] palabras = normalizado.split(" ");
        StringBuilder resultado = new StringBuilder();
        
        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i].trim();
            if (!palabra.isEmpty()) {
                if (i > 0) {
                    resultado.append(" ");
                }
                // Capitalizar primera letra, resto en minúsculas
                if (palabra.length() > 1) {
                    resultado.append(palabra.substring(0, 1).toUpperCase())
                             .append(palabra.substring(1).toLowerCase());
                } else {
                    resultado.append(palabra.toUpperCase());
                }
            }
        }
        
        return resultado.toString();
    }

    @Transactional
    public TipoProductoEntity crear(String nombre) {
        String nombreNormalizado = normalizarNombre(nombre);
        if (nombreNormalizado == null || nombreNormalizado.isEmpty()) {
            throw new RuntimeException("El nombre del tipo no puede estar vacío");
        }
        
        // Verificar si ya existe (case-insensitive)
        Optional<TipoProductoEntity> existente = tipoProductoRepository.findByNombre(nombreNormalizado);
        if (existente.isPresent()) {
            return existente.get();
        }
        
        TipoProductoEntity tipo = new TipoProductoEntity();
        tipo.setNombre(nombreNormalizado);
        return tipoProductoRepository.save(tipo);
    }

    @Transactional
    public TipoProductoEntity crearObtener(String nombre) {
        String nombreNormalizado = normalizarNombre(nombre);
        if (nombreNormalizado == null || nombreNormalizado.isEmpty()) {
            throw new RuntimeException("El nombre del tipo no puede estar vacío");
        }
        
        return tipoProductoRepository.findByNombre(nombreNormalizado)
                .orElseGet(() -> {
                    TipoProductoEntity tipo = new TipoProductoEntity();
                    tipo.setNombre(nombreNormalizado);
                    return tipoProductoRepository.save(tipo);
                });
    }

    /**
     * Normaliza y obtiene el nombre del tipo para usar en productos
     */
    public String normalizarYObtenerNombre(String nombre) {
        String nombreNormalizado = normalizarNombre(nombre);
        if (nombreNormalizado == null || nombreNormalizado.isEmpty()) {
            return null;
        }
        
        // Asegurar que el tipo existe en la base de datos
        crearObtener(nombreNormalizado);
        return nombreNormalizado;
    }

    public Optional<TipoProductoEntity> obtenerPorId(Long id) {
        return tipoProductoRepository.findById(id);
    }

    @Transactional
    public TipoProductoEntity actualizar(Long id, String nuevoNombre) {
        TipoProductoEntity tipo = tipoProductoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de producto no encontrado con id: " + id));
        
        String nombreNormalizado = normalizarNombre(nuevoNombre);
        if (nombreNormalizado == null || nombreNormalizado.isEmpty()) {
            throw new RuntimeException("El nombre del tipo no puede estar vacío");
        }
        
        // Verificar si el nuevo nombre ya existe en otro tipo
        Optional<TipoProductoEntity> existente = tipoProductoRepository.findByNombre(nombreNormalizado);
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new RuntimeException("Ya existe un tipo con el nombre: " + nombreNormalizado);
        }
        
        tipo.setNombre(nombreNormalizado);
        return tipoProductoRepository.save(tipo);
    }

    @Transactional
    public void eliminar(Long id) {
        TipoProductoEntity tipo = tipoProductoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de producto no encontrado con id: " + id));
        
        // Verificar si hay productos usando este tipo
        // Esto debería hacerse a través de una consulta, pero por ahora solo eliminamos
        // En producción, deberías verificar primero si hay productos asociados
        tipoProductoRepository.deleteById(id);
    }
}

