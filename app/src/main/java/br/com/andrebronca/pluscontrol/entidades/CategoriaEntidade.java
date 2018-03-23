package br.com.andrebronca.pluscontrol.entidades;

/**
 * Created by andrebronca on 30/09/16.
 */

public final class CategoriaEntidade {
    public static final String TABLE_NAME = "categoria";

    public static final class Coluna {
        public static final String ID = "id_categoria";
        public static final String DESCRICAO = "ds_categoria";
        public static final String TIPO = "tp_categoria";
        public static final String ATIVO = "st_ativo";
        public static final String COR = "cd_cor";
        public static final String DATACAD = "dt_cadastro";
        public static final String DATAALT = "dt_alterado";
    }

    public static final class CreateTable {
        private static StringBuilder sql;
        private static boolean hasCreateTable = false;

        public static String getSqlCreateTableCategoria() {
            sql = new StringBuilder();
            sql.append("create table if not exists " + TABLE_NAME + " ( ");
            sql.append(Coluna.ID + " integer primary key autoincrement not null, ");
            sql.append(Coluna.DESCRICAO + " text not null unique, ");
            sql.append(Coluna.TIPO + " varchar(1) check(" + Coluna.TIPO + "='R' or " + Coluna.TIPO + "='D'), ");
            sql.append(Coluna.COR + " varchar(10), ");
            sql.append(Coluna.ATIVO + " varchar(1) default 'S' check(st_ativo='S' or st_ativo='N'), ");
            sql.append(Coluna.DATACAD + " date default current_date, ");
            sql.append(Coluna.DATAALT + " date ) ");
            hasCreateTable = true;
            return sql.toString();
        }

        /**
         * Se a tabela não foi criada retorna null. Chamado no onCreate do DBHelper
         * @return sql para criação do index
         */
        public static String getSqlCreateIndexCategoria() {
            sql = new StringBuilder();
            sql.append("create index idx_categoria_descricao on categoria (ds_categoria)");
            if (hasCreateTable) {
                return sql.toString();
            }
            return null;
        }

        /**
         * Chamado no update do banco de dados
         * @return
         */
        public static String dropTableCategoria() {
            return "drop table if exists " + TABLE_NAME;
        }
    }
}