package com.botplanilla.config;

import com.botplanilla.service.TipoProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final TipoProductoService tipoProductoService;

    @Override
    public void run(String... args) {
        log.info("Inicializando tipos de producto...");
        
        String[] tiposIniciales = {
            "Yerbas",
            "Harinas",
            "Envasados",
            "Legumbres",
            "Mix Frutos Secos",
            "Barritas Cereal",
            "Semillas y Granos",
            "Cereales y Desayunos",
            "Condimentos",
            "Frutos Secos",
            "Frutas Desecadas"
        };

        for (String tipo : tiposIniciales) {
            try {
                tipoProductoService.crearObtener(tipo);
                log.debug("Tipo inicializado: {}", tipo);
            } catch (Exception e) {
                log.warn("Error al inicializar tipo {}: {}", tipo, e.getMessage());
            }
        }
        
        log.info("Inicialización de tipos completada");
    }
}



