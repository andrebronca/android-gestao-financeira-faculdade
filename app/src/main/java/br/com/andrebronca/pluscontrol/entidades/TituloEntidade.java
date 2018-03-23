package br.com.andrebronca.pluscontrol.entidades;

/**
 * Created by andrebronca on 04/10/16.
 */

public final class TituloEntidade {
    public static final String TABLE_NAME = "titulo";

    public static final class Coluna {
        public static final String ID = "id_titulo";         //pk
        public static final String ID_PARCELA = "id_parcela";    //pk
        public static final String DESCRICAO = "ds_titulo";
        public static final String NATUREZA = "tp_natureza";
        public static final String EMISSAO = "dt_emissao";
        public static final String VENCIMENTO = "dt_vencimento";
        public static final String VALOR = "vl_titulo";
        public static final String VALORPARCELA = "vl_parcela";
        public static final String SALDO = "vl_saldo";
        public static final String BAIXA = "dt_baixa";
        public static final String QTPARCELA = "qt_parcela";
        public static final String RELATORIO = "st_relatorio";
        public static final String MOVIMENTO = "st_movimento";   //se estiver sim, nao será listado para edição
        public static final String DATACAD = "dt_cadastro";
        public static final String DATAALT = "dt_alterado";
        public static final String ID_CATEGORIA = "id_categoria"; //fk
        public static final String ID_PESSOA = "id_pessoa";       //fk
    }

    public static final class CreateTable {
        private static StringBuilder sql;
        private static boolean hasCreateTable = false;

        public static String getSqlCreateTableTitulo() {
            sql = new StringBuilder();
            sql.append("create table if not exists " + TABLE_NAME + " ( ");
            sql.append(Coluna.ID + " integer not null, ");
            sql.append(Coluna.ID_PARCELA + " integer not null,");
            sql.append(Coluna.DESCRICAO + " text not null, ");
            sql.append(Coluna.NATUREZA + " varchar(1) check(tp_natureza='R' or tp_natureza='D'),");
            sql.append(Coluna.EMISSAO + " date default current_date, ");
            sql.append(Coluna.VENCIMENTO + " date default '', ");
            sql.append(Coluna.VALOR + " numeric(8,2) check(vl_titulo > 0),");
            sql.append(Coluna.VALORPARCELA + " numeric(8,2) check(vl_parcela > 0),");
            sql.append(Coluna.SALDO + " numeric(8,2) check(vl_saldo >= 0),");
            sql.append(Coluna.BAIXA + " date default '', ");
            sql.append(Coluna.QTPARCELA + " integer check(qt_parcela > 0),");
            sql.append(Coluna.RELATORIO + " varchar(1) default 'S' check(st_relatorio='S' or st_relatorio='N'), ");
            sql.append(Coluna.MOVIMENTO + " varchar(1) default 'N' check(st_movimento='S' or st_movimento='N'), ");
            sql.append(Coluna.DATACAD + " date default current_date, ");
            sql.append(Coluna.DATAALT + " date default '', ");
            sql.append(Coluna.ID_CATEGORIA + " integer not null, ");
            sql.append(Coluna.ID_PESSOA + " integer not null, ");
            sql.append(" primary key(id_titulo, id_parcela) ");
//            sql.append( "foreign key(id_categoria) references categoria(id_categoria),"); //controlar via código
//            sql.append( "foreign key(id_pessoa) references pessoa(id_pessoa)");   //controlar via código
            sql.append(")");
            hasCreateTable = true;
            return sql.toString();
        }

        /**
         * Se a tabela não foi criada retorna null. Chamado no onCreate do DBHelper
         * @return sql para criação do index
         */
        public static String getSqlCreateIndexTitulo() {
            sql = new StringBuilder();
            sql.append("create index idx_titulo_descricao on titulo (ds_titulo)");
            if (hasCreateTable) {
                return sql.toString();
            }
            return null;
        }

        /**
         * Chamado no update do banco de dados
         * @return
         */
        public static String dropTableTitulo() {
            return "drop table if exists " + TABLE_NAME;
        }
    }
}