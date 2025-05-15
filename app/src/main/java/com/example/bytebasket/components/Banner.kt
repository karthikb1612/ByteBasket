package com.example.bytebasket.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.IndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType

@Composable
fun Banner(modifier: Modifier = Modifier) {
    var bannerList by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data").document("banners").
                get().addOnCompleteListener {
                    bannerList=it.result.get("urls") as List<String>
        }
    }
    Column {
        var pagerState = rememberPagerState { bannerList.size }
        HorizontalPager(
            pagerState,
        ) {
            AsyncImage(
                model = bannerList.get(it),
                contentDescription = "banners",
                modifier = Modifier.fillMaxWidth().padding(6.dp).clip(RoundedCornerShape(16.dp))
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        DotsIndicator(
            dotCount = bannerList.size,
            type = ShiftIndicatorType(
                dotsGraphic = DotGraphic(color = MaterialTheme.colorScheme.secondary, size = 6.dp)),
            pagerState = pagerState,
        )
    }

}