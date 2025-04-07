package com.example.myapplicationpep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class GestisciNoteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestisci_note);


    }

    public void goToScriviNota(View view) {
        Intent intent = new Intent(GestisciNoteActivity.this, AggiungiNotaActivity.class);
        startActivity(intent);
    }

    public void goToVisulaizzaNota(View view) {
        Intent intent = new Intent(GestisciNoteActivity.this, VisualizzaNotaActivity.class);
        startActivity(intent);
    }

    public void tornaIndietro(View view) {
        onBackPressed();
    }
}
