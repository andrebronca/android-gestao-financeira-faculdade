package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class Movimentos extends AppCompatActivity {
    private Button btnRecebimento, btnPagamento, btnTransfererencia, btnSaque, btnEstorno;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimentos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linkToXml();

        btnRecebimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Movimentos.this, RecebimentoLista.class);
                startActivity(intent);
            }
        });

        btnPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Movimentos.this, PagamentoLista.class);
                startActivity(intent);
            }
        });

        btnTransfererencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Movimentos.this, TransferenciaLista.class);
                startActivity(intent);
            }
        });

        btnSaque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Movimentos.this, SaqueLista.class);
                startActivity(intent);
            }
        });

        btnEstorno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Movimentos.this, EstornoLista.class);
                startActivity(intent);
            }
        });
    }

    private void linkToXml() {
        btnRecebimento = (Button) findViewById(R.id.btn_movimento_recebimento);
        btnPagamento = (Button) findViewById(R.id.btn_movimento_pagamento);
        btnTransfererencia = (Button) findViewById(R.id.btn_movimento_transferencia);
        btnSaque = (Button) findViewById(R.id.btn_movimento_saque);
        btnEstorno = (Button) findViewById(R.id.btn_movimento_estorno);
    }

}