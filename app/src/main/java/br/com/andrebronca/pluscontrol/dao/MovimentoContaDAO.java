package br.com.andrebronca.pluscontrol.dao;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.entidades.DBHelper;
import br.com.andrebronca.pluscontrol.entidades.MovimentoEntidade;
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.modelo.Titulo;

/**
 * Created by andrebronca on 08/10/16.
 */

public class MovimentoContaDAO {
    private static MovimentoContaDAO instance;
    private SQLiteDatabase db;
    private DBHelper conexao;
    private Context context;

    public static MovimentoContaDAO getInstance(Context context) {
        if (instance == null) {
            instance = new MovimentoContaDAO(context.getApplicationContext());
        }
        return instance;
    }

    private MovimentoContaDAO(Context context) {
        conexao = DBHelper.getInstance(context);
    }

    /**
     * Este método é específico para a direção de entrada. Título de recebimento
     *
     * @return
     */
    public void salvarMovimentoEntradaTransacao(String sqlMovimento, String sqlConta, String sqlTitulo, String sqlStMovi) {
        db = conexao.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmMovimento = db.compileStatement(sqlMovimento);
        SQLiteStatement stmConta = db.compileStatement(sqlConta);
        SQLiteStatement stmTitulo = db.compileStatement(sqlTitulo);
        SQLiteStatement stmStMov = db.compileStatement(sqlStMovi);
        stmMovimento.execute();
        stmConta.execute();
        stmTitulo.execute();
        stmStMov.execute();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Utilizado na importação do backup
     * @param mov
     */
    public void salvarMovimentoImportado(MovimentoConta mov) {
        db = conexao.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovimentoEntidade.Coluna.ID_CONTA, mov.getIdConta());
        values.put(MovimentoEntidade.Coluna.ID_TITULO, mov.getIdTitulo());
        values.put(MovimentoEntidade.Coluna.ID_PARCELA, mov.getIdParcela());
        values.put(MovimentoEntidade.Coluna.VALOR, mov.getVlMovimento());
        values.put(MovimentoEntidade.Coluna.DATA, mov.getDtMovimento());
        values.put(MovimentoEntidade.Coluna.DIRECAO, mov.getStDirecao());
        if (mov.getIdTransferencia() > 0) {
            values.put(MovimentoEntidade.Coluna.ID_TRANSF, mov.getIdTransferencia());
        }
        values.put(MovimentoEntidade.Coluna.HISTORICO, mov.getDsHistorico());
        values.put(MovimentoEntidade.Coluna.ESTORNO, mov.getStEstorno());
        values.put(MovimentoEntidade.Coluna.TIPO, mov.getStTipo());
        db.insert(MovimentoEntidade.TABLE_NAME, null, values);
        db.close();
    }

    public void salvarMovimentoTransferenciaTransacao(String sqlMovOrigem, String sqlSaldoOrigem,
                                                      String sqlSaldoDestino, String sqlMovDestino) {
        db = conexao.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmMovOrigem = db.compileStatement(sqlMovOrigem);
        SQLiteStatement stmSaldoOrigem = db.compileStatement(sqlSaldoOrigem);
        SQLiteStatement stmSaldoDestino = db.compileStatement(sqlSaldoDestino);
        SQLiteStatement stmMovDestino = db.compileStatement(sqlMovDestino);
        stmMovOrigem.execute();
        stmSaldoOrigem.execute();
        stmSaldoDestino.execute();
        stmMovDestino.execute();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Nesse caso não permitir a alteração se já teve movimento
     * @param movimento
     * @return
     */
    public int atualizar(MovimentoConta movimento) {
        String where = MovimentoEntidade.Coluna.ID + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(movimento.getIdMovConta())};

        ContentValues values = new ContentValues();
        values.put(MovimentoEntidade.Coluna.ID_CONTA, movimento.getConta().getIdConta());
        values.put(MovimentoEntidade.Coluna.ID_TITULO, movimento.getTitulo().getIdTitulo());
        values.put(MovimentoEntidade.Coluna.VALOR, movimento.getVlMovimento());
        values.put(MovimentoEntidade.Coluna.DATA, movimento.getDtMovimento());
        values.put(MovimentoEntidade.Coluna.DIRECAO, movimento.getStDirecao());
        values.put(MovimentoEntidade.Coluna.ID_TRANSF, movimento.getIdTransferencia());
        values.put(MovimentoEntidade.Coluna.HISTORICO, movimento.getDsHistorico());
        values.put(MovimentoEntidade.Coluna.ESTORNO, movimento.getStEstorno());

        db = conexao.getWritableDatabase();
        int rows = db.update(MovimentoEntidade.TABLE_NAME, values, where, whereArgs);
        db.close();
        return rows;
    }

