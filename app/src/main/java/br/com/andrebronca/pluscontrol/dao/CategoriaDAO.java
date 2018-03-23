package br.com.andrebronca.pluscontrol.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.entidades.CategoriaEntidade;
import br.com.andrebronca.pluscontrol.entidades.DBHelper;
import br.com.andrebronca.pluscontrol.modelo.Categoria;

/**
 * Created by andrebronca on 30/09/16.
 * Por hora não implementa o delete(categoria)
 */

public class CategoriaDAO {
    private static CategoriaDAO instance;
    private SQLiteDatabase db;
    private DBHelper conexao;
    private Context context;

    public static CategoriaDAO getInstance(Context context) {
        if (instance == null) {
            instance = new CategoriaDAO(context);
        }
        return instance;
    }

    private CategoriaDAO(Context context) {
        conexao = DBHelper.getInstance(context);
    }

    /**
     * Se id for maior do que zero significa que o insert ocorreu com sucesso
     * @param categoria objeto
     * @return um long da inserção
     */
    public long salvar(Categoria categoria) {
        ContentValues values = new ContentValues();
        values.put(CategoriaEntidade.Coluna.DESCRICAO, categoria.getDsCategoria());
        values.put(CategoriaEntidade.Coluna.TIPO, categoria.getTpCategoria());
        values.put(CategoriaEntidade.Coluna.ATIVO, categoria.getStAtivo());
        values.put(CategoriaEntidade.Coluna.COR, categoria.getCdCor());
        db = conexao.getWritableDatabase();
        long id = db.insert(CategoriaEntidade.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     * Será utilizado também para desativar uma categoria
     * @param categoria objeto
     * @return quantidade de linhas afetadas
     */
    public int atualizar(Categoria categoria) {
        String where = CategoriaEntidade.Coluna.ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(categoria.getIdCategoria())};
        ContentValues values = new ContentValues();
        values.put(CategoriaEntidade.Coluna.DESCRICAO, categoria.getDsCategoria());
        values.put(CategoriaEntidade.Coluna.TIPO, categoria.getTpCategoria());
        values.put(CategoriaEntidade.Coluna.ATIVO, categoria.getStAtivo());
        values.put(CategoriaEntidade.Coluna.COR, categoria.getCdCor());
        values.put(CategoriaEntidade.Coluna.DATAALT, categoria.getDtAlterado());
        db = conexao.getWritableDatabase();
        int rows = db.update(CategoriaEntidade.TABLE_NAME, values, where, whereArgs);
        db.close();
        return rows;
    }

    /**
     * Lista todos os produtos cadastrados na categoria
     * @return
     */
    public List<Categoria> listarTudo() {
        List<Categoria> listaCategoria = new ArrayList<>();
        String[] aColunas = new String[]{
                CategoriaEntidade.Coluna.ID,
                CategoriaEntidade.Coluna.DESCRICAO,
                CategoriaEntidade.Coluna.TIPO,
                CategoriaEntidade.Coluna.COR,
                CategoriaEntidade.Coluna.ATIVO,
                CategoriaEntidade.Coluna.DATACAD,
                CategoriaEntidade.Coluna.DATAALT
        };

        db = conexao.getReadableDatabase();
        Cursor cursor = db.query(CategoriaEntidade.TABLE_NAME, aColunas, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Categoria obj = new Categoria();
            obj.setIdCategoria(cursor.getInt(cursor.getColumnIndex(aColunas[0])));
            obj.setDsCategoria(cursor.getString(cursor.getColumnIndex(aColunas[1])));
            obj.setTpCategoria(cursor.getString(cursor.getColumnIndex(aColunas[2])));
            obj.setCdCor(cursor.getString(cursor.getColumnIndex(aColunas[3])));
            obj.setStAtivo(cursor.getString(cursor.getColumnIndex(aColunas[4])));
            obj.setDtCadastro(cursor.getString(cursor.getColumnIndex(aColunas[5])));
            obj.setDtAlterado(cursor.getString(cursor.getColumnIndex(aColunas[6])));
            listaCategoria.add(obj);
        }
        db.close();
        cursor.close();
        return listaCategoria;
    }

    /**
     * retorna o total de categorias cadastradas
     * @return
     */
    public int getTotalCategoria() {
        return listarTudo().size();
    }

    /**
     * retorna a categoria se o id for igual
     * @param id tipo int
     * @return
     */
    public Categoria getCategoriaById(int id) {
        Categoria categoria = null;
        String sql = null;
        if (id > 0) {
            db = conexao.getReadableDatabase();
            sql = "select * from " + CategoriaEntidade.TABLE_NAME + " where " + CategoriaEntidade.Coluna.ID + " = " + id;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                categoria = new Categoria();
                categoria.setIdCategoria(cursor.getInt(cursor.getColumnIndex(CategoriaEntidade.Coluna.ID)));
                categoria.setDsCategoria(cursor.getString(cursor.getColumnIndex(CategoriaEntidade.Coluna.DESCRICAO)));
                categoria.setTpCategoria(cursor.getString(cursor.getColumnIndex(CategoriaEntidade.Coluna.TIPO)));
                categoria.setCdCor(cursor.getString(cursor.getColumnIndex(CategoriaEntidade.Coluna.COR)));
                categoria.setStAtivo(cursor.getString(cursor.getColumnIndex(CategoriaEntidade.Coluna.ATIVO)));
                categoria.setDtCadastro(cursor.getString(cursor.getColumnIndexOrThrow(CategoriaEntidade.Coluna.DATACAD)));
                categoria.setDtAlterado(cursor.getString(cursor.getColumnIndexOrThrow(CategoriaEntidade.Coluna.DATAALT)));
            }
            cursor.close();
            db.close();

        }
        return categoria;
    }

    /**
     * Tipo deve ser: R ou D
     * @param tipo
     * @return
     */
    public List<Categoria> getCategoriaByTipo(String tipo) {
        List<Categoria> lista = new ArrayList<>();
        Categoria categoria = null;
        String sql = null;
        if (tipo.equals("R") || tipo.equals("D")) {
            db = conexao.getReadableDatabase();
            sql = "select * from " + CategoriaEntidade.TABLE_NAME + " where " +
                    CategoriaEntidade.Coluna.TIPO + " = '" + tipo + "' and st_ativo = 'S' ";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                categoria = new Categoria();
                categoria.setIdCategoria(cursor.getInt(cursor.getColumnIndex(CategoriaEntidade.Coluna.ID)));
                categoria.setDsCategoria(cursor.getString(cursor.getColumnIndex(CategoriaEntidade.Coluna.DESCRICAO)));
                categoria.setTpCategoria(cursor.getString(cursor.getColumnIndex(CategoriaEntidade.Coluna.TIPO)));
                categoria.setCdCor(cursor.getString(cursor.getColumnIndex(CategoriaEntidade.Coluna.COR)));
                categoria.setStAtivo(cursor.getString(cursor.getColumnIndex(CategoriaEntidade.Coluna.ATIVO)));
                categoria.setDtCadastro(cursor.getString(cursor.getColumnIndexOrThrow(CategoriaEntidade.Coluna.DATACAD)));
                categoria.setDtAlterado(cursor.getString(cursor.getColumnIndexOrThrow(CategoriaEntidade.Coluna.DATAALT)));
                lista.add(categoria);
            }
            cursor.close();
            db.close();
        }
        return lista;
    }

    public int deleteCategoriaByID(int id) {
        db = conexao.getWritableDatabase();
        int result = db.delete(CategoriaEntidade.TABLE_NAME, " id_categoria = " + id, null);
        return result;
    }

}