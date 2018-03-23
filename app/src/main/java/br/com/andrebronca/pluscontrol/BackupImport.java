package br.com.andrebronca.pluscontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.dao.BackupDAO;
import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.dao.ContaDAO;
import br.com.andrebronca.pluscontrol.dao.MovimentoContaDAO;
import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.dao.UsuarioDAO;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.modelo.UsuarioMod;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class BackupImport extends AppCompatActivity {
    private EditText edtConteudo;
    private Button btnImportar;
    private Intent intent;
    private String conteudo = null;
    Gson gson = new Gson();
    private List<Categoria> listaCategoria;
    private List<Pessoa> listaPessoa;
    private List<Titulo> listaTitulo;
    private List<Conta> listaConta;
    private List<MovimentoConta> listaMovimentoConta;
    private List<UsuarioMod> listaUsuario;
    private final int LIMITE = 100;   //limite estipulado contando caracteres de uma consulta simples
    private boolean senhaIdentica = false;
    private ArrayList<String> aConteudo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_import);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkToXml();

        btnImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (verificaSeTemUsuario()) { //todo: desativado criptografia
//                    getDadosFormulario();
//                    if (senhaIdentica) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(BackupImport.this);
                alerta
                        .setTitle("Atenção")
                        .setIcon(R.drawable.ic_delete_24dp)
                        .setCancelable(false)
                        .setMessage("A importação irá excluir todos os lançamentos!\nÉ recomendável realizar um backup antes.")
                        .setPositiveButton("Importar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BackupDAO backupDAO = BackupDAO.getInstance(BackupImport.this);
                                conteudo = edtConteudo.getText().toString().trim();
                                backupDAO.removerDados();
//                                splitStringImportacao(Util.descriptografar(aConteudo.get(0)));//todo: desativado criptografia
                                splitStringImportacao(conteudo);
                                salvarImportacao();
                                Util.exibeMensagem(BackupImport.this, "Importação realizada com sucesso!");
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
//                    } else {
//                        Util.exibeMensagem(BackupImport.this, "Descriptografia não será realizada.\nDivergência de origem!");
//                    }
//                } else {
//                    Util.exibeMensagem(BackupImport.this, "Cadastre um usuário para validar a senha!");
//                } //todo: desativado criptografia
            }
        });
    }

    private void linkToXml() {
        edtConteudo = (EditText) findViewById(R.id.edt_backup_importar_conteudo);
        btnImportar = (Button) findViewById(R.id.btn_backup_importar_conteudo);
    }

    private void getDadosFormulario() {
        conteudo = edtConteudo.getText().toString().trim();
        verificaSenhaConteudo();
    }

    private void verificaSenhaConteudo() {
        String[] aCont = conteudo.split("cTd");
        aConteudo.add(aCont[0]);
        aConteudo.add(aCont[1]);
        String senhaUsuario = getSenhaUsuario();
        if (aCont[1].equals(senhaUsuario)) {
            senhaIdentica = true;
        }
    }

    private boolean verificaSeTemUsuario() {
        UsuarioDAO usuarioDAO = UsuarioDAO.getInstance(this);
        if (usuarioDAO.getMaxIDUsuario() > 0) {
            UsuarioMod usuario = usuarioDAO.getUsuario();
//            idUsuario = usuario.getIdUsuario();
//            emailUsuario = usuario.getDsEmail();
//            senhaUsuario = Util.descriptografar(usuario.getDsSenha());
//            temUsuario = true;
            return true;
        } else {
            return false;
        }
    }

    private String getSenhaUsuario() {
        UsuarioDAO usuarioDAO = UsuarioDAO.getInstance(this);
        UsuarioMod usuario = usuarioDAO.getUsuario();
        String senha = usuario.getDsSenha();
        return senha.substring(0, senha.length() - 1);
    }

    private void fecharActivity() { //ok
        intent = new Intent(BackupImport.this, MainActivity.class); //voltar para a tela principal
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void salvarImportacao() {
        CategoriaDAO categoriaDAO = CategoriaDAO.getInstance(this);
        for (Categoria categoria : listaCategoria) {
            categoriaDAO.salvar(categoria);
        }

        PessoaDAO pessoaDAO = PessoaDAO.getInstance(this, "BackupImport_salvarImportacao()");
        for (Pessoa pessoa : listaPessoa) {
            pessoaDAO.salvar(pessoa);
        }

        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        for (Titulo titulo : listaTitulo) {
            tituloDAO.salvarTituloImportacao(titulo);
        }

        ContaDAO contaDAO = ContaDAO.getInstance(this);
        for (Conta conta : listaConta) {
            contaDAO.salvar(conta);
        }

        MovimentoContaDAO movimentoContaDAO = MovimentoContaDAO.getInstance(this);
        for (MovimentoConta mov : listaMovimentoConta) {
            movimentoContaDAO.salvarMovimentoImportado(mov);
        }

        UsuarioDAO usuarioDAO = UsuarioDAO.getInstance(this);
        for (UsuarioMod usuario : listaUsuario) {
            usuarioDAO.salvarUsuarioImportado(usuario);
        }
    }

    private void splitStringImportacao(String str) {
        String[] listaJson = str.split("@@");
        preencherCategoria(listaJson[0]);
        preencherPessoa(listaJson[1]);
        preencherTitulo(listaJson[2]);
        preencherConta(listaJson[3]);
        preencherMovimento(listaJson[4]);
        preencherUsuario(listaJson[5]);
    }

    private void preencherCategoria(String str) {
        Categoria[] categorias = gson.fromJson(str, Categoria[].class);
        listaCategoria = new ArrayList<>();
        for (Categoria cat : categorias) {
            listaCategoria.add(cat);
        }
    }

    private void preencherPessoa(String str) {
        Pessoa[] arObj = gson.fromJson(str, Pessoa[].class);
        listaPessoa = new ArrayList<>();
        for (Pessoa obj : arObj) {
            listaPessoa.add(obj);
        }
    }

    private void preencherTitulo(String str) {
        Titulo[] arObj = gson.fromJson(str, Titulo[].class);
        listaTitulo = new ArrayList<>();
        for (Titulo obj : arObj) {
            listaTitulo.add(obj);
        }
    }

    private void preencherConta(String str) {
        Conta[] arObj = gson.fromJson(str, Conta[].class);
        listaConta = new ArrayList<>();
        for (Conta obj : arObj) {
            listaConta.add(obj);
        }
    }

    private void preencherMovimento(String str) {
        MovimentoConta[] arObj = gson.fromJson(str, MovimentoConta[].class);
        listaMovimentoConta = new ArrayList<>();
        for (MovimentoConta obj : arObj) {
            listaMovimentoConta.add(obj);
        }
    }

    private void preencherUsuario(String str) {
        UsuarioMod[] arObj = gson.fromJson(str, UsuarioMod[].class);
        listaUsuario = new ArrayList<>();
        for (UsuarioMod obj : arObj) {
            listaUsuario.add(obj);
        }
    }

}