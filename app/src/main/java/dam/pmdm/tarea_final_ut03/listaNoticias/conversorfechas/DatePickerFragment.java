package dam.pmdm.tarea_final_ut03.listaNoticias.conversorfechas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import dam.pmdm.tarea_final_ut03.R;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Usa la fecha actual como fecha predeterminada en el picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Crea un nuevo objeto DatePickerDialog
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Almacenar la fecha seleccionada
        EditText etFecha = getActivity().findViewById(R.id.et_fecha);
        etFecha.setText(day + "/" + (month + 1) + "/" + year);
    }
}

