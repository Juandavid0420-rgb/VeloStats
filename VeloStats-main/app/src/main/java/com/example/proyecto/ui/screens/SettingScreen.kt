package com.example.proyecto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.R
import com.example.proyecto.ui.components.footerNavigation
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto.ui.ViewModels.RideViewModel


@Composable
fun SettingsScreen(navController: NavController) {
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
                    .background(brush = Brush.linearGradient(
                        colors = listOf(Color.Black, Color(0xFF957A2B)),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    ))
                    .padding(16.dp)
            ) {
                // Título de la pantalla
                Text(
                    text = "Configuración",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                )
                SensorSettingsSection()
                // Opciones de configuración
                SettingOptionList(



                )
            }
        }
    }
}

@Composable
fun SettingOptionList() {
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        item {
            SettingOptionCard(
                text = "Perfil",
                image =Icons.Filled.Person,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(8.dp)
            )
        }


                item {
                    SettingOptionCard(
                        text = "Notificaciones",
                        image = Icons.Filled.Notifications, // Ícono de notificaciones
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(8.dp)
                    )
                }
        item {
            SettingOptionCard(
                text = "Tema",
                image = Icons.Filled.Settings, // Ícono de configuración
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(8.dp)
            )
        }
        item {
            SettingOptionCard(
                text = "Cuenta",
                image = Icons.Filled.AccountCircle, // Ícono de cuenta
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(8.dp)
            )
        }

    }
}


@Composable
fun SettingOptionCard(
    text: String,
    image: ImageVector, // Usamos ImageVector para el ícono
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    iconColor: Color = Color.White // Parámetro adicional para el color del ícono
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = image, // Usamos Icon para los ImageVector
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = iconColor // Aquí se aplica el color al ícono
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
@Composable
fun SensorSettingsSection(viewModel: RideViewModel = viewModel()) {
    val useLightSensor by viewModel.useLightSensor.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Sensores",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SensorSwitchItem(
            title = "Usar sensor de luz para tema automático",
            checked = useLightSensor,
            onCheckedChange = { viewModel.setUseLightSensor(it) }
        )
    }
}

@Composable
fun SensorSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFFD700),
                    uncheckedThumbColor = Color.Gray
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(navController = rememberNavController())
}
