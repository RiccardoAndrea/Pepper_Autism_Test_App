package com.example.myapplicationpep;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PunteggioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punteggio);

        // Ottieni il layout dal file XML
        LinearLayout layout = findViewById(R.id.dynamicLayout);

        // Ottieni l'elenco dei pazienti
        String[] nomiPazienti = visualizzaPazienti();

        if (nomiPazienti.length == 0) {
            TextView textView = new TextView(this);
            textView.setText("Nessun paziente registrato!");
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);
        } else {
            for (String nome : nomiPazienti) {
                // Layout orizzontale per la cartella
                LinearLayout folderLayout = new LinearLayout(this);
                folderLayout.setOrientation(LinearLayout.HORIZONTAL);
                folderLayout.setGravity(Gravity.CENTER_VERTICAL);
                folderLayout.setPadding(20, 20, 20, 20);
                folderLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                // Immagine della cartella
                ImageView folderIcon = new ImageView(this);
                folderIcon.setImageResource(R.drawable.cartella); // Usa un'icona della cartella
                folderIcon.setLayoutParams(new LinearLayout.LayoutParams(100, 100)); // Imposta dimensione icona

                // Nome del paziente
                TextView folderLabel = new TextView(this);
                folderLabel.setText(nome);
                folderLabel.setTextSize(18);
                folderLabel.setTextColor(Color.BLACK);
                folderLabel.setPadding(20, 0, 0, 0); // Spazio tra icona e testo


                // Click sulla cartella per aprire i dettagli
                folderLayout.setOnClickListener(v -> {
                    CounterSingleton nomePaziente = CounterSingleton.getInstance();
                    nomePaziente.setBambinoSelezionato(nome);
                    Intent intent = new Intent(PunteggioActivity.this,DettagliPazienteActivity.class);
                    startActivity(intent);
                });

                // Aggiungi icona e nome al layout della cartella
                folderLayout.addView(folderIcon);
                folderLayout.addView(folderLabel);

                // Aggiungi la cartella al layout principale
                layout.addView(folderLayout);
            }
        }
    }

    public String[] visualizzaPazienti() {
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailMedico = userSession.getEmail();

        // Sanifica l'email per creare la directory
        String emailSanitized = emailMedico.replaceAll("[/\\\\:*?\"<>|@.]", "_");

        File userDir = new File(getFilesDir(), emailSanitized);
        if (!userDir.exists() || !userDir.isDirectory()) {
            return new String[0];
        }

        List<String> pazientiList = new ArrayList<>();
        File[] files = userDir.listFiles();

        if (files == null || files.length == 0) {
            return new String[0];
        }

        for (File file : files) {
            if (file.isDirectory()) { // Controlla se è una cartella (quindi un paziente)
                String pazienteNome = file.getName().replace("_", " ");
                pazientiList.add(pazienteNome);
            }
        }

        return pazientiList.toArray(new String[0]);
    }



}
