package dam.pmdm.tarea_final_ut03;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import dam.pmdm.tarea_final_ut03.listaNoticias.ListadoNoticiasActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imbPulsaBoton;
    private Button btnListado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imbPulsaBoton = findViewById(R.id.imageView5);

        btnListado = findViewById(R.id.button);
        imbPulsaBoton.setOnClickListener(this);

        imbPulsaBoton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                imbPulsaBoton.setOnClickListener(MainActivity.this);

                return true;
            }
        });

        btnListado.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        imbPulsaBoton.setImageResource(R.drawable.pulse_aqui_pulsado);
        Intent intent = new Intent(this, ListadoNoticiasActivity.class);
        startActivity(intent);
    }
}