    public List<MovimentoConta> listarTudo() {
        List<MovimentoConta> listaMovimentoConta = new ArrayList<>();
        String[] aColunas = new String[]{
                MovimentoEntidade.Coluna.ID,
                MovimentoEntidade.Coluna.ID_CONTA,
                MovimentoEntidade.Coluna.ID_TITULO,
                MovimentoEntidade.Coluna.VALOR,
                MovimentoEntidade.Coluna.DATA,
                MovimentoEntidade.Coluna.DIRECAO,
                MovimentoEntidade.Coluna.ID_TRANSF,
                MovimentoEntidade.Coluna.HISTORICO,
                MovimentoEntidade.Coluna.ESTORNO
        };

        db = conexao.getReadableDatabase();
        Cursor cursor = db.query(MovimentoEntidade.TABLE_NAME, aColunas, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            MovimentoConta movConta = new MovimentoConta();
            movConta.setIdMovConta(cursor.getInt(cursor.getColumnIndex(aColunas[0])));
            movConta.setIdConta(cursor.getInt(cursor.getColumnIndex(aColunas[1])));
            movConta.setIdTitulo(cursor.getInt(cursor.getColumnIndex(aColunas[2])));
            movConta.setVlMovimento(cursor.getDouble(cursor.getColumnIndex(aColunas[3])));
            movConta.setDtMovimento(cursor.getString(cursor.getColumnIndex(aColunas[4])));
            movConta.setStDirecao(cursor.getString(cursor.getColumnIndex(aColunas[5])));
            movConta.setIdTransferencia(cursor.getInt(cursor.getColumnIndex(aColunas[6])));
            movConta.setDsHistorico(cursor.getString(cursor.getColumnIndex(aColunas[7])));
            movConta.setStEstorno(cursor.getString(cursor.getColumnIndex(aColunas[8])));

            Conta conta = ContaDAO.getInstance(context).getContaById(movConta.getIdConta());
            movConta.setConta(conta);

            Titulo titulo = TituloDAO.getInstance(context).getTituloByID(movConta.getIdTitulo());
            movConta.setTitulo(titulo);

            listaMovimentoConta.add(movConta);
        }
        db.close();
        cursor.close();
        return listaMovimentoConta;
    }

