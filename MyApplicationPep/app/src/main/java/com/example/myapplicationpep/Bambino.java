package com.example.myapplicationpep;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class Bambino {
    private String nome;
    private String cognome;
    private String dataTest;
    private String sesso;
    private int eta;
    private int punteggio;
    private int tempoImpiegatoFineTest;
    private int numeroRisposteErrate;
    private int numeroSoundAttention;


    public Bambino(String Nome, String Cognome, int Punteggio, int TempoImpiegatoFineTest, int NumeroRisposteErrate, int NumeroSoundAttention){
        nome=Nome;
        cognome=Cognome;
        punteggio=Punteggio;
        tempoImpiegatoFineTest = TempoImpiegatoFineTest;
        numeroRisposteErrate = NumeroRisposteErrate;
        numeroSoundAttention = NumeroSoundAttention;
    }
    public Bambino(String Nome, String Cognome, String Sesso, int Eta ){
        nome=Nome;
        cognome=Cognome;
        eta=Eta;
        sesso = Sesso;

    }

    public Bambino(){}

    public void setNumeroSoundAttention(int numeroSoundAttention) { this.numeroSoundAttention = numeroSoundAttention; }

    public void setNumeroRisposteErrate(int numeroRisposteErrate) { this.numeroRisposteErrate = numeroRisposteErrate; }

    public void setTempoImpiegatoFineTest(int tempoImpiegatoFineTest) { this.tempoImpiegatoFineTest = tempoImpiegatoFineTest; }

    public void setSesso(String sesso) { this.sesso = sesso; }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    protected void setData(String data) {
        this.dataTest = data;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }
    public void setNome(String nome){
        this.nome=nome;
    }
    public void setEta(int eta){
        this.eta=eta;
    }

    public int getNumeroSoundAttention() { return numeroSoundAttention; }

    public int getNumeroRisposteErrate() { return numeroRisposteErrate; }

    public int getTempoImpiegatoFineTest() { return tempoImpiegatoFineTest; }

    public String getSesso() { return sesso; }

    public int getEta() {
        return eta;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public String getCognome() {
        return cognome;
    }

    public String getDataTest() {
        getDataOggi();
        return this.dataTest;
    }

    public String getNome() {
        return nome;
    }

    private void getDataOggi(){

        LocalDate oggi = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            oggi = LocalDate.now();
        }

        // Formatta la data come stringa
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        }
        String dataOggi = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dataOggi = oggi.format(formatter);
        } else {
            // Usa Calendar e SimpleDateFormat per versioni precedenti
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dataOggi = sdf.format(calendar.getTime());
        }

        System.out.println("Data di oggi: " + dataOggi);
        setData(dataOggi);
    }
}

