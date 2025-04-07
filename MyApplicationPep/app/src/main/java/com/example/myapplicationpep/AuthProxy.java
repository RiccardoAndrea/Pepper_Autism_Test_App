package com.example.myapplicationpep;


import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;



// passiamo un booleano. se il booleano e false allora non e loggato e lo reindirizziamo alla pagina di login. altrimenti il metodo semplicemente non fa nulla
public class AuthProxy  {


    private Context context;


    public AuthProxy(Context context){
        this.context = context;
    }
    // Verifica se l'utente è loggato
    public void isLoggedIn(boolean isLogged) {
       if(!isLogged){
           Intent intent = new Intent(context, LoginActivity.class);
           // Avvia l'Activity
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           context.startActivity(intent);

       }
    }

}
