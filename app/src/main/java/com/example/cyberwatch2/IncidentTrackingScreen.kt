package com.example.cyberwatch2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun IncidentTrackingScreen() {
    val firestore = Firebase.firestore
    var incidents by remember { mutableStateOf(emptyList<Incident>()) }
    var isLoading by remember { mutableStateOf(true) }

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
                isLoading = false
            }
            .addOnFailureListener {
                // Handle error
                isLoading = false
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (isLoading) {
            items(1) {
                // Skeleton loading animation or loading indicator can be added here
            }
        } else if (incidents.isEmpty()) {
            item {
                // Display an empty state message
            }
        } else {
            items(incidents) { incident ->
                IncidentItem(incident)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
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
