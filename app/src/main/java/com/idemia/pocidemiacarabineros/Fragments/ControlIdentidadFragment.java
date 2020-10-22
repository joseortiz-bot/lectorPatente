package com.idemia.pocidemiacarabineros.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.idemia.pocidemiacarabineros.Activity.ActivityScannerPatente;
import com.idemia.pocidemiacarabineros.Controlador.AdminSQLiteOpenHelper;
import com.idemia.pocidemiacarabineros.Controlador.BuscaPosicion;
import com.idemia.pocidemiacarabineros.Modelo.ControlVehiculoContract;
import com.idemia.pocidemiacarabineros.R;
import com.idemia.pocidemiacarabineros.Activity.ActivityEscanearChip;
import com.idemia.pocidemiacarabineros.Activity.ActivityScannerQR;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ControlIdentidadFragment  extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private String TAG="ControlIdentidadFragment";
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    TextView fechaHora;
    EditText nombre, fecha, etObservacion;
    Spinner spProcedimiento;
    public static EditText rut;
    Date tiempo;
    int diaAct, mesAct, anoAct, horaAct, minAct;
    String fechaAct, horarioAct;
    AdminSQLiteOpenHelper dbHelper;
    double longitude, latitude;
    float presi;
    BuscaPosicion buscaPosicion;
    String direccion,textRut;
    Button ingresa,volver;
    ImageView imageQR;
    SharedPreferences sharedPref;
    ImageView calendar;
    RequestQueue requestQueue;
    String pos="0";
    AlertDialog.Builder builder;

    public static ControlIdentidadFragment newInstance() {
        return new ControlIdentidadFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_control_identidad, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeKeyboard();

        dbHelper = new AdminSQLiteOpenHelper(getActivity());
        fechaHora=(TextView) view.findViewById(R.id.textFechaHora);
        rut = (EditText) view.findViewById(R.id.textRutUser);
        ingresa = view.findViewById(R.id.btn_ingresar);
        volver = view.findViewById(R.id.btn_cancelar);
        etObservacion = view.findViewById(R.id.et_observacion);
        spProcedimiento = view.findViewById(R.id.spinnerproc);
        nombre = view.findViewById(R.id.et_nom);
        fecha = view.findViewById(R.id.et_fecha_nacimiento);
        calendar = view.findViewById(R.id.imageCalendarioUser);
        imageQR = view.findViewById(R.id.imageQRUser);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(getActivity());
        obtenerDireccion();
        rescatarRut();
        ArrayAdapter<CharSequence> adapter_procedimiento = ArrayAdapter.createFromResource(getActivity(),R.array.lista_procedimiento, android.R.layout.simple_spinner_item);
        spProcedimiento.setAdapter(adapter_procedimiento);
        //rut.setText(sharedPref.getString("rut", ""));
        //nombre.setText(sharedPref.getString("nombre", ""));
        //etObservacion.setText(sharedPref.getString("observacion", ""));
        //etProcedimiento.setText(sharedPref.getString("procedimiento", ""));
        //rescatarPatente();;
        //fecha.setText(sharedPref.getString("fecha", ""));
        obtenerTiempo();
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //confirmaSalida();
                SharedPreferences.Editor editor = sharedPref.edit();
                rut.setText("");
                nombre.setText("");
                fecha.setText("");
                //spProcedimiento.setText("");
                etObservacion.setText("");
                editor.putString("rut", "");
                editor.putString("nombre","");
                editor.putString("fecha","");
                editor.putString("observacion","");
                editor.putString("procedimiento","");
                editor.commit();
                //Intent back = new Intent(RegistroPatente.this, ActivityMenuPrincipal.class);
                //startActivity(back);
            }
        });
        ingresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rut.getText().length()>0 && rut.getText().length() < 10){
                    if(nombre.getText().length()>0){
                        if(fecha.getText().length()>0){
                            if(etObservacion.getText().length()>0){
                              //  if(spProcedimiento.getText().length()>0) {
                                    if (ingresarUsuario()) {
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("rut", "");
                                        editor.putString("nombre", "");
                                        editor.putString("fecha", "");
                                        editor.putString("patente", "");
                                        editor.putString("observacion", "");
                                        editor.putString("procedimiento", "");
                                        editor.commit();
                                        confirmaIngreso();
                                    }
                               /* }else {
                                    Toast.makeText(getActivity(),"Debe ingresar un Procedimiento",Toast.LENGTH_SHORT).show();
                                }*/
                            }else {
                                Toast.makeText(getActivity(),"Debe ingresar una Observación",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getActivity(),"Debe ingresar la Fecha de Nacimiento",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getActivity(),"Debe ingresar un Nombre",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(),"Rut no ingresado o demasiado largo",Toast.LENGTH_SHORT).show();
                }
            }
        });
        nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("nombre", s.toString());
                editor.apply();
            }
        });
        fecha.setOnClickListener(this);
        calendar.setOnClickListener(this);
        imageQR.setOnClickListener(this);
        spProcedimiento.setOnItemSelectedListener(this);

    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    public void confirmaSalida() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Seguro que desea cerrar la app?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.show();
    }
    public void confirmaIngreso() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Registro almacenado correctamente..!")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rut.setText("");
                        nombre.setText("");
                        fecha.setText("");
                        etObservacion.setText("");
                       // spProcedimiento.getSelectedItem();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
    public void confirmaImpresion() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Registro almacenado y en proceso de impresión..!")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
    private void rescatarPatente() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle.getString("patente")!= null)
        {
            //textPatente = bundle.getString("patente");
            //patente.setText(textPatente);
            //SharedPreferences.Editor editor = sharedPref.edit();
            //editor.putString("patente", textPatente);
            //editor.commit();
        }
    }
    private void rescatarRut() {
        Bundle bundle = getActivity().getIntent().getExtras();
        //System.out.println("rut: " + bundle.getString("rut"));
        try {
            if(bundle.getString("rut")!= null)
            {
                textRut = bundle.getString("rut");
                rut.setText(textRut);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("rut", textRut);
                editor.commit();
            }

        }catch (Exception ex){
            System.out.println("rut: ");
        }

    }
    private void ObtenerFecha() {
        Calendar calendar;
        DatePickerDialog pickerDialog;
        calendar = Calendar.getInstance();
        pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final String selectedDate = String.format("%02d",dayOfMonth) + " / " + String.format("%02d",(month+1)) + " / " + year;
                fecha.setText(selectedDate);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("fecha", selectedDate);
                editor.commit();
            }
        },anoAct,mesAct,diaAct);
        pickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        pickerDialog.show();
    }
    private void obtenerTiempo() {
        tiempo = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(tiempo);
        anoAct = calendar.get(Calendar.YEAR);
        mesAct = calendar.get(Calendar.MONTH)+1;
        diaAct = calendar.get(Calendar.DAY_OF_MONTH);
        horaAct = calendar.get(Calendar.HOUR_OF_DAY);
        minAct = calendar.get(Calendar.MINUTE);
        fechaAct = String.format("%02d",diaAct)+"/"+ String.format("%02d",mesAct)+"/"+anoAct;
        horarioAct = String.format("%02d",horaAct)+":"+ String.format("%02d",minAct);
        fechaHora.setText(fechaAct+" "+horarioAct);
    }
    public void ingresaPatente(View view){
        Intent siguiente = new Intent(getActivity(), ActivityScannerPatente.class);
        startActivity(siguiente);
        //finish();
    }
    public void ingresaNFC(View view){
        Intent siguiente = new Intent(getActivity(), ActivityEscanearChip.class);
        siguiente.putExtra("opcion",1);
        startActivity(siguiente);
        // finish();
    }
    private boolean ingresarUsuario(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String fecha=horarioAct.replace(" ","")+" "+fechaAct;
        ContentValues values = new ContentValues();
        //values.put(ControlVehiculoContract.ControlVehiculoEntry.PATENTE, patente.getText().toString());
        values.put(ControlVehiculoContract.ControlVehiculoEntry.FECHACONTROL, fecha);
        values.put(ControlVehiculoContract.ControlVehiculoEntry.LATITUD,latitude);
        values.put(ControlVehiculoContract.ControlVehiculoEntry.LONGITUD,longitude);
        values.put(ControlVehiculoContract.ControlVehiculoEntry.PRECISION,presi);
        values.put(ControlVehiculoContract.ControlVehiculoEntry.ESTADO,0);
        long newRowId = db.insert(ControlVehiculoContract.ControlVehiculoEntry.TABLE_NAME, null, values);
        dbHelper.close();
        db.close();
        if(newRowId>0){
            Log.i(TAG,"Nuevo Registro almacenado numero: "+ String.valueOf(newRowId));
            /*try {
                PruebaWebService();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            return true;
        }else{
            return false;
        }
    }
    private void obtenerDireccion() {
        buscaPosicion = new BuscaPosicion(getActivity());
        if (buscaPosicion.canGetLocation()) {
            longitude = buscaPosicion.getLongitude();
            latitude = buscaPosicion.getLatitude();
            presi = buscaPosicion.getAccuracy();
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> list = geocoder.getFromLocation(latitude,longitude,1);
                if(!list.isEmpty()){
                    Address direc = list.get(0);
                    direccion = direc.getAddressLine(0);
                }else{
                    direccion="Sin dirección";
                }
                String[] parts = direccion.split(",");
                if(parts.length>1){
                    direccion = parts[0]+", "+parts[1];
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            buscaPosicion.showSettingsAlert();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageCalendarioUser:
            case R.id.et_fecha_nacimiento:
                ObtenerFecha();
                break;
            case R.id.imageQRUser:
                Intent siguiente = new Intent(getActivity(), ActivityScannerQR.class);
                siguiente.putExtra("opcion",0);
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[] { Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                }else {
                    startActivity(siguiente);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences.Editor editor;
        String pos="";
        //editor = sharedPref.edit();
        //pos = String.valueOf(snp_marca.getSelectedItemPosition());
        //editor.putString("marca",pos);
        //editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void PruebaWebService() throws JSONException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null,date2 = null;
        Timestamp stamp=null,stamp2=null;
        String fechaNac = fecha.getText().toString().replace(" ","");
        try {
            date = format.parse(fechaHora.getText().toString());
            date2 = format.parse(fechaNac);
            stamp = new Timestamp(date.getTime());
            stamp2 = new Timestamp(date2.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dat = "{\n" +
                " \"datos\": [\n" +
                "      {\n" +
                " \"id\": 1, " +
                " \"fechaControl\": "+stamp.getTime()+", "+
                /*" \"patente\": \""+patente.getText().toString()+"\", "+
                " \"marca\": \""+snp_marca.getItemAtPosition(Integer.valueOf(pos))+"\", "+*/
                " \"modelo\": \"TIGUAN\", "+
                " \"color\": \"BLANCO PLATEADO\", "+
                " \"nmrChasis\": \"1111\", "+
                " \"nmrMotor\": \"1111\", "+
                " \"latitud\": "+latitude+", "+
                " \"longitud\": "+longitude+", "+
                " \"precision\": "+presi+", "+
                " \"estado\": 1, "+
                " \"direccionRef\": \""+direccion+"\", "+
                " \"idCarabinero\": \"1\",\n" +
                " \"personaConductor\": {\n" +
                " \"id\": 0," +
                " \"nombre\": \""+nombre.getText().toString()+"\", "+
                " \"rut\": \""+rut.getText().toString()+"\", "+
                " \"fchNacimiento\": "+stamp2.getTime()+
                " }\n" +
                "}\n" +
                "]"+
                "}";
        JSONObject jsnobject = new JSONObject(dat);
        JSONArray data = jsnobject.getJSONArray("datos");
        JsonArrayRequest strRequest = new JsonArrayRequest(Request.Method.POST,
                "https://pocidemia.valorunico.cl:8443/ModuloApiRest/ctrl-vehi/reg-ctrls",
                data,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        Log.i(TAG,"Ingreso a onResponse RegistrarWebService");
                        Log.i(TAG,"El mensaje es: "+response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                Log.i(TAG,res);
                            } catch (UnsupportedEncodingException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                Log.i(TAG,"El estado de la conexion es "+response.statusCode);
                return super.parseNetworkResponse(response);
            }
        }
                ;
        requestQueue.add(strRequest);
        Log.i(TAG,"https://pocidemia.valorunico.cl:8443/ModuloApiRest/ctrl-vehi/reg-ctrls\n"
                +"&fechaControl="+fechaAct
                /*+"&patente="+patente.getText().toString()
                +"&marca="+snp_marca.getItemAtPosition(Integer.valueOf(pos))*/
                +"&direccionRef="+direccion);
    }
}