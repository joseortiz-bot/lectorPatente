package com.idemia.pocidemiacarabineros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.idemia.pocidemiacarabineros.Activity.ActivityControlIdentidad;

public class IngresoSistemaActivity extends AppCompatActivity implements View.OnClickListener {

    public EditText etUsuario, etPass;
    public Button btnIngresar, btnLimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_sistema);

        btnIngresar = (Button) findViewById(R.id.btn_ingresar);
        btnLimpiar = (Button) findViewById(R.id.btn_limpiar);

        etUsuario = (EditText) findViewById(R.id.et_usuario);
        etPass = (EditText) findViewById(R.id.et_pass);

        btnIngresar.setOnClickListener(this);
        btnLimpiar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ingresar:
                hint();
                Toast.makeText(this, "Ingresando...", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(IngresoSistemaActivity.this, ActivityControlIdentidad.class);
                IngresoSistemaActivity.this.startActivity(myIntent);
                break;
            case R.id.btn_limpiar:
                hint();
            case R.id.et_usuario:
                //Toast.makeText(this, "", Toast.LENGTH_LONG).show();
            case R.id.et_pass:
                //Toast.makeText(this, "Ingresando...", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void hint() {
        etUsuario.setText("");
        etPass.setText("");
    }

}