package br.com.andrebronca.pluscontrol.entidades;

/**
 * Created by andrebronca on 11/10/16.
 * Não será implementada
 */

public class FotoEntidade {
    public static final String TABLE_NAME = "foto";

    public static final class Coluna {
        public static final String ID           = "id_foto";
        public static final String foto         = "ft_fotobinaria"; //blob, byte[] foto
        public static final String IDMOVIMENTO  = "id_movconta";
        public static final String IDTITULO     = "id_titulo";
    }
}
