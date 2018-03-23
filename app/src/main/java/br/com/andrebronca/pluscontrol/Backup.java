package br.com.andrebronca.pluscontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import br.com.andrebronca.pluscontrol.dao.BackupDAO;
import br.com.andrebronca.pluscontrol.dao.UsuarioDAO;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.modelo.UsuarioMod;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class Backup extends AppCompatActivity {
    private Intent intent;
    private Button btnExportar, btnImportar, btnApagarDados;
    private CheckBox cbx_criptografar_envio;
    private boolean temUsuario = false;
    private List<Categoria> listaCategoria;
    private List<Pessoa> listaPessoa;
    private List<Titulo> listaTitulo;
    private List<Conta> listaConta;
    private List<MovimentoConta> listaMovimentoConta;
    private List<UsuarioMod> listaUsuario;
    private String emailUsuario;
    private Gson gson = new Gson();
    private String categoriaJson, pessoaJson, tituloJson, contaJson, movimentoJson, usuarioJson;
    private String criptoFinal = null;
    private String senhaUsuario = null;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkToXml();
        verificaSeTemUsuario();
        gson.serializeNulls();

        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temUsuario) {
                    extrairDados();
                    if (cbx_criptografar_envio.isChecked()) {
                        envioEmailGmail(Util.criptografar( //todo: desativado criptografia
                                categoriaJson +
                                        pessoaJson +
                                        tituloJson +
                                        contaJson +
                                        movimentoJson +
                                        usuarioJson
                                ) + criptoFinal //todo: desativado criptografia
                        );
                    } else {
                        envioEmailGmail( //Util.criptografar( //todo: desativado criptografia
                                categoriaJson +
                                        pessoaJson +
                                        tituloJson +
                                        contaJson +
                                        movimentoJson +
                                        usuarioJson
                                //) + criptoFinal //todo: desativado criptografia
                        );
                    }
                } else {
                    Util.exibeMensagem(Backup.this, "É necessário cadastrar um usuário!");
                }
            }
        });

        btnImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Backup.this, BackupImport.class);
                startActivity(intent);
            }
        });

        btnApagarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temUsuario) {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(Backup.this);
                    alerta.setTitle("Cuidado!");
                    alerta.setIcon(R.drawable.ic_delete_24dp);
                    alerta.setCancelable(false);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Deseja excluir todos os dados?\n");
                    sb.append("É recomendável ter um backup.\n");
                    sb.append("Insira a senha do usuário:");
                    alerta.setMessage(sb.toString());
                    final EditText input = new EditText(Backup.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input.setSelection(input.getText().length());
                    alerta.setView(input);
                    alerta.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (confereSenhaExcluirDados(input.getText().toString())) {
                                BackupDAO backupDAO = BackupDAO.getInstance(Backup.this);
                                backupDAO.removerDados();
                                fecharActivity();
                            } else {
                                Util.exibeMensagem(Backup.this, "Não será possível excluir os dados!");
                            }
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
                    Util.exibeMensagem(Backup.this, "É necessário cadastrar um usuário!");
                }
            }
        });
    }

    private void extrairDados() {
        setCategoriaToList();
        setPessoaToList();
        setTituloToList();
        setContaToList();
        setMovimentoContaToList();
        setUsuarioToList();
    }

    private void linkToXml() {
        btnExportar = (Button) findViewById(R.id.btn_backup_exportar);
        btnImportar = (Button) findViewById(R.id.btn_backup_importar);
        btnApagarDados = (Button) findViewById(R.id.btn_backup_limpar_dados);
        cbx_criptografar_envio = (CheckBox) findViewById(R.id.cbx_backup_criptografar);
    }

    private void fecharActivity() { //ok
        intent = new Intent(Backup.this, MainActivity.class); //voltar para a tela principal
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void verificaSeTemUsuario() {
        UsuarioDAO usuarioDAO = UsuarioDAO.getInstance(this);
        if (usuarioDAO.getMaxIDUsuario() > 0) {
            UsuarioMod usuario = usuarioDAO.getUsuario();
            idUsuario = usuario.getIdUsuario();
            emailUsuario = usuario.getDsEmail();
            senhaUsuario = Util.descriptografar(usuario.getDsSenha());
            temUsuario = true;
        }
    }

    private boolean confereSenhaExcluirDados(String senhaInput) {
//        verificaSeTemUsuario();

        if (temUsuario) {
            if (senhaUsuario.equals(senhaInput)) {
                return true;
            }
        }
        return false;
    }

    private void setCategoriaToList() {
        BackupDAO backupDAO = BackupDAO.getInstance(this);
        listaCategoria = backupDAO.getCategoriaBackup();
        categoriaJson = gson.toJson(listaCategoria) + "@@";
    }

    private void setPessoaToList() {
        BackupDAO backupDAO = BackupDAO.getInstance(this);
        listaPessoa = backupDAO.getPessoaBackup();
        pessoaJson = gson.toJson(listaPessoa) + "@@";
    }

    private void setTituloToList() {
        BackupDAO backupDAO = BackupDAO.getInstance(this);
        listaTitulo = backupDAO.getTituloBackup();
        tituloJson = gson.toJson(listaTitulo) + "@@";
    }

    private void setContaToList() {
        BackupDAO backupDAO = BackupDAO.getInstance(this);
        listaConta = backupDAO.getContaBackup();
        contaJson = gson.toJson(listaConta) + "@@";
    }

    private void setMovimentoContaToList() {
        BackupDAO backupDAO = BackupDAO.getInstance(this);
        listaMovimentoConta = backupDAO.getMovimentoContaBackup();
        movimentoJson = gson.toJson(listaMovimentoConta) + "@@";
    }

    private void setUsuarioToList() {
        BackupDAO backupDAO = BackupDAO.getInstance(this);
        listaUsuario = backupDAO.getUsuarioModBackup();
        if (listaUsuario.get(0).getDsSenha().trim().length() > 0) {
            criptoFinal = "cTd" + listaUsuario.get(0).getDsSenha();
        } else {
            criptoFinal = "N0opO";
        }
        usuarioJson = gson.toJson(listaUsuario);
    }

    private void envioEmailGmail(String conteudo) {
//        Intent i = new Intent(Intent.ACTION_VIEW);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("plain/text");
        //i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        //i.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        i.putExtra(Intent.EXTRA_EMAIL, emailUsuario);
        i.putExtra(Intent.EXTRA_SUBJECT, "Backup app PlusControl");
        String data = "--" + Util.getDataAtual() + "\n\n";
        i.putExtra(Intent.EXTRA_TEXT, data + conteudo);
        startActivity(Intent.createChooser(i, "Enviando e-mail"));
    }

}