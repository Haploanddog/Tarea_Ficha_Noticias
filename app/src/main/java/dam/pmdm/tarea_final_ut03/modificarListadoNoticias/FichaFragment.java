package dam.pmdm.tarea_final_ut03.modificarListadoNoticias;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dam.pmdm.tarea_final_ut03.R;
import dam.pmdm.tarea_final_ut03.entidades.Noticia;
import dam.pmdm.tarea_final_ut03.listaNoticias.ListadoViewModel;
import dam.pmdm.tarea_final_ut03.listaNoticias.conversorfechas.DatePickerFragment;

public class FichaFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private EditText etTitular, etEnlace, etFecha;
    private CheckBox cbLeido, cbFavorito;
    private RadioButton rbFiable, rbNoFiable;
    private ListadoViewModel mViewModel;
    private boolean esParaActualizar;
    private Noticia noticia;

    public static FichaFragment newInstance() {
        return new FichaFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            esParaActualizar = args.getBoolean("esParaActualizar");
            if (esParaActualizar) {
                noticia = args.getParcelable("noticia", Noticia.class);

            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ficha, container, false);

        etTitular = view.findViewById(R.id.et_nombre);
        etEnlace = view.findViewById(R.id.et_enlace);
        cbLeido = view.findViewById(R.id.cb_leida);
        cbFavorito = view.findViewById(R.id.cb_favorita);
        rbFiable = view.findViewById(R.id.rb_fiable);
        rbNoFiable = view.findViewById(R.id.rb_nofiable);
        etFecha = view.findViewById(R.id.et_fecha);


        mViewModel = new ViewModelProvider(this).get(ListadoViewModel.class);
        if (esParaActualizar) {
            rellenarFicha(noticia);

        } else {
            rbFiable.setChecked(true);
        }
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        return view;
        // TODO: Use the ViewModel


    }


    public void guardarEstado(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("titular", etTitular.getText().toString());
        outState.putString("fecha", etFecha.getText().toString());
        outState.putString("enlace", etEnlace.getText().toString());
        outState.putBoolean("leido", cbLeido.isChecked());
        outState.putBoolean("favorito", cbFavorito.isChecked());
        outState.putBoolean("fiable", rbFiable.isChecked());
        outState.putBoolean("no_fiable", rbNoFiable.isChecked());
    }


    public void recuperarEstado(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            etTitular.setText(savedInstanceState.getString("titular"));
            etFecha.setText(savedInstanceState.getString("fecha"));
            etEnlace.setText(savedInstanceState.getString("enlace"));
            cbLeido.setChecked(savedInstanceState.getBoolean("leido"));
            cbFavorito.setChecked(savedInstanceState.getBoolean("favorito"));
            rbFiable.setChecked(savedInstanceState.getBoolean("fiable"));
            rbNoFiable.setChecked(savedInstanceState.getBoolean("no_fiable"));
        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getChildFragmentManager(), "date picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        etFecha.setText(currentDateString);
    }

    public Noticia getNoticiaDesdeFicha() {
        Noticia noticiaN = new Noticia();

        noticiaN.setTitular(etTitular.getText().toString());
        noticiaN.setEnlace(etEnlace.getText().toString());
        noticiaN.setFechaFromString(etFecha.getText().toString());
        noticiaN.setLeida(cbLeido.isChecked());
        noticiaN.setFavorita(cbFavorito.isChecked());

        if (esParaActualizar) {
            noticiaN.setUid(noticia.getUid());
        }
        boolean esFiable = false;
        if (rbFiable.isChecked()) {
            esFiable = true;
        } else if (rbNoFiable.isChecked()) {
            esFiable = false;
        }
        noticiaN.setFiable(esFiable);

        return noticiaN;
    }

    public boolean sonDatosCorrectos() {
        boolean sonCorrectos = false;

        if (etTitular.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "La noticia debe tener un titular", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            sonCorrectos = true;
        }

        if (etEnlace.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "La noticia debe tener un enlace", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            sonCorrectos = true;
        }

        if (etFecha.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "La fecha no puede estar vacía", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            sonCorrectos = true;
        }
        String fecha = etFecha.getText().toString();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.parse(fecha);

        } catch (ParseException e) {
            Toast.makeText(getContext(), "La fecha no es válida", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (rbFiable.isChecked() || rbNoFiable.isChecked()) {
            sonCorrectos = true;
        } else {
            Toast.makeText(getContext(), "Debe seleccionar si la noticia es fiable o no", Toast.LENGTH_SHORT).show();
            return false;
        }
        return sonCorrectos;
    }

    private void rellenarFicha(Noticia noticia) {
        etTitular.setText(noticia.getTitular());
        etEnlace.setText(noticia.getEnlace());
        etFecha.setText(transformarFechaLongToString(noticia.getFecha())); //TODO: No lo muestra bien
        if (noticia.isFavorita()) {
            cbFavorito.setChecked(true);
        }
        if (noticia.isLeida()) {
            cbLeido.setChecked(true);
        }
        if (noticia.isFiable()) {
            rbFiable.setChecked(true);
        } else {
            rbNoFiable.setChecked(true);
        }
    }

    private String transformarFechaLongToString(long fechaLong) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fecha = new Date(fechaLong);
        return sdf.format(fecha);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("titular", etTitular.getText().toString());
        outState.putString("fecha", etFecha.getText().toString());
        outState.putString("enlace", etEnlace.getText().toString());
        outState.putBoolean("leido", cbLeido.isChecked());
        outState.putBoolean("favorito", cbFavorito.isChecked());
        outState.putBoolean("fiable", rbFiable.isChecked());
        outState.putBoolean("no_fiable", rbNoFiable.isChecked());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            etTitular.setText(savedInstanceState.getString("titular"));
            etFecha.setText(savedInstanceState.getString("fecha"));
            etEnlace.setText(savedInstanceState.getString("enlace"));
            cbLeido.setChecked(savedInstanceState.getBoolean("leido"));
            cbFavorito.setChecked(savedInstanceState.getBoolean("favorito"));
            rbFiable.setChecked(savedInstanceState.getBoolean("fiable"));
            rbNoFiable.setChecked(savedInstanceState.getBoolean("no_fiable"));
        }
    }
}