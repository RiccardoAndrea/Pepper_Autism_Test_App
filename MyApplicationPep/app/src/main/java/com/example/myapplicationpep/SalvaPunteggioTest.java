package com.example.myapplicationpep;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SalvaPunteggioTest {
    private Context context;

    public SalvaPunteggioTest(Context context) {
        this.context = context;
    }

    // Metodo che prende in ingresso i dati e salva nel file corrispondente
    public void salvaPunteggio(Bambino bambino, String nomeTest) {

        // Ottieni l'email del medico
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailMedico = userSession.getEmail();

        // Sanifica l'email per evitare caratteri non validi nei nomi dei file
        String emailSanitized = emailMedico.replaceAll("[/\\\\:*?\"<>|@.]", "_");

        // Percorso della cartella del medico
        File medicoDir = new File(context.getFilesDir(), emailSanitized);

        // Se la directory non esiste, creala
        if (!medicoDir.exists()) {
                Toast.makeText(context, "Errore cartella non trovata!", Toast.LENGTH_SHORT).show();
                return;
        }

        // Crea una directory con il nome e cognome del bambino dentro la directory del medico
        File bambinoDir = new File(medicoDir, bambino.getNome() + "_" + bambino.getCognome());
        if (!bambinoDir.exists()) {
                Toast.makeText(context, "Errore nella creazione della cartella del bambino!", Toast.LENGTH_SHORT).show();
                return;
        }

        // Nome del file con nome e cognome del bambino
        String fileName = "dati_test.txt";
        File file = new File(bambinoDir, fileName);

        // Formattazione dei dati da salvare
        String datiBambino = "\n"+"Test Avviato: " + nomeTest + "\n" +
                "Nome: " + bambino.getNome() + "\n" +
                "Cognome: " + bambino.getCognome() + "\n" +
                "Data: " + bambino.getDataTest() + "\n" +
                "Punteggio: " + bambino.getPunteggio() + "\n" +
                "Tempo Impiegato (s): " + bambino.getTempoImpiegatoFineTest() + "\n" +
                "Numero Risposte Errate: " + bambino.getNumeroRisposteErrate() + "\n" +
                "Numero Attenzioni Sonore: " + bambino.getNumeroSoundAttention() + "\n\n";

        // Scrivi i dati nel file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true); // true = modalità append
            fos.write(datiBambino.getBytes());
            fos.flush();
            Toast.makeText(context, "Dati salvati con successo", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Errore nel salvataggio dei dati!", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void salvaInfoDiagrammi(Bambino bambino) {

        // Ottieni l'email del medico
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailMedico = userSession.getEmail();

        // Sanifica l'email per evitare caratteri non validi nei nomi dei file
        String emailSanitized = emailMedico.replaceAll("[/\\\\:*?\"<>|@.]", "_");

        // Percorso della cartella del medico
        File medicoDir = new File(context.getFilesDir(), emailSanitized);

        // Se la directory del medico non esiste, crea la cartella
        if (!medicoDir.exists()) {
            Toast.makeText(context, "Errore cartella non trovata!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crea una directory con il nome e cognome del bambino dentro la directory del medico
        File bambinoDir = new File(medicoDir, bambino.getNome() + "_" + bambino.getCognome());
        if (!bambinoDir.exists()) {
            Toast.makeText(context, "Errore nella creazione della cartella del bambino!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nome del file info_diagrammi.txt
        String fileName = "info_diagrammi.txt";
        File file = new File(bambinoDir, fileName);

        // Leggi i dati esistenti nel file (se ci sono) e cumula i risultati
        int risposteEsattePrecedenti = 0;
        int risposteSbagliatePrecedenti = 0;
        int distrazioniPrecedenti = 0;

        // Se il file esiste già, recupera i valori precedenti
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Risposte Esatte:")) {
                        risposteEsattePrecedenti = Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Risposte Sbagliate:")) {
                        risposteSbagliatePrecedenti = Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Distrazioni:")) {
                        distrazioniPrecedenti = Integer.parseInt(line.split(":")[1].trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Errore nella lettura dei dati precedenti!", Toast.LENGTH_SHORT).show();
            }
        }

        // Cumulare i risultati con quelli già salvati
        risposteEsattePrecedenti += bambino.getPunteggio();
        risposteSbagliatePrecedenti += bambino.getNumeroRisposteErrate();
        distrazioniPrecedenti += bambino.getNumeroSoundAttention();

        // Formattazione dei dati da salvare
        String datiDiagrammi = "\n" +
                "Risposte Esatte: " + risposteEsattePrecedenti + "\n" +
                "Risposte Sbagliate: " + risposteSbagliatePrecedenti + "\n" +
                "Distrazioni: " + distrazioniPrecedenti + "\n\n";
        System.out.println("vediamo i dati che salveremo in info_diagrammi: " + datiDiagrammi);

        // Scrivi i dati nel file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false); // false = sovrascrive il file
            fos.write(datiDiagrammi.getBytes());
            fos.flush();
            Toast.makeText(context, "Dati Diagrammi salvati con successo", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Errore nel salvataggio dei dati!", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
