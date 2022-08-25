package mx.itesm.firebasepm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.itesm.firebasepm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        // lo primero que vamos a hacer es view binding
        // data binding - técnica que asocia un dato con un elemento de GUI
        // lo que hace: crea un objeto con referencias a todos los elementos de la vista
        // 1 - simplifica el acceso
        // 2 - reduce errores

    }

    fun registrar(view : View?){

        var emailStr = binding.email.text.toString()
        var pwdStr = binding.password.text.toString()
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

        var emailStr = binding.email.text.toString()
        var pwdStr = binding.password.text.toString()
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

    fun registrarFirestore(view : View?) {

        // vamos a guardar perritos (obviamente)
        // la info se guarda por medio de hashmaps

        val perrito = hashMapOf(
            "nombre" to binding.nombrePerrito.text.toString(),
            "edad" to binding.edadPerrito.text.toString()
        )

        // 1er paso - obtener referencia a la colección
        val coleccion : CollectionReference =
            Firebase.firestore.collection("perritos")

        // 2do paso - solicitar guardar dato
        val taskAdd = coleccion.add(perrito)

        taskAdd.addOnSuccessListener { doc ->

            Toast.makeText(
                this,
                "id: ${doc.id}",
                Toast.LENGTH_SHORT
            ).show()

        }.addOnFailureListener{ error ->

            Toast.makeText(
                this,
                "ERROR AL GUARDAR REGISTRO",
                Toast.LENGTH_SHORT
            ).show()

            Log.e("FIRESTORE", "error: $error")
        }
    }

    fun queryFirestore(view : View?){

        // vamos a solicitar datos desde firestore

        // 1 - query "tradicional", no real-time
        // traducción:
        // - solicitamos datos
        // - recibimos respuesta
        // - fin de la comunicación
        val coleccion = Firebase.firestore.collection("perritos")

        val queryTask = coleccion.get()

        queryTask.addOnSuccessListener { resultado ->

            // aquí llegamos si solicitud fue exitosa
            Toast.makeText(
                this,
                "QUERY EXITOSO",
                Toast.LENGTH_SHORT
            ).show()

            // recorremos todos los documentos de la coleccion
            for(docActual in resultado){

                Log.d(
                    "FIRESTORE",
                    "${docActual.id} ${docActual.data}"
                )
            }
        }.addOnFailureListener { error ->

            Log.e("FIRESTORE", "error en query: $error")
        }

        // 2 - escuchando updates en tiempo real
        // nos suscribimos a una colección y escuchamos cambios
        coleccion.addSnapshotListener{ datos, e ->

            // verificamos si hay excepcion
            if(e != null){

                // terminar ejecución de método
                // comandos con @ - limitados a scope
                return@addSnapshotListener
            }

            // si llegamos aquí no hubo excepción
            // todo OK

            Log.d("FIRESTORE", "HUBO CAMBIOS")

            // !! - assert non-nullable
            // declarando forzosamente al compilador
            // que una llamada no es nula (aunque sí pueda ser)
            for(cambios in datos!!.documentChanges){

                when(cambios.type){

                    DocumentChange.Type.ADDED ->
                        Log.d(
                            "FIRESTORE REALTIME",
                            "agregado: ${cambios.document.data}"
                        )

                    DocumentChange.Type.MODIFIED ->
                        Log.d(
                            "FIRESTORE REALTIME",
                            "modificado: ${cambios.document.data}"
                        )

                    DocumentChange.Type.REMOVED ->
                        Log.d(
                            "FIRESTORE REALTIME",
                            "removido: ${cambios.document.data}"
                        )
                }
            }
        }
    }
}