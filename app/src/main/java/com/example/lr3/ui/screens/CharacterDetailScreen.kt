package com.example.lr3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lr3.model.Character
import com.example.lr3.ui.CharacterDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    uiState: CharacterDetailUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onFavouriteClick: (Character) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Персонаж", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Button(onClick = onBack) { Text("Назад") }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (uiState) {
                is CharacterDetailUiState.Loading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Загрузка")
                    }
                }

                is CharacterDetailUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRetry) { Text("Повторить") }
                    }
                }

                is CharacterDetailUiState.Success -> {
                    val character = uiState.character
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = character.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                DetailRow("Статус", character.status)
                                DetailRow("Вид", character.species)
                                if (character.type.isNotBlank()) {
                                    DetailRow("Тип", character.type)
                                }
                                DetailRow("Пол", character.gender)
                                DetailRow("Происхождение", character.originName)
                                DetailRow("Локация", character.locationName)
                                DetailRow("Эпизодов", character.episodeCount.toString())
                                DetailRow("Добавлен", character.created)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onFavouriteClick(character) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (character.isFavourite) "Убрать из избранного"
                                else "Добавить в избранное"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(value, modifier = Modifier.weight(1.5f))
    }
    HorizontalDivider()
}