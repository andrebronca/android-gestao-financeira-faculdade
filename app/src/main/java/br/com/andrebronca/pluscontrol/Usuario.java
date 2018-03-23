package br.com.andrebronca.pluscontrol;

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

import br.com.andrebronca.pluscontrol.dao.UsuarioDAO;
import br.com.andrebronca.pluscontrol.modelo.UsuarioMod;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class Usuario extends AppCompatActivity {
    private EditText edtNome, edtEmail, edtSenha;
    private Button btnSalvar, btnExcluir;
    private boolean temUsuario = false;
    private String nm_usuario, ds_email, st_cripto, ds_senha;
    private UsuarioMod objUsuario;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linkToXml();
        verificaSeTemUsuario();
        if (temUsuario) {
            getUsuario();
            preencherFormulario();
        }
        alteraBotoes();

        edtNome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(Usuario.this, edtNome);
                }
            }
        });

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(Usuario.this, edtEmail);
                }
            }
        });

        edtSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(Usuario.this, edtSenha);
                }
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDadosFormulario();
                if (validarDados()) {
                    if (temUsuario) {
//                        preencherFormulario();
//                        getDadosFormulario();
                        if (validarDados()) {
                            UsuarioDAO usuarioDAO = UsuarioDAO.getInstance(Usuario.this);
                            usuarioDAO.atualizar(preencherUsuarioAtualizar());
                            fecharActivity();
                        }
                    } else {
                        //insert
                        UsuarioDAO usuarioDAO = UsuarioDAO.getInstance(Usuario.this);
                        usuarioDAO.salvar(preencherUsuarioSalvar());
                        fecharActivity();
                    }
                }
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(Usuario.this);
                alerta
                        .setTitle("Atenção")
                        .setIcon(R.drawable.ic_delete_24dp)
                        .setCancelable(false)
                        .setMessage("Deseja excluir o Usuário?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UsuarioDAO usuarioDAO = UsuarioDAO.getInstance(Usuario.this);
                                usuarioDAO.deleteUsuarioByID(objUsuario.getIdUsuario());
                                fecharActivity();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alerta.create();
                alertDialog.show();
            }
        });
    }

    private void linkToXml() {
        edtNome = (EditText) findViewById(R.id.edt_usuario_nome);
        edtEmail = (EditText) findViewById(R.id.edt_usuario_email);
        edtSenha = (EditText) findViewById(R.id.edt_usuario_senha);
        btnSalvar = (Button) findViewById(R.id.btn_usuario_salvar);
        btnExcluir = (Button) findViewById(R.id.btn_usuario_excluir);
    }

    private void fecharActivity() { //ok
        intent = new Intent(Usuario.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void verificaSeTemUsuario() {
        UsuarioDAO usuarioDAO = UsuarioDAO.getInstance(this);
        if (usuarioDAO.getMaxIDUsuario() > 0) {
            temUsuario = true;
        }
    }

    private void alteraBotoes() {
        if (temUsuario) {
            btnSalvar.setText("Atualizar");
            btnExcluir.setEnabled(true);
        } else {
            btnExcluir.setEnabled(false);
        }
    }

    private void getDadosFormulario() {
        nm_usuario = edtNome.getText().toString().trim();
        ds_email = edtEmail.getText().toString().trim();
        ds_senha = edtSenha.getText().toString();
    }

    private boolean validarDados() {
        if (nm_usuario.length() <= 3) {
            edtNome.setError("O nome é muito curto!");
            return false;
        }
        if (!Util.isEmailValido(ds_email)) {
            edtEmail.setError("Email inválido");
            return false;
        }
        if (ds_senha.length() < 3) {
            edtSenha.setError("Senha dever ter mais de 2 caracteres!");
            return false;
        }
        return true;
    }

    private void getUsuario() {
        UsuarioDAO usuarioDAO = UsuarioDAO.getInstance(this);
        objUsuario = usuarioDAO.getUsuario();
    }

    private void preencherFormulario() {
        edtNome.setText(objUsuario.getNmUsuario());
        edtEmail.setText(objUsuario.getDsEmail());
        btnSalvar.setText("Atualizar");
    }

    private UsuarioMod preencherUsuarioSalvar() {
        UsuarioMod usuario = new UsuarioMod();
        usuario.setNmUsuario(nm_usuario);
        usuario.setDsEmail(ds_email);
        usuario.setDsSenha(Util.criptografar(ds_senha));
        return usuario;
    }

    private UsuarioMod preencherUsuarioAtualizar() {
        UsuarioMod usuario = new UsuarioMod();
        usuario.setIdUsuario(objUsuario.getIdUsuario());
        usuario.setNmUsuario(nm_usuario);
        usuario.setDsEmail(ds_email);
        usuario.setDsSenha(Util.criptografar(ds_senha));
        return usuario;
    }

}