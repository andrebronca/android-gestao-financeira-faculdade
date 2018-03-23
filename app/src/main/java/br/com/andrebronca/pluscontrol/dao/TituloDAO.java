package br.com.andrebronca.pluscontrol.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.entidades.DBHelper;
import br.com.andrebronca.pluscontrol.entidades.TituloEntidade;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;
import br.com.andrebronca.pluscontrol.modelo.Titulo;

/**
 * Created by andrebronca on 04/10/16.
 */

public class TituloDAO {
    private static TituloDAO instance;
    private SQLiteDatabase db;
    private DBHelper conexao;
    private Context context;

    public static TituloDAO getInstance(Context context) {
        if (instance == null) {
            instance = new TituloDAO(context.getApplicationContext());
        }
        return instance;
    }

    private TituloDAO(Context context) {
        conexao = DBHelper.getInstance(context);
    }

    public long salvarTitulo(Titulo titulo) {
        ContentValues values = new ContentValues();
        values.put(TituloEntidade.Coluna.ID, titulo.getIdTitulo());
        values.put(TituloEntidade.Coluna.ID_PARCELA, titulo.getIdParcela());
        values.put(TituloEntidade.Coluna.DESCRICAO, titulo.getDsTitulo());
        values.put(TituloEntidade.Coluna.NATUREZA, titulo.getTpNatureza());
        values.put(TituloEntidade.Coluna.EMISSAO, titulo.getDtEmissao());
        values.put(TituloEntidade.Coluna.VENCIMENTO, titulo.getDtVencimento());
        values.put(TituloEntidade.Coluna.VALOR, titulo.getVlTitulo());
        values.put(TituloEntidade.Coluna.VALORPARCELA, titulo.getVlParcela());
        values.put(TituloEntidade.Coluna.SALDO, titulo.getVlSaldo());
        values.put(TituloEntidade.Coluna.BAIXA, titulo.getDtBaixa());
        values.put(TituloEntidade.Coluna.QTPARCELA, titulo.getQtParcela());
        values.put(TituloEntidade.Coluna.RELATORIO, titulo.getStRelatorio());
        values.put(TituloEntidade.Coluna.MOVIMENTO, titulo.getStMovimento());
        values.put(TituloEntidade.Coluna.DATACAD, titulo.getDtCadastro());
        values.put(TituloEntidade.Coluna.DATAALT, titulo.getDtAlterado());
        values.put(TituloEntidade.Coluna.ID_CATEGORIA, titulo.getCategoria().getIdCategoria());
        values.put(TituloEntidade.Coluna.ID_PESSOA, titulo.getPessoa().getIdPessoa());

        db = conexao.getWritableDatabase();
        long id = db.insert(TituloEntidade.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     * Utilizado na importação do backup
     *
     * @param titulo
     * @return
     */
    public long salvarTituloImportacao(Titulo titulo) {
        ContentValues values = new ContentValues();
        values.put(TituloEntidade.Coluna.ID, titulo.getIdTitulo());
        values.put(TituloEntidade.Coluna.ID_PARCELA, titulo.getIdParcela());
        values.put(TituloEntidade.Coluna.DESCRICAO, titulo.getDsTitulo());
        values.put(TituloEntidade.Coluna.NATUREZA, titulo.getTpNatureza());
        values.put(TituloEntidade.Coluna.EMISSAO, titulo.getDtEmissao());
        values.put(TituloEntidade.Coluna.VENCIMENTO, titulo.getDtVencimento());
        values.put(TituloEntidade.Coluna.VALOR, titulo.getVlTitulo());
        values.put(TituloEntidade.Coluna.VALORPARCELA, titulo.getVlParcela());
        values.put(TituloEntidade.Coluna.SALDO, titulo.getVlSaldo());
        values.put(TituloEntidade.Coluna.BAIXA, titulo.getDtBaixa());
        values.put(TituloEntidade.Coluna.QTPARCELA, titulo.getQtParcela());
        values.put(TituloEntidade.Coluna.RELATORIO, titulo.getStRelatorio());
        values.put(TituloEntidade.Coluna.MOVIMENTO, titulo.getStMovimento());
        values.put(TituloEntidade.Coluna.DATACAD, titulo.getDtCadastro());
        values.put(TituloEntidade.Coluna.DATAALT, titulo.getDtAlterado());
        values.put(TituloEntidade.Coluna.ID_CATEGORIA, titulo.getIdCategoria());
        values.put(TituloEntidade.Coluna.ID_PESSOA, titulo.getIdPessoa());

        db = conexao.getWritableDatabase();
        long id = db.insert(TituloEntidade.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     * Nesse caso não permitir a alteração se já teve movimento
     *
     * @param titulo
     * @return
     */
    public int atualizar(Titulo titulo) {
        String where = TituloEntidade.Coluna.ID + " = ? and " + TituloEntidade.Coluna.ID_PARCELA + " = ?";
        String[] whereArgs = new String[]{
                String.valueOf(titulo.getIdTitulo()),
                String.valueOf(titulo.getIdParcela())};
        ContentValues values = new ContentValues();
        values.put(TituloEntidade.Coluna.DESCRICAO, titulo.getDsTitulo());
        values.put(TituloEntidade.Coluna.NATUREZA, titulo.getTpNatureza());
        values.put(TituloEntidade.Coluna.EMISSAO, titulo.getDtEmissao());
        values.put(TituloEntidade.Coluna.VENCIMENTO, titulo.getDtVencimento());
        values.put(TituloEntidade.Coluna.VALOR, titulo.getVlTitulo());
        values.put(TituloEntidade.Coluna.VALORPARCELA, titulo.getVlParcela());
        values.put(TituloEntidade.Coluna.SALDO, titulo.getVlSaldo());
        values.put(TituloEntidade.Coluna.QTPARCELA, titulo.getQtParcela());
        values.put(TituloEntidade.Coluna.RELATORIO, titulo.getStRelatorio());
        values.put(TituloEntidade.Coluna.MOVIMENTO, titulo.getStMovimento());
        values.put(TituloEntidade.Coluna.DATAALT, titulo.getDtAlterado());
        values.put(TituloEntidade.Coluna.ID_CATEGORIA, titulo.getCategoria().getIdCategoria());
        values.put(TituloEntidade.Coluna.ID_PESSOA, titulo.getPessoa().getIdPessoa());

        db = conexao.getWritableDatabase();
        int rows = db.update(TituloEntidade.TABLE_NAME, values, where, whereArgs);
        db.close();
        return rows;
    }

    /**
     * Quando ocorrer um recebimento, esse método é chamado na transação
     *
     * @param titulo
     * @return
     */
    public int atualizarSaldoTransacao(Titulo titulo) {
        String where = TituloEntidade.Coluna.ID + " = ? and " + TituloEntidade.Coluna.ID_PARCELA + " = ?";
        String[] whereArgs = new String[]{String.valueOf(titulo.getIdTitulo()),
                String.valueOf(titulo.getIdParcela())};
        ContentValues values = new ContentValues();
//        values.put(TituloEntidade.Coluna.DESCRICAO, titulo.getDsTitulo());
//        values.put(TituloEntidade.Coluna.NATUREZA, titulo.getTpNatureza());
//        values.put(TituloEntidade.Coluna.EMISSAO, titulo.getDtEmissao());
//        values.put(TituloEntidade.Coluna.VENCIMENTO, titulo.getDtVencimento());
//        values.put(TituloEntidade.Coluna.VALOR, titulo.getVlTitulo());
        values.put(TituloEntidade.Coluna.SALDO, titulo.getVlSaldo());
//        values.put(TituloEntidade.Coluna.PARCELA, titulo.getNrParcela());
//        values.put(TituloEntidade.Coluna.RELATORIO, titulo.getStRelatorio());
//        values.put(TituloEntidade.Coluna.MOVIMENTO, titulo.getStMovimento());
        values.put(TituloEntidade.Coluna.DATAALT, titulo.getDtAlterado());
//        values.put(TituloEntidade.Coluna.ID_CATEGORIA, titulo.getCategoria().getIdCategoria());
//        values.put(TituloEntidade.Coluna.ID_PESSOA, titulo.getPessoa().getIdPessoa());

        db = conexao.getWritableDatabase();
        int rows = db.update(TituloEntidade.TABLE_NAME, values, where, whereArgs);
        db.close();
        return rows;
    }

    /**
     * com a chave composta a tabela perdeu a característica do autoincrement
     * Se retornar 0 significa que não tem registro inserido na tabela
     * maxId -1 é para sinalizar se o cursor alterou o valor.
     *
     * @return o máximo id
     */
    public int getMaxIDTitulo() {
        int maxId = -1;
        db = conexao.getReadableDatabase();
        Cursor cursor = db.rawQuery("select max(id_titulo) as max from titulo", null);
        while (cursor.moveToNext()) {
            maxId = cursor.getInt(0);
        }
        return maxId;
    }

    /**
     * retorna a quantidade de linhas removidas
     *
     * @param id
     */
    public int deleteTituloByID(int id) {
        db = conexao.getWritableDatabase();
        int result = db.delete(TituloEntidade.TABLE_NAME, " id_titulo = " + id, null);
        return result;
    }

    public List<Titulo> listarTudo() {
        List<Titulo> listaTitulo = new ArrayList<>();
        String[] aColunas = new String[]{
                TituloEntidade.Coluna.ID,
                TituloEntidade.Coluna.ID_PARCELA,
                TituloEntidade.Coluna.DESCRICAO,
                TituloEntidade.Coluna.NATUREZA,
                TituloEntidade.Coluna.EMISSAO,
                TituloEntidade.Coluna.VENCIMENTO,
                TituloEntidade.Coluna.VALOR,
                TituloEntidade.Coluna.SALDO,
                TituloEntidade.Coluna.BAIXA,
                TituloEntidade.Coluna.QTPARCELA,
                TituloEntidade.Coluna.RELATORIO,
                TituloEntidade.Coluna.MOVIMENTO,
                TituloEntidade.Coluna.DATACAD,
                TituloEntidade.Coluna.DATAALT,
                TituloEntidade.Coluna.ID_CATEGORIA,
                TituloEntidade.Coluna.ID_PESSOA
        };

        db = conexao.getReadableDatabase();
        Cursor cursor = db.query(TituloEntidade.TABLE_NAME, aColunas, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Titulo titulo = new Titulo();
            titulo.setIdTitulo(cursor.getInt(cursor.getColumnIndex(aColunas[0])));
            titulo.setIdParcela(cursor.getInt(cursor.getColumnIndex(aColunas[1])));
            titulo.setDsTitulo(cursor.getString(cursor.getColumnIndex(aColunas[2])));
            titulo.setTpNatureza(cursor.getString(cursor.getColumnIndex(aColunas[3])));
            titulo.setDtEmissao(cursor.getString(cursor.getColumnIndex(aColunas[4])));
            titulo.setDtVencimento(cursor.getString(cursor.getColumnIndex(aColunas[5])));
            titulo.setVlTitulo(cursor.getDouble(cursor.getColumnIndex(aColunas[6])));
            titulo.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(aColunas[7])));
            titulo.setDtBaixa(cursor.getString(cursor.getColumnIndex(aColunas[8])));
            titulo.setQtParcela(cursor.getInt(cursor.getColumnIndex(aColunas[9])));
            titulo.setStRelatorio(cursor.getString(cursor.getColumnIndex(aColunas[10])));
            titulo.setStMovimento(cursor.getString(cursor.getColumnIndex(aColunas[11])));
            titulo.setDtCadastro(cursor.getString(cursor.getColumnIndex(aColunas[12])));
            titulo.setDtAlterado(cursor.getString(cursor.getColumnIndex(aColunas[13])));
            titulo.setIdCategoria(cursor.getInt(cursor.getColumnIndex(aColunas[14])));
            titulo.setIdPessoa(cursor.getInt(cursor.getColumnIndex(aColunas[15])));

            Categoria categoria = CategoriaDAO.getInstance(context).getCategoriaById(titulo.getIdCategoria());
            titulo.setCategoria(categoria);

            Pessoa pessoa = PessoaDAO.getInstance(context, "TituloDAO_listarTudo()").getPessoaByID(titulo.getIdPessoa());
            titulo.setPessoa(pessoa);

            listaTitulo.add(titulo);
        }
        db.close();
        cursor.close();
        return listaTitulo;
    }

    /**
     * utilizado por hora no listview principal
     *
     * @return
     */
    public List<Titulo> listaTituloDescricao() {
        List<Titulo> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        Cursor cursor = db.query(TituloEntidade.TABLE_NAME,
                new String[]{TituloEntidade.Coluna.ID, TituloEntidade.Coluna.DESCRICAO},
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Titulo obj = new Titulo();
            obj.setIdTitulo(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.ID)));
            obj.setDsTitulo(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.DESCRICAO)));
            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }

    public List<Titulo> listaTituloReceitaDescricao() {
        List<Titulo> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        String sql = "select *, sum(vl_saldo) as vl_saldo from " + TituloEntidade.TABLE_NAME
                + " where tp_natureza = 'R'  and st_movimento = 'N' group by id_titulo"; //título que já tiver movimento não será listado
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Titulo obj = new Titulo();
            obj.setIdTitulo(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.ID)));
            obj.setIdParcela(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.ID_PARCELA)));
            obj.setDsTitulo(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.DESCRICAO)));
            obj.setTpNatureza(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.NATUREZA)));
            obj.setDtEmissao(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.EMISSAO)));
            obj.setDtVencimento(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.VENCIMENTO)));
            obj.setVlTitulo(cursor.getDouble(cursor.getColumnIndex(TituloEntidade.Coluna.VALOR)));
            obj.setVlParcela(cursor.getDouble(cursor.getColumnIndex(TituloEntidade.Coluna.VALORPARCELA)));
            obj.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(TituloEntidade.Coluna.SALDO)));
            obj.setDtBaixa(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.BAIXA)));
            obj.setQtParcela(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.QTPARCELA)));
            obj.setStRelatorio(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.RELATORIO)));
            obj.setStMovimento(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.MOVIMENTO)));
            obj.setDtCadastro(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.DATACAD)));
            obj.setDtAlterado(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.DATAALT)));
            obj.setIdCategoria(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.ID_CATEGORIA)));
            obj.setIdPessoa(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.ID_PESSOA)));
            obj.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(TituloEntidade.Coluna.SALDO)));

            Categoria categoria = CategoriaDAO.getInstance(context).getCategoriaById(obj.getIdCategoria());
            obj.setCategoria(categoria);

            Pessoa pessoa = PessoaDAO.getInstance(context, "TituloDAO_listarTudo()").getPessoaByID(obj.getIdPessoa());
            obj.setPessoa(pessoa);

            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }


    /**
     * Utilizado na tela principal para exibir um resumo dos vencimentos e da previsão para o próximo mês
     *
     * @param vencido
     * @param natureza
     * @return
     */
    public double getValorReceitaDespesaVencidoVencer(boolean vencido, String natureza) {
        double valor = 0.0;
        db = conexao.getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("select sum(vl_saldo) as vl_saldo ");
        sb.append(" from titulo ");
        sb.append(" where tp_natureza = '" + natureza + "' ");
        sb.append(" and vl_saldo > 0 ");
        if (vencido) {
//            sb.append(" and strftime('%m', dt_vencimento) <= strftime('%m', date('now'))");
            sb.append(" and dt_vencimento < date('now') ");
        } else {
//            sb.append("and strftime('%m', dt_vencimento) = strftime('%m', date('now','+1 month')) ");
//            sb.append("and strftime('%m', dt_vencimento) = strftime('%m', date('now','+30 days')) ");
            sb.append("and dt_vencimento <= date('now','+30 days') ");
        }
        sb.append(" order by dt_vencimento ");

        Cursor cursor = db.rawQuery(sb.toString(), null);
        while (cursor.moveToNext()) {
            valor = cursor.getDouble(cursor.getColumnIndex("vl_saldo"));
        }
        db.close();
        cursor.close();
        return valor;
    }

    public double getSaldoTotalContas() {
        double valor = 0.0;
        db = conexao.getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("select sum(vl_saldo) as vl_total from conta ");

        Cursor cursor = db.rawQuery(sb.toString(), null);
        while (cursor.moveToNext()) {
            valor = cursor.getDouble(cursor.getColumnIndex("vl_total"));
        }
        db.close();
        cursor.close();
        return valor;
    }

    public double getTotalDoPeriodo(String natureza) {
        double valor = 0.0;
        db = conexao.getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("select sum(vl_parcela) as vl_total from titulo ");
        sb.append(" where tp_natureza = '" + natureza + "' ");
        sb.append(" and dt_vencimento between date('now','start of month') ");
        sb.append(" and date('now','start of month','+1 month','-1 day') ");

        Cursor cursor = db.rawQuery(sb.toString(), null);
        while (cursor.moveToNext()) {
            valor = cursor.getDouble(cursor.getColumnIndex("vl_total"));
        }
        db.close();
        cursor.close();
        return valor;
    }
    /*
    select sum(vl_parcela) as vl_total
from titulo
where tp_natureza = 'R'
and dt_vencimento between date('now','start of month') and date('now','start of month','+1 month','-1 day')
     */

    public List<Titulo> listaTituloDespesaDescricao() {
        List<Titulo> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        String sql = "select *, sum(vl_saldo) as vl_saldo from " + TituloEntidade.TABLE_NAME
                + " where tp_natureza = 'D'  and st_movimento = 'N' group by id_titulo"; //título que já tiver movimento não será listado
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Titulo obj = new Titulo();
            obj.setIdTitulo(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.ID)));
            obj.setIdParcela(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.ID_PARCELA)));
            obj.setDsTitulo(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.DESCRICAO)));
            obj.setTpNatureza(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.NATUREZA)));
            obj.setDtEmissao(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.EMISSAO)));
            obj.setDtVencimento(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.VENCIMENTO)));
            obj.setVlTitulo(cursor.getDouble(cursor.getColumnIndex(TituloEntidade.Coluna.VALOR)));
            obj.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(TituloEntidade.Coluna.SALDO)));
            obj.setDtBaixa(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.BAIXA)));
            obj.setQtParcela(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.QTPARCELA)));
            obj.setStRelatorio(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.RELATORIO)));
            obj.setStMovimento(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.MOVIMENTO)));
            obj.setDtCadastro(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.DATACAD)));
            obj.setDtAlterado(cursor.getString(cursor.getColumnIndex(TituloEntidade.Coluna.DATAALT)));
            obj.setIdCategoria(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.ID_CATEGORIA)));
            obj.setIdPessoa(cursor.getInt(cursor.getColumnIndex(TituloEntidade.Coluna.ID_PESSOA)));
            obj.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(TituloEntidade.Coluna.SALDO)));
            Categoria categoria = CategoriaDAO.getInstance(context).getCategoriaById(obj.getIdCategoria());
            obj.setCategoria(categoria);

            Pessoa pessoa = PessoaDAO.getInstance(context, "TituloDAO_listarTudo()").getPessoaByID(obj.getIdPessoa());
            obj.setPessoa(pessoa);
            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }

