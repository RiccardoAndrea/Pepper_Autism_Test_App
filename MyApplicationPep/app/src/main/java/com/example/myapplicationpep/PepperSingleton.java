package com.example.myapplicationpep;

import com.aldebaran.qi.sdk.QiContext;



public class PepperSingleton {

    private static PepperSingleton instance;
    private QiContext qiContext;

    // Costruttore privato
    private PepperSingleton() {}

    // Metodo per ottenere l'istanza del singleton (senza richiedere QiContext)
    public static synchronized PepperSingleton getInstance() {
        if (instance == null) {
            instance = new PepperSingleton();
        }
        return instance;
    }

    // Metodo per ottenere il QiContext
    public QiContext getQiContext() {
        return qiContext;
    }

    // Metodo per impostare il QiContext
    public void setQiContext(QiContext qiContext) {
        this.qiContext = qiContext;
    }
}
