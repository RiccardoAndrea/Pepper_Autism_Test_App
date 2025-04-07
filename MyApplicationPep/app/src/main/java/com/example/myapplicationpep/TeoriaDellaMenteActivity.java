package com.example.myapplicationpep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class TeoriaDellaMenteActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 1001;
    private VideoView videoView;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private TextView recordingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teoria_della_mente);

        videoView = findViewById(R.id.videoView);
        recordingText = findViewById(R.id.recordingText);
        // Verifica i permessi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        } else {
            // Avvia il video se i permessi sono già concessi
            startVideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permessi concessi, avvia il video
                startVideo();
            } else {
                // Permessi negati, mostra un messaggio
                Toast.makeText(this, "I permessi sono necessari per registrare l'audio.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startVideo() {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/raw/teoriadellamente");
        videoView.setVideoURI(videoUri);
        Log.d("TeoriaDellaMente", "Video URI: " + videoUri.toString());

        // Imposta il listener per l'eventuale completamento del video
        videoView.setOnCompletionListener(mp -> startRecording());
        videoView.start();
    }

    private void startRecording() {
        videoView.setVisibility(VideoView.INVISIBLE);  // Nascondi il video
        recordingText.setVisibility(TextView.VISIBLE);  // Mostra il testo
        try {
            // Verifica se MediaRecorder è già configurato o meno
            if (mediaRecorder == null) {
                audioFilePath = getAudioFilePath();
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  // Usa il microfono per la registrazione
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                mediaRecorder.setOutputFile(audioFilePath);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.prepare();
                mediaRecorder.start();
                Log.d("TeoriaDellaMente", "File audio salvato in: " + audioFilePath);
                Log.d("TeoriaDellaMente", "Registrazione audio avviata.");
            } else {
                Log.d("TeoriaDellaMente", "MediaRecorder è già stato configurato.");
            }
        } catch (IOException e) {
            Log.e("TeoriaDellaMente", "Errore durante la configurazione di MediaRecorder", e);
            Toast.makeText(this, "Errore nella configurazione del registratore audio.", Toast.LENGTH_SHORT).show();
        }

        // Ferma la registrazione dopo 30 secondi
        videoView.postDelayed(this::stopRecording, 30000);
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            videoView.postDelayed(() -> {
                mediaRecorder.release();
                mediaRecorder = null;
            }, 500);
            mediaRecorder = null;
            Log.d("TeoriaDellaMente", "Registrazione audio fermata.");
            // Verifica se il file è stato salvato
            File file = new File(audioFilePath);
            if (file.exists()) {
                Log.d("TeoriaDellaMente", "File salvato con successo: " + file.getAbsolutePath());
                Toast.makeText(this, "Registrazione salvata in: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } else {
                Log.e("TeoriaDellaMente", "Errore: file NON trovato dopo la registrazione!");
                Toast.makeText(this, "Errore: registrazione non salvata!", Toast.LENGTH_SHORT).show();
            }
        }
        // Una volta fermata la registrazione, torna alla MainActivity
        Intent intent = new Intent(TeoriaDellaMenteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Questo chiude l'activity corrente
    }

    private String getAudioFilePath() {
        // Ottieni la sessione utente
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailSanitized = userSession.getEmail().replaceAll("[/\\\\:*?\"<>|@.]", "_");  // Sanitizza l'email
        String bambinoNome = CounterSingleton.getInstance().getBambinoSelezionato().replace(" ", "_");  // Sanitizza il nome del bambino

        // Crea il percorso per il file audio del bambino
        File userDir = new File(getFilesDir(), emailSanitized);  // Directory del medico
        File bambinoDir = new File(userDir, bambinoNome);  // Directory del bambino
        if (!bambinoDir.exists()) {
            Log.e("TeoriaDellaMente", "La directory del bambino non esiste: " + bambinoDir.getAbsolutePath());
            return null;  // Restituisce null se la directory non esiste
        }

        // Usa un timestamp e data per garantire un nome univoco
        Bambino bambino = new Bambino();
        String dataDiOggi = bambino.getDataTest();
        dataDiOggi.replaceAll("[/\\\\:*?\"<>|@.]", "_");
        System.out.println("la data di registrazione e: " + dataDiOggi);
        String timestamp = String.valueOf(System.currentTimeMillis());  // Ottieni il timestamp corrente
        return new File(bambinoDir, "registrazione_" + dataDiOggi + "_" + timestamp + ".3gp").getAbsolutePath();  // Percorso completo del file audio
    }

}
