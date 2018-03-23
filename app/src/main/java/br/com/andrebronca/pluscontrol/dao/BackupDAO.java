package br.com.andrebronca.pluscontrol.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.entidades.CategoriaEntidade;
import br.com.andrebronca.pluscontrol.entidades.ContaEntidade;
import br.com.andrebronca.pluscontrol.entidades.DBHelper;
import br.com.andrebronca.pluscontrol.entidades.MovimentoEntidade;
import br.com.andrebronca.pluscontrol.entidades.PessoaEntidade;
import br.com.andrebronca.pluscontrol.entidades.TituloEntidade;
import br.com.andrebronca.pluscontrol.entidades.UsuarioEntidade;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.modelo.UsuarioMod;

/**
 * Created by andrebronca on 24/10/16.
 */

public class BackupDAO {
    private static BackupDAO instance;
    private SQLiteDatabase db;
    private DBHelper conexao;
    private Context context;

    public static BackupDAO getInstance(Context context) {
        if (instance == null) {
            instance = new BackupDAO(context);
        }
        return instance;
    }

    private BackupDAO(Context context) {
        conexao = DBHelper.getInstance(context);
    }

    public List<Categoria> getCategoriaBackup() {
        List<Categoria> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        StringBuilder sql = new StringBuilder();
        sql.append("select id_categoria, ds_categoria, tp_categoria, st_ativo, cd_cor, ");
        sql.append(" dt_cadastro, dt_alterado ");
        sql.append(" from categoria ");

        Cursor cursor = db.rawQuery(sql.toString(), null);
        while (cursor.moveToNext()) {
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(cursor.getInt(0));
            categoria.setDsCategoria(cursor.getString(1));
            categoria.setTpCategoria(cursor.getString(2));
            categoria.setStAtivo(cursor.getString(3));
            categoria.setCdCor(cursor.getString(4));
            categoria.setDtCadastro(cursor.getString(5));
            categoria.setDtAlterado(cursor.getString(6));
            lista.add(categoria);
        }
        db.close();
        cursor.close();
        return lista;
    }

    public List<Pessoa> getPessoaBackup() {
        List<Pessoa> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        StringBuilder sql = new StringBuilder();
        sql.append("select id_pessoa, nm_pessoa, tp_pessoa, ds_email, st_ativo, ");
        sql.append(" dt_cadastro, dt_alterado ");
        sql.append(" from pessoa ");

        Cursor cursor = db.rawQuery(sql.toString(), null);
        while (cursor.moveToNext()) {
            Pessoa pessoa = new Pessoa();
            pessoa.setIdPessoa(cursor.getInt(0));
            pessoa.setNmPessoa(cursor.getString(1));
            pessoa.setTpPessoa(cursor.getString(2));
            pessoa.setDsEmail(cursor.getString(3));
            pessoa.setStAtivo(cursor.getString(4));
            pessoa.setDtCadastro(cursor.getString(5));
            pessoa.setDtAlterado(cursor.getString(6));
            lista.add(pessoa);
        }
        db.close();
        cursor.close();
        return lista;
    }

