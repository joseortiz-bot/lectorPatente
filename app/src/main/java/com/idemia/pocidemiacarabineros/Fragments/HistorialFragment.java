package com.idemia.pocidemiacarabineros.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.idemia.pocidemiacarabineros.Controlador.AdminSQLiteOpenHelper;
import com.idemia.pocidemiacarabineros.Adapters.AdapterInfoPatente;
import com.idemia.pocidemiacarabineros.Modelo.ControlVehiculo;
import com.idemia.pocidemiacarabineros.Modelo.ControlVehiculoContract;
import com.idemia.pocidemiacarabineros.R;
import com.idemia.pocidemiacarabineros.Activity.ActivityMapa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistorialFragment extends Fragment {

    ArrayList<String> filtros = new ArrayList<String>();
    String filtro="";
    Spinner staticSpinner;
    private AdapterInfoPatente patenteAdapter;
    private ListView listView;
    ArrayList<ControlVehiculo> items, itemsFiltro;
    AdminSQLiteOpenHelper dbHelper;
    ControlVehiculo controlVehiculo;
    Button mapa,actualizar;

    public static HistorialFragment newInstance() {
        return new HistorialFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.item_listado_patentes, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new AdminSQLiteOpenHelper(getActivity());
        actualizar = (Button) view.findViewById(R.id.btn_actualizar);
        mapa = (Button) view.findViewById(R.id.btn_mapa);
        filtros.add("Seleccione Fecha");

        //Visualizacion Filtros
        staticSpinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,filtros);
        staticSpinner.setDropDownWidth(R.layout.spinner_dropdown_item);
        staticSpinner.setAdapter(adapter);
        //Visualizacion Listado
        listView = view.findViewById(R.id.lista);
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
                if(items.size()>0){
                    Intent map = new Intent(getActivity(), ActivityMapa.class);
                    map.putExtra("filtro", filtro);
                    startActivity(map);
                }else{
                    Toast.makeText(getActivity(), "No existe información para mostrar",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void leerPatente(){
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
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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
            patenteAdapter = new AdapterInfoPatente(getActivity(), itemsFiltro);
        }else {
            patenteAdapter = new AdapterInfoPatente(getActivity(), items);
        }
        listView.setAdapter(patenteAdapter);
    }
}
