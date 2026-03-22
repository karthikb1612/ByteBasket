package com.example.bytebasket.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.bytebasket.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var profileImageUrlList by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }

    // Load user data
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            Firebase.firestore.collection("user").document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("Profile", "Firestore error: ", error)
                        return@addSnapshotListener
                    }
                    snapshot?.data?.let {
                        userData = it
                    }
                }

            Firebase.firestore.collection("profileImage").document("image")
                .get().addOnSuccessListener {
                    val urls = it.get("imageUrls") as? List<String>
                    if (urls != null) profileImageUrlList = urls
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ---------- HEADER SECTION WITH GRADIENT ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape)
                ) {
                    if (profileImageUrlList.isNotEmpty()) {
                        AsyncImage(
                            model = profileImageUrlList.first(),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Default Profile",
                            modifier = Modifier.fillMaxSize(),
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = userData?.get("name")?.toString() ?: "User Name",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = userData?.get("email")?.toString() ?: "user@email.com",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(0.8f))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- USER INFO CARD ----------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = "Name", value = userData?.get("name")?.toString() ?: "N/A")
                Divider()
                InfoRow(label = "Email", value = userData?.get("email")?.toString() ?: "N/A")
                Divider()
                InfoRow(label = "Phone", value = userData?.get("phoneNo")?.toString() ?: "N/A")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- MENU SECTION ----------
        Text("My Sections", style = MaterialTheme.typography.titleMedium, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(12.dp))
        ProfileMenuItem(Icons.Default.Favorite, "My Favorites") {
            navController.navigate("Favorite")
        }
        ProfileMenuItem(Icons.Default.ShoppingCart, "My Cart") {
            navController.navigate("Cart")
        }
        ProfileMenuItem(Icons.Default.AddCircle, "Add Category") {
            navController.navigate("AddCategoryForm")
        }
        ProfileMenuItem(Icons.Default.AddCircle, "Add Products") {
            navController.navigate("AddProducts")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- SIGN OUT BUTTON ----------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("Login") {
                        popUpTo("Profile") { inclusive = true }
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Red)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Sign Out",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ---------- REUSABLE COMPONENTS ----------

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.width(80.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp
        )
    }
}

@Composable
fun ProfileMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
        }
    }
}
