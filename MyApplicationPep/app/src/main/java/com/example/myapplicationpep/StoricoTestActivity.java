package com.example.myapplicationpep;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class StoricoTestActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storico_test);
        CounterSingleton nomePaziente = CounterSingleton.getInstance();
        String datiPaziente = leggiDatiDaFile(nomePaziente.getBambinoSelezionato());
        mostraDialog(datiPaziente);


    }

    private String leggiDatiDaFile(String nome) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            // Recupera l'email del medico e ottieni la directory corretta
            UserSessionSingleton userSession = UserSessionSingleton.getInstance();
            String emailSanitized = userSession.getEmail().replaceAll("[/\\\\:*?\"<>|@.]", "_");
            File userDir = new File(getFilesDir(), emailSanitized);
            File bambinoDir = new File(userDir, nome.replace(" ", "_")); // Cartella del bambino
            File file = new File(bambinoDir, "dati_test.txt"); // Nome fisso del file dei dati

            if (!file.exists()) {
                return "Nessun dato disponibile per questo paziente.";
            }

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            reader.close();
            fis.close();
        } catch (IOException e) {
            return "Errore nel leggere il file!";
        }

        return stringBuilder.toString();
    }


    private void mostraDialog(String datiPaziente) {
        new AlertDialog.Builder(this)
                .setTitle("Dati del Paziente")
                .setMessage(datiPaziente)
                .setPositiveButton("Chiudi", (dialog, which) -> { onBackPressed();})
                .setCancelable(true)
                .show();
    }

}
