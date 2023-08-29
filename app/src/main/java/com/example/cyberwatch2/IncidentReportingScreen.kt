package com.example.cyberwatch2

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentReportingScreen(
    onReportSubmit: (title: String, description: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (title.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty()) {
                    // Save incident report to Firebase Firestore
                    val firestore = Firebase.firestore
                    val incidentsCollection = firestore.collection("incidents")

                    val incidentData = hashMapOf(
                        "title" to title,
                        "description" to description,
                        "category" to category
                    )

                    incidentsCollection.add(incidentData)
                        .addOnSuccessListener {
                            // Notify the parent that the report has been submitted
                            onReportSubmit(title, description, category)
                        }
                        .addOnFailureListener {
                            // Handle error
                            Log.e("FirestoreError", "Failed to add incident: $it")
                        }
                }
            }
        ) {
            Text("Submit Report")
        }
    }
}

@Preview
@Composable
fun PreviewIncidentReportingScreen(){

}