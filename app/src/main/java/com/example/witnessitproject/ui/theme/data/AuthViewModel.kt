package com.example.witnessitproject.ui.theme.data

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.witnessitproject.ui.theme.models.UserModel
import com.example.witnessitproject.ui.theme.navigation.ROUTE_ADMIN
import com.example.witnessitproject.ui.theme.navigation.ROUTE_DASHBOARD
import com.example.witnessitproject.ui.theme.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




class AuthViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    companion object {
        const val ADMIN_EMAIL = "admin@thesamaritan.com"
        const val WEB_CLIENT_ID = "205381490369-bb8iv3sgpfn47eish0ae3ape7gcid50s.apps.googleusercontent.com"

    }
    fun signup(username:String, email:String, phonenumber:String, password:String, confirmpassword:String, navController: NavController, context: Context){
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmpassword.isBlank()|| phonenumber.isBlank()){
            Toast.makeText(context,"Please fill all the fields", Toast.LENGTH_LONG).show()
            return
        }
        if (password != confirmpassword){
            Toast.makeText(context,"Password do not match", Toast.LENGTH_LONG).show()
            return
        }
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                task ->
            if (task.isSuccessful){
                val userId = auth.currentUser?.uid ?: ""
                val user = UserModel(
                    username = username,
                    email = email,
                    userId = userId,
                    phonenumber = phonenumber
                )

                saveUserToDatabase(user,navController,context)
            }else{
                Toast.makeText(context,task.exception?.message ?:
                "Registration failed", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun saveUserToDatabase(user: UserModel, navController: NavController, context: Context){
        val dbRef = FirebaseDatabase.getInstance().getReference("User/${user.userId}")
        dbRef.setValue(user).addOnCompleteListener{
                task ->
            if (task.isSuccessful){
                Toast.makeText(context,"User Registered successfully",
                    Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_LOGIN){
                    popUpTo(0)
                }
            }else{
                Toast.makeText(context,task.exception?.message ?: "Failed to save user",
                    Toast.LENGTH_LONG).show()
            }
        }


    }
    fun login(
        email: String,
        password: String,
        navController: NavController,
        context: Context
    ) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Email and Password required", Toast.LENGTH_LONG).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                    if (email.trim().lowercase() == ADMIN_EMAIL) {
                        navController.navigate(ROUTE_ADMIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate(ROUTE_DASHBOARD) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        task.exception?.message ?: "Login failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
    }
    fun signInWithGoogle(
        context: Context,
        navController: NavController,
        scope: CoroutineScope
    ) {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch(Dispatchers.IO) {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = auth.currentUser
                            val userId = firebaseUser?.uid ?: return@addOnCompleteListener
                            val email = firebaseUser.email ?: ""
                            val username = firebaseUser.displayName ?: email.substringBefore("@")


                            val dbRef = FirebaseDatabase.getInstance()
                                .getReference("User/$userId")

                            dbRef.get().addOnSuccessListener { snapshot ->
                                if (!snapshot.exists()) {

                                    val user = UserModel(
                                        username = username,
                                        email = email,
                                        userId = userId,
                                        phonenumber = ""
                                    )
                                    dbRef.setValue(user)
                                }

                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                if (email.trim().lowercase() == ADMIN_EMAIL) {
                                    navController.navigate(ROUTE_ADMIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(ROUTE_DASHBOARD) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                task.exception?.message ?: "Google Sign-In failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } catch (e: GetCredentialException) {
                Toast.makeText(
                    context,
                    "Google Sign-In error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}