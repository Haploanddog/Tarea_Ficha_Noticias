package dam.pmdm.tarea_final_ut03;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import dam.pmdm.tarea_final_ut03.listaNoticias.ListadoNoticiasActivity;
import dam.pmdm.tarea_final_ut03.menus.AboutUs;
import dam.pmdm.tarea_final_ut03.preferencias.PreferenciasActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imbPulsaBoton;
    private Button btnListado;

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asociamos las preferencias
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se infla el menú
        getMenuInflater().inflate(R.menu.menu, menu);
        // Se muestra el grupo de opciones
        menu.setGroupVisible(R.id.it_menu_ppal, true);
        menu.setGroupVisible(R.id.it_menu_listado, false);
        menu.setGroupVisible(R.id.it_menu_preferencias,true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.it_acerca_de:
                // Si se pulsa este botón, pasamos a la actividad AboutUs
                Intent intentAbout = new Intent(this, AboutUs.class);

                startActivity(intentAbout);
                break;
            case R.id.it_preferencias:
                // Si se pulsa este botón, pasamos a la actividad HelpMenu
                Intent intentHelp = new Intent(this, PreferenciasActivity.class);

                startActivity(intentHelp);
                break;
            case R.id.it_salir:
                // Si se pulsa este botón, salimos de la aplicación
               finishAffinity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        imbPulsaBoton.setImageResource(R.drawable.pulse_aqui_pulsado);
        Intent intent = new Intent(this, ListadoNoticiasActivity.class);
        startActivity(intent);
    }
}
