package com.example.proyecto.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.R
import com.example.proyecto.ui.components.footerNavigation

data class Friend(
    val id: String,
    val name: String,
    val profileImageRes: Int
)

@Composable
fun FriendScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { footerNavigation(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color.Black, Color(0xFF957A2B)),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis amigos",
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.perfil),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }

                CardList(navController = navController, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun CardList(navController: NavController, modifier: Modifier = Modifier) {
    val friends = listOf(
        Friend("estefania123", "Estefania", R.drawable.ciclista1),
        Friend("carlos456", "Carlos", R.drawable.ciclista2),
        Friend("juan789", "Juan", R.drawable.ciclista3)
    )

    LazyColumn(modifier = modifier.fillMaxHeight()) {
        items(friends) { friend ->
            CardWithBackground(
                text = friend.name,
                image = painterResource(id = friend.profileImageRes),
                alpha = 0.4f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                onClick = {
                    navController.navigate("friend_routes_screen/${friend.id}")
                }
            )
        }
    }
}

@Composable
fun CardWithBackground(
    text: String,
    image: Painter,
    alpha: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = alpha),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewFriend() {
    FriendScreen(navController = rememberNavController())
}
