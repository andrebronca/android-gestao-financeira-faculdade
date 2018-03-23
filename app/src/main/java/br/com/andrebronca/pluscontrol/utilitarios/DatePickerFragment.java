package br.com.andrebronca.pluscontrol.utilitarios;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by andrebronca on 06/10/16.
 * Referência: https://developer.android.com/guide/topics/ui/controls/pickers.html
 * Acessado em: 6/10/2016
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private int ano, mes, dia;

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //DateEdit.setText(dayOfMonth +"/"+ (monthOfYear+1) +"/"+ year);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
        setAno(c.get(Calendar.YEAR));
//        int month = c.get(Calendar.MONTH);
        setMes(c.get(Calendar.MONTH));
//        int day = c.get(Calendar.DAY_OF_MONTH);
        setDia(c.get(Calendar.DAY_OF_MONTH));

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, getAno(), getMes(), getDia());
    }

    /**
     * @return dd/MM/yyyy
     */
    public String getDate() {
        return getDia() + "/" + getMes() + "/" + getAno();
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }
}
