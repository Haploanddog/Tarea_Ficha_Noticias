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
        EditText etFecha = getActivity().findViewById(R.id.et_fecha);
        String dia = day < 10 ? "0" + day : "" + day;
        String mes = (month + 1) < 10 ? "0" + (month + 1) : "" + (month + 1);
        String fecha = dia + "/" + mes + "/" + year;
        etFecha.setText(fecha);
    }
}

