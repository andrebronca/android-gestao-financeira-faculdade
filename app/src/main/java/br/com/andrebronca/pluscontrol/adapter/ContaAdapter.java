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
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

/**
 * Created by andrebronca on 28/10/16.
 */

public class ContaAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Conta> listaConta;

    public ContaAdapter(Context context, ArrayList<Conta> contas) {
        this.listaConta = contas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listaConta.size();
    }

    @Override
    public Object getItem(int position) {
        return listaConta.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Conta conta = listaConta.get(position);

        view = inflater.inflate(R.layout.listview_item_conta, null);

        TextView descricao = (TextView) view.findViewById(R.id.conta_adapter_descricao);
        TextView saldo = (TextView) view.findViewById(R.id.conta_adapter_saldo);
        ImageView cor = (ImageView) view.findViewById(R.id.conta_adapter_cor);

        descricao.setText(conta.getDsConta());
        double vlSaldo = conta.getVlSaldo();
        if (vlSaldo < 0.0) {
            saldo.setTextColor(Color.RED);
        } else {
            saldo.setTextColor(Color.BLUE);
        }
        saldo.setText(String.format("R$ %.2f", vlSaldo));
        cor.setImageResource(Util.getCirculoCor(conta.getCdCor()));
//        ((TextView)view.findViewById(R.id.categoria_adapter_descricao)).
//                setText(conta.getDsConta());
//        ((TextView)view.findViewById(R.id.categoria_adapter_tipo)).
//                setText(String.format("R$ %.2f",conta.getVlSaldo()));
//        ((ImageView)view.findViewById(R.id.categoria_adapter_cor)).
//                setImageResource(Util.getCirculoCor(conta.getCdCor()));

        return view;
    }
}