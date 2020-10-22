package com.idemia.pocidemiacarabineros.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.idemia.pocidemiacarabineros.R;

import java.io.IOException;

public class ActivityEscanearChip extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private NfcAdapter NfcAndroidAdapter;
    private PendingIntent NfcIntent;
    private TextView textRut;
    Button cancel;
    Class clase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear_chip);
        NfcAndroidAdapter = NfcAdapter.getDefaultAdapter(this);
        textRut = findViewById(R.id.dato_rut);
        cancel = findViewById(R.id.btn_cancelar);
        rescatarOpcion();
        if (NfcAndroidAdapter == null) {
            // Device does not support NFC
            Toast.makeText(this,"Device does not support NFC!",Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (!NfcAndroidAdapter.isEnabled()) {
                // NFC is disabled
                Toast.makeText(this, "NFC desabilitado",Toast.LENGTH_LONG).show();
            } else {
                //startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                NfcIntent = PendingIntent.getActivity(this,0,
                        new Intent(this, getClass())
                                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                //handleIntent(getIntent());
            }

        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void rescatarOpcion() {
        Bundle bundle = getIntent().getExtras();
        int opcion = bundle.getInt("opcion");
        switch (opcion){
            case 0:
                clase = ActivityControlIdentidad.class;
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(NfcAndroidAdapter!=null){
            NfcAndroidAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(NfcAndroidAdapter!=null){
            NfcAndroidAdapter.enableForegroundDispatch(this,NfcIntent,null,null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "La accion es "+action);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.d(TAG, "entra al NDEF ");
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.d(TAG, "entra al TECH ");
        }else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            byte[] extraId = tag.getId();
            if (techList[0].equals(IsoDep.class.getName())){
                IsoDep isoDepTag = IsoDep.get(tag);
                byte[] hilayer = isoDepTag.getHiLayerResponse();
                StringBuilder stringBuilder = new StringBuilder();

                if (isoDepTag != null) {
                    try {
                        byte[] SELECT = {
                                (byte) 0x00, // CLA Class
                                (byte) 0xA4, // INS Instruction
                                (byte) 0x00, // P1  Parameter 1
                                (byte) 0x00, // P2  Parameter 2
                                (byte) 0x02, // Length
                                (byte) 0x01,
                                (byte) 0x00 // AID 315449432e494341
                        };
                        isoDepTag.connect();
                        byte[] result = isoDepTag.transceive(SELECT);
                        Log.i(TAG,"La respuesta de 1 es "+toHex(result));
                        if (result[0] == (byte) 0x90 && result[1] == (byte) 0x00){
                            Log.i(TAG,"La respuesta es exitosa ");
                        }else{
                            Log.i(TAG,"La respuesta No es exitosa ");
                        }
                        Log.i(TAG,"El resultado de IsoDepTag es "+hilayer.toString()+" y el tamaño de result es "+result.length);

                        // TODO: send further APDU commands according to the protocol specification
                        //response = isoDep.transceive(APDU);
                        isoDepTag.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.i(TAG,"El ID del Extra es "+tag+" y el tamaño de lista es "+techList.length);
            textRut.setText(String.valueOf(toHex(extraId)));
        }
    }
    String toHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10) {
                stringBuilder.append("0");
            }
            stringBuilder.append(Integer.toHexString(b));
            if (i > 0) {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }
}