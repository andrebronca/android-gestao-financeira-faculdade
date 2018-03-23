package br.com.andrebronca.pluscontrol.Constantes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import br.com.andrebronca.pluscontrol.R;

/**
 * Created by andrebronca on 19/08/16.
 * Este código está pronto e postado no link: http://pt.stackoverflow.com/questions/68590/spinner-para-selecionar-cor
 */
public class Cores extends ArrayAdapter<String> {
    private final LayoutInflater inflater;

    public Cores(Context context, int resource, String[] cores) {
        super(context, resource, cores);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_cores_row, parent, false);  //layout/spinner_cores_row
        ImageView image = (ImageView) row.findViewById(R.id.imageViewCores); //constantes/Cores

        //Obtém a cor referente a esta posição
        int color = getColor(position);
        // Obtém a referência ao circlo
        Drawable circle = image.getDrawable();
        //Atribui a cor
        circle.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        return row;
    }

    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    public int getColor(int position) {
        return Color.parseColor(getItem(position));
    }
}
