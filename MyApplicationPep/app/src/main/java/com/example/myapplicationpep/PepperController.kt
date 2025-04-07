package com.example.myapplicationpep

import android.content.Context
import android.media.MediaPlayer
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.builder.*


class PepperController(private val context: Context) {

    private var qiContext: QiContext? = null
    private var mediaPlayer: MediaPlayer? = null


    val frasiMotivazionali = arrayOf(
            "Vai alla grande! Continua così!",
            "Ottimo lavoro! Sei sulla strada giusta!",
            "Perfetto, continua così!",
            "Sei fantastico! Non fermarti!",
            "Bravo! Ogni risposta giusta ti avvicina di più!",
            "Molto bene, stai facendo un ottimo lavoro!",
            "Stai superando questo test con successo!",
            "Fantastico! La tua concentrazione è incredibile!",
            "Impressionante! Sei davvero in gamba!",
            "Risposta giusta! Sei proprio un esperto!",
            "Eccezionale! Sei davvero concentrato!",
            "Ottima scelta! Sei un campione!",
            "Stai andando alla grande, continua così!",
            "Molto bene! Sei sempre più vicino alla fine!",
            "Risposta corretta! Grande lavoro!",
            "Incredibile! Stai facendo un progresso fantastico!",
            "Non ti fermare, stai facendo faville!",
            "Continua a così, sei imbattibile!",
            "Super! Sei veramente bravo in questo!",
            "Ottimo! Continua così, non c'è niente che ti fermi!"
    )




    // Metodo per ottenere il QiContext
    fun setQiContext(qiContext: QiContext) {
        this.qiContext = qiContext

    }


    // Metodo per far parlare Pepper usato per guidare utente nell'applicazione
    fun speak(frase: String) {
        val context = qiContext
        if (context != null) {
            // Esegui l'operazione in un thread separato usando AsyncTask
            AsyncTask.execute {
                try {
                    // Crea l'oggetto Say
                    val say = SayBuilder.with(context)
                            .withText(frase)
                            .build()

                    // Esegui l'operazione di parlato in background
                    val future: Future<Void> = say.async().run()

                    // Gestisci il risultato quando l'operazione è completata
                    future.thenConsume { result ->
                        if (result.hasError()) {
                            Log.e("SPEAK", "Errore durante il parlato", result.error)
                        } else {
                            Log.d("SPEAK", "Pepper ha parlato con successo")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SPEAK", "Errore", e)
                }
            }
        } else {
            // Mostra un messaggio se QiContext non è disponibile
            Toast.makeText(context, "QiContext non disponibile", Toast.LENGTH_SHORT).show()
        }
    }
    // la funzione incoraggia i bambini a continaure il test
    fun incoraggiaUtente() {
        // Scegli una frase casuale dall'array di frasi motivazionali
        val fraseMotivazionale = frasiMotivazionali.random()

        // Fai parlare Pepper con la frase scelta
        speak(fraseMotivazionale)
    }

    // metodo che genera un suono per attirare l'attenzione dei bambini
    fun soundAttention() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                mediaPlayer = null
            }
        }

        mediaPlayer = MediaPlayer.create(context, R.raw.attention)
        mediaPlayer?.let {
            Toast.makeText(context, "Play sound on Robot", Toast.LENGTH_LONG).show()
            it.start()

            it.setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
            }
        }
    }

    fun soundSuccess() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                mediaPlayer = null
            }
        }

        mediaPlayer = MediaPlayer.create(context, R.raw.success)
        mediaPlayer?.let {
            Toast.makeText(context, "Play sound on Robot", Toast.LENGTH_LONG).show()
            it.start()

            it.setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
            }
        }
    }



    fun playAnimation(animationResource: Int) {
        // Esegui in un thread separato
        AsyncTask.execute {
            try {
                // Crea l'animazione usando la risorsa locale (qianim)
                val animation: Animation = AnimationBuilder.with(qiContext)
                        .withResources(animationResource) // ID della risorsa dell'animazione
                        .build()

                // Crea il comportamento Animate
                val animate: Animate = AnimateBuilder.with(qiContext)
                        .withAnimation(animation)
                        .build()

                // Esegui l'animazione
                animate.async().run()

            } catch (e: Exception) {
                Log.e("ANIMATION", "Errore durante l'animazione", e)
            }
        }
    }

    // Metodo per riconoscere parole (asincrono)
    fun riconoscereParole(parolaDaRiconoscere: String) {
        val context = qiContext ?: return // Restituisce se qiContext non è disponibile
        Log.d("PAROLA DA RICONOSCERE", parolaDaRiconoscere);

        // Esegui in un thread separato usando AsyncTask
        AsyncTask.execute {
            try {
                val phraseSet = PhraseSetBuilder.with(context)
                        .withTexts(parolaDaRiconoscere)
                        .build()

                val listen = ListenBuilder.with(context)
                        .withPhraseSet(phraseSet)
                        .build()
                Log.d("RICONOSCIMENTO", "Sta ascoltando per le parole...")
                // Ascolta la parola
                val result = listen.run()
                Log.d("RICONOSCIMENTO", "Ascolto completato. Parola riconosciuta: ${result.heardPhrase?.text ?: "Nessuna parola"}")
                val parolaRiconosciuta = result.heardPhrase?.text ?: ""

                println("Pepper ha riconosciuto: $parolaRiconosciuta")
                // Confronta la parola riconosciuta con la parola da riconoscere
                val isCorrect = parolaRiconosciuta.equals(parolaDaRiconoscere, ignoreCase = true)

                // Se necessario, puoi comunicare il risultato al thread principale
                // per esempio usando un callback, o anche aggiornare la UI
                val contatore = CounterSingleton.getInstance()
                if (isCorrect) {
                    Log.d("RICONOSCIMENTO", "Parola corretta riconosciuta")
                    contatore.incrementaPunteggio()
                } else {
                    Log.d("RICONOSCIMENTO", "Parola errata riconosciuta")

                }




            } catch (e: Exception) {
                Log.e("RICONOSCIMENTO", "Errore durante il riconoscimento", e)
            }
        }
    }

}
