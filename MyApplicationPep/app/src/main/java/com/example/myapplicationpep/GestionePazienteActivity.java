package com.example.myapplicationpep;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class GestionePazienteActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private PepperController pepperController;
    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestione_paziente);

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

    }

    @Override
    public void onRobotFocusLost() {
        // Il focus è stato perso, rimuovi il riferimento
        this.qiContext = null;
        pepperController.setQiContext(null);
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.e("GestionePaziente", "Focus rifiutato: " + reason);
    }

    // Funzione per tornare indietro
    public void tornaIndietro(View view) {
        onBackPressed();
    }

    // Funzione per iniziare il test
    public void visualizzaPaziente(View view) {
        // Verifica se il QiContext è disponibile (ossia, il focus è acquisito)
        Intent intent = new Intent(GestionePazienteActivity.this, PunteggioActivity.class);
        startActivity(intent);
        // Aggiungi qui il codice per avviare il test...
    }

    public void aggiungiPaziente(View view){
        Intent intent = new Intent(GestionePazienteActivity.this, SalvaDatiActivity.class);
        startActivity(intent);

    }
}