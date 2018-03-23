package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.andrebronca.pluscontrol.adapter.PessoaAdapter;
import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class FornecedorLista extends AppCompatActivity {
    private Button btnLimpar;
    private EditText edtPesquisa;
    private ListView listViewFornecedor;
    private Intent intent;
    private PessoaDAO pessoaDAO;
    private ArrayList<Pessoa> listaFornecedor;
    private ArrayList<Pessoa> listaPesquisada = new ArrayList<>();
    private boolean temPessoa = false;
    private PessoaAdapter pessoaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fornecedor_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linkToXml();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(FornecedorLista.this, FornecedorAdicionar.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pessoaDAO = PessoaDAO.getInstance(this, "FornecedorLista_onCreate()");
        listaFornecedor = (ArrayList<Pessoa>) pessoaDAO.listarTudo("F");
        pessoaAdapter = new PessoaAdapter(this, listaFornecedor);
        desativarPesquisaVazia();
        atualizarListViewPessoa();

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparPesquisa();
            }
        });

        edtPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pesquisar();
//                listViewFornecedor.setAdapter(
//                        new ArrayAdapter<Pessoa>(FornecedorLista.this,
//                                android.R.layout.simple_list_item_1, listaPesquisada));
                pessoaAdapter = new PessoaAdapter(FornecedorLista.this, listaPesquisada);
                listViewFornecedor.setAdapter(pessoaAdapter);
                //cor quando a linha é selecionada para rolagem
                listViewFornecedor.setCacheColorHint(Color.TRANSPARENT);
            }
        });

        edtPesquisa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(FornecedorLista.this, edtPesquisa);
                }
            }
        });

        listViewFornecedor.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(FornecedorLista.this, FornecedorDetalhes.class);
                Pessoa pessoa = (Pessoa) listViewFornecedor.getItemAtPosition(position);
                intent.putExtra("id", String.valueOf(pessoa.getIdPessoa()));
                startActivity(intent);
                return true;
            }
        });
    }

    private void linkToXml() {
        btnLimpar = (Button) findViewById(R.id.btn_fornecedor_lista_limpar);
        edtPesquisa = (EditText) findViewById(R.id.edt_fornecedor_pesquisar_lista);
        listViewFornecedor = (ListView) findViewById(R.id.ltv_fornecedor_lista);
    }

    private void limparPesquisa() {
        edtPesquisa.setText("");
        edtPesquisa.requestFocus();
        atualizarListViewPessoa();
    }

    private void desativarPesquisaVazia() {
        if (listaFornecedor.size() > 0) {
            temPessoa = true;
            edtPesquisa.setEnabled(true);
        } else {
            edtPesquisa.setEnabled(false);
        }
    }

    private void atualizarListViewPessoa() {
        if (temPessoa) {
//            listViewFornecedor.setAdapter(
//                    new ArrayAdapter<Pessoa>(this,
//                            android.R.layout.simple_list_item_1, listaFornecedor));
            pessoaAdapter = new PessoaAdapter(FornecedorLista.this, listaFornecedor);
            listViewFornecedor.setAdapter(pessoaAdapter);
            //cor quando a linha é selecionada para rolagem
            listViewFornecedor.setCacheColorHint(Color.TRANSPARENT);
            pesquisar();
        }
    }

    private void pesquisar() {
        if (temPessoa) {
            int tamPesq = edtPesquisa.getText().length();
            if (!(listaPesquisada == null)) {
                listaPesquisada.clear();
                for (Pessoa pessoa : listaFornecedor) {
                    String pesq = edtPesquisa.getText().toString();
                    String nome = pessoa.getNmPessoa();
                    if (tamPesq <= nome.length()) {
                        if (pesq.equals(nome.subSequence(0, tamPesq))) {
                            listaPesquisada.add(pessoa);
                        }
                    }
                }
            }
        }
    }

}