    public List<Titulo> getTituloBackup() {
        List<Titulo> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        StringBuilder sql = new StringBuilder();
        sql.append("select id_titulo, id_parcela, ds_titulo, tp_natureza, dt_emissao, dt_vencimento, ");
        sql.append("vl_titulo, vl_parcela, vl_saldo, dt_baixa, qt_parcela, st_relatorio, st_movimento, dt_cadastro, dt_alterado, ");
        sql.append("id_categoria, id_pessoa ");
        sql.append(" from titulo ");

        Cursor cursor = db.rawQuery(sql.toString(), null);
        while (cursor.moveToNext()) {
            Titulo obj = new Titulo();
            obj.setIdTitulo(cursor.getInt(0));
            obj.setIdParcela(cursor.getInt(1));
            obj.setDsTitulo(cursor.getString(2));
            obj.setTpNatureza(cursor.getString(3));
            obj.setDtEmissao(cursor.getString(4));
            obj.setDtVencimento(cursor.getString(5));
            obj.setVlTitulo(cursor.getDouble(6));
            obj.setVlParcela(cursor.getDouble(7));
            obj.setVlSaldo(cursor.getDouble(8));
            obj.setDtBaixa(cursor.getString(9));
            obj.setQtParcela(cursor.getInt(10));
            obj.setStRelatorio(cursor.getString(11));
            obj.setStMovimento(cursor.getString(12));
            obj.setDtCadastro(cursor.getString(13));
            obj.setDtAlterado(cursor.getString(14));
            obj.setIdCategoria(cursor.getInt(15));
            obj.setIdPessoa(cursor.getInt(16));
            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }

    public List<Conta> getContaBackup() {
        List<Conta> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        StringBuilder sql = new StringBuilder();
        sql.append("select id_conta, ds_conta, vl_saldo, cd_cor, ");
        sql.append(" dt_cadastro, dt_alterado ");
        sql.append(" from conta ");

        Cursor cursor = db.rawQuery(sql.toString(), null);
        while (cursor.moveToNext()) {
            Conta obj = new Conta();
            obj.setIdConta(cursor.getInt(0));
            obj.setDsConta(cursor.getString(1));
            obj.setVlSaldo(cursor.getDouble(2));
            obj.setCdCor(cursor.getString(3));
            obj.setDtCadastro(cursor.getString(4));
            obj.setDtAlterado(cursor.getString(5));
            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }

    public List<MovimentoConta> getMovimentoContaBackup() {
        List<MovimentoConta> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        StringBuilder sql = new StringBuilder();
        sql.append("select id_conta, id_titulo, id_parcela, vl_movimento, dt_movimento, st_direcao, ");
        sql.append("coalesce(id_transferencia,0), ds_historico, st_estorno, st_tipo ");
        sql.append(" from movimentoconta ");

        Cursor cursor = db.rawQuery(sql.toString(), null);
        while (cursor.moveToNext()) {
            MovimentoConta obj = new MovimentoConta();
            obj.setIdConta(cursor.getInt(0));
            obj.setIdTitulo(cursor.getInt(1));
            obj.setIdParcela(cursor.getInt(2));
            obj.setVlMovimento(cursor.getDouble(3));
            obj.setDtMovimento(cursor.getString(4));
            obj.setStDirecao(cursor.getString(5));
            obj.setIdTransferencia(cursor.getInt(6));
            obj.setDsHistorico(cursor.getString(7));
            obj.setStEstorno(cursor.getString(8));
            obj.setStTipo(cursor.getString(9));
            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }

    public List<UsuarioMod> getUsuarioModBackup() {
        List<UsuarioMod> lista = new ArrayList<>();
        db = conexao.getReadableDatabase();
        StringBuilder sql = new StringBuilder();
        sql.append("select id_usuario, nm_usuario, ds_email, ds_senha from usuario limit 1");

        Cursor cursor = db.rawQuery(sql.toString(), null);
        while (cursor.moveToNext()) {
            UsuarioMod obj = new UsuarioMod();
            obj.setIdUsuario(cursor.getInt(0));
            obj.setNmUsuario(cursor.getString(1));
            obj.setDsEmail(cursor.getString(2));
            obj.setDsSenha(cursor.getString(3));
            lista.add(obj);
        }
        db.close();
        cursor.close();
        return lista;
    }

    /**
     * 1
     * Utilizado na importação
     */
    public String getSqlCategoriaDelete() {
        return "delete from categoria";
    }

    public String getSqlCategoriaSequencia() {
        return "delete from sqlite_sequence where name = 'categoria'";
    }

    public String getSqlCategoriaInsert(String backup) {
        final String VALUES = "TABCATEGORIAV";
        String colunas = " ds_categoria, tp_categoria, st_ativo, cd_cor, dt_cadastro, dt_alterado ";
        String insert = "insert into categoria (" + colunas + ") values";
        String bkp = null;
        if (backup.contains(VALUES)) {
            bkp = backup.replace(VALUES, insert);
        }
        return bkp;
    }

    /**
     * 2
     * Utilizado na importação
     */
    public String getSqlPessoaDelete() {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from pessoa;\n");
        sb.append("delete from sqlite_sequence where name = 'pessoa';\n");
        return sb.toString();
    }

    public String getSqlPessoaInsert(String backup) {
        final String VALUES = "TABPESSOAV";
        String colunas = " nm_pessoa, tp_pessoa, ds_email, st_ativo, dt_cadastro, dt_alterado ";
        String insert = "insert into pessoa (" + colunas + ") values";
        String sql = null;
        if (backup.contains(VALUES)) {
            sql = backup.replace(VALUES, insert);
        }
        return sql;
    }

    /**
     * 3
     * Utilizado na importação
     */
    public String getSqlTituloDelete() {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from titulo;\n");
        sb.append("delete from sqlite_sequence where name = 'titulo';\n");
        return sb.toString();
    }

    public String getSqlTituloInsert(String backup) {
        final String VALUES = "TABTITULOV";
        String colunas = " id_titulo, id_parcela, ds_titulo, tp_natureza, dt_emissao, dt_vencimento, ";
        colunas += " vl_titulo, vl_saldo, dt_baixa, qt_parcela, st_relatorio, dt_cadastro, dt_alterado, ";
        colunas += " id_categoria, id_pessoa ";
        String insert = "insert into titulo (" + colunas + ") values";
        String sql = null;
        if (backup.contains(VALUES)) {
            sql = backup.replace(VALUES, insert);
        }
        return sql;
    }

    /**
     * 4
     * Utilizado na importação
     */
    public String getSqlContaDelete() {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from conta;\n");
        sb.append("delete from sqlite_sequence where name = 'conta';\n");
        return sb.toString();
    }

    public String getSqlContaInsert(String backup) {
        final String VALUES = "TABCONTAV";
        String colunas = " ds_conta, vl_saldo, cd_cor, dt_cadastro, dt_alterado ";
        String insert = "insert into conta (" + colunas + ") values";
        String sql = null;
        if (backup.contains(VALUES)) {
            sql = backup.replace(VALUES, insert);
        }
        return sql;
    }

    /**
     * 5
     * Utilizado na importação
     */
    public String getSqlMovimentoContaDelete() {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from movimentoconta;\n");
        sb.append("delete from sqlite_sequence where name = 'movimentoconta';\n");
        return sb.toString();
    }

    public String getSqlMovimentoContaInsert(String backup) {
        final String VALUES = "TABMOVCONTAV";
        String colunas = " id_conta, id_titulo, id_parcela, vl_movimento, dt_movimento, st_direcao, ";
        colunas += "id_transferencia, ds_historico, st_estorno, st_tipo ";
        String insert = "insert into movimentoconta (" + colunas + ") values";
        String sql = null;
        if (backup.contains(VALUES)) {
            sql = backup.replace(VALUES, insert);
        }
        return sql;
    }

    /**
     * 6
     * Utilizado na importação
     */
    public String getSqlUsuarioDelete() {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from usuario;\n");
        sb.append("delete from sqlite_sequence where name = 'usuario';\n");
        return sb.toString();
    }

    public String getSqlUsuarioInsert(String backup) {
        final String VALUES = "TABUSUARIOV";
        String colunas = " nm_usuario, ds_email ";
        String insert = "insert into usuario (" + colunas + ") values ";
        String bkp = null;
        if (backup.contains(VALUES)) {
            bkp = backup.replace(VALUES, insert);
        }
        return bkp;
    }

    // 2
    public void inserirDados(List<String> sql) {
        db = conexao.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            for (String insert : sql) {
                db.execSQL(insert);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.endTransaction();
    }

    // 1
    public void removerDados() {
        db = conexao.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            db.delete(CategoriaEntidade.TABLE_NAME, null, null);
            db.execSQL("update sqlite_sequence set seq=0 where name='" + CategoriaEntidade.TABLE_NAME + "'");
            db.delete(PessoaEntidade.TABLE_NAME, null, null);
            db.execSQL("update sqlite_sequence set seq=0 where name='" + PessoaEntidade.TABLE_NAME + "'");
            db.delete(TituloEntidade.TABLE_NAME, null, null);
//            db.execSQL("update sqlite_sequence set seq=0 where name='"+ TituloEntidade.TABLE_NAME +"'");
            db.delete(ContaEntidade.TABLE_NAME, null, null);
            db.execSQL("update sqlite_sequence set seq=0 where name='" + ContaEntidade.TABLE_NAME + "'");
            db.delete(MovimentoEntidade.TABLE_NAME, null, null);
            db.execSQL("update sqlite_sequence set seq=0 where name='" + MovimentoEntidade.TABLE_NAME + "'");
            db.delete(UsuarioEntidade.TABLE_NAME, null, null);
            db.execSQL("update sqlite_sequence set seq=0 where name='" + UsuarioEntidade.TABLE_NAME + "'");
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.endTransaction();
    }

//    //nao está funcionando. deixar para depois (se sobrar tempo)
//    public String getUsuarioModBackupJSON(){
//        List<UsuarioMod> lista = new ArrayList<>();
//        db = conexao.getReadableDatabase();
//        StringBuilder sql = new StringBuilder();
//        sql.append("select nm_usuario, ds_email from usuario ");
//
//        Cursor cursor = db.rawQuery(sql.toString(),null);
//        return cursorToJson(cursor).toString();
//
//    }
//
//    //http://stackoverflow.com/questions/25722585/convert-sqlite-to-json
//    static JSONObject cursorToJson(Cursor c) {
//        JSONObject retVal = new JSONObject();
//        for(int i=0; i<c.getColumnCount(); i++) {
//            String cName = c.getColumnName(i);
//            try {
//                switch (c.getType(i)) {
//                    case Cursor.FIELD_TYPE_INTEGER:
//                        retVal.put(cName, c.getInt(i));
//                        break;
//                    case Cursor.FIELD_TYPE_FLOAT:
//                        retVal.put(cName, c.getFloat(i));
//                        break;
//                    case Cursor.FIELD_TYPE_STRING:
//                        retVal.put(cName, c.getString(i));
//                        break;
////                    case Cursor.FIELD_TYPE_BLOB:
////                        retVal.put(cName, DataUtils.bytesToHexString(c.getBlob(i)));
////                        break;
//                }
//            }
//            catch(Exception ex) {
//                Log.e("BACKUP_TO_JSON", "Exception converting cursor column to json field: " + cName);
//            }
//        }
//        return retVal;
//    }
}