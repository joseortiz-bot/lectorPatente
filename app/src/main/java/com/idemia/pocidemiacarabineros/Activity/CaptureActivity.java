package com.idemia.pocidemiacarabineros.Vista;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;


import com.idemia.pocidemiacarabineros.R;
import com.idemia.pocidemiacarabineros.services.AsynchronousTask;
import com.idemia.pocidemiacarabineros.services.OCRPlateService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureActivity extends Activity implements AsynchronousTask {

    private ImageView imageViewPreview;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private Button btnContinue;
    String currentPhotoPath;
    private LinearLayout linearPreview;
    private TextView txt_item;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        initialSetups();


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CaptureActivity.this, IngresarPatente.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initialSetups() {
        imageViewPreview = findViewById(R.id.imageViewPreview);
        linearPreview = findViewById(R.id.linearPreview);
        txt_item = findViewById(R.id.txt_item);
        btnContinue = findViewById(R.id.btnContinue);
        dispatchTakePictureIntent();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String lPath = currentPhotoPath;
            imageViewPreview.setImageURI(photoURI);
            System.out.println("JJPP Path" + lPath);
            OCRPlateService ocrService = new OCRPlateService();
            ocrService.processPlate(CaptureActivity.this, lPath, CaptureActivity.this);
        }
    }

    @Override
    public void onReceiveResults(final String msn) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearPreview.setVisibility(View.VISIBLE);
                txt_item.setText(msn);
                //Intent intent = new Intent(CaptureActivity.this, IngresarPatente.class);
                //startActivity(intent);
                IngresarPatente.ingresoPatente.setText(msn);
                finish();
            }
        });

    }

    @Override
    public void onFailure(String msn) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearPreview.setVisibility(View.VISIBLE);
                txt_item.setText("NO LICENCIA");
            }
        });

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IDM_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.out.println("EXC::" + ex.getMessage());
            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.idemia.pocidemiacarabineros.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }
}
