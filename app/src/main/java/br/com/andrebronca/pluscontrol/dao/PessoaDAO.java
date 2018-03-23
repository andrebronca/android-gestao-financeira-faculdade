package br.com.andrebronca.pluscontrol.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.entidades.DBHelper;
import br.com.andrebronca.pluscontrol.entidades.PessoaEntidade;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;

/**
 * Created by andrebronca on 04/10/16.
 */

public class PessoaDAO {
    private static PessoaDAO instance;
    private SQLiteDatabase db;
    private DBHelper conexao;
    private Context context;

    /**
     * Flag foi adicionado para auxiliar no debug
     * @param context
     * @param flag ajuda a identificar qual Activity ou Classe está requisitando a instancia.
     * @return
     */
    public static PessoaDAO getInstance(Context context, String flag) {
        if (instance == null) {
            instance = new PessoaDAO(context.getApplicationContext());
        }
        return instance;
    }

    private PessoaDAO(Context context) {
        conexao = DBHelper.getInstance(context);
    }

    public long salvar(Pessoa pessoa) {
        ContentValues values = new ContentValues();
        values.put(PessoaEntidade.Coluna.NOME, pessoa.getNmPessoa());
        values.put(PessoaEntidade.Coluna.TIPO, pessoa.getTpPessoa());
        values.put(PessoaEntidade.Coluna.EMAIL, pessoa.getDsEmail());
        values.put(PessoaEntidade.Coluna.ATIVO, pessoa.getStAtivo());
        db = conexao.getWritableDatabase();
        long id = db.insert(PessoaEntidade.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public int atualizar(Pessoa pessoa){
        String where = PessoaEntidade.Coluna.ID +" = ?";
        String[] whereArgs = new String[] {String.valueOf( pessoa.getIdPessoa() )};
        ContentValues values = new ContentValues();
        values.put(PessoaEntidade.Coluna.NOME, pessoa.getNmPessoa());
        values.put(PessoaEntidade.Coluna.TIPO, pessoa.getTpPessoa());
        values.put(PessoaEntidade.Coluna.EMAIL, pessoa.getDsEmail());
        values.put(PessoaEntidade.Coluna.ATIVO, pessoa.getStAtivo());
        values.put(PessoaEntidade.Coluna.DATAALT, pessoa.getDtAlterado());
        db = conexao.getWritableDatabase();
        int rows = db.update(PessoaEntidade.TABLE_NAME, values, where, whereArgs);
        db.close();
        return rows;
    }

    public List<Pessoa> listarTudo(String tipo){
        List<Pessoa> listaPessoa = new ArrayList<>();
        String[] aColunas = new String[]{
                PessoaEntidade.Coluna.ID,
                PessoaEntidade.Coluna.NOME,
                PessoaEntidade.Coluna.TIPO,
                PessoaEntidade.Coluna.EMAIL,
                PessoaEntidade.Coluna.ATIVO,
                PessoaEntidade.Coluna.DATACAD,
                PessoaEntidade.Coluna.DATAALT
        };

        String where = null;
        if (tipo != null){
            if (tipo.equals("F")) {
                where = " tp_pessoa != 'C'";    //F & A
            }
            else if (tipo.equals("C")){
                where = " tp_pessoa != 'F'";    //C & A
            }
        }
        db = conexao.getReadableDatabase();
        Cursor cursor = db.query(PessoaEntidade.TABLE_NAME, aColunas, where, null, null, null, null, null);

        while(cursor.moveToNext()){
            Pessoa obj = new Pessoa();
            obj.setIdPessoa( cursor.getInt( cursor.getColumnIndex( aColunas[0] )));     //aColunas[0] foi só uma outra forma testada
            obj.setNmPessoa( cursor.getString( cursor.getColumnIndex( aColunas[1])));
            obj.setTpPessoa( cursor.getString( cursor.getColumnIndex(aColunas[2])));
            obj.setDsEmail( cursor.getString( cursor.getColumnIndex(aColunas[3])));
            obj.setStAtivo( cursor.getString( cursor.getColumnIndex(aColunas[4])));
            obj.setDtCadastro( cursor.getString( cursor.getColumnIndex(aColunas[5])));
            obj.setDtAlterado( cursor.getString( cursor.getColumnIndex(aColunas[6])));
//            if (obj.getTpPessoa().equals(tipo)) {
                listaPessoa.add(obj);
//            }
        }
        db.close();
        cursor.close();
        return  listaPessoa;
    }

    public List<Pessoa> listaPessoaNome(){
        List<Pessoa> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        Cursor cursor = db.query(PessoaEntidade.TABLE_NAME,
                new String[]{PessoaEntidade.Coluna.ID, PessoaEntidade.Coluna.NOME},
                null, null, null, null, null, null);
        while (cursor.moveToNext()){
            Pessoa obj = new Pessoa();
            obj.setIdPessoa( cursor.getInt( cursor.getColumnIndex( PessoaEntidade.Coluna.ID )));
            obj.setNmPessoa( cursor.getString( cursor.getColumnIndex( PessoaEntidade.Coluna.NOME)));
            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }

    /**
     * Lista tipo de pessoa: Cliente & Ambos
     * Aqui lista tanto ativos quanto inativos
     * utilizado na listagem de clientes
     * @return
     */
    public List<Pessoa> listaPessoaNomeCliente(){
        List<Pessoa> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        String sql = "select * from "+ PessoaEntidade.TABLE_NAME +" where tp_pessoa = 'C' or tp_pessoa = 'A'";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            Pessoa obj = new Pessoa();
            obj.setIdPessoa( cursor.getInt( cursor.getColumnIndex( PessoaEntidade.Coluna.ID )));
            obj.setNmPessoa( cursor.getString( cursor.getColumnIndex( PessoaEntidade.Coluna.NOME)));
            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }
    /**
     * Lista tipo de pessoa: Fornecedor & Ambos
     * Aqui lista tanto ativos quanto inativos
     * utilizado na listagem de fornecedores
     * @return
     */
    public List<Pessoa> listaPessoaNomeFornecedor(){
        List<Pessoa> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        String sql = "select * from "+ PessoaEntidade.TABLE_NAME +" where tp_pessoa = 'F' or tp_pessoa = 'A'";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            Pessoa obj = new Pessoa();
            obj.setIdPessoa( cursor.getInt( cursor.getColumnIndex( PessoaEntidade.Coluna.ID )));
            obj.setNmPessoa( cursor.getString( cursor.getColumnIndex( PessoaEntidade.Coluna.NOME)));
            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }

    public int getTotalPessoa(){
        return listarTudo(null).size();
    }

    public Pessoa getPessoaByID(int id){
        Pessoa pessoa = null;
        String sql = null;
        if (id > 0){
            db = conexao.getReadableDatabase();
            sql = "select * from "+ PessoaEntidade.TABLE_NAME +" where "+ PessoaEntidade.Coluna.ID +" = "+ id;
            Cursor cursor = db.rawQuery(sql,null);
            while (cursor.moveToNext()){
                pessoa = new Pessoa();
                pessoa.setIdPessoa( cursor.getInt( cursor.getColumnIndex(PessoaEntidade.Coluna.ID)));
                pessoa.setNmPessoa( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.NOME)));
                pessoa.setTpPessoa( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.TIPO)));
                pessoa.setDsEmail( cursor.getString(cursor.getColumnIndex(PessoaEntidade.Coluna.EMAIL)));
                pessoa.setStAtivo( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.ATIVO)));
                pessoa.setDtCadastro( cursor.getString( cursor.getColumnIndexOrThrow(PessoaEntidade.Coluna.DATACAD)));
                pessoa.setDtAlterado(cursor.getString(cursor.getColumnIndexOrThrow(PessoaEntidade.Coluna.DATAALT)));
            }
            cursor.close();
            db.close();

        }
        return pessoa;
    }

