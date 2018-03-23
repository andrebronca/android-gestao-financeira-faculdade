package br.com.andrebronca.pluscontrol.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.andrebronca.pluscontrol.R;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

/**
 * Created by andrebronca on 28/10/16.
 */

public class CategoriaAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Categoria> listaCategoria;

    public CategoriaAdapter(Context context, ArrayList<Categoria> categorias) {
        this.listaCategoria = categorias;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listaCategoria.size();
    }

    @Override
    public Object getItem(int position) {
        return listaCategoria.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Categoria categoria = listaCategoria.get(position);

        view = inflater.inflate(R.layout.listview_item_categoria, null);

        ((TextView) view.findViewById(R.id.categoria_adapter_descricao)).
                setText(categoria.getDsCategoria());
        ((TextView) view.findViewById(R.id.categoria_adapter_tipo)).
                setText(categoria.getTpCategoriaLong());

        TextView status = (TextView) view.findViewById(R.id.categoria_adapter_ativo);
        String stAtivo = categoria.getStAtivo();
        if (stAtivo.equals("S")) {
            status.setTextColor(Color.BLUE);
        } else {
            status.setTextColor(Color.RED);
        }
        status.setText("Ativo: " + categoria.getStAtivoLong());

//        ((TextView)view.findViewById(R.id.categoria_adapter_ativo)).
//                setText(categoria.getTpCategoriaLong());

        ((ImageView) view.findViewById(R.id.categoria_adapter_cor)).
                setImageResource(Util.getCirculoCor(categoria.getCdCor()));

        return view;
    }
}