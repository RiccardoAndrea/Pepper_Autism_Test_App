package com.example.myapplicationpep;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GraficoTortaActivity extends AppCompatActivity {

    private PieChart pieChart;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico_torta);

        // Inizializza il contesto
        context = this;

        // Ottieni il PieChart dal layout XML
        pieChart = findViewById(R.id.pieChart);

        // Ottieni i dati dal file info_diagrammi.txt
        ArrayList<PieEntry> pieEntries = leggiDatiDalFile();

        // Configura il grafico
        if (pieEntries != null && !pieEntries.isEmpty()) {
            configuraGrafico(pieEntries);
        } else {
            Toast.makeText(context, "Nessun dato trovato per il grafico", Toast.LENGTH_SHORT).show();
        }
    }

    // Funzione per leggere i dati dal file info_diagrammi.txt
    private ArrayList<PieEntry> leggiDatiDalFile() {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        // Ottieni l'email del medico
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailMedico = userSession.getEmail();
        String emailSanitized = emailMedico.replaceAll("[/\\\\:*?\"<>|@.]", "_");

        // Percorso della cartella del medico
        File medicoDir = new File(context.getFilesDir(), emailSanitized);
        System.out.println("il percorso del medico e: " + medicoDir.getPath());
        // Se la directory del medico non esiste, restituisci null
        if (!medicoDir.exists()) {
            return null;
        }

        CounterSingleton bambinoSelezionato = CounterSingleton.getInstance();
        String pazienteSelezionato = bambinoSelezionato.getBambinoSelezionato();
        System.out.println("direcotry del bambino in grafico e: " + pazienteSelezionato);
        String[] parts = pazienteSelezionato.split(" ");
        String nomeBambino = parts[0];  // Il primo elemento è il nome
        String cognomeBambino = parts[1];  // Il secondo elemento è il cognome
        // Cerca la directory con il nome e cognome del bambino dentro la directory del medico
        File bambinoDir = new File(medicoDir, nomeBambino + "_" + cognomeBambino); // Sostituire con il nome e cognome del bambino
        if (!bambinoDir.exists()) {
            return null;
        }
        System.out.println("il percordo del bambino e: " + bambinoDir.getPath());
        // Nome del file info_diagrammi.txt
        File file = new File(bambinoDir, "info_diagrammi.txt");

        // Leggi i dati dal file
        int risposteEsatte = 0;
        int risposteSbagliate = 0;
        int distrazioni = 0;

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Risposte Esatte:")) {
                        risposteEsatte = Integer.parseInt(line.split(":")[1].trim());
                        System.out.println("risposte esatte: "+risposteEsatte);
                    } else if (line.startsWith("Risposte Sbagliate:")) {
                        risposteSbagliate = Integer.parseInt(line.split(":")[1].trim());
                        System.out.println("risposte sbagliate: "+risposteSbagliate);
                    } else if (line.startsWith("Distrazioni:")) {
                        distrazioni = Integer.parseInt(line.split(":")[1].trim());
                        System.out.println("distrazioni: "+distrazioni);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Aggiungi le voci al grafico
        pieEntries.add(new PieEntry(risposteEsatte, "Risposte Esatte"));
        pieEntries.add(new PieEntry(risposteSbagliate, "Risposte Sbagliate"));
        pieEntries.add(new PieEntry(distrazioni, "Distrazioni"));

        return pieEntries;
    }

    // Funzione per configurare il grafico a torta
    private void configuraGrafico(ArrayList<PieEntry> pieEntries) {
        // Imposta i dati nel grafico
        PieDataSet dataSet = new PieDataSet(pieEntries, "Risultati Test");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // Crea la PieData
        PieData pieData = new PieData(dataSet);

        // Imposta i dati al PieChart
        pieChart.setData(pieData);
        pieChart.invalidate(); // Rende il grafico visibile
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setUsePercentValues(true);
    }

    public void tornaIndietro(View view) {
        onBackPressed();
    }
}
