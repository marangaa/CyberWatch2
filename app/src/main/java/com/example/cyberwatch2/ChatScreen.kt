package com.example.cyberwatch2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

// ChatMessage data class to represent individual chat messages
data class ChatMessage(
    val senderId: String,
    val content: String,
    val timestamp: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String, // Use chatId to uniquely identify the chat
    currentUser: String?, // Current user's ID
    firebaseDatabase: FirebaseDatabase
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }

    // Real-time message listener
    val databaseReference = firebaseDatabase.getReference("chats/$chatId/messages")

    LaunchedEffect(Unit) {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newMessages = dataSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(ChatMessage::class.java)
                }
                messages.clear()
                messages.addAll(newMessages)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        }

        databaseReference.addValueEventListener(eventListener)
//        DisposableEffect(Unit){
//            onDispose {
//                // Remove the listener when the Composable is disposed
//                databaseReference.removeEventListener(eventListener)
//            }
//
//        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat messages
        LazyColumn {
            items(messages) { message ->
                // Display chat messages here
                Text(text = message.content)
            }
        }

        // Message input field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Chat messages
                LazyColumn {
                    items(messages) { message ->
                        // Display chat messages here
                        Text(text = message.content)
                    }
                }

                // Message input field
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { newValue ->
                        messageText = newValue
                    },
                    textStyle = TextStyle.Default.copy(
                        color = LocalContentColor.current
                    ),
                    singleLine = true,
                    placeholder = {
                        Text("Type your message...")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Button(
                    onClick = {
                        // Send the message to Firebase Realtime Database
                        if (currentUser != null) {
                            sendMessage(currentUser, messageText, databaseReference)
                        }
                        messageText = ""
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(8.dp)
                ) {
                    Text("Send")
                }
            }
        }
    }
}

fun sendMessage(senderId: String, content: String, databaseReference: DatabaseReference) {
    if (content.isNotBlank()) {
        val message = ChatMessage(senderId, content, System.currentTimeMillis())
        val newMessageReference = databaseReference.push()
        newMessageReference.setValue(message)
    }
}
