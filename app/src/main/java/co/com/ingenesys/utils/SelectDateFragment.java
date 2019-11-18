package co.com.ingenesys.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.CalendarView;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.com.ingenesys.fragment.ReporteDiarioFragment;

public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private static String fecha;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Obtener fecha actual del sistema
        final Calendar c= Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH);

        //retorna una instancia del dialogo selector de fecha
        return new DatePickerDialog(getContext(), this,year,month,day);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
        Date date = null;
        Date fecha_final = null;

        final int mesActual = month + 1;
        String diaFormat = (dayOfMonth < 10) ? 0 + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
        String mesFormat = (mesActual< 10) ? 0 + String.valueOf(mesActual) : String.valueOf(mesActual);
        try{
            date = dateFormat.parse(year + "-" + mesFormat + "-" + diaFormat);
            fecha_final = dateFormat.parse(year + "-" + mesFormat + "-" + (diaFormat+1));
        }catch (ParseException e){
            e.printStackTrace();
        }

        String outDate = dateFormat.format(date);
        fecha = outDate;
    }

    public static String date(){
        return fecha;
    }
}
