package com.example.bytebasket.screen


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home

import androidx.compose.material.icons.filled.Person

import androidx.compose.material.icons.filled.ShoppingCart

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.example.bytebasket.pages.Cart
import com.example.bytebasket.pages.Favorite
import com.example.bytebasket.pages.Home
import com.example.bytebasket.pages.Profile
import com.example.bytebasket.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val navItemList=listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Favorite", Icons.Default.Favorite),
        NavItem("Cart", Icons.Default.ShoppingCart),
        NavItem("Profile", Icons.Default.Person)

    )
    var selector by remember { mutableStateOf(0) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index,navItem ->
                    NavigationBarItem(
                        selected = selector==index,
                        onClick = { selector=index },
                        icon = {
                            Icon(navItem.Icon, contentDescription = "icons")
                        },
                        label = { Text(navItem.label) }
                    )
                }
            }
        }
    ) {
        ContentScreen(modifier = modifier.padding(it),selector,navController,authViewModel)
    }
}
@Composable
fun ContentScreen(
    modifier: Modifier,
    selector: Int,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    AnimatedContent(
        targetState = selector,
        transitionSpec = {
            // You can customize the animation direction based on selection
            (slideInHorizontally { it } + fadeIn()).togetherWith(
                slideOutHorizontally { -it } + fadeOut()
            )
        },
        modifier = modifier
    ) { ts ->
        when (ts) {
            0 -> Home(modifier, navController, authViewModel)
            1 -> Favorite(modifier, navController, authViewModel)
            2 -> Cart(modifier, navController, authViewModel)
            3 -> Profile(modifier, navController,authViewModel)
        }
    }
}
data class NavItem(
    val label: String,
    val Icon: ImageVector
)
