package com.idemia.pocidemiacarabineros.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.idemia.pocidemiacarabineros.Controlador.AdminSQLiteOpenHelper;
import com.idemia.pocidemiacarabineros.Adapters.AdapterInfoPatente;
import com.idemia.pocidemiacarabineros.Modelo.ControlVehiculo;
import com.idemia.pocidemiacarabineros.Modelo.ControlVehiculoContract;
import com.idemia.pocidemiacarabineros.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ActivityHistorialPatentes extends AppCompatActivity {
    protected static final String TAG="ListadoPatente";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    ArrayList<String> filtros = new ArrayList<String>();
    String filtro="";
    Spinner staticSpinner;
    private AdapterInfoPatente patenteAdapter;
    private ListView listView;
    ArrayList<ControlVehiculo> items, itemsFiltro;
    AdminSQLiteOpenHelper dbHelper;
    ControlVehiculo controlVehiculo;
    Button mapa,actualizar;
    private ArrayList<String> permissionsToRequest;
    private ArrayList permissions = new ArrayList();
    private ArrayList<String> permissionsRejected = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_listado_patentes);
        dbHelper = new AdminSQLiteOpenHelper(this);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        actualizar = findViewById(R.id.btn_actualizar);
        mapa = findViewById(R.id.btn_mapa);
        filtros.add("Seleccione Fecha");

        //Visualizacion Filtros
        staticSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(ActivityHistorialPatentes.this,R.layout.support_simple_spinner_dropdown_item,filtros);
        staticSpinner.setAdapter(adapter);
        //Visualizacion Listado
        listView = findViewById(R.id.lista);
        items = new ArrayList<ControlVehiculo>();
        itemsFiltro = new ArrayList<ControlVehiculo>();
        leerPatente();
        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    filtro=filtros.get(position);
                    leerPatente();
                }else {
                    filtro="";
                    leerPatente();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerPatente();
            }
        });

        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Se apreta el boton Mapa");
                Intent map = new Intent(ActivityHistorialPatentes.this, ActivityMapa.class);
                map.putExtra("filtro", filtro);
                startActivity(map);
            }
        });
    }

    private void leerPatente(){
        Log.i(TAG, "Ingresa al metodo para leer una Patente");
        items.clear();
        itemsFiltro.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                ControlVehiculoContract.ControlVehiculoEntry.ID,
                ControlVehiculoContract.ControlVehiculoEntry.PATENTE,
                ControlVehiculoContract.ControlVehiculoEntry.FECHACONTROL,
                ControlVehiculoContract.ControlVehiculoEntry.LATITUD,
                ControlVehiculoContract.ControlVehiculoEntry.LONGITUD,
                ControlVehiculoContract.ControlVehiculoEntry.ESTADO,
                ControlVehiculoContract.ControlVehiculoEntry.PRECISION
        };
        String sortOrder =
                ControlVehiculoContract.ControlVehiculoEntry.ID + " DESC";
        Cursor cursor = db.query(
                ControlVehiculoContract.ControlVehiculoEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        while(cursor.moveToNext()) {
            String pat = cursor.getString(cursor.getColumnIndex(ControlVehiculoContract.ControlVehiculoEntry.PATENTE));
            Double latit = cursor.getDouble(cursor.getColumnIndex(ControlVehiculoContract.ControlVehiculoEntry.LATITUD));
            Double longi = cursor.getDouble(cursor.getColumnIndex(ControlVehiculoContract.ControlVehiculoEntry.LONGITUD));
            String fecTot = cursor.getString(cursor.getColumnIndex(ControlVehiculoContract.ControlVehiculoEntry.FECHACONTROL));
            String[] pieces = fecTot.split(" ");
            boolean est=false;
            for(int i=0;i<filtros.size();i++){
                if(filtros.get(i).equals(pieces[1])){
                    est=true;
                    break;
                }
            }
            if(!est){
                filtros.add(pieces[1]);
            }
            String direccionTot="",direccion;
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> list = geocoder.getFromLocation(latit,longi,1);
                if(!list.isEmpty()){
                    Address direc = list.get(0);
                    direccion = direc.getAddressLine(0);
                }else{
                    direccion="Sin dirección";
                }
                String[] parts = direccion.split(",");
                if(parts.length>1){
                    direccionTot = parts[0]+", "+parts[1];
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            controlVehiculo = new ControlVehiculo(pat,direccionTot,pieces[1],pieces[0]);
            if(filtro.length()>1){
                if(pieces[1].equals(filtro)){
                    itemsFiltro.add(controlVehiculo);
                }
            }
            items.add(controlVehiculo);
        }
        db.close();
        cursor.close();
        if(filtro.length()>1){
            patenteAdapter = new AdapterInfoPatente(ActivityHistorialPatentes.this, itemsFiltro);
        }else {
            patenteAdapter = new AdapterInfoPatente(ActivityHistorialPatentes.this, items);
        }
        listView.setAdapter(patenteAdapter);
    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();
        for (Object perm : wanted) {
            if (!hasPermission(perm.toString())) {
                result.add(perm);
            }
        }
        return result;
    }
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0).toString())) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ActivityHistorialPatentes.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
