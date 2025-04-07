package com.example.myapplicationpep;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class AggiungiNotaActivity extends AppCompatActivity {

    private EditText noteEditText;
    private Button salvaNoteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_nota);
        // Inizializza i componenti dell'interfaccia
        noteEditText = findViewById(R.id.textNota);
        salvaNoteButton = findViewById(R.id.salvaNota);
        // Quando l'utente preme il bottone per salvare le note
        salvaNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = noteEditText.getText().toString();
                if (note.isEmpty()) {
                    Toast.makeText(AggiungiNotaActivity.this, "Inserisci una nota prima di salvare", Toast.LENGTH_SHORT).show();
                } else {
                    // Salva le note (metodo di esempio per la gestione delle note)
                    salvaNote(AggiungiNotaActivity.this , note);
                }
            }
        });

    }

    // Metodo per salvare le note
    private void salvaNote(Context context, String note) {
        // Recupera l'email del medico per creare un file specifico
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailMedico = userSession.getEmail();

        // Sanifica l'email per evitare caratteri non validi nei nomi dei file
        String emailSanitized = emailMedico.replaceAll("[/\\\\:*?\"<>|@.]", "_");

        // Crea un file per salvare le note del medico
        File medicoDir = new File(context.getFilesDir(), emailSanitized);

        if (!medicoDir.exists()) {
            Toast.makeText(context, "Errore cartella non trovata!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nome del file con il nome del paziente
        String pazienteSelezionato = CounterSingleton.getInstance().getBambinoSelezionato();
        String[] parts = pazienteSelezionato.split(" ");
        String nomeBambino = parts[0];
        String cognomeBambino = parts[1];
        File bambinoDir = new File(medicoDir, nomeBambino + "_" + cognomeBambino);
        if (!bambinoDir.exists()) {
            Toast.makeText(context, "Errore nella creazione della cartella del bambino!", Toast.LENGTH_SHORT).show();
            return;
        }
        String fileName = "note.txt";
        File noteFile = new File(bambinoDir, fileName);

        // Aggiungi la data e la nota del medico
        String datiNote = "Data: " + getDataOggi() + "\n" +
                "Nota: " + note + "\n\n";

        // Scrivi le note nel file
        try (FileOutputStream fos = new FileOutputStream(noteFile, true)) { // true = modalità append
            fos.write(datiNote.getBytes());
            fos.flush();
            onBackPressed();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Errore nel salvataggio delle note", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo per ottenere la data odierna
    private String getDataOggi() {
        Bambino bambino = new Bambino();
        return bambino.getDataTest();

    }

    public void tornaIndietro(View view) {
        onBackPressed();
    }
}
