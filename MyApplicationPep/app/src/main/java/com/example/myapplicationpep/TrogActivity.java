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

public class TrogActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    // Array di domande
    private final String[] domande = {
            "Seleziona la scarpa.",
            "Seleziona il cappello",
            "Seleziona il tavolo.",
            "Seleziona la mela",
            "Seleziona il calciatore",
            "Seleziona chi raccoglie il fiore",
            "Seleziona la ragazza Seduta",
            "Seleziona chi legge",
            "Seleziona la matita lunga",
            "Seleziona il ragazzo più alto",
            "Seleziona il pettine rosso",
            "Seleziona il cavallo nero",
            "Seleziona il gatto",
            "Seleziona la tazza blu",
            "Seleziona il cane che sta mangiando",
            "Seleziona la palla rossa.",
            "Seleziona il cavallo",
            "Seleziona il bambino.",
            "Seleziona il gatto",
            "Seleziona il cane marrone",
            "Seleziona chi raccoglie il cavallo che salta",
            "Seleziona la ragazza Seduta sul tavolo",
            "Seleziona chi da da mangiare al cavallo",
            "Seleziona il cane che porta la spesa",
            "Seleziona il cane",
            "Seleziona chi cavalca la mucca",
            "Seleziona i bambini che saltano il muretto",
            "Seleziona la bambina che cavalca un elefante",
            "Seleziona la ragazza che spinge il ragazzo dietro la schiena",
            "Seleziona chi gioca a calcio",
            // le domande da qui vanno aggiornate
            "Seleziona la scarpa.",
            "Seleziona il cappello",
            "Seleziona il tavolo.",
            "Seleziona la mela",
            "Seleziona il calciatore",
            "Seleziona chi raccoglie il fiore",
            "Seleziona la ragazza Seduta",
            "Seleziona chi legge",
            "Seleziona la matita lunga",
            "Seleziona il ragazzo più alto",
            "Seleziona il pettine rosso",
            "Seleziona il cavallo nero",
            "Seleziona il gatto",
            "Seleziona la tazza blu",
            "Seleziona il cane che sta mangiando",
            "Seleziona il pettine rosso",
            "Seleziona il cavallo nero",
            "Seleziona il gatto",
            "Seleziona la tazza blu",
            "Seleziona il cane che sta mangiando",
            "Seleziona la scarpa.",
            "Seleziona il cappello",
            "Seleziona il tavolo.",
            "Seleziona la mela",
            "Seleziona il calciatore",
            "Seleziona chi raccoglie il fiore",
            "Seleziona la ragazza Seduta",
            "Seleziona chi legge",
            "Seleziona la matita lunga",
            "Seleziona il ragazzo più alto",
            "Seleziona il pettine rosso",
            "Seleziona il cavallo nero",
            "Seleziona il gatto",
            "Seleziona la tazza blu",
            "Seleziona il cane che sta mangiando",
            // altre domande se necessario...
    };

    // Array di risposte corrette (gli indici degli aree corrette: 1,2,3,4)
    private final int[] risposteCorrette = {
            2,4,1,4,1,3,1,2,1,2,2,3,1,1,4,
            1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
            // Assicurati che il numero di elementi corrisponda a quello di "domande"
    };
    private Runnable timeoutRunnable; // Runnable per il timeout del suono
    private Handler handler = new Handler();
    private PepperController pepperController;
    private QiContext qiContext;
    private boolean immagineToccata = false;
    private int contaRisposteSbagliate = 0;
    private int contaRisposteCorrette = 0;
    private long startTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trog);
        startTime = System.currentTimeMillis();
        // Registra l'Activity per ricevere i callback del robot
        QiSDK.register(this, this);

        // Inizializza il Text-to-Speech

        // Inizializza il PepperController
        pepperController = new PepperController(this);

        // Se il QiContext è già presente nel singleton, lo imposta nel controller
        if (PepperSingleton.getInstance().getQiContext() != null) {
            pepperController.setQiContext(PepperSingleton.getInstance().getQiContext());
        } else {
            Log.d("TrogActivity", "QiContext non disponibile inizialmente.");
        }

    }

    @Override
    protected void onDestroy() {
        // Rilascia le risorse
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
        Log.d("TrogActivity", "Robot focus acquisito.");

    }

    // Callback: il robot ha perso il focus
    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
        pepperController.setQiContext(null);
        Log.d("TrogActivity", "Robot focus perso.");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.e("TrogActivity", "Focus rifiutato: " + reason);
    }

    // Metodo chiamato per cambiare immagine e leggere la domanda successiva
    public void change(View view) {
        immagineToccata = false;
        attendi30Secondi();
        Log.d("TrogActivity", "Sono entrato nel metodo change");
        CounterSingleton contatore = CounterSingleton.getInstance();
        contatore.incrementaCont();
        int cont = contatore.getCont();
        String stringacont = Integer.toString(cont);
        String temp;
        if (cont < 10) {
            temp = "pic0" + stringacont;
            Log.d("TrogActivity", "Il valore di temp è: " + temp);
        } else {
            temp = "pic" + stringacont;
        }

        if (contatore.getCont() <= 30) {
            ImageView imageView = findViewById(R.id.imageView2);
            int idImmagine = getResources().getIdentifier(temp, "drawable", getPackageName());
            imageView.setImageResource(idImmagine);
            // Legge la domanda corrispondente tramite TTS
            String domanda = domande[cont - 1]; // le domande partono da indice 0
            if (pepperController != null) {
                pepperController.speak(domanda);  // Fa parlare Pepper
            }
            // Imposta il listener per il tocco
            imageView.setOnTouchListener((v, event) -> gestisciTocco(event, cont - 1));
        } else {
            contatore.setCount(0);
            Log.d("TrogActivity", "Fine domande, cambio vista");
            if (timeoutRunnable != null) {
                handler.removeCallbacks(timeoutRunnable);
                timeoutRunnable = null;
            }
            long endTime = System.currentTimeMillis();
            int tempoImpiegato = (int) (endTime - startTime); // Tempo in millisecondi
            contatore.setTempoImpiegatoFineTest(tempoImpiegato);
            Intent intent = new Intent(TrogActivity.this, TestTerminatoActivity.class);
            startActivity(intent);
        }
    }

    // Metodo per gestire il tocco sull'immagine
    private boolean gestisciTocco(MotionEvent event, int domandaIndex) {
        // Ottieni le coordinate del tocco (x, y)
        float x = event.getX();
        float y = event.getY();

        ImageView imageView = findViewById(R.id.imageView2);
        int width = imageView.getWidth();
        int height = imageView.getHeight();

        int areaToccata = -1;
        if (x < width / 2 && y < height / 2) {
            areaToccata = 1; // Area 1 (in alto a sinistra)
        } else if (x >= width / 2 && y < height / 2) {
            areaToccata = 2; // Area 2 (in alto a destra)
        } else if (x < width / 2 && y >= height / 2) {
            areaToccata = 3; // Area 3 (in basso a sinistra)
        } else if (x >= width / 2 && y >= height / 2) {
            areaToccata = 4; // Area 4 (in basso a destra)
        }

        Log.d("TrogActivity", "Area toccata: " + areaToccata);

        // Controlla se la risposta è corretta
        if (areaToccata == risposteCorrette[domandaIndex]) {
            CounterSingleton.getInstance().incrementaPunteggio(); // Incrementa il punteggio
            Log.d("TrogActivity", "Risposta corretta!");
            gestisciRisposteCorretta();
            // Fai parlare Pepper per incoraggiare l'utente
            if (pepperController != null) {
                pepperController.incoraggiaUtente();
            }
        } else {
            Log.d("TrogActivity", "Risposta sbagliata.");
            gestisciRisposteSbagliate();
        }

        // Rimuove il listener per evitare tocchi multipli
        imageView.setOnTouchListener(null);
        // Segna che l'utente ha interagito con l'immagine
        immagineToccata = true;

        // Annulla il timeout se c'è stata interazione
        if (timeoutRunnable != null) {
            handler.removeCallbacks(timeoutRunnable);  // Rimuove il runnable che suonerebbe
        }

        return true;
    }
    //buggato se utente interagisce suona comunque
    // il metodo geenra un suono nel caso il bimbo è distratto ed impiega troppo a rispondere per raccogliere l'attenzione
    private void attendi30Secondi() {
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (!immagineToccata) { // Esegui il suono solo se non c'è stata interazione
                    Log.d("Attesa", "30 secondi passati!");
                    pepperController.soundAttention();
                    pepperController.speak("forza non mollare!");
                    CounterSingleton contatore = CounterSingleton.getInstance();
                    contatore.incrementaNumeroSoundAttention();
                }
            }
        };
        handler.postDelayed(timeoutRunnable, 30000); // Esegui dopo 30 secondi
    }

    private void gestisciRisposteSbagliate(){
        if(contaRisposteSbagliate >= 3){
            contaRisposteSbagliate = 0;
            if (qiContext != null) {
                // Esegui animazione di saluto
                if(pepperController != null)
                    pepperController.playAnimation(R.raw.respiro);

                // Aggiungi una pausa di 3 secondi (3000 ms)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Dopo 3 secondi, far parlare Pepper
                        pepperController.speak("fai un lugo respiro e poi riparti!" );
                    }
                }, 3000);  // La pausa è di 3000 millisecondi (3 secondi)
            }

        }
        else{
            contaRisposteSbagliate++;
            CounterSingleton contatore = CounterSingleton.getInstance();
            contatore.incrementaNumeroRisposteErrate();
        }
    }
    private void gestisciRisposteCorretta(){
        if(contaRisposteCorrette>= 10){
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
                        pepperController.speak("fai un lugo respiro e poi riparti!" );
                    }
                }, 3000);  // La pausa è di 3000 millisecondi (3 secondi)
            }

        }
        else
            contaRisposteCorrette++;
    }


}
