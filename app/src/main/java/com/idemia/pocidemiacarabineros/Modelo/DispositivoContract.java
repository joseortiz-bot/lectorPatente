package com.idemia.pocidemiacarabineros.Modelo;

import android.provider.BaseColumns;

public class DispositivoContract {
    public static final String SQL_CREATE_DISPOSITIVO =
            "CREATE TABLE " + DispositivoEntry.TABLE_NAME + " (" +
                    DispositivoEntry.ID + " INTEGER PRIMARY KEY," +
                    DispositivoEntry.MARCA + " TEXT," +
                    DispositivoEntry.IMEI + " TEXT," +
                    DispositivoEntry.SO + " TEXT," +
                    DispositivoEntry.PARTNUMBER + " TEXT," +
                    DispositivoEntry.SERIALNUMBER + " TEXT)";
    private DispositivoContract() {}

    public static class DispositivoEntry implements BaseColumns {
        public static final String TABLE_NAME = "Dispositivo";
        public static final String ID = "id";
        public static final String MARCA = "marca";
        public static final String IMEI = "imei";
        public static final String SO = "so";
        public static final String PARTNUMBER = "partnumber";
        public static final String SERIALNUMBER = "serialnumber";
    }
}
