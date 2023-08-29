package com.example.cyberwatch2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun IncidentTrackingScreen() {
    val firestore = Firebase.firestore
    var incidents by remember { mutableStateOf(emptyList<Incident>()) }

    // Fetch incident submissions from Firebase Firestore and update the incidents list
    LaunchedEffect(Unit) {
        firestore.collection("incidents")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fetchedIncidents = mutableListOf<Incident>()
                for (document in querySnapshot) {
                    val incident = document.toObject(Incident::class.java)
                    fetchedIncidents.add(incident)
                }
                incidents = fetchedIncidents
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(incidents) { incident ->
            IncidentItem(incident)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

data class Incident(
    val title: String = "",
    val status: String = ""
)

@Composable
fun IncidentItem(incident: Incident) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = incident.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Status: ${incident.status}")
        }
    }
}
