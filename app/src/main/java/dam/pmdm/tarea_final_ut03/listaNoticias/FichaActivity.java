package dam.pmdm.tarea_final_ut03.listaNoticias;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dam.pmdm.tarea_final_ut03.R;
import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosApp;
import dam.pmdm.tarea_final_ut03.entidades.Noticia;
import dam.pmdm.tarea_final_ut03.listaNoticias.ui.main.FichaFragment;



public class FichaActivity extends AppCompatActivity {

    private Button btnGuardar, btnVolver;
    int idNoticia;
    boolean esParaActualizar;
    private BaseDatosApp bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, FichaFragment.newInstance())
                    .commitNow();
        }
        bd = BaseDatosApp.getInstance(this);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVolver = findViewById(R.id.btnVolver);
        Bundle bundle = getIntent().getExtras();
        // Recuperamos el idNoticia, desde la posición de la noticia en el RecyclerView
        idNoticia = bundle.getInt("ID");
        esParaActualizar = bundle.getBoolean("esParaActualizar");
       if(esParaActualizar) {
           Executor executor = Executors.newFixedThreadPool(5);

           executor.execute(new EncontrarNoticia(idNoticia));

       } else {
           cargarFragmentoParaInsertar();
       }
       btnGuardar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FichaFragment fichaFragment = (FichaFragment) getSupportFragmentManager().findFragmentById(R.id.contenedor);
               Noticia noticia = fichaFragment.getNoticiaDesdeFicha();
               Executor executor = Executors.newFixedThreadPool(5);
               if(esParaActualizar){
                   executor.execute(new ActualizarNoticia(noticia));
               }
               else {
                   // Inserta el Producto en un hilo secundario
                   executor.execute(new InsertarNoticia(noticia));
               }
           }


       });
    }
    class InsertarNoticia implements Runnable {

        private Noticia noticia;

        public InsertarNoticia(Noticia noticia) {
            this.noticia = noticia;
        }

        @Override
        public void run() {
            // Se usa el método insertAll de la interfaz productoDAO, implementada en BaseDatosApp
            bd.noticiaDAO().insertAll(noticia);
        }
    }
    class ActualizarNoticia implements Runnable {

        private Noticia noticia;

        public ActualizarNoticia(Noticia noticia) {
            this.noticia = noticia;
        }

        @Override
        public void run() {
            // Se usa el método insertAll de la interfaz productoDAO, implementada en BaseDatosApp
            bd.noticiaDAO().update(noticia);
        }
    }
    class EncontrarNoticia implements Runnable {


        private int id;

        public EncontrarNoticia(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            // Se usa el método insertAll de la interfaz productoDAO, implementada en BaseDatosApp
            Noticia noticia = bd.noticiaDAO().findByNoticia(id);
            cargarFragmentoParaActualizar(noticia);
        }
    }
    private void cargarFragmentoParaInsertar(){
        FichaFragment fragment = new FichaFragment();
        boolean esParaActualizar = false;
        Bundle args = new Bundle();
        args.putBoolean("esParaActualizar", esParaActualizar);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contenedor, fragment)
                .commit();
    }

    private void cargarFragmentoParaActualizar(Noticia noticia) {
        FichaFragment fragment = new FichaFragment();
        boolean esParaActualizar = true;

        // Enviamos los datos a la vista del fragmento
        Bundle args = new Bundle();
        args.putBoolean("esParaActualizar", esParaActualizar);
        args.putParcelable("noticia", noticia);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contenedor, fragment)
                .commit();
    }
}