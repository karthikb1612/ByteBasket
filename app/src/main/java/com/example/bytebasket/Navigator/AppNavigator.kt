package com.example.bytebasket.Navigator

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bytebasket.category.AddCategoryForm
import com.example.bytebasket.category.CategoryViewModel
import com.example.bytebasket.pages.Cart
import com.example.bytebasket.pages.CategoryProducts
import com.example.bytebasket.pages.Favorite
import com.example.bytebasket.pages.ProductDetailPage
import com.example.bytebasket.products.AddProducts
import com.example.bytebasket.products.ProductViewModel
import com.example.bytebasket.screen.AuthScreen
import com.example.bytebasket.screen.HomeScreen
import com.example.bytebasket.screen.LoginScreen
import com.example.bytebasket.screen.SignUpScreen
import com.example.bytebasket.viewmodel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigator(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    categoryViewModel: CategoryViewModel,
    productViewModel: ProductViewModel
) {
    var navController = rememberNavController()
    GlobalNavigator.navHostController=navController
    val isLogged= Firebase.auth.currentUser!=null
    val firstPage = if(isLogged) "Home" else "Auth"
    NavHost(
        navController = navController, startDestination = firstPage
    ) {
        composable(
            "Auth",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            AuthScreen(modifier,navController)

        }
        composable(
            "Login",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            LoginScreen(modifier,navController,authViewModel)
        }
        composable(
            "SignUp",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            SignUpScreen(modifier,navController,authViewModel)
        }
        composable(
            "Home",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            HomeScreen(modifier,navController,authViewModel)
        }
        composable(
            "Cart",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            Cart(Modifier,navController,authViewModel)
        }
        composable(
            "Favorite",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            Favorite(Modifier,navController,authViewModel)
        }
        composable(
            "AddCategoryForm",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            AddCategoryForm(modifier,navController,categoryViewModel)
        }
        composable(
            "CategoryProducts/{categoryId}",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            var categoryId=it.arguments?.getString("categoryId")
            CategoryProducts(modifier,categoryId?:"")
        }

        composable(
            "AddProducts",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            AddProducts(modifier,navController,productViewModel)
        }
        composable(
            "ProductDetailPage/{productId}",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() }
        ) {
            var productId=it.arguments?.getString("productId")
            ProductDetailPage(modifier,productId?:"")
        }
    }
}
object GlobalNavigator{
    lateinit var navHostController: NavHostController
}