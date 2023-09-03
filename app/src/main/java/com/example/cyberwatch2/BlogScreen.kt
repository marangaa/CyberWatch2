package com.example.cyberwatch2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogScreen() {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Search Bar
        SearchBar(searchQuery) { query ->
            searchQuery = query
        }

        // Spacer
        Spacer(modifier = Modifier.height(16.dp))

        // Article Categories
        ArticleCategories()

        // Article List (Filtered by search query and selected category)
        ArticleList(searchQuery)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val density = LocalDensity.current.density

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = true
                }
                .background(Color.White, RoundedCornerShape(4.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = searchQuery.ifEmpty { "Search articles" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (expanded) {
            BasicTextField(
                value = searchQuery,
                onValueChange = {
                    onSearchQueryChange(it)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        expanded = false
                        keyboardController?.hide()
                    }
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ArticleCategories() {
    // You can fetch and display categories here
    // For simplicity, let's use static categories
    val categories = listOf("Technology", "Science", "Health", "Business")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (category in categories) {
            CategoryChip(category)
        }
    }
}

@Composable
fun CategoryChip(category: String) {
    var isSelected by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .clickable {
                isSelected = !isSelected
                // Handle category selection or filtering here
            }
            .padding(8.dp),
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
        )
    }
}

data class Article(
    val title: String = "",
    val content: String = ""
)

@Composable
fun ArticleList(searchQuery: String) {
    val firestore = Firebase.firestore
    var articles by remember { mutableStateOf(emptyList<Article>()) }
    val articlesCollection = firestore.collection("articles")

    // Fetch articles based on searchQuery and selected categories
    LaunchedEffect(searchQuery) {
        val query = articlesCollection
            .orderBy("title") // You can adjust the ordering as needed
            .startAt(searchQuery)
            .endAt(searchQuery + "\uf8ff") // Firebase wildcard search

        val fetchedArticles = mutableListOf<Article>()

        try {
            val result = query.get().await()
            for (document in result.documents) {
                val article = document.toObject(Article::class.java)
                if (article != null) {
                    fetchedArticles.add(article)
                }
            }
        } catch (e: Exception) {
            // Handle error
        }

        articles = fetchedArticles
    }

    LazyColumn {
        items(articles) { article ->
            ArticleItem(article)
        }
    }
}

@Composable
fun ArticleItem(article: Article) {
    var expanded by remember { mutableStateOf(false) }

    val cardElevation by animateDpAsState(if (expanded) 8.dp else 2.dp, label = "animate")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

