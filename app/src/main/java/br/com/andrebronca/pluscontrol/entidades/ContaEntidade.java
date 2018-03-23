package br.com.andrebronca.pluscontrol.entidades;

/**
 * Created by andrebronca on 08/10/16.
 */

public class ContaEntidade {
    public static final String TABLE_NAME = "conta";

    public static final class Coluna {
        public static final String ID = "id_conta";
        public static final String DESCRICAO = "ds_conta";
        public static final String SALDO = "vl_saldo";
        public static final String COR = "cd_cor";
        public static final String DATACAD = "dt_cadastro";
        public static final String DATAALT = "dt_alterado";
    }

    public static final class CreateTable {
        private static StringBuilder sql;
        private static boolean hasCreateTable = false;

        public static String getSqlCreateTableConta() {
            sql = new StringBuilder();
            sql.append("create table if not exists " + TABLE_NAME + " ( ");
            sql.append(Coluna.ID + " integer primary key autoincrement not null, ");
            sql.append(Coluna.DESCRICAO + " text not null unique, ");
            sql.append(Coluna.SALDO + " numeric(8,2) not null,");
            sql.append(Coluna.COR + " varchar(10), ");
            sql.append(Coluna.DATACAD + " date default current_date, ");
            sql.append(Coluna.DATAALT + " date ) ");
            hasCreateTable = true;
            return sql.toString();
        }

        /**
         * Se a tabela não foi criada retorna null. Chamado no onCreate do DBHelper
         * @return sql para criação do index
         */
        public static String getSqlCreateIndexConta() {
            sql = new StringBuilder();
            sql.append("create index idx_conta_descricao on conta (ds_conta)");
            if (hasCreateTable) {
                return sql.toString();
            }
            return null;
        }

        /**
         * Chamado no update do banco de dados
         * @return
         */
        public static String dropTableConta() {
            return "drop table if exists " + TABLE_NAME;
        }

    }

}