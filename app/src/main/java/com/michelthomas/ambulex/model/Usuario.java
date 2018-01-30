package com.michelthomas.ambulex.model;

/**
 * Created by MichelT on 18/01/2018.
 */

public class Usuario {
    private String mCpf;
    private String mCartaoSUS;
    private String mEmail;
    private String mSenha;

    public Usuario(String mCpf, String mCartaoSUS, String email, String mSenha) {
        this.mCpf = mCpf;
        this.mCartaoSUS = mCartaoSUS;
        this.mEmail = email;
        this.mSenha = mSenha;
    }

    public String getCpf() {
        return mCpf;
    }

    public void setCpf(String mCpf) {
        this.mCpf = mCpf;
    }

    public String getCartaoSUS() {
        return mCartaoSUS;
    }

    public void setCartaoSUS(String mCartaoSUS) {
        this.mCartaoSUS = mCartaoSUS;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getSenha() {
        return mSenha;
    }

    public void setSenha(String mSenha) {
        this.mSenha = mSenha;
    }

}
