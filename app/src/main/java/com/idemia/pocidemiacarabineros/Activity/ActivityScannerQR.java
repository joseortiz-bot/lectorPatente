package com.idemia.pocidemiacarabineros.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.idemia.pocidemiacarabineros.Modelo.Constants;
import com.idemia.pocidemiacarabineros.R;

import java.io.UnsupportedEncodingException;

public class ActivityScannerQR extends AppCompatActivity {
    protected static final String TAG = "ScannerQR";
    private static final int SEQ_BARCODE_OPEN = 100;
    private static final int SEQ_BARCODE_CLOSE = 200;
    TextView txtBarcodeValue;
    Button btnAction, volver;
    int opcion = 3;
    String rut;
    Class clase;
    private boolean mIsRegisterReceiver;
    public boolean otroDevice = false;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initViews();
        registerReceiver();
    }
    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        btnAction = findViewById(R.id.btn_aceptar);
        volver = findViewById(R.id.btn_cancelar);
        sharedPref = ActivityScannerQR.this.getPreferences(Context.MODE_PRIVATE);
        mIsRegisterReceiver = false;
        try {
        }catch (Exception ex){
            otroDevice = true;
        }
        rescatarOpcion();
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtBarcodeValue.getText().length() > 0) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("rut",txtBarcodeValue.getText().toString());
                    Intent back = new Intent(ActivityScannerQR.this, clase);
                    back.putExtra("rut", txtBarcodeValue.getText().toString());
                    startActivity(back);
                    editor.commit();
                    finish();
                } else {
                    Toast.makeText(ActivityScannerQR.this, "No se a reconocido en codigo QR", Toast.LENGTH_LONG).show();
                }
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void registerReceiver()
    {
        if(mIsRegisterReceiver) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction("scan.rcv.message");
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA);
        registerReceiver(mReceiver, filter);
        mIsRegisterReceiver = true;
    }
    private void rescatarOpcion() {
        Bundle bundle = getIntent().getExtras();
        opcion = bundle.getInt("opcion");
        switch (opcion) {
            case 0:
                clase = ActivityControlIdentidad.class;
                Log.i(TAG, "La opcion 0");
                break;
            case 1:
                clase = ActivityControlIdentidad.class;
                Log.i(TAG, "La opcion 1");
                break;
        }
    }
    @Override
    protected void onPause() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_BARCODE_CLOSE);
        intent.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_CLOSE);
        sendBroadcast(intent);
        unregisterReceiver();
        super.onPause();
    }
    private void unregisterReceiver()
    {
        if(!mIsRegisterReceiver) return;
        unregisterReceiver(mReceiver);
        mIsRegisterReceiver = false;
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA))
            {
                byte[] data = intent.getByteArrayExtra(Constants.EXTRA_BARCODE_DECODING_DATA);
                String result = "";
                String dataResult = "";
                if(data!=null)
                {
                    dataResult = new String(data);
                    if(dataResult.contains("�"))
                    {
                        try {
                            dataResult = new String(data, "Shift-JIS");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                result += dataResult;
                String[] datos = result.split("\\?");
                if(datos.length>1){
                    String[] dato = datos[1].split("&");
                    if(dato.length>1){
                        String[] numero = dato[0].split("=");
                        if(numero.length>1){
                            rut = numero[1];
                            txtBarcodeValue.setText(rut);
                        }else {
                            txtBarcodeValue.setText("");
                            Toast.makeText(ActivityScannerQR.this,"No es un Código QR válido",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        txtBarcodeValue.setText("");
                        Toast.makeText(ActivityScannerQR.this,"No es un Código QR válido",Toast.LENGTH_LONG).show();
                    }
                }else {
                    txtBarcodeValue.setText("");
                    Toast.makeText(ActivityScannerQR.this,"No es un Código QR válido",Toast.LENGTH_LONG).show();
                }
            }else{

                byte[] data = intent.getByteArrayExtra("barocode");
                String result = "";
                String dataResult = "";
                if(data!=null)
                {
                    dataResult = new String(data);
                    if(dataResult.contains("�"))
                    {
                        try {
                            dataResult = new String(data, "Shift-JIS");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                result += dataResult;
                String[] datos = result.split("\\?");
                if(datos.length>1){
                    String[] dato = datos[1].split("&");
                    if(dato.length>1){
                        String[] numero = dato[0].split("=");
                        if(numero.length>1){
                            rut = numero[1];
                            txtBarcodeValue.setText(rut);
                        }else {
                            txtBarcodeValue.setText("");
                            Toast.makeText(ActivityScannerQR.this,"No es un Código QR válido",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        txtBarcodeValue.setText("");
                        Toast.makeText(ActivityScannerQR.this,"No es un Código QR válido",Toast.LENGTH_LONG).show();
                    }
                }else {
                    txtBarcodeValue.setText("");
                    Toast.makeText(ActivityScannerQR.this,"No es un Código QR válido",Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    private void destroyEvent()
    {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_BARCODE_CLOSE);
        intent.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_CLOSE);
        sendBroadcast(intent);
        unregisterReceiver();
    }
    @Override
    protected void onResume() {
        registerReceiver();
        resetCurrentView();
        super.onResume();
    }
    private void resetCurrentView()
    {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_BARCODE_OPEN);
        intent.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_OPEN);
        sendBroadcast(intent);
        return;
    }

    @Override
    protected void onDestroy() {
        destroyEvent();
        super.onDestroy();
    }
}
