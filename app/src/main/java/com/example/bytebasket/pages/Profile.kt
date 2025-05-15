package com.example.bytebasket.pages


import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.example.bytebasket.viewmodel.AuthViewModel

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var profileImageUrlList by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }

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
            .fillMaxSize().verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Profile Image
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
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
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

        // User Info Section - Left aligned
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text("User Information", style = MaterialTheme.typography.bodyLarge, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))

            InfoRow(label = "Name", value = userData?.get("name")?.toString() ?: "N/A")
            InfoRow(label = "Email", value = userData?.get("email")?.toString() ?: "N/A")
            InfoRow(label = "Phone", value = userData?.get("phoneNo")?.toString() ?: "N/A")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

        // Navigation Buttons
        Text("My Sections", style = MaterialTheme.typography.bodyLarge, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = { navController.navigate("Favorite") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("My Favorites")
        }

        Spacer(modifier = Modifier.height(12.dp))

        FloatingActionButton(
            onClick = { navController.navigate("Cart") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("My Cart")
        }

        Spacer(modifier = Modifier.height(12.dp))

        FloatingActionButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("Login") {
                    popUpTo("Profile") { inclusive = true }
                }
            },
            containerColor = Color.Red,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Out", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Reusable row for user info
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
            fontSize = 18.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp
        )
    }
}













//Column(
//modifier = Modifier
//.fillMaxSize()
//.padding(32.dp),
//verticalArrangement = Arrangement.Top,
//horizontalAlignment = Alignment.CenterHorizontally
//) {
//    Spacer(modifier = Modifier.height(40.dp))
//
//
//
//    Spacer(modifier = Modifier.height(32.dp))
//
//    Button(
//        onClick = {
//            navController.navigate("AddCategoryForm")
//        }
//    ) {
//        Text("Add Category")
//    }
//
//    Spacer(modifier = Modifier.height(16.dp))
//
//    Button(
//        onClick = {
//            navController.navigate("AddProducts")
//        }
//    ) {
//        Text("Add Product")
//    }
//}
