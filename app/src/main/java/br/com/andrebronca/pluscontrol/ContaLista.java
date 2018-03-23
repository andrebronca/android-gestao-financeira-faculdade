package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import br.com.andrebronca.pluscontrol.adapter.ContaAdapter;
import br.com.andrebronca.pluscontrol.dao.ContaDAO;
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class ContaLista extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private EditText edtPesquisa;
    private ListView listView;
    private Button btnLimpar;
    private ContaDAO contaDAO;
    private ArrayList<Conta> listaConta;
    private ArrayList<Conta> listaPesquisada = new ArrayList<>();
    private boolean temConta = false;
    private ContaAdapter contaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ContaLista.this, ContaAdicionar.class);
                startActivity(intent);
            }
        });

        linkToXML();


        contaDAO = ContaDAO.getInstance(ContaLista.this);
        listaConta = (ArrayList<Conta>) contaDAO.listarTudo();
        contaAdapter = new ContaAdapter(this, listaConta);
        desativaPesquisaVazia();
        atualizaListViewConta();

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
//                listView.setAdapter(new ArrayAdapter<Conta>(ContaLista.this, android.R.layout.simple_list_item_1, listaPesquisada));
                contaAdapter = new ContaAdapter(ContaLista.this, listaPesquisada);
                listView.setAdapter(contaAdapter);
                listView.setCacheColorHint(Color.TRANSPARENT);
            }
        });

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparPesquisa();

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(ContaLista.this, ContaDetalhes.class);
                Conta conta = (Conta) listView.getItemAtPosition(position);
                params.putInt("id", conta.getIdConta());
                intent.putExtras(params);
                startActivity(intent);
                return true;
            }
        });

        edtPesquisa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(ContaLista.this, edtPesquisa);
                }
            }
        });
    }


    private void linkToXML() {
        edtPesquisa = (EditText) findViewById(R.id.edt_conta_pesquisar_lista);
        listView = (ListView) findViewById(R.id.ltv_conta_lista);
        btnLimpar = (Button) findViewById(R.id.btn_conta_cadastro_lista_limpar);
    }

    /**
     * Evita que o sistema trave por referencia nula a um array
     */
    private void desativaPesquisaVazia() { //ok
        if (listaConta.size() > 0) {
            temConta = true;
            edtPesquisa.setEnabled(true);
        } else {
            edtPesquisa.setEnabled(false);
        }
    }

    /**
     * após pressionar o botão de limpar a pesquisa, a lista deve ser atualizada, e quando a tela é carregada.
     */
    private void atualizaListViewConta() { //ok
        if (temConta) {
//            listView.setAdapter(new ArrayAdapter<Conta>(this, android.R.layout.simple_list_item_1, listaConta));
            contaAdapter = new ContaAdapter(ContaLista.this, listaConta);
            listView.setAdapter(contaAdapter);
            //cor quando a linha é selecionada para rolagem
            listView.setCacheColorHint(Color.TRANSPARENT);
            pesquisar();
        }
    }

    private void pesquisar() { //ok
        if (temConta) {
            int tamPesq = edtPesquisa.getText().length();
            if (!(listaPesquisada == null)) {
                listaPesquisada.clear();
                for (Conta conta : listaConta) {
                    String pesq = edtPesquisa.getText().toString();
                    String desc = conta.getDsConta();
                    if (tamPesq <= conta.getDsConta().length()) {
                        if (pesq.equals(desc.subSequence(0, tamPesq))) {
                            listaPesquisada.add(conta);
                        }
                    }
                }
            }
        }
    }

    //ok
    private void limparPesquisa() {
        edtPesquisa.setText("");
        edtPesquisa.requestFocus();
        atualizaListViewConta();
    }

}