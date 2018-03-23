package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.dao.PessoaDAO;

public class Cadastros extends AppCompatActivity {
    private Button btnCadCategoria, btnCadCliente, btnCadFornecedor, btnCadConta;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastros);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cadastros");
        linkToXML();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCadCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Cadastros.this, CategoriaLista.class);
                startActivity(intent);
            }
        });

        btnCadCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Cadastros.this, ClienteLista.class);
                startActivity(intent);
            }
        });

        btnCadFornecedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Cadastros.this, FornecedorLista.class);
                startActivity(intent);
            }
        });

        btnCadConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Cadastros.this, ContaLista.class);
                startActivity(intent);
            }
        });
    }

    private void linkToXML() {
        btnCadCategoria = (Button) findViewById(R.id.btn_cadastro_categoria);
        btnCadCliente = (Button) findViewById(R.id.btn_cadastro_cliente);
        btnCadFornecedor = (Button) findViewById(R.id.btn_cadastro_fornecedor);
        btnCadConta = (Button) findViewById(R.id.btn_cadastro_conta);
    }
}