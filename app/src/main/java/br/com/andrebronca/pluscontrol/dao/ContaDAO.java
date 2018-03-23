package br.com.andrebronca.pluscontrol.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.entidades.ContaEntidade;
import br.com.andrebronca.pluscontrol.entidades.DBHelper;
import br.com.andrebronca.pluscontrol.modelo.Conta;

/**
 * Created by andrebronca on 08/10/16.
 */

public class ContaDAO {
    private static ContaDAO instance;
    private SQLiteDatabase db;
    private DBHelper conexao;
    private Context context;

    public static ContaDAO getInstance(Context context) {
        if (instance == null) {
            instance = new ContaDAO(context);
        }
        return instance;
    }

    private ContaDAO(Context context) {
        conexao = DBHelper.getInstance(context);
    }

    /**
     * Se id for maior do que zero significa que o insert ocorreu com sucesso
     * @param conta objeto
     * @return um long da inserção
     */
    public long salvar(Conta conta) {
        ContentValues values = new ContentValues();
        values.put(ContaEntidade.Coluna.DESCRICAO, conta.getDsConta());
        values.put(ContaEntidade.Coluna.SALDO, conta.getVlSaldo());
        values.put(ContaEntidade.Coluna.COR, conta.getCdCor());
        db = conexao.getWritableDatabase();
        long id = db.insert(ContaEntidade.TABLE_NAME, null, values);
        db.close();
        return id;
    }


    /**
     * Será utilizado também para desativar uma conta
     * @param conta objeto
     * @return quantidade de linhas afetadas
     */
    public int atualizar(Conta conta) {
        String where = ContaEntidade.Coluna.ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(conta.getIdConta())};
        ContentValues values = new ContentValues();
        values.put(ContaEntidade.Coluna.DESCRICAO, conta.getDsConta());
        values.put(ContaEntidade.Coluna.SALDO, conta.getVlSaldo());
        values.put(ContaEntidade.Coluna.COR, conta.getCdCor());
        values.put(ContaEntidade.Coluna.DATAALT, conta.getDtAlterado());
        db = conexao.getWritableDatabase();
        int rows = db.update(ContaEntidade.TABLE_NAME, values, where, whereArgs);
        db.close();
        return rows;
    }

    /**
     * Utilizado na transação, quando ocorrer um recebimento
     * @param conta
     * @return
     */
    public int atualizarSaldoTransacao(Conta conta) {
        String where = ContaEntidade.Coluna.ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(conta.getIdConta())};
        ContentValues values = new ContentValues();
//        values.put(ContaEntidade.Coluna.DESCRICAO, conta.getDsConta());
        values.put(ContaEntidade.Coluna.SALDO, conta.getVlSaldo());
//        values.put(ContaEntidade.Coluna.COR, conta.getCdCor());
        values.put(ContaEntidade.Coluna.DATAALT, conta.getDtAlterado());
        db = conexao.getWritableDatabase();
        int rows = db.update(ContaEntidade.TABLE_NAME, values, where, whereArgs);
        db.close();
        return rows;
    }

    /**
     * Lista todas as categorias, porém somente a descrição
     * @return
     */
    public List<Conta> listaContaDescricao() {
        List<Conta> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        Cursor cursor = db.query(ContaEntidade.TABLE_NAME,
                new String[]{ContaEntidade.Coluna.ID, ContaEntidade.Coluna.DESCRICAO, ContaEntidade.Coluna.SALDO},
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Conta obj = new Conta();
            obj.setIdConta(cursor.getInt(cursor.getColumnIndex(ContaEntidade.Coluna.ID)));
            obj.setDsConta(cursor.getString(cursor.getColumnIndex(ContaEntidade.Coluna.DESCRICAO)));
            obj.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(ContaEntidade.Coluna.SALDO)));
            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }

    /**
     * retorna o total de conta cadastradas
     * @return
     */
    public int getTotalConta() {
        return listarTudo().size();
    }

    /**
     * retorna a conta se o id for igual
     * @param id tipo int
     * @return
     */
    public Conta getContaById(int id) {
        Conta conta = null;
        String sql = null;
        if (id > 0) {
            db = conexao.getReadableDatabase();
            sql = "select * from " + ContaEntidade.TABLE_NAME + " where " + ContaEntidade.Coluna.ID + " = " + id;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                conta = new Conta();
                conta.setIdConta(cursor.getInt(cursor.getColumnIndex(ContaEntidade.Coluna.ID)));
                conta.setDsConta(cursor.getString(cursor.getColumnIndex(ContaEntidade.Coluna.DESCRICAO)));
                conta.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(ContaEntidade.Coluna.SALDO)));
                conta.setCdCor(cursor.getString(cursor.getColumnIndex(ContaEntidade.Coluna.COR)));
                conta.setDtCadastro(cursor.getString(cursor.getColumnIndexOrThrow(ContaEntidade.Coluna.DATACAD)));
                conta.setDtAlterado(cursor.getString(cursor.getColumnIndexOrThrow(ContaEntidade.Coluna.DATAALT)));
            }
            cursor.close();
            db.close();
        }
        return conta;
    }

    /**
     * Lista todos os produtos cadastrados na conta
     * @return
     */
    public List<Conta> listarTudo() {
        List<Conta> listaConta = new ArrayList<>();
        String[] aColunas = new String[]{
                ContaEntidade.Coluna.ID,
                ContaEntidade.Coluna.DESCRICAO,
                ContaEntidade.Coluna.SALDO,
                ContaEntidade.Coluna.COR,
                ContaEntidade.Coluna.DATACAD,
                ContaEntidade.Coluna.DATAALT
        };

        db = conexao.getReadableDatabase();
        Cursor cursor = db.query(ContaEntidade.TABLE_NAME, aColunas, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Conta obj = new Conta();
            obj.setIdConta(cursor.getInt(cursor.getColumnIndex(aColunas[0])));
            obj.setDsConta(cursor.getString(cursor.getColumnIndex(aColunas[1])));
            obj.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(aColunas[2])));
            obj.setCdCor(cursor.getString(cursor.getColumnIndex(aColunas[3])));
            obj.setDtCadastro(cursor.getString(cursor.getColumnIndex(aColunas[4])));
            obj.setDtAlterado(cursor.getString(cursor.getColumnIndex(aColunas[5])));
            listaConta.add(obj);
        }
        db.close();
        cursor.close();
        return listaConta;
    }

    public int deleteContaByID(int id) {
        db = conexao.getWritableDatabase();
        int result = db.delete(ContaEntidade.TABLE_NAME, " id_conta = " + id, null);
        return result;
    }
}
