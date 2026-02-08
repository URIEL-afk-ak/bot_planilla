package com.botplanilla.controller;

import com.botplanilla.dto.GananciaMensualDTO;
import com.botplanilla.dto.TipoVendidoDTO;
import com.botplanilla.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReporteController {
    private final ReporteService reporteService;

    @GetMapping("/tipos-mas-vendidos")
    public ResponseEntity<List<TipoVendidoDTO>> obtenerTiposMasVendidos(
            @RequestParam(required = false) Integer anio) {
        return ResponseEntity.ok(reporteService.obtenerTiposMasVendidos(anio));
    }

    @GetMapping("/ganancias-mensuales")
    public ResponseEntity<List<GananciaMensualDTO>> obtenerGananciasPorMes(
            @RequestParam(required = false) Integer anio) {
        return ResponseEntity.ok(reporteService.obtenerGananciasPorMes(anio));
    }
}

