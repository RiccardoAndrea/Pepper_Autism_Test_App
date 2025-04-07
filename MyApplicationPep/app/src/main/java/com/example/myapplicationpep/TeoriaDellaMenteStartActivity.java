package com.example.myapplicationpep;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class TeoriaDellaMenteStartActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private PepperController pepperController;
    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teoria_della_mente_start);

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
        pepperController.speak("Ascolta e vedi il video Sul tablet");
    }

    @Override
    public void onRobotFocusLost() {
        // Il focus è stato perso, rimuovi il riferimento
        this.qiContext = null;
        pepperController.setQiContext(null);

    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.e("TeoriaDellaMente", "Focus rifiutato: " + reason);
    }

    // Funzione per tornare indietro
    public void tornaIndietro(View view) {
        onBackPressed();
    }

    // Funzione per iniziare il test
    public void iniziaTest(View view) {
        // Verifica se il QiContext è disponibile (ossia, il focus è acquisito)
        System.out.println("SONO DENTRO INIZIATEST TEORIA DELLA MENTE");
        Intent intent = new Intent(TeoriaDellaMenteStartActivity.this, TeoriaDellaMenteActivity.class);
        startActivity(intent);
    }
}