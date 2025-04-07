package com.example.myapplicationpep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends Activity implements RobotLifecycleCallbacks {

    private QiContext qiContext;  // Riferimento a QiContext per Pepper
    private PepperController pepperController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Registra l'Activity a QiSDK
        QiSDK.register(this, this);

        // Crea l'istanza di PepperController
        pepperController = new PepperController(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Puoi mettere qualsiasi azione qui che deve essere eseguita quando l'Activity riprende
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnessione da QiSDK
        QiSDK.unregister(this);
    }

    // Callback di QiSDK
    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        pepperController.setQiContext(qiContext);  // Imposta il QiContext nel controller
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Toast.makeText(this, "Focus rifiutato: " + reason, Toast.LENGTH_SHORT).show();
    }

    // Metodo di login con interazione vocale con Pepper
    public void effettuaAccesso(View view) {
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        TextView emailTextView = findViewById(R.id.email);
        TextView passwordTextView = findViewById(R.id.password);

        // Creiamo un oggetto Medico con i dati inseriti
        Medico medicoInserito = new Medico(emailTextView.getText().toString(), passwordTextView.getText().toString());

        // Sostituiamo SOLO i caratteri non validi nei nomi delle cartelle
        String emailSanitized = medicoInserito.getEmail().replaceAll("[/\\\\:*?\"<>|@.]", "_");
        System.out.println("Email sanificata per directory: " + emailSanitized);

        // Cartella privata dell'utente
        File userDir = new File(getFilesDir(), emailSanitized);
        System.out.println("Percorso cartella utente: " + userDir.getAbsolutePath());

        File file = new File(userDir, "datiAccesso.txt");
        System.out.println("Percorso file credenziali: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("File credenziali non trovato! Accesso negato.");
            Toast.makeText(this, "Email o password errate!", Toast.LENGTH_SHORT).show();
            if (qiContext != null) {
                pepperController.speak("Email o password errate, riprova!");
            }
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            // Leggiamo email e password salvate
            String emailSalvata = reader.readLine().replace("Email: ", "").trim();
            String passwordSalvata = reader.readLine().replace("Password: ", "").trim();

            System.out.println("Email inserita: " + emailSalvata);
            System.out.println("Password inserita: " + passwordSalvata);

            Medico medicoSalvato = new Medico(emailSalvata, passwordSalvata);

            // Confrontiamo direttamente gli oggetti Medico
            if (medicoInserito.getEmail().equals(medicoSalvato.getEmail()) &&
                    medicoInserito.getPassword().equals(medicoSalvato.getPassword())) {
                System.out.println("Login riuscito!");
                userSession.setIsLogged(true);
                userSession.setEmail(medicoInserito.getEmail());

                if (qiContext != null) {
                    // Esegui animazione di saluto
                    pepperController.playAnimation(R.raw.hello);

                    // Aggiungi una pausa di 3 secondi (3000 ms)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Dopo 3 secondi, far parlare Pepper
                            pepperController.speak("Login riuscito. Benvenuto, " + medicoInserito.getEmail() + "!");
                            // Vai alla MainActivity
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }, 3000);  // La pausa è di 3000 millisecondi (3 secondi)
                }
            } else {
                System.out.println("Email o password errate!");
                Toast.makeText(this, "Email o password errate!", Toast.LENGTH_SHORT).show();
                if (qiContext != null) {
                    pepperController.speak("Email o password errate, riprova!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nella lettura del file!");
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