//    /**
//     * @param tp
//     * @return
//     */
//    public Pessoa getListaPessoaPorTipo(String tp){
//        Pessoa pessoa = null;
//        String sql = null;
//        if (tp.trim().length() > 0){
//            db = conexao.getReadableDatabase();
//            sql = "select * from "+ PessoaEntidade.TABLE_NAME +" where "+ PessoaEntidade.Coluna.TIPO +" = '"+ tp +"'";
//            Cursor cursor = db.rawQuery(sql, null);
//            while(cursor.moveToNext()){
//                pessoa = new Pessoa();
//                pessoa.setIdPessoa( cursor.getInt( cursor.getColumnIndex(PessoaEntidade.Coluna.ID)));
//                pessoa.setNmPessoa( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.NOME)));
//                pessoa.setTpPessoa( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.TIPO)));
//                pessoa.setDsEmail( cursor.getString(cursor.getColumnIndex(PessoaEntidade.Coluna.EMAIL)));
//                pessoa.setStAtivo( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.ATIVO)));
//                pessoa.setDtCadastro( cursor.getString( cursor.getColumnIndexOrThrow(PessoaEntidade.Coluna.DATACAD)));
//                pessoa.setDtAlterado(cursor.getString(cursor.getColumnIndexOrThrow(PessoaEntidade.Coluna.DATAALT)));
//            }
//            cursor.close();
//            db.close();
//        }
//        return pessoa;
//    }

    /**
     * @return
     */
    public List<Pessoa> getListaPessoaClienteOrAmbos(){
        List<Pessoa> lista = new ArrayList<>();
        Pessoa pessoa = null;
        String sql = null;
        db = conexao.getReadableDatabase();
        sql = "select * from "+ PessoaEntidade.TABLE_NAME +" where "+ PessoaEntidade.Coluna.TIPO +" = 'A' or "+
                PessoaEntidade.Coluna.TIPO +" = 'C'";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            pessoa = new Pessoa();
            pessoa.setIdPessoa( cursor.getInt( cursor.getColumnIndex(PessoaEntidade.Coluna.ID)));
            pessoa.setNmPessoa( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.NOME)));
            pessoa.setTpPessoa( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.TIPO)));
            pessoa.setDsEmail( cursor.getString(cursor.getColumnIndex(PessoaEntidade.Coluna.EMAIL)));
            pessoa.setStAtivo( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.ATIVO)));
            pessoa.setDtCadastro( cursor.getString( cursor.getColumnIndexOrThrow(PessoaEntidade.Coluna.DATACAD)));
            pessoa.setDtAlterado(cursor.getString(cursor.getColumnIndexOrThrow(PessoaEntidade.Coluna.DATAALT)));
            lista.add(pessoa);
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * @return
     */
    public List<Pessoa> getListaPessoaFornecedorOrAmbos(){
        List<Pessoa> lista = new ArrayList<>();
        Pessoa pessoa = null;
        String sql = null;
        db = conexao.getReadableDatabase();
        sql = "select * from "+ PessoaEntidade.TABLE_NAME +" where "+ PessoaEntidade.Coluna.TIPO +" = 'A' or "+
                PessoaEntidade.Coluna.TIPO +" = 'F' and st_ativo = 'S' ";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            pessoa = new Pessoa();
            pessoa.setIdPessoa( cursor.getInt( cursor.getColumnIndex(PessoaEntidade.Coluna.ID)));
            pessoa.setNmPessoa( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.NOME)));
            pessoa.setTpPessoa( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.TIPO)));
            pessoa.setDsEmail( cursor.getString(cursor.getColumnIndex(PessoaEntidade.Coluna.EMAIL)));
            pessoa.setStAtivo( cursor.getString( cursor.getColumnIndex(PessoaEntidade.Coluna.ATIVO)));
            pessoa.setDtCadastro( cursor.getString( cursor.getColumnIndexOrThrow(PessoaEntidade.Coluna.DATACAD)));
            pessoa.setDtAlterado(cursor.getString(cursor.getColumnIndexOrThrow(PessoaEntidade.Coluna.DATAALT)));
            lista.add(pessoa);
        }
        cursor.close();
        db.close();
        return lista;
    }

    public int deletePessoaByID(int id) {
        db = conexao.getWritableDatabase();
        int result = db.delete(PessoaEntidade.TABLE_NAME, " id_pessoa = " + id, null);
        return result;
    }

}