package br.com.andrebronca.pluscontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.andrebronca.pluscontrol.R;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

/**
 * Created by andrebronca on 28/10/16.
 */

public class MovimentoAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<MovimentoConta> listaMovimento;
    Context context;

    public MovimentoAdapter(Context context, ArrayList<MovimentoConta> movimentoContas) {
        this.context = context;
        this.listaMovimento = movimentoContas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listaMovimento.size();
    }

    @Override
    public Object getItem(int position) {
        return listaMovimento.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MovimentoConta mov = listaMovimento.get(position);

        view = inflater.inflate(R.layout.listview_item_movimento, null);

        TextView historico = (TextView) view.findViewById(R.id.movimento_adapter_historico);
        TextView data = (TextView) view.findViewById(R.id.movimento_adapter_data);
        TextView valor = (TextView) view.findViewById(R.id.movimento_adapter_valor);

        historico.setText(mov.getDsHistorico());
        data.setText(Util.dateToStringFormat(mov.getDtMovimento()));
        valor.setText(String.format("R$ %.2f", mov.getVlMovimento()));

        return view;
    }
}