package com.example.cyberwatch2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun IncidentTrackingScreen() {
    var incidentReports by remember { mutableStateOf<List<IncidentReport>>(emptyList()) }

    val firestore = Firebase.firestore
    val incidentCollection = firestore.collection("incident_reports")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    LaunchedEffect(Unit) {
        // Fetch incident reports from Firebase Firestore
        try {
            val querySnapshot = incidentCollection.get().await()
            val reports = mutableListOf<IncidentReport>()
            for (document in querySnapshot) {
                val incident = document.toObject(IncidentReport::class.java)
                reports.add(incident)
            }
            incidentReports = reports
        } catch (e: Exception) {
            // Handle any errors here
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Incident Tracking",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (incidentReports.isEmpty()) {
            Text("No incident reports to display.")
        } else {
            // Display a list of incident reports with animations
            incidentReports.forEach { incident ->
                IncidentCard(incident = incident)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun IncidentCard(incident: IncidentReport) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = incident.title, fontWeight = FontWeight.Bold)
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_expand_more),
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(24.dp)
//                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(text = "Date: ${incident.date}")
                    //Text(text = "Status: ${incident.status}")
                    Text(text = "Description: ${incident.description}")
                }
            }
        }
    }
}

//data class IncidentReport(
//    val title: String,
//    val description: String,
//    val date: String,
//    val status: String
//)
