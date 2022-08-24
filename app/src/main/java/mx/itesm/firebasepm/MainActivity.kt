package mx.itesm.firebasepm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var nombre : EditText
    lateinit var edad : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        nombre = findViewById(R.id.nombrePerrito)
        edad = findViewById(R.id.edadPerrito)
    }

    fun registrar(view : View?){

        var emailStr = email.text.toString()
        var pwdStr = password.text.toString()
        var authTask = Firebase.auth.createUserWithEmailAndPassword(emailStr, pwdStr)

        authTask.addOnCompleteListener(this){ resultado ->

            if(resultado.isSuccessful){

                Toast.makeText(this, "REGISTRO EXITOSO", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(this, "ERROR EN REGISTRO", Toast.LENGTH_SHORT).show()
                Log.wtf("FIREBASE-DEV", "error: ${resultado.exception}")
            }
        }
    }

    fun login(view : View?){

        var emailStr = email.text.toString()
        var pwdStr = password.text.toString()
        var authTask = Firebase.auth.signInWithEmailAndPassword(emailStr, pwdStr)

        authTask.addOnCompleteListener(this) { resultado ->

            if(resultado.isSuccessful){

                Toast.makeText(this, "LOGIN EXITOSO", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(this, "ERROR EN LOGIN", Toast.LENGTH_SHORT).show()
                Log.e("FIREBASE-DEV", "error: ${resultado.exception?.message}")
            }
        }
    }

    fun logout(view : View?){

        Toast.makeText(this, "LOGOUT", Toast.LENGTH_SHORT).show()
        Firebase.auth.signOut()
    }

    fun verificarUsuario() {

        // OJO - para su aplicación va a ser necesario que verifiquen la validez del
        // usuario actual
        if(Firebase.auth.currentUser == null){

            // SIGNIFICA QUE HAY NECESIDAD DE RE-VALIDAR EL USUARIO
            // podrías redireccionar / terminar esta actividad
            Toast.makeText(this, "REVALIDA!", Toast.LENGTH_SHORT).show()
        } else {

            Toast.makeText(
                this,
                "USUARIO: ${Firebase.auth.currentUser?.email}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // FUNCIONALIDAD COMÚN!
    // VERIFICAR VALIDEZ DE USUARIO EN EL CICLO DE VIDA
    override fun onStart() {
        super.onStart()

        verificarUsuario()
    }

    fun verificarUsuarioGUI(view : View?) {

        verificarUsuario()
    }

}