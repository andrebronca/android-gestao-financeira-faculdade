package br.com.andrebronca.pluscontrol.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.entidades.DBHelper;
import br.com.andrebronca.pluscontrol.modelo.RelatorioContasPagarReceberMod;
import br.com.andrebronca.pluscontrol.modelo.RelatorioDespCategoria;
import br.com.andrebronca.pluscontrol.modelo.RelatorioFluxoCaixaMod;

/**
 * Created by andrebronca on 20/10/16.
 */

public class RelatorioDAO {
    private static RelatorioDAO instance;
    private SQLiteDatabase db;
    private DBHelper conexao;
    private Context context;

    public static RelatorioDAO getInstance(Context context) {
        if (instance == null) {
            instance = new RelatorioDAO(context.getApplicationContext());
        }
        return instance;
    }

    private RelatorioDAO(Context context) {
        conexao = DBHelper.getInstance(context);
    }

    /**
     * Utilizado no relatório de despesas por categoria.
     */
    public List<RelatorioDespCategoria> getTituloRelatorioDespCatBySQL(String sql, String[] selectionArgs) {
        List<RelatorioDespCategoria> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            RelatorioDespCategoria rel = new RelatorioDespCategoria();
            rel.setRelDsCategoria(cursor.getString(cursor.getColumnIndex("ds_categoria")));
            rel.setRelCdCor(cursor.getString(cursor.getColumnIndex("cd_cor")));
            rel.setRelVlTotal(cursor.getDouble(cursor.getColumnIndex("vl_total")));
            lista.add(rel);
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Utilizado no relatório de contas a pagar
     */
    public List<RelatorioContasPagarReceberMod> getTituloContasPagarReceberBySQL(String sql, String[] selectionArgs) {
        List<RelatorioContasPagarReceberMod> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            RelatorioContasPagarReceberMod rel = new RelatorioContasPagarReceberMod();
            rel.setIdtitulo(cursor.getInt(cursor.getColumnIndex("id_titulo")));
            rel.setIdParcela(cursor.getInt(cursor.getColumnIndex("id_parcela")));
            rel.setDsTitulo(cursor.getString(cursor.getColumnIndex("ds_titulo")));
            rel.setDtVencimento(cursor.getString(cursor.getColumnIndex("dt_vencimento")));
            rel.setVlSaldo(cursor.getDouble(cursor.getColumnIndex("vl_saldo")));
            rel.setDsCategoria(cursor.getString(cursor.getColumnIndex("ds_categoria")));
            rel.setNmPessoa(cursor.getString(cursor.getColumnIndex("nm_pessoa")));
            lista.add(rel);
        }
        cursor.close();
        db.close();
        return lista;
    }

    /**
     * Utilizado no relatório de fluxo de caixa
     */
    public List<RelatorioFluxoCaixaMod> getMovimentoFluxoBySQL(String sql, String[] selectionArgs) {
        List<RelatorioFluxoCaixaMod> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            RelatorioFluxoCaixaMod rel = new RelatorioFluxoCaixaMod();
            rel.setDtMovimento(cursor.getString(cursor.getColumnIndex("dt_movimento")));
            rel.setStTipo(cursor.getString(cursor.getColumnIndex("st_tipo")));
            rel.setDsHistorico(cursor.getString(cursor.getColumnIndex("ds_historico")));
            rel.setVlMovimento(cursor.getDouble(cursor.getColumnIndex("vl_movimento")));
            rel.setStDirecao(cursor.getString(cursor.getColumnIndex("st_direcao")));
            lista.add(rel);
        }
        cursor.close();
        db.close();
        return lista;
    }

    public String getSqlDataEmissVencBySql(String coluna, String sql) {
        String data = null;
        db = conexao.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            data = cursor.getString(cursor.getColumnIndex(coluna));
        }
        return data;
    }
}