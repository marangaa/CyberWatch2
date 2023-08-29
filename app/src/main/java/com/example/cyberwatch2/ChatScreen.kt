package com.example.cyberwatch2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@Composable
fun ChatScreen() {
    val firestore = Firebase.firestore
    val messagesCollection = firestore.collection("messages")

    var message by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(emptyList<Message>()) }

    // Fetch chat messages from Firebase Firestore and update the messages list
    LaunchedEffect(Unit) {
        messagesCollection.orderBy("timestamp")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                val fetchedMessages = mutableListOf<Message>()
                for (document in snapshot!!.documents) {
                    val chatMessage = document.toObject(Message::class.java)
                    fetchedMessages.add(chatMessage!!)
                }
                messages = fetchedMessages
            }
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MessageList(messages)
        Spacer(modifier = Modifier.height(16.dp))
        SendMessageArea(
            message = message,
            onMessageChange = { message = it },
            onSendClick = {
                if (message.isNotBlank()) {
                    val newMessage = Message(text = message)
                    messagesCollection.add(newMessage)
                    message = ""
                }
            }
        )
    }
}

data class Message(
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun MessageList(messages: List<Message>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(messages) { message ->
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun SendMessageArea(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        BasicTextField(
            value = message,
            onValueChange = { onMessageChange(it) },
            modifier = Modifier
                .weight(1f)
                .background(Color.White),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onSendClick,
            enabled = message.isNotBlank()
        ) {
            Text("Send")
        }
    }
}
