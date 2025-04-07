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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class SelezionaPazienteActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private PepperController pepperController;
    private QiContext qiContext;
    private String testScelto;
    private ImageView imageViewAvanti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleziona_paziente);
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
        Button myButton = findViewById(R.id.selezionaPaziente);
        myButton.setText("Seleziona Paziente");

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
    public void scegliPaziente(View view) {

        // Ottieni la lista dei pazienti dalla cartella del medico
        String[] pazienti = visualizzaPazienti();

        if (pazienti.length == 0) {
            // Se non ci sono pazienti, mostra un messaggio di errore
            Toast.makeText(this, "Nessun paziente trovato!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crea il PopupMenu per i pazienti
        PopupMenu popupMenu = new PopupMenu(this, view);
        for (String paziente : pazienti) {
            paziente = paziente.replace("_", " ");
            popupMenu.getMenu().add(paziente); // Aggiungi i pazienti al menu
        }

        // Gestisci la selezione del paziente
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            String pazienteSelezionato = menuItem.getTitle().toString();  // Salva il nome del paziente selezionato
            // Rimuovi il trattino basso e sostituiscilo con uno spazio
            pazienteSelezionato = pazienteSelezionato.replace("_", " ");

            // Fai parlare Pepper per confermare la selezione
            pepperController.speak("Hai scelto il paziente " + pazienteSelezionato);

            // Seleziona il nome del paziente e fai comparire il pulsante per continuare
            Button myButton = findViewById(R.id.selezionaPaziente);
            myButton.setText("Test per " + pazienteSelezionato);
            CounterSingleton paziente = CounterSingleton.getInstance();
            paziente.setBambinoSelezionato(pazienteSelezionato);
            // Mostra il pulsante per andare avanti
            imageViewAvanti.setVisibility(View.VISIBLE);

            return true;
        });

        // Mostra il menu
        popupMenu.show();
    }


    // Metodo per iniziare il test
    public void iniziaTest(View view) {


        // Avvia l'attività del test scelto
        Intent intent;
        intent = new Intent(SelezionaPazienteActivity.this, SelezionaTestActivity.class);
        startActivity(intent);

    }

    public String[] visualizzaPazienti() {
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailMedico = userSession.getEmail();

        // Sanifica l'email del medico per creare la directory
        String emailSanitized = emailMedico.replaceAll("[/\\\\:*?\"<>|@.]", "_");

        File userDir = new File(getFilesDir(), emailSanitized);
        if (!userDir.exists() || !userDir.isDirectory()) {
            return new String[0];
        }

        List<String> pazientiList = new ArrayList<>();
        File[] directories = userDir.listFiles(File::isDirectory); // Ottieni solo le directory

        if (directories == null || directories.length == 0) {
            return new String[0];
        }

        for (File directory : directories) {
            String directoryName = directory.getName();
            // Sanifica il nome della directory del paziente
            String sanitizedPazienteName = directoryName.replaceAll("[/\\\\:*?\"<>|]", "_");
            pazientiList.add(sanitizedPazienteName); // Aggiungi il nome sanificato
        }
        return pazientiList.toArray(new String[0]);
    }


    public void tornaIndietro(View view) {
        onBackPressed();
    }
}
