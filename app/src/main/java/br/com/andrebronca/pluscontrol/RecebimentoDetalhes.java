package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class RecebimentoDetalhes extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private int idTitulo, idParcela;
    private TextView tvCategoria, tvCliente, tvDescricao, tvNatureza, tvEmissao, tvVencimento;
    private TextView tvVlTitulo, tvVlSaldo, tvDtBaixa, tvQtdParcela, tvParcela, tvRelatorio, tvDtCad, tvDtAlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recebimento_detalhes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                idTitulo = params.getInt("id");
                idParcela = params.getInt("id_parcela");
            }
        }

        linkToXml();
        preencherDetalhes(getTitulo());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(RecebimentoDetalhes.this, RecebimentoEditar.class);
                params.putInt("id", idTitulo);
                params.putInt("id_parcela", idParcela);
                intent.putExtras(params);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void linkToXml() {
        tvCategoria = (TextView) findViewById(R.id.tv_recebimento_detalhe_categoria);
        tvCliente = (TextView) findViewById(R.id.tv_recebimento_detalhe_cliente);
        tvDescricao = (TextView) findViewById(R.id.tv_recebimento_detalhe_titulo);
        tvNatureza = (TextView) findViewById(R.id.tv_recebimento_detalhe_natureza);
        tvEmissao = (TextView) findViewById(R.id.tv_recebimento_detalhe_emissao);
        tvVencimento = (TextView) findViewById(R.id.tv_recebimento_detalhe_vencimento);
        tvVlTitulo = (TextView) findViewById(R.id.tv_recebimento_detalhe_valor);
        tvVlSaldo = (TextView) findViewById(R.id.tv_recebimento_detalhe_saldo);
        tvDtBaixa = (TextView) findViewById(R.id.tv_recebimento_detalhe_baixa);
        tvQtdParcela = (TextView) findViewById(R.id.tv_recebimento_detalhe_qtparcela);
        tvParcela = (TextView) findViewById(R.id.tv_recebimento_detalhe_parcelanr);  //id_parcela
        tvRelatorio = (TextView) findViewById(R.id.tv_recebimento_detalhe_relatorio);
        tvDtCad = (TextView) findViewById(R.id.tv_recebimento_detalhe_cadastro);
        tvDtAlt = (TextView) findViewById(R.id.tv_recebimento_detalhe_alterado);
    }

    private void preencherDetalhes(Titulo titulo) {
        tvCategoria.setText(titulo.getCategoria().getDsCategoria());
        tvCliente.setText(titulo.getPessoa().getNmPessoa());
        tvDescricao.setText(titulo.getDsTitulo());
        tvNatureza.setText(titulo.getTpNaturezaLongo());
        tvEmissao.setText(Util.dateToStringFormat(titulo.getDtEmissao()));
        tvVencimento.setText(Util.dateToStringFormat(titulo.getDtVencimento()));
        tvVlTitulo.setText(String.format("R$%.2f", titulo.getVlTitulo()));
        tvVlSaldo.setText(String.format("R$%.2f", titulo.getVlSaldo()));
        tvDtBaixa.setText(Util.dateToStringFormat(titulo.getDtBaixa()));
        tvQtdParcela.setText(String.valueOf(titulo.getQtParcela()));
        tvParcela.setText(String.valueOf(titulo.getIdParcela()));
        tvRelatorio.setText(titulo.getStRelatorioLongo());
        tvDtCad.setText(Util.dateToStringFormat(titulo.getDtCadastro()));
        tvDtAlt.setText(Util.dateToStringFormat(titulo.getDtAlterado()));
    }

    private Titulo getTitulo() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        PessoaDAO pessoaDAO = PessoaDAO.getInstance(this, "RecebimentoDetalhes_getTitulo()");
        CategoriaDAO categoriaDAO = CategoriaDAO.getInstance(this);

        Titulo titulo = tituloDAO.getTituloByID(idTitulo, idParcela);
        return titulo;
    }

}