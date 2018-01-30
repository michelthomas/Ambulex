package com.michelthomas.ambulex.model;

/**
 * Created by MichelT on 18/01/2018.
 */

public class Ambulancia {
    private String mPlaca;
    private String mModelo;
    private String mNumCRV;
    private String mNumCRVL;

    private String[] mPosicaoAtual = new String[2];
    private Object mContrato;
    private boolean mSituacaoDocumento;
    private boolean mDisponivel;
    private Estado mEstado;

    public Ambulancia(String mPlaca, String mModelo, String mNumCRV, String mNumCRVL) {
        this.mPlaca = mPlaca;
        this.mModelo = mModelo;
        this.mNumCRV = mNumCRV;
        this.mNumCRVL = mNumCRVL;
    }

    public boolean isSituacaoDocumento() {
        return mSituacaoDocumento;
    }

    public void setSituacaoDocumento(boolean mSituacaoDocumento) {
        this.mSituacaoDocumento = mSituacaoDocumento;
    }

    public String getPlaca() {
        return mPlaca;
    }

    public void setPlaca(String mPlaca) {
        this.mPlaca = mPlaca;
    }

    public String getModelo() {
        return mModelo;
    }

    public void setModelo(String mModelo) {
        this.mModelo = mModelo;
    }

    public String getNumCRV() {
        return mNumCRV;
    }

    public void setNumCRV(String mNumCRV) {
        this.mNumCRV = mNumCRV;
    }

    public String getNumCRVL() {
        return mNumCRVL;
    }

    public void setNumCRVL(String mNumCRVL) {
        this.mNumCRVL = mNumCRVL;
    }

    public boolean isDisponivel() {
        return mDisponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.mDisponivel = disponivel;
    }

    public Estado getEstado() {
        return mEstado;
    }

    public void setEstado(Estado mEstado) {
        this.mEstado = mEstado;
    }


    public String[] getPosicaoAtual() {
        return mPosicaoAtual;
    }

    public void setPosicaoAtual(String[] mPosicaoAtual) {
        this.mPosicaoAtual = mPosicaoAtual;
    }

    public Object getContrato() {
        return mContrato;
    }

    public void setContrato(Object mContrato) {
        this.mContrato = mContrato;
    }

    public void adicionar(){

    }
    public void remover(){

    }

    public void verificarSituacao(){

    }

    public void digitalizarContrato(){

    }

}