//    /**
//     * Com os valores é possível saber se o título pode ser editado, caso Valor e Saldo são iguais.
//     * @param id
//     * @return
//     *
//     */
//    public List<Double> getValorSaldoTituloByID(int id, int idp){
//        double valor, saldo;
//        List<Double> listaValores = new ArrayList<>();
//        db = conexao.getReadableDatabase();
//        String sql = "select vl_titulo, vl_saldo from "+ TituloEntidade.TABLE_NAME +" where "+ TituloEntidade.Coluna.ID +" = "+ id;
//        Cursor cursor = db.rawQuery(sql, null);
//        while (cursor.moveToNext()){
//            valor = cursor.getDouble(cursor.getColumnIndex(TituloEntidade.Coluna.VALOR));
//            saldo = cursor.getDouble(cursor.getColumnIndex(TituloEntidade.Coluna.SALDO));
//            listaValores.add(0,(double) valor);
//            listaValores.add(1,(double) saldo);
//        }
//        db.close();
//        cursor.close();
//        return listaValores;
//    }

//    /**
//     * Se ocorrer erro, retorna o valor máximo do double
//     * @param idTitulo
//     * @return
//     */
//    public double getValorSaldoTitulo(int idTitulo){
//        double saldo = Double.MAX_VALUE;
//        db = conexao.getReadableDatabase();
//        Cursor cursor = db.rawQuery("select sum(vl_saldo) as vl_saldo from titulo where id_titulo = ?",new String[]{String.valueOf(idTitulo)});
//        while (cursor.moveToNext()){
//            saldo = cursor.getDouble(0);
//        }
//        return saldo;
//    }


    /**
     * Não faz filtro, simplesmente recupera tudo que há na tabela de título
     *
     * @return
     */
    public int getTotalTitulo(String flag) {
        return listarTudo().size();
    }

    public Titulo getTituloByID(int idTitulo, int idParcela) {
        String[] aColunas = new String[]{
                TituloEntidade.Coluna.ID,
                TituloEntidade.Coluna.ID_PARCELA,
                TituloEntidade.Coluna.DESCRICAO,
                TituloEntidade.Coluna.NATUREZA,
                TituloEntidade.Coluna.EMISSAO,
                TituloEntidade.Coluna.VENCIMENTO,
                TituloEntidade.Coluna.VALOR,
                TituloEntidade.Coluna.SALDO,
                TituloEntidade.Coluna.BAIXA,
                TituloEntidade.Coluna.QTPARCELA,
                TituloEntidade.Coluna.RELATORIO,
                TituloEntidade.Coluna.MOVIMENTO,
                TituloEntidade.Coluna.DATACAD,
                TituloEntidade.Coluna.DATAALT,
                TituloEntidade.Coluna.ID_CATEGORIA,
                TituloEntidade.Coluna.ID_PESSOA,
                TituloEntidade.Coluna.MOVIMENTO
        };
        Titulo titulo = null;
        String sql = null;
        if ((idTitulo > 0) && (idParcela > 0)) {
            db = conexao.getReadableDatabase();
            sql = "select * from " + TituloEntidade.TABLE_NAME + " where " + TituloEntidade.Coluna.ID + " = " + idTitulo + " and id_parcela = " + idParcela;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                titulo = new Titulo();
                titulo.setIdTitulo(cursor.getInt(cursor.getColumnIndex(aColunas[0])));
                titulo.setIdParcela(cursor.getInt(cursor.getColumnIndex(aColunas[1])));
                titulo.setDsTitulo(cursor.getString(cursor.getColumnIndex(aColunas[2])));
                titulo.setTpNatureza(cursor.getString(cursor.getColumnIndex(aColunas[3])));
                titulo.setDtEmissao(cursor.getString(cursor.getColumnIndex(aColunas[4])));
                titulo.setDtVencimento(cursor.getString(cursor.getColumnIndex(aColunas[5])));
                titulo.setVlTitulo(cursor.getDouble(cursor.getColumnIndex(aColunas[6])));
                titulo.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(aColunas[7])));
                titulo.setDtBaixa(cursor.getString(cursor.getColumnIndex(aColunas[8])));
                titulo.setQtParcela(cursor.getInt(cursor.getColumnIndex(aColunas[9])));
                titulo.setStRelatorio(cursor.getString(cursor.getColumnIndex(aColunas[10])));
                titulo.setStMovimento(cursor.getString(cursor.getColumnIndex(aColunas[11])));
                titulo.setDtCadastro(cursor.getString(cursor.getColumnIndex(aColunas[12])));
                titulo.setDtAlterado(cursor.getString(cursor.getColumnIndex(aColunas[13])));
                titulo.setIdCategoria(cursor.getInt(cursor.getColumnIndex(aColunas[14])));
                titulo.setIdPessoa(cursor.getInt(cursor.getColumnIndex(aColunas[15])));
                titulo.setStMovimento(cursor.getString(cursor.getColumnIndex(aColunas[16])));

                Categoria categoria = CategoriaDAO.getInstance(context).getCategoriaById(titulo.getIdCategoria());
                titulo.setCategoria(categoria);

                Pessoa pessoa = PessoaDAO.getInstance(context, "TituloDAO_getTituloById()").getPessoaByID(titulo.getIdPessoa());
                titulo.setPessoa(pessoa);
            }
            cursor.close();
            db.close();

        }
        return titulo;
    }

    public Titulo getTituloByID(int idTitulo) {
        String[] aColunas = new String[]{
                TituloEntidade.Coluna.ID,
                TituloEntidade.Coluna.ID_PARCELA,
                TituloEntidade.Coluna.DESCRICAO,
                TituloEntidade.Coluna.NATUREZA,
                TituloEntidade.Coluna.EMISSAO,
                TituloEntidade.Coluna.VENCIMENTO,
                TituloEntidade.Coluna.VALOR,
                TituloEntidade.Coluna.SALDO,
                TituloEntidade.Coluna.BAIXA,
                TituloEntidade.Coluna.QTPARCELA,
                TituloEntidade.Coluna.RELATORIO,
                TituloEntidade.Coluna.DATACAD,
                TituloEntidade.Coluna.DATAALT,
                TituloEntidade.Coluna.ID_CATEGORIA,
                TituloEntidade.Coluna.ID_PESSOA,
                TituloEntidade.Coluna.MOVIMENTO
        };
        Titulo titulo = null;
        String sql = null;
        if (idTitulo > 0) {
            db = conexao.getReadableDatabase();
            sql = "select * from " + TituloEntidade.TABLE_NAME + " where " + TituloEntidade.Coluna.ID + " = " + idTitulo;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                titulo = new Titulo();
                titulo.setIdTitulo(cursor.getInt(cursor.getColumnIndex(aColunas[0])));
                titulo.setIdParcela(cursor.getInt(cursor.getColumnIndex(aColunas[1])));
                titulo.setDsTitulo(cursor.getString(cursor.getColumnIndex(aColunas[2])));
                titulo.setTpNatureza(cursor.getString(cursor.getColumnIndex(aColunas[3])));
                titulo.setDtEmissao(cursor.getString(cursor.getColumnIndex(aColunas[4])));
                titulo.setDtVencimento(cursor.getString(cursor.getColumnIndex(aColunas[5])));
                titulo.setVlTitulo(cursor.getDouble(cursor.getColumnIndex(aColunas[6])));
                titulo.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(aColunas[7])));
                titulo.setDtBaixa(cursor.getString(cursor.getColumnIndex(aColunas[8])));
                titulo.setQtParcela(cursor.getInt(cursor.getColumnIndex(aColunas[9])));
                titulo.setStRelatorio(cursor.getString(cursor.getColumnIndex(aColunas[10])));
                titulo.setStMovimento(cursor.getString(cursor.getColumnIndex(aColunas[15])));
                titulo.setDtCadastro(cursor.getString(cursor.getColumnIndex(aColunas[11])));
                titulo.setDtAlterado(cursor.getString(cursor.getColumnIndex(aColunas[12])));
                titulo.setIdCategoria(cursor.getInt(cursor.getColumnIndex(aColunas[13])));
                titulo.setIdPessoa(cursor.getInt(cursor.getColumnIndex(aColunas[14])));

                Categoria categoria = CategoriaDAO.getInstance(context).getCategoriaById(titulo.getIdCategoria());
                titulo.setCategoria(categoria);

                Pessoa pessoa = PessoaDAO.getInstance(context, "TituloDAO_getTituloById()").getPessoaByID(titulo.getIdPessoa());
                titulo.setPessoa(pessoa);
            }
            cursor.close();
            db.close();

        }
        return titulo;
    }

    /**
     * Receber uma expressão sql e um selectionArgs[]
     * Objetivo é flexibilizar a consulta. Se quiser por natureza, por valor do título etc
     * Utilizar ? nas expressões do sql
     *
     * @param selectionArgs array com os argumentos
     * @param sql
     * @return
     */
    public List<Titulo> getTituloBySQL(String sql, String[] selectionArgs) {
        List<Titulo> listaTitulo = new ArrayList<>();
        String[] aColunas = new String[]{
                TituloEntidade.Coluna.ID,
                TituloEntidade.Coluna.ID_PARCELA,
                TituloEntidade.Coluna.DESCRICAO,
                TituloEntidade.Coluna.NATUREZA,
                TituloEntidade.Coluna.EMISSAO,
                TituloEntidade.Coluna.VENCIMENTO,
                TituloEntidade.Coluna.VALOR,
                TituloEntidade.Coluna.SALDO,
                TituloEntidade.Coluna.BAIXA,
                TituloEntidade.Coluna.QTPARCELA,
                TituloEntidade.Coluna.RELATORIO,
                TituloEntidade.Coluna.DATACAD,
                TituloEntidade.Coluna.DATAALT,
                TituloEntidade.Coluna.ID_CATEGORIA,
                TituloEntidade.Coluna.ID_PESSOA,
                TituloEntidade.Coluna.MOVIMENTO
        };
        Titulo titulo = null;
        db = conexao.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            titulo = new Titulo();
            titulo.setIdTitulo(cursor.getInt(cursor.getColumnIndex(aColunas[0])));
            titulo.setIdParcela(cursor.getInt(cursor.getColumnIndex(aColunas[1])));
            titulo.setDsTitulo(cursor.getString(cursor.getColumnIndex(aColunas[2])));
            titulo.setTpNatureza(cursor.getString(cursor.getColumnIndex(aColunas[3])));
            titulo.setDtEmissao(cursor.getString(cursor.getColumnIndex(aColunas[4])));
            titulo.setDtVencimento(cursor.getString(cursor.getColumnIndex(aColunas[5])));
            titulo.setVlTitulo(cursor.getDouble(cursor.getColumnIndex(aColunas[6])));
            titulo.setVlSaldo(cursor.getDouble(cursor.getColumnIndex(aColunas[7])));
            titulo.setDtBaixa(cursor.getString(cursor.getColumnIndex(aColunas[8])));
            titulo.setQtParcela(cursor.getInt(cursor.getColumnIndex(aColunas[9])));
            titulo.setStRelatorio(cursor.getString(cursor.getColumnIndex(aColunas[10])));
            titulo.setStMovimento(cursor.getString(cursor.getColumnIndex(aColunas[15])));
            titulo.setDtCadastro(cursor.getString(cursor.getColumnIndex(aColunas[11])));
            titulo.setDtAlterado(cursor.getString(cursor.getColumnIndex(aColunas[12])));
            titulo.setIdCategoria(cursor.getInt(cursor.getColumnIndex(aColunas[13])));
            titulo.setIdPessoa(cursor.getInt(cursor.getColumnIndex(aColunas[14])));

            Categoria categoria = CategoriaDAO.getInstance(context).getCategoriaById(titulo.getIdCategoria());
            titulo.setCategoria(categoria);

            Pessoa pessoa = PessoaDAO.getInstance(context, "TituloDAO_getTituloBySQL()").getPessoaByID(titulo.getIdPessoa());
            titulo.setPessoa(pessoa);
            listaTitulo.add(titulo);
        }
        cursor.close();
        db.close();

        return listaTitulo;
    }


    public void atualizarTituloBySql(String sqlUpdateTitulo) {
        db = conexao.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmUpdateTitulo = db.compileStatement(sqlUpdateTitulo);
        stmUpdateTitulo.execute();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public int getQtdVinculoContaById(int id) {
        int qtd = 0;
        db = conexao.getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) as qt_registro ");
        sb.append(" from titulo ");
        sb.append(" where id_categoria = " + id);
        Cursor cursor = db.rawQuery(sb.toString(), null);
        while (cursor.moveToNext()) {
            qtd = cursor.getInt(cursor.getColumnIndex("qt_registro"));
        }
        return qtd;
    }

    public int getQtdVinculoPessoaById(int id) {
        int qtd = 0;
        db = conexao.getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) as qt_registro ");
        sb.append(" from titulo ");
        sb.append(" where id_pessoa = " + id);
        Cursor cursor = db.rawQuery(sb.toString(), null);
        while (cursor.moveToNext()) {
            qtd = cursor.getInt(cursor.getColumnIndex("qt_registro"));
        }
        return qtd;
    }
}