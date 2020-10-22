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
import android.printer.sdk.PosFactory;
import android.printer.sdk.bean.TextData;
import android.printer.sdk.constant.BarCode;
import android.printer.sdk.interfaces.IPosApi;
import android.printer.sdk.interfaces.OnPrintEventListener;
import android.printer.sdk.util.PowerUtils;
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

import cn.pda.serialport.SerialDriver;

public class ControlVehiculoFragment extends Fragment  implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    protected static final String TAG="ControlVehiculoFragment";
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    TextView fechaHora;
    public static EditText rut;
    public static TextView patente;
    EditText nombre, fecha;
    Date tiempo;
    int diaAct, mesAct, anoAct, horaAct, minAct;
    String fechaAct, horarioAct;
    AdminSQLiteOpenHelper dbHelper;
    double longitude, latitude;
    float presi;
    BuscaPosicion buscaPosicion;
    String direccion,textPatente;
    Button limpiar,ingresa,imprimir;
    ImageView imageQR, imagePatente;
    SharedPreferences sharedPref;
    ImageView calendar;
    Spinner snp_marca, snp_motivo;
    RequestQueue requestQueue;
    String pos="0";
    AlertDialog.Builder builder;
    private IPosApi mPosApi;
    private int mConcentration=25;

    public static ControlVehiculoFragment newInstance() {
        return new ControlVehiculoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.item_control_vehiculo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeKeyboard();
        dbHelper = new AdminSQLiteOpenHelper(getActivity());
        fechaHora=(TextView) view.findViewById(R.id.textFechaHora);
        patente = (TextView) view.findViewById(R.id.textPatente);
        rut = (EditText) view.findViewById(R.id.textRut);
        ingresa = view.findViewById(R.id.btn_ingresar);
        limpiar = view.findViewById(R.id.btn_limpiar);
        imprimir = view.findViewById(R.id.btn_imprimir);
        nombre = view.findViewById(R.id.textNombre);
        fecha = view.findViewById(R.id.textFechaNacimiento);
        calendar = view.findViewById(R.id.imageCalendario);
        snp_marca = (Spinner) view.findViewById(R.id.spinnerMarca);
        snp_motivo = (Spinner) view.findViewById(R.id.spinnerMotivo);
        imprimir = view.findViewById(R.id.btn_imprimir);
        imageQR = view.findViewById(R.id.imageQR);
        imagePatente = view.findViewById(R.id.imagePatente);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(getActivity());
        obtenerDireccion();
        rut.setText(sharedPref.getString("rut", ""));
        initPos();
        //nombre.setText(sharedPref.getString("nombre", ""));
        rescatarPatente();
        //patente.setText(sharedPref.getString("patente", ""));
        //fecha.setText(sharedPref.getString("fecha", ""));
        int marc = Integer.valueOf(sharedPref.getString("marca", "0"));
        int motiv = Integer.valueOf(sharedPref.getString("motivo", "0"));
        obtenerTiempo();
        ArrayAdapter<CharSequence> adapter_marca = ArrayAdapter.createFromResource(getActivity(),R.array.lista_marcas, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter_motivo = ArrayAdapter.createFromResource(getActivity(),R.array.lista_motivos, android.R.layout.simple_spinner_item);
        adapter_marca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_motivo.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        snp_marca.setAdapter(adapter_marca);
        snp_motivo.setAdapter(adapter_motivo);
        snp_marca.setSelection(marc);
        snp_motivo.setSelection(motiv);
        ingresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rut.getText().length()>0){
                    if(nombre.getText().length()>0){
                        if(fecha.getText().length()>0){
                            if(patente.getText().length()>0){
                                if(ingresarPatente()){
                                    confirmaIngreso();
                                }
                            }else {
                                Toast.makeText(getActivity(),"Debe ingresar una Patente",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getActivity(),"Debe ingresar la Fecha de Nacimiento",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getActivity(),"Debe ingresar un Nombre",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(),"Debe ingresar un Rut",Toast.LENGTH_SHORT).show();
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
        snp_marca.setOnItemSelectedListener(this);
        imageQR.setOnClickListener(this);
        imagePatente.setOnClickListener(this);
        snp_motivo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor  = sharedPref.edit();
                pos = String.valueOf(snp_motivo.getSelectedItemPosition());
                editor.putString("motivo",pos);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rut.getText().length()>0){
                    if(rut.getText().length()>0 && rut.getText().length() < 10){
                        if(fecha.getText().length()>0){
                            if(patente.getText().length()>0){
                                if(ingresarPatente()){

                                    confirmaImpresion();
                                }
                            }else {
                                Toast.makeText(getActivity(),"Debe ingresar una Patente",Toast.LENGTH_SHORT).show();
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
        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarCampos();
            }
        });
    }

    public void confirmaIngreso() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Registro almacenado correctamente..!")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        limpiarCampos();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
    public void limpiarCampos(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("rut", "");
        editor.putString("nombre","");
        editor.putString("fecha","");
        editor.putString("patente","");
        editor.putString("marca","0");
        editor.putString("motivo","0");
        editor.commit();
        rut.setText("");
        nombre.setText("");
        fecha.setText("");
        patente.setText("");
        snp_marca.setSelection(0);
        snp_motivo.setSelection(0);
    }
    public void confirmaImpresion() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Registro almacenado y en proceso de impresión..!")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String Content= "Infración de Tránsito" + "\n"+ " .......................... "+"\n"
                                + "Fecha y Hora: " + fechaHora.getText() + "\n"
                                + "Rut: " + rut.getText() + "\n"
                                + "Nombre: " + nombre.getText() + "\n"
                                + "Fecha Nacimiento: " + fecha.getText() + "\n"
                                + "Patente: " + patente.getText() + "\n"
                                + "Marca: " + String.valueOf(snp_marca.getSelectedItem()) + "\n"
                                + "Infración: " + String.valueOf(snp_motivo.getSelectedItem()) + "\n"
                                + "...................................";

                        TextData textData1=new TextData ();
                        textData1.addConcentration (mConcentration);
                        textData1.addFont (BarCode.FONT_ASCII_12x24);
                        textData1.addTextAlign (BarCode.ALIGN_CENTER);
                        textData1.addFontSize (BarCode.NORMAL);
                        textData1.addText (Content);
                        textData1.addText ("\n");
                        textData1.addText ("\n");
                        textData1.addText ("\n");
                        textData1.addText ("\n");
                        textData1.addText ("\n");

                        try {
                        mPosApi.addText (textData1);
                        mPosApi.printStart ();
                        }catch (Exception ex){

                        }
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
    private void rescatarPatente() {
        Bundle bundle = getActivity().getIntent().getExtras();
        try {
            if(bundle.getString("patente")!= null)
            {
                textPatente = bundle.getString("patente");
                patente.setText(textPatente);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("patente", textPatente);
                editor.commit();
            }

        }catch (Exception ex){

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
    private boolean ingresarPatente(){
        obtenerTiempo();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String fecha=horarioAct.replace(" ","")+" "+fechaAct;
        ContentValues values = new ContentValues();
        values.put(ControlVehiculoContract.ControlVehiculoEntry.PATENTE, patente.getText().toString());
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
            try {
                PruebaWebService();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        buscaPosicion.stopListener();
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageCalendario:
            case R.id.textFechaNacimiento:
                ObtenerFecha();
                break;
            case R.id.imageQR:
                Intent siguiente = new Intent(getActivity(), ActivityScannerQR.class);

                siguiente.putExtra("opcion",1);
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[] { Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                }else {
                    startActivity(siguiente);
                }
                break;
            case R.id.imagePatente:
                Intent patenteIntent = new Intent(getActivity(), ActivityScannerPatente.class);
                patenteIntent.putExtra("opcion",1);
                startActivity(patenteIntent);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences.Editor editor;
        String pos="";
        editor = sharedPref.edit();
        pos = String.valueOf(snp_marca.getSelectedItemPosition());
        editor.putString("marca",pos);
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void PruebaWebService() throws JSONException {
        tiempo = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date2 = null;
        Timestamp stamp=null,stamp2=null;
        String fechaNac = fecha.getText().toString().replace(" ","");
        try {
            date2 = format.parse(fechaNac);
            stamp = new Timestamp(tiempo.getTime());
            stamp2 = new Timestamp(date2.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dat = "{\n" +
                " \"datos\": [\n" +
                "      {\n" +
                " \"id\": 1, " +
                " \"fechaControl\": "+stamp.getTime()+", "+
                " \"patente\": \""+patente.getText().toString()+"\", "+
                " \"marca\": \""+snp_marca.getItemAtPosition(Integer.valueOf(pos))+"\", "+
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
                                String res = new String(response.data,HttpHeaderParser.parseCharset(response.headers, "utf-8"));
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
                +"&patente="+patente.getText().toString()
                +"&marca="+snp_marca.getItemAtPosition(Integer.valueOf(pos))
                +"&direccionRef="+direccion);
    }


    //PrintApi

    public void initPos() {

        try {
            PowerUtils.powerOnOrOff (1, "1");
            PosFactory.registerCommunicateDriver (getActivity(), new SerialDriver()); // 注册串口类 Register serial driver
            mPosApi=PosFactory.getPosDevice (); // 获取打印机实例 get printer driver
            mPosApi.setPrintEventListener (onPrintEventListener);
            mPosApi.openDev ("/dev/ttyS2", 115200, 0);
            mPosApi.setPos ().setAutoEnableMark (false)
                    .setEncode (-1)
                    .setLanguage (-1)
                    .setPrintSpeed (-1)
                    .setMarkDistance (-1).init ();// 初始化打印机 init printer
        }catch (Exception ex){

        }
    }

    public OnPrintEventListener onPrintEventListener=new OnPrintEventListener () {
        @Override
        public void onPrintState(int state) {
            switch (state) {
                case BarCode.ERR_POS_PRINT_SUCC:
                    //showToastShort (getString (R.string.toast_print_success));
                    break;
                case BarCode.ERR_POS_PRINT_FAILED:
                   // showToastShort (getString (R.string.toast_print_error));
                    break;
                case BarCode.ERR_POS_PRINT_HIGH_TEMPERATURE:
                    //showToastShort (getString (R.string.toast_high_temperature));
                    break;
                case BarCode.ERR_POS_PRINT_NO_PAPER:
                    //showToastShort (getString (R.string.toast_no_paper));
                    break;
                case 4:
                    break;
            }
        }
    };

}
