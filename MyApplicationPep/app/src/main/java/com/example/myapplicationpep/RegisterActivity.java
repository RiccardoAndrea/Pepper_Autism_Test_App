package com.example.myapplicationpep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.bumptech.glide.Glide;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        // Trova il tuo ImageView
        ImageView imageView = findViewById(R.id.imageView2);

        // Carica la GIF usando Glide
        Glide.with(this)
                .load(R.drawable.register) // Assicurati che "register.gif" sia presente nella cartella res/drawable
                .into(imageView);
    }
    public void registrazione(View view) {
        // Ottieni i dati inseriti nel modulo
        EditText nameEditText = findViewById(R.id.et_name);
        EditText surnameEditText = findViewById(R.id.et_surname);
        EditText emailEditText = findViewById(R.id.et_email);
        EditText passwordEditText = findViewById(R.id.et_password);

        // Verifica che i campi obbligatori non siano vuoti
        if (nameEditText.getText().toString().trim().isEmpty() ||
                surnameEditText.getText().toString().trim().isEmpty() ||
                emailEditText.getText().toString().trim().isEmpty() ||
                passwordEditText.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Tutti i campi sono obbligatori!", Toast.LENGTH_SHORT).show();
            return;
        }
        String emailInput = emailEditText.getText().toString().trim();
        System.out.println("Email inserita: " + emailInput);

        // Instanzia l'oggetto Medico con i dati inseriti
        Medico medico = new Medico(
                nameEditText.getText().toString(),
                surnameEditText.getText().toString(),
                emailEditText.getText().toString(),
                passwordEditText.getText().toString()
        );
        System.out.println("email del medico: " + medico.getEmail());
        // Sanifica l'email per creare il nome della directory
        String directoryName = medico.getEmail().replaceAll("[/\\\\:*?\"<>|@.]", "_");
        System.out.println("Email sanificata per directory: " + directoryName);

        // Ottieni la directory privata dell'app
        File directory = new File(getFilesDir(), directoryName);
        System.out.println("Percorso directory utente: " + directory.getAbsolutePath());

        // Verifica se la directory esiste già
        if (directory.exists()) {
            System.out.println("La directory dell'utente esiste già!");
            Toast.makeText(this, "Utente già registrato!", Toast.LENGTH_LONG).show();
        } else {
            // Crea la directory per l'utente
            boolean dirCreated = directory.mkdirs();
            if (dirCreated) {
                System.out.println("Directory creata con successo.");
                // Crea un file "datiAccesso.txt" per salvare i dati
                File file = new File(directory, "datiAccesso.txt");
                System.out.println("Percorso file credenziali: " + file.getAbsolutePath());

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    // Salva i dati del medico nel file
                    String userData = "Email: " + medico.getEmail() + "\nPassword: " + medico.getPassword() +
                            "\nNome: " + medico.getNome() + "\nCognome: " + medico.getCognome();
                    System.out.println("Dati salvati nel file:\n" + userData);

                    fos.write(userData.getBytes());

                    // Mostra un messaggio di successo
                    Toast.makeText(this, "Registrazione completata!", Toast.LENGTH_LONG).show();

                    // Torna alla MainActivity
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Errore nel salvataggio del file!");
                    Toast.makeText(this, "Errore nel salvataggio dei dati.", Toast.LENGTH_LONG).show();
                }
            } else {
                System.out.println("Errore nella creazione della directory!");
                Toast.makeText(this, "Errore nella creazione della directory.", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void goLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
