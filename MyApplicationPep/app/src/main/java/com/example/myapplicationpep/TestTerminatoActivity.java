package com.example.myapplicationpep;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class TestTerminatoActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private PepperController pepperController;
    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_terminato);

        // Registra l'Activity per ricevere i callback del robot
        QiSDK.register(this, this);

        // Inizializza il PepperController
        pepperController = new PepperController(this);

        // Recupera il punteggio dal CounterSingleton e aggiornalo nella TextView
        CounterSingleton contatore = CounterSingleton.getInstance();
        int punteggio = contatore.getPoint();
        TextView punti = findViewById(R.id.punti);
        punti.setText("" + punteggio);

        // Se il QiContext è già disponibile (caso raro), fai parlare Pepper
        if (qiContext != null) {
            pepperController.soundSuccess();
            pepperController.speak("Il tuo punteggio è " + punteggio);
        }

    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this);
        super.onDestroy();
    }

    // Callback: il robot ha acquisito il focus
    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        pepperController.setQiContext(qiContext);
        PepperSingleton.getInstance().setQiContext(qiContext);
        Log.d("TestTerminatoActivity", "Robot focus acquisito.");

        // Quando il focus è acquisito, fai parlare Pepper con il punteggio
        int punteggio = CounterSingleton.getInstance().getPoint();
        pepperController.soundSuccess();
        pepperController.speak("Il tuo punteggio è " + punteggio);
    }

    // Callback: il robot ha perso il focus
    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
        pepperController.setQiContext(null);
        Log.d("TestTerminatoActivity", "Robot focus perso.");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.e("TestTerminatoActivity", "Focus rifiutato: " + reason);
    }

    // Metodo per passare alla vista di salvataggio dei dati
    public void goToSave(View view) {
        Log.d("TestTerminatoActivity", "Cambio vista per salvare i dati");
        salvaDatiTest();
        Intent intent = new Intent(TestTerminatoActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void salvaDatiTest()
    {
        CounterSingleton paziente = CounterSingleton.getInstance();
        int punteggio = paziente.getPoint();
        int risposteErrate = paziente.getNumeroRisposteErrate();
        int numeroSoundAttention = paziente.getNumeroSoundAttention();
        int tempoImpiegatoFineTest = paziente.getTempoImpiegatoFineTest();
        String nomeTest = paziente.getTestAvviato();
        String pazienteSelezionato = paziente.getBambinoSelezionato();
        String nomeBambino,cognomeBambino;
        String[] parts = pazienteSelezionato.split(" ");
        nomeBambino = parts[0];  // Il primo elemento è il nome
        cognomeBambino = parts[1];  // Il secondo elemento è il cognome
        System.out.println("Nome: " + nomeBambino);
        System.out.println("Cognome: " + cognomeBambino);
        System.out.println("Punteggio: " + punteggio);
        System.out.println("Risposte Errate: " + risposteErrate);
        System.out.println("Numero Sound Attentio: " + numeroSoundAttention);
        System.out.println("Tempo Impiegato Per Finire il test: " + tempoImpiegatoFineTest);
        Bambino bambino = new Bambino(nomeBambino,cognomeBambino,punteggio,tempoImpiegatoFineTest,risposteErrate,numeroSoundAttention);
        SalvaPunteggioTest punteggioTest = new SalvaPunteggioTest(this);
        punteggioTest.salvaPunteggio(bambino,nomeTest);
        punteggioTest.salvaInfoDiagrammi(bambino);
        paziente.reset();

    }

}
