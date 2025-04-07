package com.example.myapplicationpep;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

import java.util.Locale;

public class ClozeTestActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    // Array di domande
    private final String[] domande = {
            "La ragazza passeggia dove?.",
            "La ragazza passeggia con chi?.",
            "La ragazza passeggia quando?",
            "La befana regala a chi?",
            "La befana regala che cosa?",
            "La befana regala quando?",
            "Il bambino mangia quando?",
            "Il bambino mangia che cosa?",
            "Il bambino mangia con chi??",

    };

    // Array di risposte corrette (gli indici degli aree corrette: 1,2,3,4)
    private final String[] risposteCorrette = {
           "nel Parco", "con il cane", "al tramonto","ai bambini", "le caramelle", "all epifania", "al compleanno", "la torta", "con le amiche"
    };
    private long startTime;
    private Runnable timeoutRunnable; // Runnable per il timeout del suono
    private Handler handler = new Handler();
    private PepperController pepperController;
    private QiContext qiContext;
    private boolean rispostaData = false;
    private int contaRisposteCorrette = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloze_test);
        startTime = System.currentTimeMillis();
        // Registra l'Activity per ricevere i callback del robot
        QiSDK.register(this, this);

        // Inizializza il PepperController
        pepperController = new PepperController(this);

        // Se il QiContext è già presente nel singleton, lo imposta nel controller
        if (PepperSingleton.getInstance().getQiContext() != null) {
            pepperController.setQiContext(PepperSingleton.getInstance().getQiContext());
        } else {
            Log.d("ClozeTestActivity", "QiContext non disponibile inizialmente.");
        }

    }

    @Override
    protected void onDestroy() {
        if (timeoutRunnable != null) {
            handler.removeCallbacks(timeoutRunnable);
        }
        QiSDK.unregister(this);
        super.onDestroy();
    }

    // Callback: il robot ha acquisito il focus
    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        pepperController.setQiContext(qiContext);
        PepperSingleton.getInstance().setQiContext(qiContext);
        Log.d("ClozeTestActivity", "Robot focus acquisito.");

    }

    // Callback: il robot ha perso il focus
    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
        pepperController.setQiContext(null);
        Log.d("ClozeTestActivity", "Robot focus perso.");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.e("ClozeTestActivity", "Focus rifiutato: " + reason);
    }

    // Metodo chiamato per cambiare immagine e leggere la domanda successiva
    public void cambiaImmagineCloze(View view) {
        rispostaData = false;
        attendi15Secondi();
        Log.d("ClozeTestActivity", "Sono entrato nel metodo cambiaImmagineCloze");
        CounterSingleton contatore = CounterSingleton.getInstance();
        contatore.incrementaCont();
        int cont = contatore.getCont();
        String stringacont = Integer.toString(cont);
        String temp;
        if (cont < 10) {
            temp = "cloze0" + stringacont;
            Log.d("ClozeTestActivity", "Il valore di temp è: " + temp);
        } else {
            temp = "cloze" + stringacont;
        }

        if (contatore.getCont() <= 9) {
            ImageView imageView = findViewById(R.id.imageView2);
            int idImmagine = getResources().getIdentifier(temp, "drawable", getPackageName());
            imageView.setImageResource(idImmagine);
            // Legge la domanda corrispondente tramite Pepper
            String domanda = domande[cont - 1]; // le domande partono da indice 0
            String rispostaCorretta = risposteCorrette[contatore.getCont()-1];
            System.out.println("la risposta corretta e: "+rispostaCorretta);

            if (pepperController != null) {
                pepperController.speak(domanda);  // Fa parlare Pepper
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Dopo che ha finito di parlare, avvia il riconoscimento vocale
                    Log.d("ClozeTestActivity", "Pepper ha finito di parlare, ora inizio ad ascoltare la risposta.");
                    pepperController.riconoscereParole(rispostaCorretta); // Esegui riconoscimento vocale
                }
            }, 3000);
            pepperController.speak("premi il bottone Avanti e passa alla prossima domanda!");
            rispostaData = true;
            gestisciRisposteCorretta();
            if (timeoutRunnable != null) {
                handler.removeCallbacks(timeoutRunnable);  // Rimuove il runnable che suonerebbe
            }

        } else {
            contatore.setCount(0);
            Log.d("ClozeTestActivity", "Fine domande, cambio vista");
            if (timeoutRunnable != null) {
                handler.removeCallbacks(timeoutRunnable);
                timeoutRunnable = null;
            }
            long endTime = System.currentTimeMillis();
            int tempoImpiegato = (int) (endTime - startTime); // Tempo in millisecondi
            contatore.setTempoImpiegatoFineTest(tempoImpiegato);
            int risposteErrate = domande.length - contatore.getPoint();
            contatore.setNumeroRisposteErrate(risposteErrate);
            Intent intent = new Intent(ClozeTestActivity.this, TestTerminatoActivity.class);
            startActivity(intent);
        }
    }

    // Metodo per gestire il tocco sull'immagine

    //buggato se utente interagisce suona comunque
    // il metodo geenra un suono nel caso il bimbo è distratto ed impiega troppo a rispondere per raccogliere l'attenzione
    private void attendi15Secondi() {
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (!rispostaData) { // Esegui il suono solo se non c'è stata interazione
                    Log.d("Attesa", "15 secondi passati!");
                    pepperController.soundAttention();
                    CounterSingleton contatore = CounterSingleton.getInstance();
                    contatore.incrementaNumeroSoundAttention();
                    pepperController.speak("forza non mollare!");
                }
            }
        };
        handler.postDelayed(timeoutRunnable, 15000); // Esegui dopo 15 secondi
    }


    private void gestisciRisposteCorretta(){
        if(contaRisposteCorrette>= 3){
            contaRisposteCorrette = 0;
            if (qiContext != null) {
                // Esegui animazione di saluto
                if(pepperController != null)
                    pepperController.playAnimation(R.raw.eccitato);

                // Aggiungi una pausa di 3 secondi (3000 ms)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Dopo 3 secondi, far parlare Pepper
                        pepperController.speak("Continua Cosi!" );
                    }
                }, 3000);  // La pausa è di 3000 millisecondi (3 secondi)
            }

        }
        else
            contaRisposteCorrette++;

    }






}
