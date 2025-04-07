package com.example.myapplicationpep;

public class Medico {
    private String email;
    private String nome;
    private String cognome;
    private String password;
    private String percorso;

    public Medico(String nome, String cognome, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.password = password;
        this.email = email;
    }

    public Medico(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPercorso() {
        return percorso;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setPercorso(String percorso) {
        this.percorso = percorso;
    }
}
