package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ToggleButton;

import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class ClienteAdicionar extends AppCompatActivity {
    private EditText edtNome, edtEmail;
    private Button btnSalvar, btnCancelar;
    private ToggleButton tgbAtivo;
    private CheckBox cbxTipoCliente;
    private Intent intent;
    private String nm_pessoa, tp_pessoa, ds_email, st_ativo;
    private PessoaDAO pessoaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_adicionar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linkToXml();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(ClienteAdicionar.this, edtNome);
                }
            }
        });

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(ClienteAdicionar.this, edtEmail);
                }
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValorCampos();

                if (validarCampos()) {
                    pessoaDAO = PessoaDAO.getInstance(ClienteAdicionar.this, "ClienteAdicionar");
                    pessoaDAO.salvar(preencherPessoa());
                    Util.exibeMensagem(getApplicationContext(), "Adicionado com sucesso!");
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
    }

    private void linkToXml() {
        edtNome = (EditText) findViewById(R.id.edt_cliente_nm_pessoa);
        edtEmail = (EditText) findViewById(R.id.edt_cliente_ds_email);
        btnSalvar = (Button) findViewById(R.id.btn_cliente_salvar);
        btnCancelar = (Button) findViewById(R.id.btn_cliente_cancelar);
        tgbAtivo = (ToggleButton) findViewById(R.id.tbtn_cliente_ativo);
        cbxTipoCliente = (CheckBox) findViewById(R.id.cbx_cliente_tipo);    //se marcado, gravar no campo: 'A'
    }

    private void fecharActivity() {
        intent = new Intent(ClienteAdicionar.this, ClienteLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void getValorCampos() {
        nm_pessoa = edtNome.getText().toString().trim();
        ds_email = edtEmail.getText().toString().trim();
        tp_pessoa = cbxTipoCliente.isChecked() ? "A" : "C";
        st_ativo = tgbAtivo.isChecked() ? "S" : "N";
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
        objPessoa.setNmPessoa(nm_pessoa);
        objPessoa.setDsEmail(ds_email);
        objPessoa.setTpPessoa(tp_pessoa);
        objPessoa.setStAtivo(st_ativo);
        return objPessoa;
    }

}