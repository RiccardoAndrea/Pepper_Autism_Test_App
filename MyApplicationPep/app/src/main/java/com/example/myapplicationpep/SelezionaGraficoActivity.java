package com.example.myapplicationpep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SelezionaGraficoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleziona_grafico);


    }







    public void visualizzaGraficoTorta(View view) {
        Intent intent = new Intent(SelezionaGraficoActivity.this, GraficoTortaActivity.class);
        startActivity(intent);
    }

    public void visualizzaGraficoTemporale(View view) {
        Intent intent = new Intent(SelezionaGraficoActivity.this, GraficoTemporaleActivity.class);
        startActivity(intent);
    }

    public void visualizzaGraficoBarre(View view) {
        Intent intent = new Intent(SelezionaGraficoActivity.this, GraficoBarreActivity.class);
        startActivity(intent);
    }

    public void tornaIndietro(View view) {
        onBackPressed();
    }

}
