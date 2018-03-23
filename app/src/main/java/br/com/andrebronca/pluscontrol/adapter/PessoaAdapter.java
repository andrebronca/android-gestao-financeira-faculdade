package br.com.andrebronca.pluscontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.andrebronca.pluscontrol.R;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;

/**
 * Created by andrebronca on 28/10/16.
 */

public class PessoaAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Pessoa> listaPessoa;

    public PessoaAdapter(Context context, ArrayList<Pessoa> pessoas) {
        this.listaPessoa = pessoas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listaPessoa.size();
    }

    @Override
    public Object getItem(int position) {
        return listaPessoa.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Pessoa pessoa = listaPessoa.get(position);

        view = inflater.inflate(R.layout.listview_item_pessoa, null);

        TextView nome = (TextView) view.findViewById(R.id.pessoa_adapter_nome);
        TextView email = (TextView) view.findViewById(R.id.pessoa_adapter_email);
        TextView tipo = (TextView) view.findViewById(R.id.pessoa_adapter_tipo);
        nome.setText(pessoa.getNmPessoa());
        email.setText(pessoa.getDsEmail());
        tipo.setText(pessoa.getTpPessoa()); //propositadamente somente o flag

        return view;
    }
}