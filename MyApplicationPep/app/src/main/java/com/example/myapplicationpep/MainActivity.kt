package com.example.myapplicationpep

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity(), RobotLifecycleCallbacks {

    //private var testScelto: String? = null
    private var qiContext: QiContext? = null  // Riferimento a QiContext
    private lateinit var pepperController: PepperController  // Aggiungi questa variabile

    private val REQUEST_CODE_PERMISSIONS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Crea l'istanza di PepperController
        pepperController = PepperController(this)

        // Chiamata per controllare e richiedere permessi se necessario
        checkPermissions()

        // Registra l'Activity a QiSDK senza usare QiActivity
        QiSDK.register(this, this)

        val authProxy = AuthProxy(this)
        val userSession = UserSessionSingleton.getInstance()
        authProxy.isLoggedIn(userSession.getIsLogged())

        val imageView: ImageView = findViewById(R.id.robot)
        Glide.with(this).load(R.drawable.redrobot).into(imageView)

        val myButton: Button = findViewById(R.id.start)
        val shape = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 40f
            setColor(0xFFFF4081.toInt())
        }
        myButton.background = shape
    }

    override fun onResume() {
        super.onResume()
        //testScelto = null
        //findViewById<Button>(R.id.test).text = "Scegli Test"
    }

    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this)  // Disconnessione da QiSDK
    }

    // Callback di QiSDK
    override fun onRobotFocusGained(qiContext: QiContext) {
        this.qiContext = qiContext  // Salva il QiContext quando disponibile
        pepperController.setQiContext(qiContext)  // Imposta il QiContext nel controller
        PepperSingleton.getInstance().setQiContext(qiContext)
    }

    override fun onRobotFocusLost() {
        qiContext = null  // Rilascia il riferimento quando il focus è perso
    }

    override fun onRobotFocusRefused(reason: String) {
        Log.e("MainActivity", "Focus rifiutato: $reason")
    }

    // Metodo per far parlare Pepper
    fun speak(view: View) {
        //pepperController.speak(" Smettila di toccare a caso!")  // Usa il controller per far parlare Pepper
        pepperController.riconoscereParole("ESATTO")
    }

    /*fun scegliTest(view: View) {
        val test = arrayOf("F.R.O.G", "TORRE DI LONDRA")
        val popupMenu = PopupMenu(this, view)
        test.forEach { popupMenu.menu.add(it) }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val myButton: Button = findViewById(R.id.test)
            testScelto = menuItem.title.toString()
            myButton.text = testScelto
            pepperController.speak("Hai scelto il test ${testScelto}")
            true
        }
        popupMenu.show()
    }

    fun inizio(view: View) {
        val testoScegliTest = findViewById<TextView>(R.id.textView)
        if (testScelto == null) {
            pepperController.speak("scegli prima un test!")
            testoScegliTest.visibility = View.VISIBLE
        } else {
            testoScegliTest.visibility = View.INVISIBLE
            pepperController.speak("Iniziamo il test: $testScelto!")
            val intent = if (testScelto == "F.R.O.G") {
                Intent(this, TrogStartActivity::class.java)
            } else {
                Intent(this, TorreDiLondraActivity::class.java)
            }
            startActivity(intent)
        }
    }*/

    fun inizio(view : View){
        pepperController.speak("Iniziamo il test T ROG!")
        val intent = Intent(this, SelezionaPazienteActivity::class.java)
        startActivity(intent)
    }

    fun visualizzaPunteggio(view: View) {
        pepperController.speak("Sto per mostrarti i tuoi pazienti!")
        startActivity(Intent(this, GestionePazienteActivity::class.java))
    }

    private fun checkPermissions() {
        // Verifica se i permessi sono già concessi
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Se non sono concessi, chiedi i permessi
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSIONS
            )
        } else {
            // I permessi sono già concessi, puoi procedere
            Toast.makeText(this, "Permessi già concessi", Toast.LENGTH_SHORT).show()
        }
    }

    // Gestisci la risposta ai permessi
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permesso concesso
                Toast.makeText(this, "Permesso concesso", Toast.LENGTH_SHORT).show()
            } else {
                // Permesso negato
                Toast.makeText(this, "Permesso negato", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
