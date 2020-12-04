package com.example.poupar.activity;

import android.os.Parcel;
import android.os.Parcelable;

public class DadosUsuario implements Parcelable {
    private String uid;
    private String nome;
    private String email;
    private String senha;
    private Double receitaTotal;
    private Double despesaTotal;

    public DadosUsuario(String uid, String nome, String email, String senha, Double receitaTotal, Double despesaTotal) {
        this.uid = uid;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.receitaTotal = receitaTotal;
        this.despesaTotal = despesaTotal;
    }

    protected DadosUsuario(Parcel in) {
        uid = in.readString();
        nome = in.readString();
        email = in.readString();
        senha = in.readString();
        receitaTotal = in.readDouble();
        despesaTotal = in.readDouble();
    }

    public static final Creator<DadosUsuario> CREATOR = new Creator<DadosUsuario>() {
        @Override
        public DadosUsuario createFromParcel(Parcel in) {
            return new DadosUsuario(in);
        }

        @Override
        public DadosUsuario[] newArray(int size) {
            return new DadosUsuario[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(nome);
        dest.writeString(email);
        dest.writeString(senha);
        dest.writeDouble(receitaTotal);
        dest.writeDouble(despesaTotal);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public Double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }
}
