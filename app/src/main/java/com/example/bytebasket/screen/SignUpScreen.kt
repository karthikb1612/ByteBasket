package com.example.bytebasket.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bytebasket.AppUtil
import com.example.bytebasket.R
import com.example.bytebasket.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, navController: NavHostController,authViewModel: AuthViewModel) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var PhoneNo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var context= LocalContext.current
    var isloading by remember { mutableStateOf(false) }
    Column(
        modifier= Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Hello there!",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFF5871F)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Create an account",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
            fontFamily = FontFamily.Serif,
            color = Color(0xFFF5871F)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.signupbanner),
            contentDescription = "banner",
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = {name=it},
            label = { Text("Full Name")}
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = PhoneNo,
            onValueChange = {PhoneNo=it},
            label = { Text("Phone Number")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {email=it},
            label = { Text("Email address")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {password=it},
            label = { Text("Password")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(20.dp))
        if(!isloading){
            FloatingActionButton(
                onClick = {
                    isloading=true
                    if(name.isEmpty() || email.isEmpty() || PhoneNo.isEmpty() || password.isEmpty()){
                        isloading=false
                        AppUtil.showToast(context,"Field cannot be empty")
                    }else{
                        authViewModel.signUp(name,PhoneNo,email,password){ success,errorMess ->
                            if(success){
                                isloading=false
                                AppUtil.showToast(context,"Account created successfully!")
                                navController.navigate("Home") {
                                    popUpTo("Auth"){ inclusive=true}
                                }
                            }else{
                                isloading=false
                                AppUtil.showToast(context, message = errorMess?:"Something went wrong")
                            }

                        }
                    }
                    if(isloading){
                        AppUtil.showToast(context,"Creating account...")
                    }
                },
                modifier = Modifier.width(288.dp).height(60.dp),
                containerColor = Color(0xF0F1E57F),
            ) {
                Text("Signup", fontSize = 22.sp, color = Color(0xFF3A0146))
            }

        }

    }
}