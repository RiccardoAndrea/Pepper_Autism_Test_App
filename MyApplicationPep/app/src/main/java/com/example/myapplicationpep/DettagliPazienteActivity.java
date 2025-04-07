package com.example.myapplicationpep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class DettagliPazienteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_paziente);


    }

    public void apriDiagramma(View view) {
        Intent intent = new Intent(DettagliPazienteActivity.this, SelezionaGraficoActivity.class);
        startActivity(intent);
    }

    public void apriStoricoTest(View view) {
        Intent intent = new Intent(DettagliPazienteActivity.this, StoricoTestActivity.class);
        startActivity(intent);
    }

    public void apriRegistrazioni(View view) {
        Intent intent = new Intent(DettagliPazienteActivity.this, RegistrazioniTeoriaDellaMenteActivity.class);
        startActivity(intent);
    }

    public void tornaIndietro(View view) {
        CounterSingleton nomePaziente = CounterSingleton.getInstance();
        nomePaziente.reset();
        onBackPressed();
    }

    public void apriNote(View view) {
        Intent intent = new Intent(DettagliPazienteActivity.this, GestisciNoteActivity.class);
        startActivity(intent);
    }
}
