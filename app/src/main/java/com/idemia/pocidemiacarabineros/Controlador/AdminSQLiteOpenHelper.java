package com.idemia.pocidemiacarabineros.Controlador;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.idemia.pocidemiacarabineros.Modelo.CarabineroContract;
import com.idemia.pocidemiacarabineros.Modelo.ControlIdentidadContract;
import com.idemia.pocidemiacarabineros.Modelo.ControlVehiculoContract;
import com.idemia.pocidemiacarabineros.Modelo.InfraccionTransitoContract;
import com.idemia.pocidemiacarabineros.Modelo.PersonaContract;
import com.idemia.pocidemiacarabineros.Modelo.UnidadPolicialContract;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PoC.db";

    public AdminSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UnidadPolicialContract.SQL_CREATE_UNIDADPOLICIAL);
        db.execSQL(CarabineroContract.SQL_CREATE_CARABINERO);
        db.execSQL(PersonaContract.SQL_CREATE_PERSONA);
        db.execSQL(InfraccionTransitoContract.SQL_CREATE_UNIDADPOLICIAL);
        db.execSQL(ControlVehiculoContract.SQL_CREATE_CONTROLVEHICULO);
        db.execSQL(ControlIdentidadContract.SQL_CREATE_CONTROLIDENTIDAD);
        db.execSQL("create table articulos(codigo int primary key , nombre text, cedula text, placa text, fecha date, hora time, motivo text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
