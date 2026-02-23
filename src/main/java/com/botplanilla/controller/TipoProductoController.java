package com.botplanilla.controller;

import com.botplanilla.model.TipoProductoEntity;
import com.botplanilla.service.TipoProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tipos-producto")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoProductoController {
    private final TipoProductoService tipoProductoService;

    @GetMapping
    public ResponseEntity<List<TipoProductoEntity>> obtenerTodos() {
        return ResponseEntity.ok(tipoProductoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoProductoEntity> obtenerPorId(@PathVariable Long id) {
        return tipoProductoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, String> request) {
        try {
            String nombre = request.get("nombre");
            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Error: El nombre del tipo es requerido");
            }
            TipoProductoEntity tipo = tipoProductoService.crear(nombre);
            return ResponseEntity.status(HttpStatus.CREATED).body(tipo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error al crear tipo: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String nombre = request.get("nombre");
            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Error: El nombre del tipo es requerido");
            }
            TipoProductoEntity tipo = tipoProductoService.actualizar(id, nombre);
            return ResponseEntity.ok(tipo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error al actualizar tipo: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            tipoProductoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error al eliminar tipo: " + e.getMessage());
        }
    }
}


