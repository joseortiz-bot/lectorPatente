package com.idemia.pocidemiacarabineros.Modelo;

import android.provider.BaseColumns;

public class ControlVehiculoContract {
    public static final String SQL_CREATE_CONTROLVEHICULO =
            "CREATE TABLE " + ControlVehiculoEntry.TABLE_NAME + " (" +
                    ControlVehiculoEntry.ID + " INTEGER PRIMARY KEY," +
                    ControlVehiculoEntry.FECHACONTROL + " TEXT," +
                    ControlVehiculoEntry.PATENTE + " TEXT," +
                    ControlVehiculoEntry.MARCA + " TEXT," +
                    ControlVehiculoEntry.MODELO + " TEXT," +
                    ControlVehiculoEntry.COLOR + " TEXT," +
                    ControlVehiculoEntry.NMRCHASIS + " TEXT," +
                    ControlVehiculoEntry.NMRMOTOR + " TEXT," +
                    ControlVehiculoEntry.LATITUD + " DOUBLE," +
                    ControlVehiculoEntry.LONGITUD + " DOUBLE," +
                    ControlVehiculoEntry.PRECISION + " FLOAT," +
                    ControlVehiculoEntry.ESTADO + " INTEGER," +
                    ControlVehiculoEntry.ID_CARABINERO + " INTEGER," +
                    ControlVehiculoEntry.ID_PERSONA_INFRACTOR + " INTEGER)";
    private ControlVehiculoContract() {}

    public static class ControlVehiculoEntry implements BaseColumns {
        public static final String TABLE_NAME = "ControlVehiculo";
        public static final String ID = "id";
        public static final String FECHACONTROL = "fechacontrol";
        public static final String PATENTE = "patente";
        public static final String MARCA = "marca";
        public static final String MODELO = "modelo";
        public static final String COLOR = "color";
        public static final String NMRCHASIS = "nmrchasis";
        public static final String NMRMOTOR = "nmrmotor";
        public static final String LATITUD = "latitud";
        public static final String LONGITUD = "longitud";
        public static final String ESTADO = "estado";
        public static final String PRECISION = "precision";
        public static final String ID_CARABINERO = "id_carabinero";
        public static final String ID_PERSONA_INFRACTOR = "id_persona_infractor";
    }
}
