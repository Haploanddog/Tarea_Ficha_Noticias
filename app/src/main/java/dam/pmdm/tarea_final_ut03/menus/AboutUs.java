package dam.pmdm.tarea_final_ut03.menus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import dam.pmdm.tarea_final_ut03.R;

public class AboutUs extends AppCompatActivity {
    Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // binding
        btnVolver = findViewById(R.id.btn_volver_menu);

        // escuchador
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Nos lleva a la Actividad desde la que se abrió esta opción de menú
                finish();
            }
        });
    }
}