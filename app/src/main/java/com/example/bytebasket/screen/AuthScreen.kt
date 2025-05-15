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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bytebasket.R

@Composable
fun AuthScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.banner),
            contentDescription = "banner",
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Start you shopping journey now",
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            color = Color(0xFFF57D9F)
        )
        Spacer(modifier = Modifier.height(20.dp))
        FloatingActionButton(
            onClick = {
                navController.navigate("Login")
            },
            containerColor = Color(0xFFBCEDFA),
            modifier = Modifier.width(320.dp).height(60.dp).clip(RoundedCornerShape(50))
        ) {
            Text("Login", fontSize = 22.sp, color = Color(0xFF3A0146))
        }
        Spacer(modifier = Modifier.height(20.dp))
        FloatingActionButton(
            onClick = {
                navController.navigate("SignUp")
            },
            containerColor = Color(0xFFBCEDFA),
            modifier = Modifier.width(320.dp).height(60.dp).clip(RoundedCornerShape(50))
        ) {
            Text("Signup", fontSize = 22.sp, color = Color(0xFF3A0146))
        }
    }
}