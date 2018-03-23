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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.andrebronca.pluscontrol.adapter.CategoriaAdapter;
import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class CategoriaLista extends AppCompatActivity {
    private Intent intent;
    private EditText edtPesquisa;
    private ListView listView;
    private Button btnLimpar;
    private CategoriaDAO categoriaDAO;
    private ArrayList<Categoria> listaCategoria;
    private ArrayList<Categoria> listaPesquisada = new ArrayList<>();
    private boolean temCategoria = false;
    private CategoriaAdapter categoriaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linkToXML();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CategoriaLista.this, CategoriaAdicionar.class);
                startActivity(intent);
            }
        });

        categoriaDAO = CategoriaDAO.getInstance(CategoriaLista.this);
        listaCategoria = (ArrayList<Categoria>) categoriaDAO.listarTudo();
        categoriaAdapter = new CategoriaAdapter(this, listaCategoria);

        desativaPesquisaVazia();
        atualizaListViewCategoria();

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
//                listView.setAdapter(new ArrayAdapter<Categoria>(CategoriaLista.this,
//                        android.R.layout.simple_list_item_1, listaPesquisada));
                categoriaAdapter = new CategoriaAdapter(CategoriaLista.this, listaPesquisada);
                listView.setAdapter(categoriaAdapter);
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
                intent = new Intent(CategoriaLista.this, CategoriaDetalhes.class);
                Categoria categoria = (Categoria) listView.getItemAtPosition(position);
                intent.putExtra("id", String.valueOf(categoria.getIdCategoria()));
                startActivity(intent);
                return true;
            }
        });

        edtPesquisa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(CategoriaLista.this, edtPesquisa);
                }
            }
        });
    }

    private void linkToXML() {
        edtPesquisa = (EditText) findViewById(R.id.edt_categoria_pesquisar_lista);
        listView = (ListView) findViewById(R.id.ltv_categoria_lista);
        btnLimpar = (Button) findViewById(R.id.btn_cadastro_lista_limpar);
    }

    /**
     * Evita que o sistema trave por referencia nula a um array
     */
    private void desativaPesquisaVazia() { //ok
        if (listaCategoria.size() > 0) {
            temCategoria = true;
            edtPesquisa.setEnabled(true);
        } else {
            edtPesquisa.setEnabled(false);
        }
    }

    /**
     * após pressionar o botão de limpar a pesquisa, a lista deve ser atualizada, e quando a tela é carregada.
     */
    private void atualizaListViewCategoria() { //ok
        if (temCategoria) {
//            listView.setAdapter(new ArrayAdapter<Categoria>(this,
//                    android.R.layout.simple_list_item_1, listaCategoria));
            categoriaAdapter = new CategoriaAdapter(CategoriaLista.this, listaCategoria);
            listView.setAdapter(categoriaAdapter);
            //cor quando a linha é selecionada para rolagem
            listView.setCacheColorHint(Color.TRANSPARENT);
            pesquisar();
        }
    }

    private void pesquisar() { //ok
        if (temCategoria) {
            int tamPesq = edtPesquisa.getText().length();
            if (!(listaPesquisada == null)) {
                listaPesquisada.clear();

                for (Categoria categoria : listaCategoria) {
                    String pesq = edtPesquisa.getText().toString();
                    String desc = categoria.getDsCategoria();
                    if (tamPesq <= categoria.getDsCategoria().length()) {
                        if (pesq.equals(desc.subSequence(0, tamPesq))) {
                            listaPesquisada.add(categoria);
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
        atualizaListViewCategoria();
    }

}
