package com.example.myapplicationpep;

public class CounterSingleton {
    // questa classe salva il valore del contatore delle chiamate di onclick.

    private static CounterSingleton instance;
    private int cont=1;
    private int point=0;
    private int tempoImpiegatoFineTest = 0;
    private int numeroRisposteErrate = 0;
    private int numeroSoundAttention = 0;
    private String testAvviato;
    private String bambinoSelezionato;
    private CounterSingleton() {
        // Costruttore privato per impedire la creazione diretta dell'istanza
    }

    // definisco l'oggetto ed evito race condition grazie a syncronized
    public static synchronized CounterSingleton getInstance(){
        if (instance==null) {
            instance=new CounterSingleton();
        }
        return instance;

    }

    public int getNumeroSoundAttention() {
        return numeroSoundAttention;
    }

    public void setNumeroSoundAttention(int numeroSoundAttention) {
        this.numeroSoundAttention = numeroSoundAttention;
    }

    int getNumeroRisposteErrate(){return this.numeroRisposteErrate;}

    public void setNumeroRisposteErrate(int numeroRisposteErrate) {
        this.numeroRisposteErrate = numeroRisposteErrate;
    }

    int getTempoImpiegatoFineTest(){return this.tempoImpiegatoFineTest;}
    void setTempoImpiegatoFineTest(int tempoImpiegatoFineTest){this.tempoImpiegatoFineTest=tempoImpiegatoFineTest;}


    String getTestAvviato(){
        return this.testAvviato;
    }
    void setTestAvviato(String testAvviato){
        this.testAvviato = testAvviato;
    }

    void incrementaNumeroRisposteErrate(){this.numeroRisposteErrate++;}
    void incrementaNumeroSoundAttention(){this.numeroSoundAttention++;}

    void incrementaCont(){
        cont+=1;

    }

    void incrementaPunteggio(){
        point+=1;
    }
    int getCont(){
        return cont;

    }

    public int getPoint() {
        return point;
    }


    void setCount(int x){
        cont=x;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getBambinoSelezionato() {
        return bambinoSelezionato;
    }

    public void setBambinoSelezionato(String bambinoSelezionato) {
        this.bambinoSelezionato = bambinoSelezionato;
    }

    public void reset(){
        setNumeroRisposteErrate(0);
        setNumeroSoundAttention(0);
        setTestAvviato(null);
        setPoint(0);
        setCount(1);

    }

}

