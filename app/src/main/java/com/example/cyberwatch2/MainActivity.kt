package com.example.cyberwatch2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ReportingSystemApp()
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object IncidentReporting : Screen("incident_reporting")
    object Registration : Screen("registration")
    object Blog : Screen("blog")
    object IncidentTracking : Screen("incident_tracking")
    object Chat : Screen("chat")
    object ForgotPassword : Screen("forgot_password")
}

@Composable
fun ReportingSystemApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { email, password ->
                    // Implement login logic here
                    // Navigate to the next screen after successful login
                    navController.navigate(Screen.IncidentReporting.route)
                },
                onCreateAccountClick = {
                    navController.navigate(Screen.Registration.route)
                },
                onForgotAccountClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }
        composable(Screen.IncidentReporting.route) {
            IncidentReportingScreen { title, description, category ->
                // Implement incident reporting logic here
                // For example, store incident details in Firebase
                // After submission, navigate to appropriate screen
                // navController.navigate(com.example.cyberwatch2.Screen.SomeOtherScreen.route)
                val firestore = Firebase.firestore
                val incidentsCollection = firestore.collection("incidents")

                val incidentData = hashMapOf(
                    "title" to title,
                    "description" to description,
                    "category" to category
                )

                incidentsCollection.add(incidentData)
                    .addOnSuccessListener {
                        navController.navigate(Screen.IncidentTracking.route)
                    }
                    .addOnFailureListener {
                        Log.e("FirestoreError", "Failed to add incident: $it")
                    }
            }
        }
        composable(Screen.Registration.route) {
            RegistrationScreen {
                navController.navigate(Screen.Login.route)
            }
        }
        composable(Screen.Blog.route) {
            BlogScreen()
        }
        composable(Screen.IncidentTracking.route) {
            IncidentTrackingScreen()
        }
        composable(Screen.Chat.route) {
            ChatScreen()
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen()
        }
    }
}



