package com.botplanilla.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class TipoProductoDeserializer extends JsonDeserializer<TipoProducto> {
    @Override
    public TipoProducto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        // Intentar encontrar el enum por nombre (case-insensitive)
        try {
            return TipoProducto.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Si no funciona, buscar por el nombre legible
            for (TipoProducto tipo : TipoProducto.values()) {
                if (tipo.getNombre().equalsIgnoreCase(value)) {
                    return tipo;
                }
            }
            // Si tampoco funciona, buscar por nombre del enum sin importar mayúsculas/minúsculas
            for (TipoProducto tipo : TipoProducto.values()) {
                if (tipo.name().equalsIgnoreCase(value)) {
                    return tipo;
                }
            }
            return null;
        }
    }
}

