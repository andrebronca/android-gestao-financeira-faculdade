package br.com.andrebronca.pluscontrol.entidades;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by andrebronca on 30/09/16.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NOME = "pluscontrolp";    //versao: o
    public static final int DB_VERSAO = 1;
    private static DBHelper instance;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NOME, null, DB_VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql1 = CategoriaEntidade.CreateTable.getSqlCreateTableCategoria();
            db.execSQL(sql1);
            db.execSQL(CategoriaEntidade.CreateTable.getSqlCreateIndexCategoria());
            Log.i("Categoria", sql1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql2 = PessoaEntidade.CreateTable.getSqlCreateTablePessoa();
            db.execSQL(sql2);
            db.execSQL(PessoaEntidade.CreateTable.getSqlCreateIndexPessoa());
            Log.i("Cliente", sql2);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql3 = TituloEntidade.CreateTable.getSqlCreateTableTitulo();
            db.execSQL(sql3);
            db.execSQL(TituloEntidade.CreateTable.getSqlCreateIndexTitulo());
            Log.i("Titulo", sql3);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql4 = ContaEntidade.CreateTable.getSqlCreateTableConta();
            db.execSQL(sql4);
            db.execSQL(TituloEntidade.CreateTable.getSqlCreateIndexTitulo());
            Log.i("Conta", sql4);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql5 = MovimentoEntidade.CreateTable.getSqlCreateTableMovimentoConta();
            db.execSQL(sql5);
            Log.i("MovimentoConta", sql5);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql6 = UsuarioEntidade.CreateTable.getSqlCreateTableUsuario();
            db.execSQL(sql6);
            Log.i("Usuario", sql6);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            db.execSQL(CategoriaEntidade.CreateTable.dropTableCategoria());
            db.execSQL(PessoaEntidade.CreateTable.dropTablePessoa());
            db.execSQL(TituloEntidade.CreateTable.dropTableTitulo());
            db.execSQL(ContaEntidade.CreateTable.dropTableConta());
            db.execSQL(MovimentoEntidade.CreateTable.dropTableMovimentoConta());
            db.execSQL(UsuarioEntidade.CreateTable.dropTableUsuario());
            onCreate(db);
        }

    }
}