package com.idemia.pocidemiacarabineros.Modelo;

import android.provider.BaseColumns;

public class CarabineroContract {
    public static final String SQL_CREATE_CARABINERO =
            "CREATE TABLE " + CarabineroEntry.TABLE_NAME + " (" +
                    CarabineroEntry.ID + " INTEGER PRIMARY KEY," +
                    CarabineroEntry.MODOSERVICIO + " TEXT," +
                    CarabineroEntry.NOMBREUSR + " TEXT," +
                    CarabineroEntry.CLAVEUSR + " TEXT," +
                    CarabineroEntry.ID_UNIDADPOLICIAL + " INTEGER," +
                    CarabineroEntry.ID_PERSONA + " INTEGER)";
    private CarabineroContract() {}

    public static class CarabineroEntry implements BaseColumns {
        public static final String TABLE_NAME = "Carabinero";
        public static final String ID = "id";
        public static final String MODOSERVICIO = "modoservicio";
        public static final String NOMBREUSR = "nombreusr";
        public static final String CLAVEUSR = "claveusr";
        public static final String ID_UNIDADPOLICIAL = "id_unidadpolicial";
        public static final String ID_PERSONA = "id_persona";
    }
}
