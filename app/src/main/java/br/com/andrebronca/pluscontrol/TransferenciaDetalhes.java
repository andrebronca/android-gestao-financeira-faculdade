package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.com.andrebronca.pluscontrol.dao.ContaDAO;
import br.com.andrebronca.pluscontrol.dao.MovimentoContaDAO;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class TransferenciaDetalhes extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private TextView tvContaOrigem, tvContaDestino, tvValor, tvData;
    private int id_origem;
    private MovimentoConta movOrigem, movDestino;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia_detalhes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                id_origem = params.getInt("id_origem");
            }
        }

        linkToXml();
        getMovimentoOrigemDestino();
        preencherDetalhes();
    }

    private void linkToXml() {
        tvContaOrigem = (TextView) findViewById(R.id.tv_transferencia_detalhe_origem);
        tvContaDestino = (TextView) findViewById(R.id.tv_transferencia_detalhe_destino);
        tvValor = (TextView) findViewById(R.id.tv_transferencia_detalhe_valor);
        tvData = (TextView) findViewById(R.id.tv_transferencia_detalhe_data);
    }

    private void getMovimentoOrigemDestino() {
        MovimentoContaDAO movDao = MovimentoContaDAO.getInstance(this);
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        movOrigem = movDao.getMovimentoByID(id_origem, this);
        movDestino = movDao.getMovimentoByID((id_origem + 1), this);
    }

    private void preencherDetalhes() {
        tvContaOrigem.setText(movOrigem.getConta().getDsConta());
        tvContaDestino.setText(movDestino.getConta().getDsConta());
        tvValor.setText(String.format("R$%.2f", movOrigem.getVlMovimento()));
        tvData.setText(Util.dateToStringFormat(movOrigem.getDtMovimento()));
    }

}
