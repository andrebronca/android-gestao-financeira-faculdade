package br.com.andrebronca.pluscontrol.modelo;

import java.io.Serializable;

import br.com.andrebronca.pluscontrol.utilitarios.Util;

/**
 * Created by andrebronca on 23/10/16.
 */

public class UsuarioMod implements Serializable {
    private int idUsuario;
    private String nmUsuario;
    private String dsEmail;
    private String dsSenha;

    public UsuarioMod() {
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNmUsuario() {
        return nmUsuario;
    }

    public void setNmUsuario(String nmUsuario) {
        this.nmUsuario = nmUsuario;
    }

    public String getDsEmail() {
        return dsEmail;
    }

    public void setDsEmail(String dsEmail) {
        this.dsEmail = dsEmail;
    }

    public String getDsSenha() {
        return dsSenha;
    }

    public void setDsSenha(String dsSenha) {
        this.dsSenha = dsSenha;
    }
}