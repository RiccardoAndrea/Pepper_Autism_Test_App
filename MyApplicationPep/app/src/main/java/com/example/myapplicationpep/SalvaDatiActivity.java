package com.example.myapplicationpep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class SalvaDatiActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private PepperController pepperController;
    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salva_dati);

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
        pepperController.speak("Dottore, inserisca i dati del suo paziente");
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

    // Metodo chiamato quando l'utente preme il pulsante per salvare i dati
    public void salvataggio(View view) {
        EditText editText1 = findViewById(R.id.editText1);
        String text1 = editText1.getText().toString().trim();

        EditText editText2 = findViewById(R.id.editText2);
        String text2 = editText2.getText().toString().trim();

        EditText editText3 = findViewById(R.id.editText3);
        String text3 = editText3.getText().toString().trim();

        EditText editText4 = findViewById(R.id.editText4);
        String text4 = editText3.getText().toString().trim();

        // Controllo che tutti i campi siano compilati
        if (text1.isEmpty() || text2.isEmpty() || text3.isEmpty()) {
            Toast.makeText(this, "Tutti i campi sono obbligatori!", Toast.LENGTH_SHORT).show();
            return; // Blocca l'esecuzione se i dati non sono completi
        }
        int text5 = 0;
        if (!text3.isEmpty()) {
            text5 = Integer.parseInt(text3);
        }

        Bambino babe = new Bambino(text1, text2, text4,text5);
        String datiBambino =
                        "Età: " + babe.getEta();
        System.out.println("dati bambino: " + babe.getNome() + " " + babe.getCognome() + " " + babe.getEta());

        salvaDatiSuFile(babe.getNome(), babe.getCognome(), datiBambino);

        Intent intent = new Intent(SalvaDatiActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void salvaDatiSuFile(String nome, String cognome, String data) {
        // Ottieni l'email del medico
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailMedico = userSession.getEmail();

        // Sanifica l'email per evitare caratteri non validi nei nomi dei file
        String emailSanitized = emailMedico.replaceAll("[/\\\\:*?\"<>|@.]", "_");
        File userDir = new File(getFilesDir(), emailSanitized);

        // Se la directory non esiste, creala
        if (!userDir.exists()) {
            Toast.makeText(this, "Errore nella apertura della cartella!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Crea una directory con il nome e cognome del bambino dentro la directory del medico
        File bambinoDir = new File(userDir, nome + "_" + cognome);
        if (!bambinoDir.exists()) {
            if (!bambinoDir.mkdirs()) {
                Toast.makeText(this, "Errore nella creazione della cartella del bambino!", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        // Nome del file con nome e cognome
        String fileName = "dati_test.txt";
        File file = new File(bambinoDir, fileName); // Percorso completo del file

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true); // true = modalità append
            fos.write(data.getBytes());
            System.out.println("File scritto con successo: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(this, "Dati salvati con successo", Toast.LENGTH_SHORT).show();
    }

    public void tornaIndietro(View view) {
        onBackPressed();
    }

}
