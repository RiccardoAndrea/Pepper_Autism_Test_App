package com.example.myapplicationpep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class TrogStartActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private PepperController pepperController;
    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trog_start);

        // Inizializza il PepperController con il contesto dell'Activity
        pepperController = new PepperController(this);

        // Registra l'Activity per ricevere i callback del QiSDK
        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QiSDK.unregister(this);
    }

    // Callback di QiSDK: viene chiamato quando il robot acquisisce il focus
    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        pepperController.setQiContext(qiContext);
        // Aggiorna il singleton se necessario
        PepperSingleton.getInstance().setQiContext(qiContext);
        Log.d("TrogStartActivity", "Focus acquisito, QiContext impostato.");

        // Usa il metodo speak per dire "Seleziona le immagini indicate"
        pepperController.speak("Seleziona le immagini indicate");
    }

    @Override
    public void onRobotFocusLost() {
        // Il focus è stato perso, rimuovi il riferimento
        this.qiContext = null;
        pepperController.setQiContext(null);
        Log.d("TrogStartActivity", "Focus perso.");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.e("TrogStartActivity", "Focus rifiutato: " + reason);
    }

    // Funzione per andare alla schermata successiva
    public void goTrogActivity(View view) {
        Intent intent = new Intent(this, TrogActivity.class);
        startActivity(intent);
    }

    // Funzione per tornare indietro
    public void tornaIndietro(View view) {
        onBackPressed();  // Questo metodo chiama il comportamento di "indietro" del dispositivo
    }
}
