package com.example.cyberwatch2


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogScreen() {
    var searchQuery by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search articles") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Divider(modifier = Modifier.fillMaxWidth())
        ArticleList()
    }
}

@Composable
fun ArticleList() {
    val firestore = Firebase.firestore
    var articles by remember { mutableStateOf(emptyList<Article>()) }
    val articlesCollection = firestore.collection("articles")


    val sampleCybersecurityArticles = listOf(
        Article(
            "Protecting Your Online Privacy",
            "In a digital age, safeguarding your online privacy is of utmost importance. Learn about tools and practices to keep your personal information secure."
        ),
        Article(
            "Understanding Phishing Attacks",
            "Phishing is a common cyber threat that tricks users into revealing sensitive information. Explore how to recognize and defend against phishing attacks."
        ),
        Article(
            "The Importance of Two-Factor Authentication",
            "Two-factor authentication (2FA) adds an extra layer of security to your accounts. Discover how to enable and use 2FA for enhanced protection."
        ),
        Article(
            "Securing Your Home Network",
            "Securing your home network is essential to prevent unauthorized access. Learn about router settings, encryption, and best practices for network security."
        ),
        Article(
            "Ransomware: A Growing Threat",
            "Ransomware attacks can encrypt your data and demand a ransom for its release. Find out how to defend against ransomware and steps to take if infected."
        ),
        // Add more sample articles here
    )

    for (article in sampleCybersecurityArticles) {
        articlesCollection.add(article)
    }

    // Fetch articles from Firebase Firestore and update the articles list
    LaunchedEffect(Unit) {
        firestore.collection("articles")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fetchedArticles = mutableListOf<Article>()
                for (document in querySnapshot) {
                    val article = document.toObject(Article::class.java)
                    fetchedArticles.add(article)
                }
                articles = fetchedArticles
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    LazyColumn {
        items(articles) { article ->
            ArticleItem(article)
        }
    }
}

data class Article(
    val title: String = "",
    val content: String = ""
)

@Composable
fun ArticleItem(article: Article) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = article.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = article.content)
        }
    }
}
