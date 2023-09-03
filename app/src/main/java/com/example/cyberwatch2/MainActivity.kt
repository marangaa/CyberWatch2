package com.example.cyberwatch2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
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
                onLoginClick = { _, _ ->
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
            IncidentReportingScreen(navController = navController)
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
            // Firebase Authentication
            val auth = Firebase.auth
            val user = auth.currentUser
            val userId = auth.currentUser?.uid
            val selectedChatId = "your_selected_chat_id" // Replace with the actual selected chat ID
            val firebaseDatabase = FirebaseDatabase.getInstance()

            ChatScreen(selectedChatId, userId, firebaseDatabase)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen()
        }
    }
}



