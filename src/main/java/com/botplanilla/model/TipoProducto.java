package com.botplanilla.model;

public enum TipoProducto {
    YERBAS("Yerbas"),
    HARINAS("Harinas"),
    ENVASADOS("Envasados"),
    LEGUMBRES("Legumbres"),
    MIX_FRUTOS_SECOS("Mix Frutos Secos"),
    BARRITAS_CEREAL("Barritas Cereal"),
    SEMILLAS_Y_GRANOS("Semillas y Granos"),
    CEREALES_Y_DESAYUNOS("Cereales y Desayunos"),
    CONDIMENTOS("Condimentos"),
    FRUTOS_SECOS("Frutos Secos"),
    FRUTAS_DESECADAS("Frutas Desecadas");

    private final String nombre;

    TipoProducto(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}

