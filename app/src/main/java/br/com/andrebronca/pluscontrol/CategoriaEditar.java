package br.com.andrebronca.pluscontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import br.com.andrebronca.pluscontrol.Constantes.Cores;
import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class CategoriaEditar extends AppCompatActivity {
    private Bundle params = new Bundle();
    private Intent intent;
    private Button btnSalvar, btnExcluir;
    private ToggleButton tgbAtivo;
    private Spinner spnCor, spnTipo;
    private EditText edtDescricao;
    private CategoriaDAO categoriaDao;
    private String ds_categoria, tp_categoria, cd_cor, st_ativo;    //colunas da tabela
    private String[] tipoCategoria = {"--SELECIONE--", "RECEITA", "DESPESA"};
    private ArrayAdapter adapterTipo;
    private String[] listaCores;
    private Cores adapterCores;
    private Categoria categoria;
    private int idCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_editar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linkToXML();            //ok
        listaCores = getResources().getStringArray(R.array.cores_array);
        adapterCores = new Cores(this, R.layout.spinner_cores_row, listaCores);

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                idCategoria = Integer.parseInt(params.getString("id"));
            }
        }

        preencheListaCores();   //ok
        preencheTipo();         //ok

        preencherFormulario();  //ok

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSalvar.callOnClick();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        spnCor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    categoriaDao = CategoriaDAO.getInstance(CategoriaEditar.this);
                    categoriaDao.atualizar(preencherCategoria());
                    Util.exibeMensagem(getApplicationContext(), "Editado com sucesso!");
                    fecharActivity();
                }

            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!verificaVinculoComTitulo()) {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(CategoriaEditar.this);
                    alerta.setTitle("Atenção");
                    alerta.setIcon(R.drawable.ic_delete_24dp);
                    alerta.setCancelable(false);
                    alerta.setMessage("Deseja excluir esta categoria?");
                    alerta.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CategoriaDAO categoriaDAO = CategoriaDAO.getInstance(CategoriaEditar.this);
                            categoriaDAO.deleteCategoriaByID(idCategoria);
                            fecharActivity();
                        }
                    });
                    alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alerta.create();
                    alertDialog.show();
                } else {
                    Util.exibeMensagem(CategoriaEditar.this, "Esta categoria tem vínculo com título.\n" +
                            "Não pode ser excluída!");
                }
            }
        });

        edtDescricao.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(CategoriaEditar.this, edtDescricao);
                }
            }
        });

    }

    //ok
    private void linkToXML() {
        edtDescricao = (EditText) findViewById(R.id.edt_categoria_editar_categoria);
        spnTipo = (Spinner) findViewById(R.id.spn_categoria_editar_tipo);
        spnCor = (Spinner) findViewById(R.id.spn_categoria_editar_cor);
        tgbAtivo = (ToggleButton) findViewById(R.id.tbt_categoria_editar_ativo);
        btnSalvar = (Button) findViewById(R.id.btn_categoria_editar_salvar);
        btnExcluir = (Button) findViewById(R.id.btn_categoria_editar_excluir);
    }

    //ok
    private void preencherFormulario() {
        Categoria categoria = getCategoriaById();
        edtDescricao.setText(categoria.getDsCategoria());
        setTipoCategoria();     //ok
        setCorCategoria();      //ok
        tgbAtivo.setChecked(categoria.getStAtivoBool());
    }

    private boolean verificaVinculoComTitulo() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        int qtd = 0;
        qtd = tituloDAO.getQtdVinculoContaById(idCategoria);
        return qtd > 0;
    }

    //ok
    private void fecharActivity() {
        intent = new Intent(CategoriaEditar.this, CategoriaLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void getValorCampos() {
        ds_categoria = edtDescricao.getText().toString().trim();
        tp_categoria = Util.getPrimLetra(String.valueOf(spnTipo.getSelectedItem()));
        cd_cor = String.valueOf(spnCor.getSelectedItem());
        st_ativo = tgbAtivo.isChecked() ? "S" : "N";
    }

    private boolean validarCampos() {
        if (ds_categoria.length() < 3) {
            edtDescricao.setError("A descrição deve ter mais de 3 caracteres!");
            return false;
        }
        if (tp_categoria == Util.getPrimLetra(tipoCategoria[0])) {
            Util.exibeMensagem(this, "Selecione um tipo: Receita / Despesa!");
            return false;
        }
        return true;
    }

    private Categoria preencherCategoria() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(idCategoria);
        categoria.setDsCategoria(ds_categoria);
        categoria.setTpCategoria(tp_categoria);
        categoria.setCdCor(cd_cor);
        categoria.setStAtivo(st_ativo);
//        categoria.setDtAlterado( "2016-10-01" );
        categoria.setDtAlterado(Util.dateAtualToDateBD(Util.getDataAtual()));
        return categoria;
    }

    //ok
    private void preencheListaCores() {
        //Obtém o array de cores
        //Cria o adapter
        //Atribui o adapter
        spnCor.setAdapter(adapterCores);
    }

    //ok
    private void preencheTipo() {
        adapterTipo = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tipoCategoria);
        spnTipo.setAdapter(adapterTipo);
    }

    private Categoria getCategoriaById() {
        categoria = new Categoria();
        categoriaDao = CategoriaDAO.getInstance(this);
        int id = Integer.parseInt(params.getString("id"));
        categoria = categoriaDao.getCategoriaById(id);
        return categoria;
    }

    //ok
    private void setTipoCategoria() {
        // spnTipo.setSelection( adapterTipo.getPosition( 2 ));
        spnTipo.post(new Runnable() {
            @Override
            public void run() {
                spnTipo.setSelection(linkTipoComAdapter(categoria.getTpCategoria()));
            }
        });
    }

    //ok
    private int linkTipoComAdapter(String tipo) {
        if (tipo.equals("R")) {
            return 1;
        } else if (tipo.equals("D")) {
            return 2;
        } else {
            return 0;
        }
    }

    //ok
    private void setCorCategoria() {
        spnCor.post(new Runnable() {
            @Override
            public void run() {
//                spnCor.setSelection( 8 );
                spnCor.setSelection(linkCorComAdapter(categoria.getCdCor()));
            }
        });
    }

    private int linkCorComAdapter(String cor) {
        int posicao = 0;
        for (int i = 0; i < listaCores.length; i++) {
//            String corArray = listaCores[i];
            if (listaCores[i].equalsIgnoreCase(cor)) {
                posicao = i;
            }
        }
        return posicao;
    }

}