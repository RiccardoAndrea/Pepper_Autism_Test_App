package com.example.myapplicationpep;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GraficoBarreActivity extends AppCompatActivity {

    private BarChart barChart;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico_barre);

        context = this;

        barChart = findViewById(R.id.barChart);

        ArrayList<BarEntry> barEntries = leggiDatiDalFile();

        if (barEntries != null && !barEntries.isEmpty()) {
            configuraGrafico(barEntries);
        } else {
            Toast.makeText(context, "Nessun dato trovato per il grafico", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<BarEntry> leggiDatiDalFile() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        // Ottieni l'email del medico
        UserSessionSingleton userSession = UserSessionSingleton.getInstance();
        String emailMedico = userSession.getEmail();
        String emailSanitized = emailMedico.replaceAll("[/\\\\:*?\"<>|@.]", "_");

        // Percorso della cartella del medico
        File medicoDir = new File(context.getFilesDir(), emailSanitized);
        if (!medicoDir.exists()) return null;

        CounterSingleton bambinoSelezionato = CounterSingleton.getInstance();
        String pazienteSelezionato = bambinoSelezionato.getBambinoSelezionato();
        String[] parts = pazienteSelezionato.split(" ");
        String nomeBambino = parts[0];
        String cognomeBambino = parts[1];

        File bambinoDir = new File(medicoDir, nomeBambino + "_" + cognomeBambino);
        if (!bambinoDir.exists()) return null;

        // File info_diagrammi.txt
        File file = new File(bambinoDir, "info_diagrammi.txt");

        int risposteEsatte = 0, risposteSbagliate = 0, distrazioni = 0;

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Risposte Esatte:")) {
                        risposteEsatte = Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Risposte Sbagliate:")) {
                        risposteSbagliate = Integer.parseInt(line.split(":")[1].trim());
                    } else if (line.startsWith("Distrazioni:")) {
                        distrazioni = Integer.parseInt(line.split(":")[1].trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        barEntries.add(new BarEntry(0f, risposteEsatte));
        barEntries.add(new BarEntry(1f, risposteSbagliate));
        barEntries.add(new BarEntry(2f, distrazioni));

        return barEntries;
    }

    private void configuraGrafico(ArrayList<BarEntry> barEntries) {
        BarDataSet dataSet = new BarDataSet(barEntries, "Risultati Test");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.setViewPortOffsets(20f, 20f, 20f, 20f);
        barChart.invalidate();

        // Configura l'asse X con etichette
        final String[] xLabels = {"Risposte Esatte", "Risposte Sbagliate", "Distrazioni"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < xLabels.length) {
                    return xLabels[index];
                } else {
                    return "";
                }
            }
        });

        // Configura l'asse Y
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setTextSize(10f);
        barChart.getAxisRight().setEnabled(false);
    }

    public void tornaIndietro(View view) {
        onBackPressed();
    }
}
