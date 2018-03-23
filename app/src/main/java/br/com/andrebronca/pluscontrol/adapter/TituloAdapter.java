package br.com.andrebronca.pluscontrol.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.andrebronca.pluscontrol.R;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

/**
 * Created by andrebronca on 28/10/16.
 */

public class TituloAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Titulo> listaTitulo;

    public TituloAdapter(Context context, ArrayList<Titulo> titulos) {
        this.listaTitulo = titulos;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listaTitulo.size();
    }

    @Override
    public Object getItem(int position) {
        return listaTitulo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Titulo titulo = listaTitulo.get(position);

        view = inflater.inflate(R.layout.listview_item_titulo, null);

        TextView descricao = (TextView) view.findViewById(R.id.titulo_adapter_descricao);
        TextView categoria = (TextView) view.findViewById(R.id.titulo_adapter_categoria);
        TextView pessoa = (TextView) view.findViewById(R.id.titulo_adapter_pessoa);
        TextView emissao = (TextView) view.findViewById(R.id.titulo_adapter_emissao);
        TextView vencimento = (TextView) view.findViewById(R.id.titulo_adapter_vencimento);
        TextView valor = (TextView) view.findViewById(R.id.titulo_adapter_valor);
        TextView qtdParc = (TextView) view.findViewById(R.id.titulo_adapter_qtdparcela);
//        TextView parcNr = (TextView) view.findViewById(R.id.titulo_adapter_parcela_nr);

        TextView tipo = (TextView) view.findViewById(R.id.titulo_adapter_tipo);
        ImageView img = (ImageView) view.findViewById(R.id.titulo_adapter_categoria_imagem);

        descricao.setText(titulo.getDsTitulo());
        descricao.setTypeface(Typeface.DEFAULT_BOLD);

        categoria.setText(titulo.getCategoria().getDsCategoria());
        pessoa.setText(titulo.getPessoa().getNmPessoa());

        emissao.setText("E: " + Util.dateToStringFormat(titulo.getDtEmissao()));
        vencimento.setText("V: " + Util.dateToStringFormat(titulo.getDtVencimento()));

        valor.setText(String.format("R$ %.2f", titulo.getVlSaldo()));
        qtdParc.setText("parc. "+ titulo.getIdParcela() +" de " + titulo.getQtParcela());
//        parcNr.setText("P.Nr.: "+ titulo.getIdParcela());
        tipo.setText(titulo.getTpNatureza());

        img.setImageResource(Util.getCirculoCor(titulo.getCategoria().getCdCor()));

        return view;
    }
}