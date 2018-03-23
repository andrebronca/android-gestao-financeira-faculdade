package br.com.andrebronca.pluscontrol.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.andrebronca.pluscontrol.entidades.DBHelper;
import br.com.andrebronca.pluscontrol.entidades.UsuarioEntidade;
import br.com.andrebronca.pluscontrol.modelo.UsuarioMod;

/**
 * Created by andrebronca on 23/10/16.
 */

public class UsuarioDAO {
    private static UsuarioDAO instance;
    private SQLiteDatabase db;
    private DBHelper conexao;
    private Context context;

    public static UsuarioDAO getInstance(Context context) {
        if (instance == null) {
            instance = new UsuarioDAO(context);
        }
        return instance;
    }

    private UsuarioDAO(Context context) {
        conexao = DBHelper.getInstance(context);
    }

    public long salvar(UsuarioMod usuarioMod) {
        ContentValues values = new ContentValues();
        values.put(UsuarioEntidade.Coluna.NOME, usuarioMod.getNmUsuario());
        values.put(UsuarioEntidade.Coluna.EMAIL, usuarioMod.getDsEmail());
        values.put(UsuarioEntidade.Coluna.SENHA, usuarioMod.getDsSenha());
        db = conexao.getWritableDatabase();
        long id = db.insert(UsuarioEntidade.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public void atualizar(UsuarioMod usuarioMod) {
        String where = UsuarioEntidade.Coluna.ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(usuarioMod.getIdUsuario())};
        ContentValues values = new ContentValues();
        values.put(UsuarioEntidade.Coluna.NOME, usuarioMod.getNmUsuario());
        values.put(UsuarioEntidade.Coluna.EMAIL, usuarioMod.getDsEmail());
        values.put(UsuarioEntidade.Coluna.SENHA, usuarioMod.getDsSenha());  //já vem criptografada
        db = conexao.getWritableDatabase();
        db.update(UsuarioEntidade.TABLE_NAME, values, where, whereArgs);
        db.close();
    }

    public UsuarioMod getUsuario() {
        db = conexao.getReadableDatabase();
        Cursor cursor = db.query(UsuarioEntidade.TABLE_NAME,
                new String[]{UsuarioEntidade.Coluna.ID, UsuarioEntidade.Coluna.NOME,
                        UsuarioEntidade.Coluna.EMAIL, UsuarioEntidade.Coluna.SENHA},
                null, null, null, null, null, null);
        UsuarioMod obj = new UsuarioMod();
        while (cursor.moveToNext()) {
            obj.setIdUsuario(cursor.getInt(cursor.getColumnIndex(UsuarioEntidade.Coluna.ID)));
            obj.setNmUsuario(cursor.getString(cursor.getColumnIndex(UsuarioEntidade.Coluna.NOME)));
            obj.setDsEmail(cursor.getString(cursor.getColumnIndex(UsuarioEntidade.Coluna.EMAIL)));
            obj.setDsSenha(cursor.getString(cursor.getColumnIndex(UsuarioEntidade.Coluna.SENHA)));
        }
        db.close();
        cursor.close();
        return obj;
    }

    public int deleteUsuarioByID(int id) {
        db = conexao.getWritableDatabase();
        db.delete(UsuarioEntidade.TABLE_NAME, null, null);
        db.execSQL("update sqlite_sequence set seq = 0 where name='" + UsuarioEntidade.TABLE_NAME + "'");
        int result = db.delete(UsuarioEntidade.TABLE_NAME, " id_usuario = " + id, null);
        return result;
    }

    /**
     * Se retornar -1, ocorreu algum erro. Se retornar 0 é porque não há usuário cadastrado
     *
     * @return
     */
    public int getMaxIDUsuario() {
        int maxId = -1;
        db = conexao.getReadableDatabase();
        Cursor cursor = db.rawQuery("select coalesce(max(id_usuario),0) as max from usuario", null);
        while (cursor.moveToNext()) {
            maxId = cursor.getInt(0);
        }
        return maxId;
    }

    public void salvarUsuarioImportado(UsuarioMod mov) {
        db = conexao.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UsuarioEntidade.Coluna.ID, mov.getIdUsuario());
        values.put(UsuarioEntidade.Coluna.NOME, mov.getNmUsuario());
        values.put(UsuarioEntidade.Coluna.EMAIL, mov.getDsEmail());
        values.put(UsuarioEntidade.Coluna.SENHA, mov.getDsSenha());
        db.insert(UsuarioEntidade.TABLE_NAME, null, values);
        db.close();
    }
}