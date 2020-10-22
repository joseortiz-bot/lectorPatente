package com.idemia.pocidemiacarabineros.Modelo;

import android.provider.BaseColumns;

public final class UnidadPolicialContract {
    public static final String SQL_CREATE_UNIDADPOLICIAL =
            "CREATE TABLE " + UnidadPolicialEntry.TABLE_NAME + " (" +
                    UnidadPolicialEntry.ID + " INTEGER PRIMARY KEY," +
                    UnidadPolicialEntry.NOMBRE + " TEXT," +
                    UnidadPolicialEntry.COMUNA + " TEXT)";
    private UnidadPolicialContract() {}

    public static class UnidadPolicialEntry implements BaseColumns {
        public static final String TABLE_NAME = "UnidadPolicial";
        public static final String ID = "id";
        public static final String NOMBRE = "nombre";
        public static final String COMUNA = "comuna";
    }
}
