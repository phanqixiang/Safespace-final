package com.example.safespace.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.safespace.entity.User
import kotlinx.android.synthetic.main.activity_main.buttonLogin
import kotlinx.android.synthetic.main.activity_main.buttonRegister
import kotlinx.android.synthetic.main.activity_register.*
import android.graphics.Color
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.safespace.R
import com.example.safespace.entity.Admin
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.database.FirebaseDatabase




class RegisterActivity: AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_register)





        buttonRegister.setOnClickListener {
            registerAccount()
        }


            buttonLogin.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

    }

    private fun registerAccount() {

        // Getting registration details
        var name = editTextName.text.toString().trim()
        var email = editTextEmail.text.toString().trim()
        var password = editTextPass.text.toString().trim()
        var confirmPassword = editTextConfirmPass.text.trim().toString().trim()



        if (!password.equals(confirmPassword) || name.length < 6 || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            if(name.length<6)
                showError("Registration", "Username must be at least 6 characters")
            else if(password.isEmpty() || confirmPassword.isEmpty()){
                showError("Registration", "Password cannot be empty")
            }
            else if(email.isEmpty()){
                showError("Registration", "Email address cannot be empty")
            }
            else if(!password.equals(confirmPassword))
                showError("Registration", "Password and confirm password must match")



        } else {

            // Show progress bar
            val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading"
            pDialog.setCancelable(false)
            pDialog.show()

            // Connect to Firebase Auth
            var auth = FirebaseAuth.getInstance()




                        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Register Activity", "createUserWithEmail:success")






                        // Add new user to firebase database
                        val userId = FirebaseAuth.getInstance().uid ?: ""
                        val ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
                        val newUser = User(userId, name,"none", email,"offline")


                        ref.setValue(newUser).addOnSuccessListener {
                            // Hide loading animation

                            pDialog.dismiss()
                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Registration")
                                .setContentText("Your account has been created")
                                .setConfirmClickListener {
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                                .show()

                        }.addOnFailureListener{
                            pDialog.dismiss()
                            showError("Registration",task.exception.toString())
                        }





                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Register Activity", "createUserWithEmail:failure", task.exception)


                        // Hide Animation
                        pDialog.dismiss()


                        showError("Registration", task.exception.toString())
                    }
                }
        }
    }

    private fun showError(title:String, msg: String){
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(msg)
            .show()
    }

    private fun createAdmin(){
        val auth = FirebaseAuth.getInstance()
        val adminEmail = "tankimkwi@gmail.com"
        val adminPass = "abc123"


        //class Admin(val adminId: String, val name: String, val email: String, val icNum: String, val phoneNum: String){
        auth.createUserWithEmailAndPassword(adminEmail, adminPass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Registration", "Admin is created")
                    val adminId = auth.currentUser?.uid?:""
                    val adminName = "Tan Kim Kwi"
                    val phoneNo = "01158109600"
                    val icNum = "640706015374"

                    val userId = FirebaseAuth.getInstance().uid ?: ""
                    val ref = FirebaseDatabase.getInstance().getReference("/admin/$adminId")
                    val admin = Admin(adminId, adminName, adminEmail, icNum, phoneNo,"offline")


                    ref.setValue(admin).addOnSuccessListener {
                        Log.d("Admin", "Admin account is created")



                    }




                }
            }
            .addOnFailureListener{
                Log.d("Registration", "Admin Fail")
            }
    }

}
