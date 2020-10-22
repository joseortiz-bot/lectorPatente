package com.idemia.pocidemiacarabineros.Modelo;

import android.provider.BaseColumns;


public class PersonaContract {

    public static final String SQL_CREATE_PERSONA =
            "CREATE TABLE " + PersonaEntry.TABLE_NAME + " (" +
                    PersonaEntry.ID + " INTEGER PRIMARY KEY," +
                    PersonaEntry.RUT + " TEXT," +
                    PersonaEntry.NOMBRE + " TEXT," +
                    PersonaEntry.FCHNACIMIENTO + " TEXT )";
    private PersonaContract() {}

    public static class PersonaEntry implements BaseColumns {
        public static final String TABLE_NAME = "Persona";
        public static final String ID = "id";
        public static final String RUT = "rut";
        public static final String NOMBRE = "nombre";
        public static final String FCHNACIMIENTO = "fchnacimiento";
    }
}
