package com.michelthomas.ambulex.model;

import java.util.Date;

/**
 * Created by MichelT on 18/01/2018.
 */

public class Solicitacao {
    private String mId;
    private String mMotivo;
    private Local mLocal;
    private Usuario mUsuario;
    private Ambulancia mAmbulancia;
    private Date mData;



    public String get_Id() {
        return mId;
    }

    public void set_Id(String id) {
        this.mId = id;
    }

    public String getMotivo() {
        return mMotivo;
    }

    public void setMotivo(String mMotivo) {
        this.mMotivo = mMotivo;
    }

    public Local getLocal() {
        return mLocal;
    }

    public void setLocal(Local local) {
        this.mLocal = local;
    }

    public Usuario getUsuario() {
        return mUsuario;
    }

    public void setUsuario(Usuario mUsuario) {
        this.mUsuario = mUsuario;
    }

    public Ambulancia getAmbulancia() {
        return mAmbulancia;
    }

    public void setAmbulancia(Ambulancia mAmbulancia) {
        this.mAmbulancia = mAmbulancia;
    }

    public String darPrimeirosSocorros(){
        return null;
    }
}
