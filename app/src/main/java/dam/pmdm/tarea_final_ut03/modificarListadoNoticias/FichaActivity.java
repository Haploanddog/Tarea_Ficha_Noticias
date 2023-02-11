package dam.pmdm.tarea_final_ut03.modificarListadoNoticias;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dam.pmdm.tarea_final_ut03.R;
import dam.pmdm.tarea_final_ut03.adaptadores.AdaptadorNoticias;
import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosApp;
import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosExterna;
import dam.pmdm.tarea_final_ut03.entidades.Noticia;
import dam.pmdm.tarea_final_ut03.listaNoticias.ListadoNoticiasActivity;
import dam.pmdm.tarea_final_ut03.menus.AboutUs;


public class FichaActivity extends AppCompatActivity {

    private Button btnGuardar, btnVolver;
    int idNoticia;
    boolean esParaActualizar;
    private BaseDatosApp bd;

    private AdaptadorNoticias adaptadorNoticias;
    private boolean esBdExterna = ListadoNoticiasActivity.esBdExterna;

    private BaseDatosExterna bdExterna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, FichaFragment.newInstance())
                    .commitNow();
        }
        //Comprobamos si se utiliza la BD externa o interna y se instancia lo que corresponda
       if(esBdExterna){
        try {
            bdExterna = new BaseDatosExterna(ListadoNoticiasActivity.URL, ListadoNoticiasActivity.USER, ListadoNoticiasActivity.PASS);
        } catch (Exception ex) {
            Toast.makeText(this, "Error al conectar con bd", Toast.LENGTH_SHORT).show();
        }
       }
       else {
        bd = BaseDatosApp.getInstance(this);
       }

        btnGuardar = findViewById(R.id.btnGuardar);
        btnVolver = findViewById(R.id.btnVolver);
        Bundle bundle = getIntent().getExtras();
        // Recuperamos el idNoticia, desde la posición de la noticia en el RecyclerView
        idNoticia = bundle.getInt("ID");
        esParaActualizar = bundle.getBoolean("esParaActualizar");
        if (esParaActualizar) {

            Executor executor = Executors.newFixedThreadPool(5);

            executor.execute(new EncontrarNoticia(idNoticia));

        } else {
            cargarFragmentoParaInsertar();
        }
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Noticia noticia = new Noticia();
                FichaFragment fichaFragment = (FichaFragment) getSupportFragmentManager().findFragmentById(R.id.contenedor);
                boolean esNoticiaCorrecta = fichaFragment.sonDatosCorrectos();
                if (esNoticiaCorrecta) {
                    noticia = fichaFragment.getNoticiaDesdeFicha();

                    Executor executor = Executors.newFixedThreadPool(10);
                    if (esParaActualizar) { // Si se ha pulsado en una fila para modificar sus datos
                        if(!esBdExterna){
                            // Actualiza la Noticia en un hilo secundario
                            executor.execute(new ActualizarNoticia(noticia));
                        } else {
                            Noticia noticia1 = noticia;
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        bdExterna.actualizarNoticia(noticia1); //Se actualiza en la bdExterna
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }


                        Toast.makeText(FichaActivity.this, "La noticia con id " + (idNoticia + 1) + " ha sido actualizada", Toast.LENGTH_LONG).show();


                    } else { // Si se ha pulsado el botón del menú para añadir una Noticia al listado
                        if (!esBdExterna) { // Se añade a la bd interna
                            // Inserta la Noticia en un hilo secundario
                            executor.execute(new InsertarNoticia(noticia));
                        } else { // Se añade a la bd externa
                            Noticia noticia1 = noticia;
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        bdExterna.agregarNoticia(noticia1);
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }
                        Toast.makeText(FichaActivity.this, "La noticia se ha insertado correctamente", Toast.LENGTH_LONG).show();

                    }
                    Intent data = new Intent(FichaActivity.this, ListadoNoticiasActivity.class);
                    startActivity(data);
                }
            }


        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se infla el menú
        getMenuInflater().inflate(R.menu.menu, menu);
        // Se muestra el grupo de opciones
        menu.setGroupVisible(R.id.it_menu_ppal, true);
        menu.setGroupVisible(R.id.it_menu_listado, false);
        menu.setGroupVisible(R.id.it_menu_preferencias, false);

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

            case R.id.it_salir:
                // Si se pulsa este botón, salimos de la aplicación
                finishAffinity();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    class InsertarNoticia implements Runnable {

        private Noticia noticia;

        public InsertarNoticia(Noticia noticia) {
            this.noticia = noticia;
        }

        @Override
        public void run() {
            if (esBdExterna) {
                try {
                    bdExterna.agregarNoticia(noticia);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Se usa el método insertAll de la interfaz productoDAO, implementada en BaseDatosApp
                bd.noticiaDAO().insertAll(noticia);
            }
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
            this.id = id + 1;
        }

        @Override
        public void run() {
            Noticia noticia;
            if (!esBdExterna) {
                // Se usa el método insertAll de la interfaz productoDAO, implementada en BaseDatosApp
                noticia = bd.noticiaDAO().findByNoticia(id);
            } else {
                try {
                    noticia = bdExterna.obtenerNoticiaPorId(id);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            cargarFragmentoParaActualizar(noticia);
        }
    }

    private void cargarFragmentoParaInsertar() {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FichaFragment fichaFragment = (FichaFragment) getSupportFragmentManager().findFragmentById(R.id.contenedor);
        fichaFragment.guardarEstado(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FichaFragment fichaFragment = (FichaFragment) getSupportFragmentManager().findFragmentById(R.id.contenedor);
        fichaFragment.recuperarEstado(savedInstanceState);
    }

}