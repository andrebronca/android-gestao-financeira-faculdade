package br.com.andrebronca.pluscontrol.entidades;

/**
 * Created by andrebronca on 04/10/16.
 */

public final class PessoaEntidade {
    public static final String TABLE_NAME = "pessoa";

    public static final class Coluna {
        public static final String ID = "id_pessoa";
        public static final String NOME = "nm_pessoa";
        public static final String TIPO = "tp_pessoa";
        public static final String EMAIL = "ds_email";
        public static final String ATIVO = "st_ativo";
        public static final String DATACAD = "dt_cadastro";
        public static final String DATAALT = "dt_alterado";
    }

    public static final class CreateTable {
        private static StringBuilder sql;
        private static boolean hasCreateTable = false;

        public static String getSqlCreateTablePessoa() {
            sql = new StringBuilder();
            sql.append("create table if not exists " + TABLE_NAME + " ( ");
            sql.append(Coluna.ID + " integer primary key autoincrement not null, ");
            sql.append(Coluna.NOME + " text not null unique, ");
            sql.append(Coluna.TIPO + " varchar(1) check(" + Coluna.TIPO + "='C' or " + Coluna.TIPO + "='F' or " + Coluna.TIPO + "='A'), ");
            sql.append(Coluna.EMAIL + " text, ");
            sql.append(Coluna.ATIVO + " varchar(1) default 'S' check(st_ativo='S' or st_ativo='N'), ");
            sql.append(Coluna.DATACAD + " date default current_date, ");
            sql.append(Coluna.DATAALT + " date default '') ");
            hasCreateTable = true;
            return sql.toString();
        }

        /**
         * Se a tabela não foi criada retorna null. Chamado no onCreate do DBHelper
         * @return sql para criação do index
         */
        public static String getSqlCreateIndexPessoa() {
            sql = new StringBuilder();
            sql.append("create index idx_pessoa_nome on pessoa (nm_pessoa)");
            if (hasCreateTable) {
                return sql.toString();
            }
            return null;
        }

        /**
         * Chamado no update do banco de dados
         * @return
         */
        public static String dropTablePessoa() {
            return "drop table if exists " + TABLE_NAME;
        }
    }
}
