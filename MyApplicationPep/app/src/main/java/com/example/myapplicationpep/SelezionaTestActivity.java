package com.example.myapplicationpep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class SelezionaTestActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private PepperController pepperController;
    private QiContext qiContext;
    private String testScelto;
    private ImageView imageViewAvanti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleziona_test);
        imageViewAvanti = findViewById(R.id.avanti);
        // Registra l'Activity per ricevere i callback del robot
        QiSDK.register(this, this);

        // Inizializza il PepperController
        pepperController = new PepperController(this);
        // Se il QiContext è già presente nel singleton, impostalo nel controller
        if (PepperSingleton.getInstance().getQiContext() != null) {
            pepperController.setQiContext(PepperSingleton.getInstance().getQiContext());
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Reset del test scelto
        testScelto = null;

        // Ripristina il testo del pulsante
        Button myButton = findViewById(R.id.selezionaTest);
        myButton.setText("Seleziona Test");

        // Rendi invisibile l'ImageView
        if (imageViewAvanti != null) {
            imageViewAvanti.setVisibility(View.INVISIBLE);
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
        // Quando il focus è acquisito, fai parlare Pepper
        pepperController.speak("Seleziona il test!");
    }

    // Callback: il robot ha perso il focus
    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
        pepperController.setQiContext(null);
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // Puoi gestire il rifiuto del focus se necessario
    }

    // Metodo per gestire il click sul pulsante "Seleziona Test"
    public void scegliTest(View view) {

        final String[] test = {"T.R.O.G", "CLOZE TEST", "TEORIA DELLA MENTE"};

        // Crea il PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, view);
        for (String item : test) {
            popupMenu.getMenu().add(item);
        }

        // Gestisci la selezione del test
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            testScelto = menuItem.getTitle().toString();  // Salva il test scelto
            Button myButton = findViewById(R.id.selezionaTest);
            myButton.setText(testScelto);  // Cambia il testo del pulsante con il test scelto

            // Fai parlare Pepper per confermare
            pepperController.speak("Hai scelto il test " + testScelto);
            if (testScelto == null)
                imageViewAvanti.setVisibility(View.INVISIBLE);
            else
                imageViewAvanti.setVisibility(View.VISIBLE);
            return true;
        });

        // Mostra il menu
        popupMenu.show();

    }

    // Metodo per iniziare il test
    public void iniziaTest(View view) {

            pepperController.speak("Iniziamo il test: " + testScelto);
            CounterSingleton testAvviato = CounterSingleton.getInstance();
            testAvviato.setTestAvviato(testScelto);
            // Avvia l'attività del test scelto
            Intent intent;
            if ("T.R.O.G".equals(testScelto)) {
                intent = new Intent(SelezionaTestActivity.this, TrogStartActivity.class);
            } else if("CLOZE TEST".equals(testScelto)) {
                intent = new Intent(SelezionaTestActivity.this, ClozeTestStartActivity.class);
            }
            else if("TEORIA DELLA MENTE".equals(testScelto)){
                intent = new Intent(SelezionaTestActivity.this, TeoriaDellaMenteStartActivity.class);
            }
            else
                intent = new Intent(SelezionaTestActivity.this, MainActivity.class);
            startActivity(intent);

    }


    public void tornaIndietro(View view) {
        onBackPressed();
    }
}
