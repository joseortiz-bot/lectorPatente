package com.idemia.pocidemiacarabineros.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.idemia.pocidemiacarabineros.Controlador.CalendarioPicker;
import com.idemia.pocidemiacarabineros.Fragments.ControlIdentidadFragment;
import com.idemia.pocidemiacarabineros.Fragments.ControlVehiculoFragment;
import com.idemia.pocidemiacarabineros.Fragments.HistorialFragment;
import com.idemia.pocidemiacarabineros.R;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ActivityControlIdentidad extends AppCompatActivity implements View.OnClickListener {

    public EditText etNom, etProcedimiento, etObcervacion;
    public Button btnIngresar, btnCancelar;
    public TextView txtRut, txtfechaNacimiento, btnId, btnCar, btnHis, btnExit;
    public ImageView NFC;
    public ImageView calendar;
    public ImageView   imagePatente;

    Date tiempo;
    int diaAct, mesAct, anoAct, horaAct, minAct, opcion = 3;
    String fechaAct, horarioAct;

    MaterialViewPager mViewPager;
    private RecyclerView mRecyclerView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_identidad);
        setTitle("");
        closeKeyboard();
        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);

        toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        btnId = (TextView) findViewById(R.id.btn_identity);
        btnCar = (TextView) findViewById(R.id.btn_car);
        btnHis = (TextView) findViewById(R.id.btn_his);
        btnExit = (TextView) findViewById(R.id.btn_exit);

        btnId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ActivityControlIdentidad.this, "ControlIdentidad", Toast.LENGTH_LONG).show();
                mViewPager.getViewPager().setCurrentItem(0, true);

            }
        });

        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ActivityControlIdentidad.this, "ControlIdentidad", Toast.LENGTH_LONG).show();
                mViewPager.getViewPager().setCurrentItem(1, true);

            }
        });

        btnHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.getViewPager().setCurrentItem(2,true);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmaSalida();
            }
        });


        final Toolbar toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }


        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                switch (position % 3) {
                    case 0:
                        return ControlIdentidadFragment.newInstance();
                    case 1:
                        return ControlVehiculoFragment.newInstance();
                    case 2:
                        return HistorialFragment.newInstance();
                    default:
                        return ControlIdentidadFragment.newInstance();
                }
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 4) {
                    case 0:
                        return "Selection";
                    case 1:
                        return "Actualités";
                    case 2:
                        return "Professionnel";
                    case 3:
                        return "Divertissement";
                }
                return "";
            }
        });
        final Drawable myIcon = getResources().getDrawable( R.drawable.ic_auto);
        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.colorPrimary,
                                "");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.colorPrimary,
                                "");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.colorPrimary,
                                "");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.colorPrimary,
                                "");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());

        final View logo = findViewById(R.id.logo_white);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                    //Toast.makeText(getApplicationContext(), "Yes, the title is clickable", Toast.LENGTH_SHORT).show();
                }
            });
        }

        rescatarOpcion();
        /*txtRut = (TextView) findViewById(R.id.et_rut);
        etNom = (EditText) findViewById(R.id.et_nom);
        txtfechaNacimiento = (TextView) findViewById(R.id.et_fecha_nacimiento);
        etProcedimiento = (EditText) findViewById(R.id.et_procedimiento);
        etObcervacion = (EditText) findViewById(R.id.et_obcervacion);
        NFC = (ImageView) findViewById(R.id.imageNfc);
        calendar = (ImageView) findViewById(R.id.imageCalendarioId);

        btnIngresar = (Button) findViewById(R.id.btn_ingresar);
        btnCancelar = (Button) findViewById(R.id.btn_cancelar);

        btnIngresar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        calendar.setOnClickListener(this);
        txtfechaNacimiento.setOnClickListener(this);

        obtenerTiempo();

        NFC.setOnClickListener(this);*/

    }

    public void confirmaSalida() {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro que desea cerrar la app?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        confirmaSalida();
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            confirmaSalida();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(getBaseContext(), "back button pressed", Toast.LENGTH_SHORT).show();
        Log.e("Test","Back Button Clicked");
        this.finish();
    }

    private void rescatarOpcion() {
        Bundle bundle = ActivityControlIdentidad.this.getIntent().getExtras();
        try {
            opcion = bundle.getInt("opcion");
            Log.d("opcionChange: ", String.valueOf(opcion));

        }catch (Exception ex){

        }
        if(opcion == 0){
            mViewPager.getViewPager().setCurrentItem(0, true);
        }else if (opcion == 1){
            mViewPager.getViewPager().setCurrentItem(1, true);
        }

    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) ActivityControlIdentidad.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void ObtenerFecha() {
        CalendarioPicker newFragment = CalendarioPicker.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = String.format("%02d",day) + " / " + String.format("%02d",(month+1)) + " / " + year;
                txtfechaNacimiento.setText(selectedDate);
                //SharedPreferences.Editor editor = sharedPref.edit();
                //editor.putString("fecha", selectedDate);
                //editor.commit();
            }
        });

        newFragment.show(this.getSupportFragmentManager(), "datePicker");
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
        txtfechaNacimiento.setText(fechaAct+" "+horarioAct);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ingresar:
                Toast.makeText(ActivityControlIdentidad.this, "Se ingresa el control exitosamente", Toast.LENGTH_SHORT).show();

                // hint();
                break;
            case R.id.btn_cancelar:
                finish();
                //hint();
                break;
            case R.id.imageNfc:
                Intent intentEscaner = new Intent(ActivityControlIdentidad.this, ActivityEscanearChip.class);
                this.startActivity(intentEscaner);
                //hint();
                break;
            case R.id.imageCalendarioId:

                ObtenerFecha();
                //hint();
                break;
            case R.id.et_fecha_nacimiento:

                ObtenerFecha();
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            confirmaSalida();
        }
        return super.onOptionsItemSelected(item);
    }
}