    /**
     * Este método está sendo utilizado na tela de estorno de movimentos
     *
     * @param id
     * @return
     */
    public MovimentoConta getMovimentoByID(int id, Activity activity) {
        String[] aColunas = new String[]{
                MovimentoEntidade.Coluna.ID,
                MovimentoEntidade.Coluna.ID_CONTA,
                MovimentoEntidade.Coluna.ID_TITULO,
                MovimentoEntidade.Coluna.VALOR,
                MovimentoEntidade.Coluna.DATA,
                MovimentoEntidade.Coluna.DIRECAO,
                MovimentoEntidade.Coluna.ID_TRANSF,
                MovimentoEntidade.Coluna.HISTORICO,
                MovimentoEntidade.Coluna.ESTORNO,
                MovimentoEntidade.Coluna.TIPO,
                MovimentoEntidade.Coluna.ID_PARCELA
        };

        MovimentoConta movimentoConta = null;
        String sql = null;
        if (id > 0) {
            db = conexao.getReadableDatabase();
            sql = "select * from " + MovimentoEntidade.TABLE_NAME + " where " + MovimentoEntidade.Coluna.ID + " = " + id;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                movimentoConta = new MovimentoConta();
                movimentoConta.setIdMovConta(cursor.getInt(cursor.getColumnIndex(aColunas[0])));
                movimentoConta.setIdConta(cursor.getInt(cursor.getColumnIndex(aColunas[1])));
                movimentoConta.setIdTitulo(cursor.getInt(cursor.getColumnIndex(aColunas[2])));
                movimentoConta.setIdParcela(cursor.getInt(cursor.getColumnIndex(aColunas[10])));
                movimentoConta.setVlMovimento(cursor.getDouble(cursor.getColumnIndex(aColunas[3])));
                movimentoConta.setDtMovimento(cursor.getString(cursor.getColumnIndex(aColunas[4])));
                movimentoConta.setStDirecao(cursor.getString(cursor.getColumnIndex(aColunas[5])));
                //movimentoConta.setIdTransferencia( cursor.getInt(cursor.getColumnIndex(aColunas[6]))); //não utilizado para estorno
                movimentoConta.setDsHistorico(cursor.getString(cursor.getColumnIndex(aColunas[7])));
                movimentoConta.setStEstorno(cursor.getString(cursor.getColumnIndex(aColunas[8])));
                movimentoConta.setStTipo(cursor.getString(cursor.getColumnIndex(aColunas[9])));

                Conta conta = ContaDAO.getInstance(activity).getContaById(movimentoConta.getIdConta());
                movimentoConta.setConta(conta);

                Titulo titulo = TituloDAO.getInstance(activity).getTituloByID(
                        movimentoConta.getIdTitulo(), movimentoConta.getIdParcela());
                movimentoConta.setTitulo(titulo);
            }
            cursor.close();
            db.close();
        }
        return movimentoConta;
    }

    /**
     * Receber uma expressão sql e um selectionArgs[]
     * Objetivo é flexibilizar a consulta. Se quiser por natureza, por valor do título etc
     * Utilizar ? nas expressões do sql
     * @param selectionArgs array com os argumentos
     * @param sql
     * @return
     */
    public List<MovimentoConta> getMovimentoContaBySQL(String sql, String[] selectionArgs) {
        List<MovimentoConta> listaMovimentoConta = new ArrayList<>();
        String[] aColunas = new String[]{
                MovimentoEntidade.Coluna.ID,
                MovimentoEntidade.Coluna.ID_CONTA,
                MovimentoEntidade.Coluna.ID_TITULO,
                MovimentoEntidade.Coluna.ID_PARCELA,
                MovimentoEntidade.Coluna.VALOR,
                MovimentoEntidade.Coluna.DATA,
                MovimentoEntidade.Coluna.DIRECAO,
                MovimentoEntidade.Coluna.ID_TRANSF,
                MovimentoEntidade.Coluna.HISTORICO,
                MovimentoEntidade.Coluna.ESTORNO,
                MovimentoEntidade.Coluna.TIPO
        };
        MovimentoConta movConta = null;
        db = conexao.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            movConta = new MovimentoConta();
            movConta.setIdMovConta(cursor.getInt(cursor.getColumnIndex(aColunas[0])));
            movConta.setIdConta(cursor.getInt(cursor.getColumnIndex(aColunas[1])));
            movConta.setIdTitulo(cursor.getInt(cursor.getColumnIndex(aColunas[2])));
            movConta.setIdParcela(cursor.getInt(cursor.getColumnIndex(aColunas[3])));
            movConta.setVlMovimento(cursor.getDouble(cursor.getColumnIndex(aColunas[4])));
            movConta.setDtMovimento(cursor.getString(cursor.getColumnIndex(aColunas[5])));
            movConta.setStDirecao(cursor.getString(cursor.getColumnIndex(aColunas[6])));
            movConta.setIdTransferencia(cursor.getInt(cursor.getColumnIndex(aColunas[7])));
            movConta.setDsHistorico(cursor.getString(cursor.getColumnIndex(aColunas[8])));
            movConta.setStEstorno(cursor.getString(cursor.getColumnIndex(aColunas[9])));
            movConta.setStTipo(cursor.getString(cursor.getColumnIndex(aColunas[10])));

            Conta conta = ContaDAO.getInstance(context).getContaById(movConta.getIdConta());
            movConta.setConta(conta);

            Titulo titulo = TituloDAO.getInstance(context).getTituloByID(movConta.getIdTitulo(), movConta.getIdParcela());
            movConta.setTitulo(titulo);

            listaMovimentoConta.add(movConta);
        }
        cursor.close();
        db.close();

        return listaMovimentoConta;
    }

    /**
     * Se retornar Integer.MAX_VALUE significa que não tem registro inserido na tabela
     *
     * @return o máximo id da tabela conta
     */
    public int getMaxIDMovimentoConta() {
        int maxId = Integer.MAX_VALUE;
        db = conexao.getReadableDatabase();
        Cursor cursor = db.rawQuery("select max(id_movconta) as max from movimentoconta", null);
        while (cursor.moveToNext()) {
            maxId = cursor.getInt(0);
        }
        return maxId;
    }

    public int getQtdVinculoContaById(int id) {
        int qtd = 0;
        db = conexao.getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) as qt_registro ");
        sb.append(" from movimentoconta ");
        sb.append(" where id_conta = " + id);
        sb.append(" and st_estorno = 'N' ");
        Cursor cursor = db.rawQuery(sb.toString(), null);
        while (cursor.moveToNext()) {
            qtd = cursor.getInt(cursor.getColumnIndex("qt_registro"));
        }
        return qtd;
    }

    public boolean movimentoEstorno(List<String> sqlEstorno) {
        boolean sucesso = false;
        try {
            db = conexao.getWritableDatabase();
            db.beginTransactionNonExclusive();

            for (String sqlToStm : sqlEstorno) {
                SQLiteStatement stm = db.compileStatement(sqlToStm);
                stm.execute();
            }
            db.setTransactionSuccessful();
            sucesso = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.endTransaction();
        return sucesso;
    }

}