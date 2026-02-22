package com.botplanilla.controller;

import com.botplanilla.model.Producto;
import com.botplanilla.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Producto>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(productoService.buscarPorTipo(tipo));
    }

    @GetMapping("/buscar-filtrado")
    public ResponseEntity<List<Producto>> buscarFiltrado(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String nombre) {
        if (tipo != null && !tipo.trim().isEmpty() && nombre != null && !nombre.trim().isEmpty()) {
            return ResponseEntity.ok(productoService.buscarPorTipoYNombre(tipo, nombre));
        } else if (tipo != null && !tipo.trim().isEmpty()) {
            return ResponseEntity.ok(productoService.buscarPorTipo(tipo));
        } else if (nombre != null && !nombre.trim().isEmpty()) {
            return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
        } else {
            return ResponseEntity.ok(productoService.obtenerTodos());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Producto producto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(productoService.crear(producto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error al crear producto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            return ResponseEntity.ok(productoService.actualizar(id, producto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error al actualizar producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            productoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

