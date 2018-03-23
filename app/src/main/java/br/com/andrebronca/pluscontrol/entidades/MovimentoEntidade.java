package br.com.andrebronca.pluscontrol.entidades;

/**
 * Created by andrebronca on 08/10/16.
 */

public class MovimentoEntidade {
    public static final String TABLE_NAME = "movimentoconta";

    public static final class Coluna {
        public static final String ID = "id_movconta";
        public static final String ID_CONTA = "id_conta";
        public static final String ID_TITULO = "id_titulo";
        public static final String ID_PARCELA = "id_parcela";
        public static final String VALOR = "vl_movimento";
        public static final String DATA = "dt_movimento";
        public static final String DIRECAO = "st_direcao";
        public static final String ID_TRANSF = "id_transferencia";
        public static final String HISTORICO = "ds_historico";
        public static final String ESTORNO = "st_estorno";
        public static final String TIPO = "st_tipo";    //T=transferencia, S=saque, R=recebimento, P=pagamento
    }

    public static final class CreateTable {
        private static StringBuilder sql;
        private static boolean hasCreateTable = false;

        public static String getSqlCreateTableMovimentoConta() {
            sql = new StringBuilder();
            sql.append("create table if not exists " + TABLE_NAME + " ( ");
            sql.append(Coluna.ID + " integer primary key autoincrement not null, ");
            sql.append(Coluna.ID_CONTA + " integer not null, ");    //fk
            sql.append(Coluna.ID_TITULO + " integer, ");    //fk
            sql.append(Coluna.ID_PARCELA + " integer, ");   //fk
            sql.append(Coluna.VALOR + " numeric(8,2) not null check(vl_movimento > 0), ");
            sql.append(Coluna.DATA + " date default current_date, ");
            sql.append(Coluna.DIRECAO + " varchar(1) check(st_direcao='E'or st_direcao='S'), ");
            sql.append(Coluna.ID_TRANSF + " integer, ");
            sql.append(Coluna.HISTORICO + " text, ");
            sql.append(Coluna.ESTORNO + " varchar(1) default 'N' check(st_estorno='S' or st_estorno='N'), ");
            sql.append(Coluna.TIPO + " varchar(1) default '' check(st_tipo='T' or st_tipo='S' or st_tipo='R' or st_tipo='P') ");
//            sql.append(" foreign key(id_conta) references conta (id_conta), ");   //controlar via código
//            sql.append(" foreign key(id_titulo) references titulo (id_titulo) ");
            sql.append(" ) ");
            hasCreateTable = true;
            return sql.toString();
        }

        /**
         * Se a tabela não foi criada retorna null. Chamado no onCreate do DBHelper
         * @return sql para criação do index
         */
        public static String getSqlCreateIndexMovimentoConta() {
            sql = new StringBuilder();
            sql.append("create index idx_movimentoconta_historico on movimentoconta (ds_historico)");
            if (hasCreateTable) {
                return sql.toString();
            }
            return null;
        }

        /**
         * Chamado no update do banco de dados
         * @return
         */
        public static String dropTableMovimentoConta() {
            return "drop table if exists " + TABLE_NAME;
        }
    }
}
