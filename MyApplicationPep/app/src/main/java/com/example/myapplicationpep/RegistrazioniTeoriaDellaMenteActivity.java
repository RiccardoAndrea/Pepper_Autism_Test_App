package com.example.myapplicationpep;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class RegistrazioniTeoriaDellaMenteActivity extends AppCompatActivity {

    private LinearLayout listaRegistrazioni;
    private MediaPlayer mediaPlayer;
    private File[] registrazioni; // Array di file audio

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazioni_teoria_della_mente);

        listaRegistrazioni = findViewById(R.id.listaRegistrazioni);
        mediaPlayer = new MediaPlayer();

        caricaRegistrazioni();
    }

    private void caricaRegistrazioni() {
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailSanitized = userSession.getEmail().replaceAll("[/\\\\:*?\"<>|@.]", "_");

        String pazienteSelezionato = CounterSingleton.getInstance().getBambinoSelezionato();
        String[] parts = pazienteSelezionato.split(" ");
        String nomeBambino = parts[0];
        String cognomeBambino = parts.length > 1 ? parts[1] : "";

        File userDir = new File(getFilesDir(), emailSanitized);
        File bambinoDir = new File(userDir, nomeBambino + "_" + cognomeBambino);

        System.out.println( "la cartella del bambino e: " + bambinoDir.getPath());
        LinearLayout listaRegistrazioni = findViewById(R.id.listaRegistrazioni);

        if (!bambinoDir.exists() || !bambinoDir.isDirectory()) {
            // Se non ci sono cartelle per il bambino, nascondi la lista

            Toast.makeText(this, "Nessuna registrazione trovata!", Toast.LENGTH_SHORT).show();
            return;
        }

        registrazioni = bambinoDir.listFiles((dir, name) -> name.endsWith(".3gp")); // Filtra solo file audio
        if (registrazioni == null || registrazioni.length == 0) {
            // Se non ci sono registrazioni, nascondi la lista

            Toast.makeText(this, "Nessuna registrazione disponibile!", Toast.LENGTH_SHORT).show();
        } else {
            // Se ci sono registrazioni, mostra la lista
            for (File file : registrazioni) {
                aggiungiElementoLista(file); // Aggiungi le registrazioni reali
            }
        }
    }

    private void aggiungiElementoLista(File file) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Gonfia il layout separato (solo la parte che rappresenta il contenuto del bottone)
        LinearLayout itemLayout = (LinearLayout) inflater.inflate(R.layout.item_registrazione, null);

        // Trova i riferimenti agli elementi all'interno del layout gonfiato
        TextView textView = itemLayout.findViewById(R.id.nomeRegistrazione);
        ImageView playButton = itemLayout.findViewById(R.id.playButton);
        ImageView deleteButton = itemLayout.findViewById(R.id.deleteButton);

        // Imposta il testo del TextView con il nome del file
        textView.setText(file.getName());

        // Cambia gli ID dei bottoni per evitare conflitti
        playButton.setId(View.generateViewId()); // Genera un ID unico per ogni playButton
        deleteButton.setId(View.generateViewId()); // Genera un ID unico per ogni deleteButton

        // Imposta il listener per il playButton
        playButton.setOnClickListener(v -> riproduciAudio(file));

        // Imposta il listener per il deleteButton
        deleteButton.setOnClickListener(v -> eliminaRegistrazione(file, itemLayout));

        // Aggiungi il layout alla lista
        listaRegistrazioni.addView(itemLayout);
    }


    private void riproduciAudio(File file) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepareAsync();  // Usa prepareAsync per non bloccare il thread principale
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                Toast.makeText(this, "Riproduzione avviata", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            Toast.makeText(this, "Errore nella riproduzione", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void eliminaRegistrazione(File file, View itemView) {
        if (file.delete()) {
            listaRegistrazioni.removeView(itemView);
            // Rimuovi il file dalla lista
            registrazioni = aggiornaRegistrazioni(file);
            Toast.makeText(this, "Registrazione eliminata", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Errore nell'eliminazione", Toast.LENGTH_SHORT).show();
        }
    }

    private File[] aggiornaRegistrazioni(File fileEliminato) {
        // Rimuovi il file eliminato dalla lista di registrazioni
        File[] nuoviFile = new File[registrazioni.length - 1];
        int i = 0;
        for (File file : registrazioni) {
            if (!file.equals(fileEliminato)) {
                nuoviFile[i++] = file;
            }
            System.out.println("la registrazione trovata: " + file.getPath());
        }
        return nuoviFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void tornaIndietro(View view) {
        onBackPressed();
    }
}
