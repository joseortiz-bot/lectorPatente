package com.idemia.pocidemiacarabineros.Modelo;

import android.provider.BaseColumns;

public class InfraccionTransitoContract {
    public static final String SQL_CREATE_UNIDADPOLICIAL =
            "CREATE TABLE " + InfraccionTransitoEntry.TABLE_NAME + " (" +
                    InfraccionTransitoEntry.ID + " INTEGER PRIMARY KEY," +
                    InfraccionTransitoEntry.FECHAINFRACCION + " TEXT," +
                    InfraccionTransitoEntry.CDGMOTIVO + " TEXT," +
                    InfraccionTransitoEntry.OBS + " TEXT," +
                    InfraccionTransitoEntry.ESEMPADRONADA + " TEXT," +
                    InfraccionTransitoEntry.ESTADO + " INTEGER," +
                    InfraccionTransitoEntry.ID_CONTROLVEHICULO + " INTEGER)";
    private InfraccionTransitoContract() {}

    public static class InfraccionTransitoEntry implements BaseColumns {
        public static final String TABLE_NAME = "InfraccionTransito";
        public static final String ID = "id";
        public static final String FECHAINFRACCION = "fechainfraccion";
        public static final String CDGMOTIVO = "cdgmotivo";
        public static final String OBS = "obs";
        public static final String ESEMPADRONADA = "esempadronada";
        public static final String ESTADO = "estado";
        public static final String ID_CONTROLVEHICULO = "id_control_Vehiculo";
    }
}
