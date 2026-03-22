package com.example.bytebasket.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bytebasket.components.*
import com.example.bytebasket.products.ProductViewModel
import com.example.bytebasket.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun Home(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by productViewModel.searchResults

    // 🔁 Debounce search
    LaunchedEffect(searchQuery) {
        delay(400)
        productViewModel.getProductsBySearchKeyWord(context, searchQuery)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFDFDFD), Color(0xFFF4F6F9))
                )
            )
            .padding(12.dp)
    ) {
        Header(modifier)
        Spacer(modifier = Modifier.height(14.dp))

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(24.dp))
                .background(Color.White, RoundedCornerShape(24.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Scrollable Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            if (searchQuery.isNotBlank()) {
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(600)),
                        exit = fadeOut(animationSpec = tween(600))
                    ) {
                        Text(
                            "Search Results",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF222222),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }

                if (searchResults.isEmpty()) {
                    item {
                        Text(
                            "No matching products found",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    items(searchResults) { product ->
                        ProductItemCard(
                            product,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .shadow(3.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            } else {
                // 🔹 Banner
                item {
                    Banner(
                        modifier = Modifier
                            .height(220.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .shadow(5.dp, RoundedCornerShape(20.dp))
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // 🔹 Categories
                item {
                    SectionHeader("Categories")
                    CategoryView(modifier)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 🔹 Recently Viewed
                item {
                    SectionHeader("Recently Viewed")
                    RecentlyViewedSection(modifier)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search for products...", color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color(0xFF777777)
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color(0xFF777777)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFA7D208),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            cursorColor = Color(0xFFA7D208)
        )
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111111),
        modifier = Modifier.padding(bottom = 10.dp)
    )
}
