package com.example.safespace.user

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.safespace.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            PackageManager.PERMISSION_GRANTED
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
            PackageManager.PERMISSION_GRANTED
        )


        buttonLogin.setOnClickListener{
            Log.d("LoginActivity","Login Button Clicked")
            processLogin()
        }

        buttonRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun processLogin(){

        // Getting login credentials
        val email = this.editTextEmail.text.toString().trim()
        val password = this.editTextPassword.text.toString().trim()


        // Accessing Firebase Auth
        val auth = FirebaseAuth.getInstance()

        //Display loading screen
        val loginLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        loginLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        loginLoading.titleText = "Loading"
        loginLoading.setCancelable(false)
        loginLoading.show()


        if(email.isEmpty() || password.isEmpty()){
            // stop login loading bar
            loginLoading.dismissWithAnimation()
            showError("Login", "Email or password cannot be empty")
        }
        else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // stop login loading bar
                    loginLoading.dismissWithAnimation()
                    if (task.isSuccessful) {


                        // Take user to home screen to Home if login successful
                        val intent = Intent(this, HomeActivity::class.java)
                        // Clear stack
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                        // Display welcome message
                        Toast.makeText(
                            baseContext, "Welcome to SafeSpace",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        // Display error message
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Login")
                            .setContentText("Pleas enter the right combination of email and password")
                            .show()


                        // Reset input value
                        resetInput()

                    }

                }
        }


    }
    private fun resetInput(){
        editTextEmail.setText("")
        editTextPassword.setText("")
    }

    private fun showError(title:String, msg:String){
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(msg)
            .show()
    }


}
