package com.idemia.pocidemiacarabineros.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.idemia.pocidemiacarabineros.Fragments.ControlVehiculoFragment;
import com.idemia.pocidemiacarabineros.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class ActivityScannerPatente extends AppCompatActivity {
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    Date tiempo;
    int diaAct, mesAct, anoAct, horaAct, minAct;
    String fechaAct, horarioAct,patent="";
    public static TextView ingresoPatente;
    Button photo, ingresa, cancel, photo2;
    static final int TOMAR_FOTO = 1313;
    ImageView imageView;
    FirebaseVisionImage image;
    FirebaseVisionTextRecognizer detector;
    public static String patente = "";
    String salida="";
    String valorNumero = "0123456789";
    SharedPreferences sharedPref;

    //Jeimmy code:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingreso_patente);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);
        detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        ingresoPatente = (TextView) findViewById(R.id.ingreso_patente);
        photo = findViewById(R.id.btn_camara);
        photo2 = findViewById(R.id.btn_camara2);
        ingresa = findViewById(R.id.btn_ingresar);
        cancel = findViewById(R.id.btn_cancelar);
        imageView = findViewById(R.id.imageView);
        sharedPref = ActivityScannerPatente.this.getPreferences(Context.MODE_PRIVATE);
        obtenerTiempo();
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFoto();
            }
        });
        photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IngresarPatente.this, CaptureActivity.class);
                startActivity(intent);
               // finish();
            }
        });
        ingresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ingresoPatente.getText().length()>5){
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("patente",ingresoPatente.getText().toString());
                    editor.commit();
                    ControlVehiculoFragment.patente.setText(ingresoPatente.getText());
                    finish();
                }else{
                    Toast.makeText(ActivityScannerPatente.this,"Debe ingresar una Patente Valida",Toast.LENGTH_LONG).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void tomarFoto() {
        Intent foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (foto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(foto, TOMAR_FOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TOMAR_FOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            image = FirebaseVisionImage.fromBitmap(imageBitmap);
            patent="";
            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    salida="";
                                    String resultText = firebaseVisionText.getText();
                                    for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
                                        String blockText = block.getText();
                                        for (FirebaseVisionText.Line paragraph: block.getLines()) {
                                            String paragraphText = paragraph.getText();
                                            salida+="el Pagrafo es "+paragraphText+ " de largo "+paragraphText.length()+"\n";
                                            if(patent.length()==0){
                                                switch (paragraphText.length()){
                                                    case 8:
                                                        patente8(paragraph);
                                                        break;
                                                    case 7:
                                                        patente7(paragraph);
                                                        break;
                                                    case 6:
                                                        patente6(paragraph);
                                                        break;
                                                }
                                            }
                                            if(patent.length()>0){
                                                String[] blo = patent.split("-");
                                                blo[0].replace(" ","");
                                                salida+="Llego la Patente "+patent+" y el largo de las letras es "+blo[0].length()+"\n";
                                                if(blo.length!=2||blo[0].length()>4||patent.length()<6){
                                                    patent="";
                                                }else {
                                                    char ch = patent.charAt(0);
                                                    blo[1].replace(" ","");
                                                    char ch2 = blo[1].charAt(0);
                                                    if(valorNumero.indexOf(ch)!=-1||
                                                            valorNumero.indexOf(ch2)==-1||
                                                            blo[1].length()>4||blo[1].length()<2){
                                                        patent="";
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //todoTexto.setText(salida);
                                    if(patent.equals("")||patent.length()>9){
                                        patent="Sin Patente";
                                    }
                                    salida+="la patente es: "+patent+" y de largo "+patent.length()+"\n";
                                    ingresoPatente.setText(patent);
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });
        }
    }
    private void patente8(FirebaseVisionText.Line paragraph){
        for (FirebaseVisionText.Element word: paragraph.getElements()) {
            String wordText = word.getText();
            salida += "La palabra es "+wordText+ " de largo "+wordText.length()+"\n";
            if(wordText.length()==2||wordText.length()==5||
                    (wordText.length()==8&&patent.length()==0)){
                if(wordText.length()==5){
                    String[] blo = wordText.split("-");
                    if(blo.length==2){
                        patent+=wordText;
                    }
                }else if (patent.length()>0) {
                    char let = wordText.charAt(0);
                    if (valorNumero.indexOf(let)!=-1&&patent.indexOf('-')==-1){
                        patent+="-"+wordText;
                    }else{
                        patent+=wordText;
                    }
                }else {
                    patent+=wordText;
                }
                if(patent.length()==4){
                    patent+="-";
                }else if(patent.length()==8){
                    String[] blo = patent.split("-");
                    String[] ble = patent.split(" ");
                    if(blo.length<2){
                        patent="";
                    }else if (blo.length==3||ble.length==3) {
                        char let = blo[2].charAt(0);
                        if(valorNumero.indexOf(let)!=-1){
                            patent=blo[0]+"-"+blo[1]+blo[2];
                        }else{
                            patent=blo[0]+blo[1]+"-"+blo[2];
                        }
                    }
                }
            }
        }
    }


    private void patente7(FirebaseVisionText.Line paragraph){
        for (FirebaseVisionText.Element word: paragraph.getElements()) {
            String wordText = word.getText();
            salida += "La palabra es "+wordText+ " de largo "+wordText.length()+"\n";
            if(wordText.length()==2||wordText.length()==4||
                    (wordText.length()==3&&patent.length()<7)||
                    (wordText.length()==7&&patent.length()==0)){
                char let2='A', let = wordText.charAt(0);
                if(wordText.length()==4){
                    let2 = wordText.charAt(2);
                }
                if(valorNumero.indexOf(let)!=-1&&patent.length()>0){
                    patent+="-"+wordText;
                }else if(valorNumero.indexOf(let2)!=-1&&valorNumero.indexOf(let)==-1){
                    String paso = let+wordText.charAt(1)+"-"+let2+wordText.charAt(3);
                    patent+=paso;
                }
                else{
                    patent+=wordText;
                }
            }
        }
    }
    private void patente6(FirebaseVisionText.Line paragraph){
        for (FirebaseVisionText.Element word: paragraph.getElements()) {
            String wordText = word.getText();
            salida+= "La palabra es "+wordText+ " de largo "+wordText.length()+"\n";
            if(wordText.length()<5){
                char let = wordText.charAt(0);
                if(valorNumero.indexOf(let)!=-1&&patent.length()>0){
                    patent+="-"+wordText;
                }else{
                    if(wordText.length()<3||(wordText.length()==3&&patent.length()==0)){
                        patent+=wordText;
                    }
                }
            }else if(wordText.length()==6){
                String[] blo = wordText.split("-");
                if(blo.length==2){
                    patent+=wordText;
                }
            }
        }
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
        horarioAct = String.format("%02d",horaAct)+" : "+ String.format("%02d",minAct);
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
        new AlertDialog.Builder(ActivityScannerPatente.this)
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
