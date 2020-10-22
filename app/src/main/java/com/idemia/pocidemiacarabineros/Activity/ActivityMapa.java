package com.idemia.pocidemiacarabineros.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.idemia.pocidemiacarabineros.Controlador.AdminSQLiteOpenHelper;
import com.idemia.pocidemiacarabineros.Modelo.ControlVehiculo;
import com.idemia.pocidemiacarabineros.Modelo.ControlVehiculoContract;
import com.idemia.pocidemiacarabineros.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ActivityMapa extends AppCompatActivity {
    protected static final String TAG="VerMapa";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private MapView mapView;
    private MapController mapController;
    Button volver;
    private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
    ArrayList<OverlayItem> puntos;
    ArrayList<String> coordenadas = new ArrayList<String>();
    ArrayList<ControlVehiculo> items;
    AdminSQLiteOpenHelper dbHelper;
    String filtro;
    private ArrayList<String> permissionsToRequest;
    private ArrayList permissions = new ArrayList();
    private ArrayList<String> permissionsRejected = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_mapa);

        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        mapView = (MapView) findViewById(R.id.openmapview);
        volver = findViewById(R.id.btn_atras);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        items = new ArrayList<ControlVehiculo>();
        dbHelper = new AdminSQLiteOpenHelper(this);
        getRuta();
        this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(puntos,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index,
                                                     final OverlayItem item) {
                        Log.i(TAG,"Item '" + item.getTitle());
                        return true; // We 'handled' this event.
                    }
                    @Override
                    public boolean onItemLongPress(final int index,
                                                   final OverlayItem item) {
                        Log.i(TAG,"Item '" + item.getTitle());
                        return false;
                    }
                }, this);
        this.mapView.getOverlays().add(this.mMyLocationOverlay);
        mapView.invalidate();
        //capa.setFocusItemsOnTap(true);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    void getRuta(){
        GeoPoint data;
        puntos = new ArrayList<OverlayItem>();
        leerPatente();
        Log.i(TAG, "Las coordenadas son: "+coordenadas.get(0));
        for(int i=0;i<coordenadas.size();i++){
            String[] part = coordenadas.get(i).split(",");
            data = new GeoPoint(Double.valueOf(part[0]),Double.valueOf(part[1]));
            puntos.add(new OverlayItem("Patente", "direccion", data));
        }
        mapController = (MapController) mapView.getController();
        mapController.setZoom(16);
        mapController.setCenter(puntos.get(0).getPoint());
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapView.setMultiTouchControls(true);
    }
    private void leerPatente(){
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("filtro")!= null)
        {
            filtro = bundle.getString("filtro");
            Log.i(TAG,"el filtro es: "+filtro);
        }

        Log.i(TAG, "Ingresa al metodo para leer una Patente");
        items.clear();
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
            if(filtro.equals(pieces[1])){
                coordenadas.add(latit+","+longi);
            }else if(filtro.equals("")){
                coordenadas.add(latit+","+longi);
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
                //direccionTot = parts[0]+", "+parts[1];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        db.close();
        cursor.close();
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
        new AlertDialog.Builder(ActivityMapa.this)
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
