package com.idemia.pocidemiacarabineros.Modelo;

import android.provider.BaseColumns;

public class ControlIdentidadContract {
    public static final String SQL_CREATE_CONTROLIDENTIDAD =
            "CREATE TABLE " + ControlIdentidadEntry.TABLE_NAME + " (" +
                    ControlIdentidadEntry.ID + " INTEGER PRIMARY KEY," +
                    ControlIdentidadEntry.FECHACONTROL + " TEXT," +
                    ControlIdentidadEntry.CDGPROCEDIMIENTO + " INTEGER," +
                    ControlIdentidadEntry.OBS + " TEXT," +
                    ControlIdentidadEntry.LATITUD + " DOUBLE," +
                    ControlIdentidadEntry.LONGITUD + " DOUBLE," +
                    ControlIdentidadEntry.PRECISION + " FLOAT," +
                    ControlIdentidadEntry.ESTADO + " INTEGER," +
                    ControlIdentidadEntry.ID_CARABINERO + " INTEGER," +
                    ControlIdentidadEntry.ID_PERSONA_CONTROLADA + " INTEGER)";
    private ControlIdentidadContract() {}

    public static class ControlIdentidadEntry implements BaseColumns {
        public static final String TABLE_NAME = "ControlIdentidad";
        public static final String ID = "id";
        public static final String FECHACONTROL = "fechacontrol";
        public static final String CDGPROCEDIMIENTO = "cdgprocedimiento";
        public static final String OBS = "obs";
        public static final String LATITUD = "latitud";
        public static final String LONGITUD = "longitud";
        public static final String PRECISION = "precision";
        public static final String ESTADO = "estado";
        public static final String ID_CARABINERO = "id_carabinero";
        public static final String ID_PERSONA_CONTROLADA = "id_persona_controlada";
    }
}
