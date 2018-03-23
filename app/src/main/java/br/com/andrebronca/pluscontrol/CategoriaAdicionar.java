package br.com.andrebronca.pluscontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarContextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import br.com.andrebronca.pluscontrol.Constantes.Cores;
import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class CategoriaAdicionar extends AppCompatActivity {
    private String[] tipoCategoria = new String[]{"--SELECIONE--", "RECEITA", "DESPESA"};
    private Button btnSalvar, btnCancelar;
    private ToggleButton btnAtivo;
    private EditText edtDescricao, edtInputCor;
    private Spinner spnCores, spnTipCategoria;
    private Intent intent;
    private ArrayAdapter adapterTipo;
    private String ds_categoria, tp_categoria, cd_cor, cd_cor_extra, st_ativo;    //colunas da tabela
    private CategoriaDAO categoriaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_adicionar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linkToXML();
        preencheTipo();
        preencheListaCores();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spnCores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValorCampos();

                if (validarCampos()) {
                    categoriaDao = CategoriaDAO.getInstance(CategoriaAdicionar.this);
                    categoriaDao.salvar(preencherCategoria());
                    Util.exibeMensagem(getApplicationContext(), "Categoria adicionada com sucesso!");
                    fecharActivity();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fecharActivity();
            }
        });

        edtDescricao.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(CategoriaAdicionar.this, edtDescricao);
                }
            }
        });

    }

    private void linkToXML() {
        edtDescricao = (EditText) findViewById(R.id.edt_categoria_ds_categoria);
        btnSalvar = (Button) findViewById(R.id.btn_categoria_salvar);
        btnCancelar = (Button) findViewById(R.id.btn_categoria_cancelar);
        spnTipCategoria = (Spinner) findViewById(R.id.spnTipCategoria);
        spnCores = (Spinner) findViewById(R.id.spnCorCategoria);
        btnAtivo = (ToggleButton) findViewById(R.id.tbtn_categoria_ativo);
    }

    private void fecharActivity() { //ok
        intent = new Intent(CategoriaAdicionar.this, CategoriaLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void preencheListaCores() { //ok
        //Obtém o array de cores
        String[] listaCores = getResources().getStringArray(R.array.cores_array);
        //Cria o adapter
        final Cores adapterCores = new Cores(this, R.layout.spinner_cores_row, listaCores);
        //Atribui o adapter
        spnCores.setAdapter(adapterCores);
    }

    private void preencheTipo() { //ok
        adapterTipo = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tipoCategoria);
        spnTipCategoria.setAdapter(adapterTipo);
    }

    private void getValorCampos() { //ok
        ds_categoria = edtDescricao.getText().toString().trim();
        tp_categoria = Util.getPrimLetra(String.valueOf(spnTipCategoria.getSelectedItem()));
        cd_cor = String.valueOf(spnCores.getSelectedItem());
//        cd_cor_extra = edtInputCor.getEditableText().toString();
        st_ativo = btnAtivo.isChecked() ? "S" : "N";
    }

    private boolean validarCampos() { //ok
        if (ds_categoria.length() < 3) {
            edtDescricao.setError("A descrição deve ter mais de 3 caracteres!");
            return false;
        }
        if (tp_categoria.equals(Util.getPrimLetra(tipoCategoria[0]))) {
            Util.exibeMensagem(this, "Selecione um tipo: Receita / Despesa!");
            return false;
        }
        return true;
    }

    private Categoria preencherCategoria() {
        Categoria categoria = new Categoria();
        categoria.setDsCategoria(ds_categoria);
        categoria.setTpCategoria(tp_categoria);
        categoria.setCdCor(cd_cor);
        categoria.setStAtivo(st_ativo);
        return categoria;
    }

}