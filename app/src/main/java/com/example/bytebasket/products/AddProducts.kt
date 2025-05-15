package com.example.bytebasket.products

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddProducts(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    productViewModel: ProductViewModel
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var actualPrice by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // For otherDetails map
    var attributeKey by rememberSaveable { mutableStateOf("") }
    var attributeValue by rememberSaveable { mutableStateOf("") }
    var otherDetails by remember { mutableStateOf(mutableMapOf<String, String>()) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
        OutlinedTextField(value = actualPrice, onValueChange = { actualPrice = it }, label = { Text("Actual Price") })
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })

        // Key-Value Pair Input for Other Details
        Text("Add Product Attribute:")
        OutlinedTextField(value = attributeKey, onValueChange = { attributeKey = it }, label = { Text("Attribute Key") })
        OutlinedTextField(value = attributeValue, onValueChange = { attributeValue = it }, label = { Text("Attribute Value") })

        Button(onClick = {
            if (attributeKey.isNotBlank() && attributeValue.isNotBlank()) {
                otherDetails[attributeKey] = attributeValue
                attributeKey = ""
                attributeValue = ""
            } else {
                Toast.makeText(context, "Please fill both key and value", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Add Attribute")
        }

        // Display added attributes
        if (otherDetails.isNotEmpty()) {
            Text("Attributes:")
            otherDetails.forEach { (key, value) ->
                Text("- $key: $value")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Upload Image")
        }

        imageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = "Selected Image",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (title.isNotBlank() && description.isNotBlank() && price.isNotBlank()
                    && actualPrice.isNotBlank() && imageUri != null
                ) {
                    val products = Products(
                        id = null,
                        title = title,
                        description = description,
                        price = price.toDouble(),
                        actualPrice = actualPrice.toDouble(),
                        category = category,
                        imageName = null,
                        imageType = null,
                        imageData = null,
                        otherDetails = otherDetails
                    )
                    productViewModel.addProducts(context, products, imageUri!!)
                    Toast.makeText(context, "Product Added", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Product")
        }
    }
}

