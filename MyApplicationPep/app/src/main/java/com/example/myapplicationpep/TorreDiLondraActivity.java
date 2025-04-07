package com.example.myapplicationpep;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class TorreDiLondraActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private PepperController pepperController;
    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torre_di_londra);

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
        Log.d("TorreDiLondraActivity", "Focus acquisito, QiContext impostato.");
    }

    @Override
    public void onRobotFocusLost() {
        // Il focus è stato perso, rimuovi il riferimento
        this.qiContext = null;
        pepperController.setQiContext(null);
        Log.d("TorreDiLondraActivity", "Focus perso.");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.e("TorreDiLondraActivity", "Focus rifiutato: " + reason);
    }

    // Funzione per tornare indietro
    public void tornaIndietro(View view) {
        onBackPressed();
    }

    // Funzione per iniziare il test
    public void iniziaTest(View view) {
        // Verifica se il QiContext è disponibile (ossia, il focus è acquisito)
        System.out.println("SONO DENTRO INIZIATEST A TORRE DI LONDRA");
        if (qiContext != null) {
            pepperController.speak("Andiamo!");
            pepperController.soundAttention();
            pepperController.playAnimation(R.raw.hello);
        } else {
            Log.e("TorreDiLondraActivity", "Impossibile far parlare Pepper: QiContext non disponibile!");
        }
        // Aggiungi qui il codice per avviare il test...
    }
}