package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.com.andrebronca.pluscontrol.Constantes.Cores;
import br.com.andrebronca.pluscontrol.dao.ContaDAO;
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class ContaDetalhes extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private TextView tvDescricao, tvSaldo, tvCor, tvDatCad, tvDatAlt;
    private int idConta;
    private Conta conta;
    private ContaDAO contaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta_detalhes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkToXml();

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                idConta = params.getInt("id");
            }
        }

        preencherDetalhes(getDadosConta());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ContaDetalhes.this, ContaEditar.class);
                params.putInt("id", idConta);
                intent.putExtras(params);
                startActivity(intent);
            }
        });
    }

    private void linkToXml() {
        tvDescricao = (TextView) findViewById(R.id.tv_conta_detalhe_descricao);
        tvSaldo = (TextView) findViewById(R.id.tv_conta_detalhe_saldo);
        tvCor = (TextView) findViewById(R.id.tv_conta_detalhe_cor);
        tvDatCad = (TextView) findViewById(R.id.tv_conta_detalhe_dtcad);
        tvDatAlt = (TextView) findViewById(R.id.tv_conta_detalhe_dtalt);
    }

    private void preencherDetalhes(Conta conta) {
        tvDescricao.setText(conta.getDsConta());
        tvSaldo.setText(String.format("R$%.2f", conta.getVlSaldo()));
        Util.setCorCategoria(conta.getCdCor(), tvCor);
        tvDatCad.setText(Util.dateToStringFormat(conta.getDtCadastro()));
        tvDatAlt.setText(Util.dateToStringFormat(conta.getDtAlterado()));
    }

    private Conta getDadosConta() {
        contaDAO = ContaDAO.getInstance(ContaDetalhes.this);
        conta = contaDAO.getContaById(idConta);
        return conta;
    }

}