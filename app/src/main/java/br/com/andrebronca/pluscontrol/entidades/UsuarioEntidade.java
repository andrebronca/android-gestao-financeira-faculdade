package br.com.andrebronca.pluscontrol.entidades;

/**
 * Created by andrebronca on 23/10/16.
 */

public class UsuarioEntidade {
    public static final String TABLE_NAME = "usuario";

    public static final class Coluna {
        public static final String ID = "id_usuario";
        public static final String NOME = "nm_usuario";
        public static final String EMAIL = "ds_email";
        public static final String SENHA = "ds_senha";
    }

    public static final class CreateTable {
        private static StringBuilder sql;
        private static boolean hasCreateTable = false;

        public static String getSqlCreateTableUsuario() {
            sql = new StringBuilder();
            sql.append("create table if not exists " + TABLE_NAME + " ( ");
            sql.append(Coluna.ID + " integer primary key autoincrement not null, ");
            sql.append(Coluna.NOME + " text not null unique, ");
            sql.append(Coluna.EMAIL + " text not null, ");
            sql.append(Coluna.SENHA + " text )");
            hasCreateTable = true;
            return sql.toString();
        }

        /**
         * Chamado no update do banco de dados
         * @return
         */
        public static String dropTableUsuario() {
            return "drop table if exists " + TABLE_NAME;
        }

    }
}