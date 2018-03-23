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
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class DespesaDetalhes extends AppCompatActivity {
    private Bundle params = new Bundle();
    private int id_titulo, id_parcela;
    private double saldoDoTitulo, valorTitulo;
    private Intent intent;
    private TextView tvTitulo, tvNatureza, tvEmissao, tvVencimento, tvValor, tvSaldo, tvBaixa;
    private TextView tvQtParcela, tvRelatorio, tvCadastro, tvAlterado, tvCategoria, tvCliente;
    private TituloDAO tituloDAO;
    private Titulo titulo;
    private PessoaDAO pessoaDAO;
    private CategoriaDAO categoriaDAO;
    private Pessoa pessoa;
    private Categoria categoria;
    private boolean tituloNaoEditado = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa_detalhes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linkToXml();

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                id_titulo = params.getInt("id");
                id_parcela = params.getInt("id_parcela");
                //saldoDoTitulo = params.getDouble("saldo");
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (valorIgualSaldo()) {
                if (tituloNaoEditado) {
                    intent = new Intent(DespesaDetalhes.this, DespesasEditar.class);
                    params.putInt("id", id_titulo);
                    params.putInt("id_parcela", id_parcela);
                    intent.putExtras(params);
                    startActivity(intent);
                } else {
                    Util.exibeMensagem(DespesaDetalhes.this, "Título já movimentado. \nNão é possível editar!");
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preencherDetalhes(getDadosTitulo());
    }


    private void linkToXml() {
        tvTitulo = (TextView) findViewById(R.id.tv_despesa_detalhe_titulo);
        tvNatureza = (TextView) findViewById(R.id.tv_despesa_detalhe_natureza);
        tvEmissao = (TextView) findViewById(R.id.tv_despesa_detalhe_emissao);
        tvVencimento = (TextView) findViewById(R.id.tv_despesa_detalhe_vencimento);
        tvValor = (TextView) findViewById(R.id.tv_despesa_detalhe_valor);
        tvSaldo = (TextView) findViewById(R.id.tv_despesa_detalhe_saldo);
        tvBaixa = (TextView) findViewById(R.id.tv_despesa_detalhe_baixa);
        tvQtParcela = (TextView) findViewById(R.id.tv_despesa_detalhe_qtparcela);
        tvRelatorio = (TextView) findViewById(R.id.tv_despesa_detalhe_relatorio);
        tvCadastro = (TextView) findViewById(R.id.tv_despesa_detalhe_cadastro);
        tvAlterado = (TextView) findViewById(R.id.tv_despesa_detalhe_alterado);
        tvCategoria = (TextView) findViewById(R.id.tv_despesa_detalhe_categoria);
        tvCliente = (TextView) findViewById(R.id.tv_despesa_detalhe_cliente);
    }

    private Titulo getDadosTitulo() {
        tituloDAO = TituloDAO.getInstance(this);
        categoriaDAO = CategoriaDAO.getInstance(this);
        pessoaDAO = PessoaDAO.getInstance(this, "DespesasDetalhes_getDadosTitulo()");
        titulo = tituloDAO.getTituloByID(id_titulo, id_parcela);
        verificaTituloJaEditado(titulo.getStMovimento());
        return titulo;
    }

    private void verificaTituloJaEditado(String movimento) {
        if (movimento.equals("S")) {
            tituloNaoEditado = false;
        }
    }

    private void preencherDetalhes(Titulo titulo) {
        tvTitulo.setText(titulo.getDsTitulo());
        tvNatureza.setText(titulo.getTpNaturezaLongo());
        tvEmissao.setText(Util.dateToStringFormat(titulo.getDtEmissao()));
        tvVencimento.setText(Util.dateToStringFormat(titulo.getDtVencimento()));
        tvValor.setText(String.format("R$%.2f", titulo.getVlTitulo()));
        tvSaldo.setText(String.format("R$%.2f", titulo.getVlSaldo()));
        tvBaixa.setText(Util.dateToStringFormat(titulo.getDtBaixa()));
        tvQtParcela.setText(String.valueOf(titulo.getQtParcela()));
        tvRelatorio.setText(titulo.getStRelatorioLongo());
        tvCadastro.setText(Util.dateToStringFormat(titulo.getDtCadastro()));
        tvAlterado.setText(Util.dateToStringFormat(titulo.getDtAlterado()));
        tvCategoria.setText(titulo.getCategoria().getDsCategoria());
        tvCliente.setText(titulo.getPessoa().getNmPessoa());
    }


}
