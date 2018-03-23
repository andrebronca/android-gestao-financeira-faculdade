package br.com.andrebronca.pluscontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ToggleButton;

import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class FornecedorEditar extends AppCompatActivity {
    private EditText edtNome, edtEmail;
    private CheckBox cbxTipo;
    private ToggleButton tbtAtivo;
    private Button btnSalvar, btnExcluir;
    private Intent intent;
    private Bundle params;
    private Pessoa pessoa;
    private PessoaDAO pessoaDAO;
    private String nm_pessoa, tp_pessoa, ds_email, st_ativo;
    private Context context;
    private int idFornecedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fornecedor_editar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                idFornecedor = Integer.parseInt(params.getString("id"));
            }
        }

        linkToXml();
//        params = getIntent().getExtras();
        preencherFormulario();

        edtNome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(context, edtNome);
                }
            }
        });

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(context, edtEmail);
                }
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValorCampos();

                if (validarCampos()) {
                    pessoaDAO = PessoaDAO.getInstance(FornecedorEditar.this, "FornecedorEditar_btnSalvar()");
                    pessoaDAO.atualizar(preencherPessoa());
                    Util.exibeMensagem(getApplicationContext(), "Salvo com sucesso!");
                    fecharActivity();
                }
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!verificaVinculoComTitulo()) {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(FornecedorEditar.this);
                    alerta.setTitle("Atenção");
                    alerta.setIcon(R.drawable.ic_delete_24dp);
                    alerta.setCancelable(false);
                    alerta.setMessage("Deseja excluir este fornecedor?");
                    alerta.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PessoaDAO pessoaDAO = PessoaDAO.getInstance(FornecedorEditar.this, null);
                            pessoaDAO.deletePessoaByID(idFornecedor);
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
                    Util.exibeMensagem(FornecedorEditar.this, "Este fornecedor tem vínculo com título.\n" +
                            "Não pode ser excluído!");
                }
            }
        });
    }

    private void linkToXml() {
        edtNome = (EditText) findViewById(R.id.edt_fornecedor_editar_nm_pessoa);
        edtEmail = (EditText) findViewById(R.id.edt_fornecedor_editar_ds_email);
        cbxTipo = (CheckBox) findViewById(R.id.cbx_fornecedor_editar_tipo);
        tbtAtivo = (ToggleButton) findViewById(R.id.tbtn_fornecedor_editar_ativo);
        btnSalvar = (Button) findViewById(R.id.btn_fornecedor_editar_salvar);
        btnExcluir = (Button) findViewById(R.id.btn_fornecedor_editar_excluir);
    }

    private void getValorCampos() {
        nm_pessoa = edtNome.getText().toString().trim();
        ds_email = edtEmail.getText().toString().trim();
        tp_pessoa = cbxTipo.isChecked() ? "A" : "F";
        st_ativo = tbtAtivo.isChecked() ? "S" : "N";
    }

    private boolean verificaVinculoComTitulo() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        int qtd = 0;
        qtd = tituloDAO.getQtdVinculoPessoaById(idFornecedor);
        return qtd > 0;
    }

    private boolean validarCampos() {
        if (nm_pessoa.length() < 3) {
            edtNome.setError("O nome deve ter mais de 3 caracteres!");
            return false;
        }
        if (!Util.isEmailValido(ds_email)) {
            edtEmail.setError("Email inválido!");
            return false;
        }
        return true;
    }

    private Pessoa preencherPessoa() {
        Pessoa objPessoa = new Pessoa();
        objPessoa.setIdPessoa(idFornecedor);
        objPessoa.setNmPessoa(nm_pessoa);
        objPessoa.setDsEmail(ds_email);
        objPessoa.setTpPessoa(tp_pessoa);
        objPessoa.setStAtivo(st_ativo);
        objPessoa.setDtAlterado(Util.dateAtualToDateBD(Util.getDataAtual()));
        return objPessoa;
    }

    private void fecharActivity() {
        intent = new Intent(this, FornecedorLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private Pessoa getPessoaById() {
        pessoa = new Pessoa();
        pessoaDAO = PessoaDAO.getInstance(this, "FornecedorEditar_getPessoaById()");
        int id = Integer.parseInt(params.getString("id"));
        pessoa = pessoaDAO.getPessoaByID(id);
        return pessoa;
    }

    private void preencherFormulario() {
        Pessoa pessoa = getPessoaById();
        edtNome.setText(pessoa.getNmPessoa());
        edtEmail.setText(pessoa.getDsEmail());
        setTipoPessoa(pessoa.getTpPessoa());
        tbtAtivo.setChecked(pessoa.getStAtivoBool());
    }

    private void setTipoPessoa(String tp) {
        if (tp.equals("A")) {
            cbxTipo.setChecked(true);
        } else {
            cbxTipo.setChecked(false);
        }
    }

}
