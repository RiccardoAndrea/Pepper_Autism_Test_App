package com.example.myapplicationpep;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GraficoTemporaleActivity extends AppCompatActivity {

    private LineChart lineChart;
    private Context context;
    // Lista per memorizzare le date per l'asse X
    private ArrayList<String> dateLabels = new ArrayList<>();

    // Classe interna per rappresentare un record del test
    private static class TestRecord {
        String data;
        float punteggio;
        float tempoImpiegato;
        float risposteErrate;
        float attenzioniSonore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico_temporale);

        context = this;
        lineChart = findViewById(R.id.lineChart);

        // Carica e parsifica i dati dal file "dati_test.txt"
        ArrayList<TestRecord> records = leggiDatiDalFile();
        if (records == null || records.isEmpty()) {
            Toast.makeText(context, "Nessun dato trovato per il grafico", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepara i dataset per ogni parametro e le etichette per l'asse X
        ArrayList<Entry> entriesPunteggio = new ArrayList<>();
        ArrayList<Entry> entriesTempo = new ArrayList<>();
        ArrayList<Entry> entriesErrate = new ArrayList<>();
        ArrayList<Entry> entriesAttenzioni = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            TestRecord r = records.get(i);
            entriesPunteggio.add(new Entry(i, r.punteggio));
            entriesTempo.add(new Entry(i, r.tempoImpiegato));
            entriesErrate.add(new Entry(i, r.risposteErrate));
            entriesAttenzioni.add(new Entry(i, r.attenzioniSonore));
            dateLabels.add(r.data);
        }

        // Crea i dataset delle linee
        LineDataSet setPunteggio = new LineDataSet(entriesPunteggio, "Punteggio");
        setPunteggio.setColor(getResources().getColor(android.R.color.holo_green_light));
        setPunteggio.setLineWidth(2f);
        setPunteggio.setValueTextSize(10f);

        LineDataSet setTempo = new LineDataSet(entriesTempo, "Tempo Impiegato (s)");
        setTempo.setColor(getResources().getColor(android.R.color.holo_orange_light));
        setTempo.setLineWidth(2f);
        setTempo.setValueTextSize(10f);

        LineDataSet setErrate = new LineDataSet(entriesErrate, "Risposte Errate");
        setErrate.setColor(getResources().getColor(android.R.color.holo_red_light));
        setErrate.setLineWidth(2f);
        setErrate.setValueTextSize(10f);

        LineDataSet setAttenzioni = new LineDataSet(entriesAttenzioni, "Attenzioni Sonore");
        setAttenzioni.setColor(getResources().getColor(android.R.color.holo_blue_light));
        setAttenzioni.setLineWidth(2f);
        setAttenzioni.setValueTextSize(10f);

        // Combina i dataset in un'unica LineData
        LineData lineData = new LineData(setPunteggio, setTempo, setErrate, setAttenzioni);
        lineChart.setData(lineData);
        lineChart.invalidate();

        // Configura l'asse X per mostrare le date
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dateLabels.size()) {
                    return dateLabels.get(index);
                }
                return "";
            }
        });

        // Configura l'asse Y
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setTextSize(10f);
        lineChart.getAxisRight().setEnabled(false);
    }

    // Legge e parsa i dati dal file "dati_test.txt"
    private ArrayList<TestRecord> leggiDatiDalFile() {
        ArrayList<TestRecord> records = new ArrayList<>();

        // Ottieni l'email del medico e sanitizza per il percorso
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailMedico = userSession.getEmail();
        String emailSanitized = emailMedico.replaceAll("[/\\\\:*?\"<>|@.]", "_");

        File medicoDir = new File(getFilesDir(), emailSanitized);
        if (!medicoDir.exists()) return records;

        // Ottieni il bambino selezionato
        CounterSingleton bambinoSelezionato = CounterSingleton.getInstance();
        String pazienteSelezionato = bambinoSelezionato.getBambinoSelezionato();
        String[] parts = pazienteSelezionato.split(" ");
        if(parts.length < 2) return records;
        String nomeBambino = parts[0];
        String cognomeBambino = parts[1];

        File bambinoDir = new File(medicoDir, nomeBambino + "_" + cognomeBambino);
        if (!bambinoDir.exists()) return records;

        File file = new File(bambinoDir, "dati_test.txt");
        if (!file.exists()) {
            Toast.makeText(context, "File non trovato", Toast.LENGTH_SHORT).show();
            return records;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            TestRecord currentRecord = new TestRecord();
            boolean inRecord = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    // Fine del record: aggiungi se ci sono dati
                    if (inRecord) {
                        records.add(currentRecord);
                        currentRecord = new TestRecord();
                        inRecord = false;
                    }
                } else {
                    inRecord = true;
                    if (line.startsWith("Data:")) {
                        currentRecord.data = line.split(":", 2)[1].trim();
                    } else if (line.startsWith("Punteggio:")) {
                        currentRecord.punteggio = Float.parseFloat(line.split(":", 2)[1].trim());
                    } else if (line.startsWith("Tempo Impiegato (s):")) {
                        currentRecord.tempoImpiegato = Float.parseFloat(line.split(":", 2)[1].trim());
                    } else if (line.startsWith("Numero Risposte Errate:")) {
                        currentRecord.risposteErrate = Float.parseFloat(line.split(":", 2)[1].trim());
                    } else if (line.startsWith("Numero Attenzioni Sonore:")) {
                        currentRecord.attenzioniSonore = Float.parseFloat(line.split(":", 2)[1].trim());
                    }
                }
            }
            // Aggiungi l'ultimo record se non terminato da una riga vuota
            if (inRecord) {
                records.add(currentRecord);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Errore nel leggere il file", Toast.LENGTH_SHORT).show();
        }
        return records;
    }

    public void tornaIndietro(View view) {
        onBackPressed();
    }
}
