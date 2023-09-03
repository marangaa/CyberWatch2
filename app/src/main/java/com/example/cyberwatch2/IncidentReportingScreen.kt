package com.example.cyberwatch2

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class IncidentReport(
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val contactInfo: String,
    val symptoms: String,
    val source: String,
    val dataAffected: String,
    val securityTools: String,
    val usernamesURLs: String,
    val actionsTaken: String,
    val peopleInvolved: String,
    val communicationChannels: String,
    val priorIncidents: String,
    val additionalContext: String,
    val questionsConcerns: String
)

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IncidentReportingScreen(
    navController: NavController,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var dataAffected by remember { mutableStateOf("") }
    var securityTools by remember { mutableStateOf("") }
    var usernamesURLs by remember { mutableStateOf("") }
    var actionsTaken by remember { mutableStateOf("") }
    var peopleInvolved by remember { mutableStateOf("") }
    var communicationChannels by remember { mutableStateOf("") }
    var priorIncidents by remember { mutableStateOf("") }
    var additionalContext by remember { mutableStateOf("") }
    var questionsConcerns by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateTime = remember { mutableStateOf(dateFormat.format(Date())) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val attachments = remember { mutableStateListOf<Uri>() }
    val attachmentVisibilityState = remember { mutableStateOf(false) }

    val onReportSubmit: (
        title: String,
        description: String,
        date: String,
        location: String,
        contactInfo: String,
        symptoms: String,
        source: String,
        dataAffected: String,
        securityTools: String,
        usernamesURLs: String,
        actionsTaken: String,
        peopleInvolved: String,
        communicationChannels: String,
        priorIncidents: String,
        additionalContext: String,
        questionsConcerns: String
    ) -> Unit = { title, description, date, location, contactInfo, symptoms, source, dataAffected, securityTools, usernamesURLs, actionsTaken, peopleInvolved, communicationChannels, priorIncidents, additionalContext, questionsConcerns ->

        // Create an com.example.cyberwatch2.IncidentReport object with the provided data
        val incidentReport = IncidentReport(
            title,
            description,
            date,
            location,
            contactInfo,
            symptoms,
            source,
            dataAffected,
            securityTools,
            usernamesURLs,
            actionsTaken,
            peopleInvolved,
            communicationChannels,
            priorIncidents,
            additionalContext,
            questionsConcerns
        )

        // Get a reference to your Firebase Firestore database
        val firestore = Firebase.firestore

        // Add the incident report data to Firestore
        firestore.collection("incident_reports")
            .add(incidentReport)
            .addOnSuccessListener {
                // Display a confirmation message to the user
                Toast.makeText(
                    context,
                    "Incident report submitted successfully",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate to another screen if needed
                navController.navigate(Screen.Blog.route)
            }
            .addOnFailureListener { e ->
                // Handle any errors that occurred during the submission
                Log.e(TAG, "Error submitting incident report", e)
                // You may want to display an error message to the user
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
//        Image(
//            painter = painterResource(id = R.drawable.background_image),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.FillBounds
//        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Allow vertical scrolling
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Incident Title") },
                    placeholder = { Text("Enter a brief title") },
                    isError = title.isEmpty(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Description
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Incident Description") },
                    placeholder = { Text("Provide a detailed description") },
                    isError = description.isEmpty(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Date and Time
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date and Time") },
                    placeholder = { Text("Enter date and time of the incident") },
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Location
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    placeholder = { Text("Enter relevant location details") },
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Contact Information
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            ) {
                OutlinedTextField(
                    value = contactInfo,
                    onValueChange = { contactInfo = it },
                    label = { Text("Your Contact Information") },
                    placeholder = { Text("Enter your contact details") },
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Symptoms
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            ) {
                OutlinedTextField(
                    value = symptoms,
                    onValueChange = { symptoms = it },
                    label = { Text("Symptoms or Unusual Behavior") },
                    placeholder = { Text("Describe any unusual symptoms or behavior") },
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Source
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            ) {
                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("Source of the Incident") },
                    placeholder = { Text("How did you discover the incident?") },
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Attachment Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier
                        .animateContentSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    IconToggleButton(
                        checked = attachmentVisibilityState.value,
                        onCheckedChange = { attachmentVisibilityState.value = it },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text("Add Attachments")
                    }

                    AnimatedVisibility(
                        visible = attachmentVisibilityState.value,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        // Attachment handling UI
                        // ... You can add code here to handle attachments
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        onReportSubmit(
                            title, description, date, location, contactInfo, symptoms,
                            source, dataAffected, securityTools, usernamesURLs,
                            actionsTaken, peopleInvolved, communicationChannels, priorIncidents,
                            additionalContext, questionsConcerns
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Submit Report")
            }
        }
    }
}
