package com.example.cyberwatch2

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(onRegistrationSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    val profilePictureBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val auth = Firebase.auth
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val density = LocalDensity.current.density
    val imageSize = (128 * density).toInt() // Adjust the image size as needed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(imageSize.dp)
                .background(Color.Gray, shape = MaterialTheme.shapes.medium)
                .clickable { pickProfilePicture() }
        ) {
            profilePictureBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (profilePictureBitmap == null) {
                Text(
                    text = "Profile Picture",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Display Name") },
            placeholder = { Text("Enter your display name") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            placeholder = { Text("Confirm your password") },
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error messages displayed as Text
        if (displayName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Text(
                text = "Please fill in all fields.",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else if (password != confirmPassword) {
            Text(
                text = "Passwords do not match.",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                performRegistration(
                    auth,
                    context,
                    uriHandler,
                    onRegistrationSuccess,
                    displayName,
                    email,
                    password,
                    confirmPassword,
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}

private fun pickProfilePicture() {
    // Implement code to allow the user to select a profile picture from their device.
    // You can use Android's Intent.ACTION_GET_CONTENT or similar methods.
    // After selecting an image, update the profilePictureUri and profilePictureBitmap.
}

private fun performRegistration(
    auth: FirebaseAuth,
    context: Context,
    uriHandler: UriHandler,
    onRegistrationSuccess: () -> Unit,
    displayName: String,
    email: String,
    password: String,
    confirmPassword: String,
) {
    // Validate inputs
    if (displayName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        // Do nothing if fields are empty
        return
    }

    if (password != confirmPassword) {
        // Do nothing if passwords don't match
        return
    }

    // Perform registration
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    // Update the user's display name
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()

                    user.updateProfile(profileUpdate)
                        .addOnCompleteListener { displayNameTask ->
                            if (displayNameTask.isSuccessful) {
                                // Send email verification
                                sendEmailVerification(context, uriHandler, onRegistrationSuccess)
                            }
                        }
                }
            }
        }
}

private fun sendEmailVerification(
    context: android.content.Context,
    uriHandler: UriHandler,
    onRegistrationSuccess: () -> Unit
) {
    val auth = Firebase.auth
    val user = auth.currentUser

    user?.sendEmailVerification()
        ?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Registration successful, email verification sent
                // You can prompt the user to verify their email address

                val emailIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_EMAIL)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val emailPackage = emailIntent.resolveActivity(context.packageManager)?.packageName

                if (emailPackage != null) {
                    context.startActivity(emailIntent)
                } else {
                    val emailVerificationUri = Uri.parse("https://www.example.com/verify-email")
                    uriHandler.openUri(emailVerificationUri.toString())
                }

                onRegistrationSuccess()
            }
        }
}
