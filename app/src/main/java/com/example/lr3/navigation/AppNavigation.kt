package com.example.lr3.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lr3.ui.CharacterViewModel
import com.example.lr3.ui.screens.CharacterDetailScreen
import com.example.lr3.ui.screens.CharacterListScreen
import androidx.compose.runtime.remember

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {

        composable("list") { listEntry ->
            val viewModel: CharacterViewModel = hiltViewModel(listEntry)
            CharacterListScreen(
                uiState = viewModel.uiState,
                searchQuery = viewModel.searchQuery,
                selectedStatus = viewModel.selectedStatus,
                favourites = viewModel.favourites,
                favouritesError = viewModel.favouritesError,
                onSearchChange = viewModel::onSearchChange,
                onStatusChange = viewModel::onStatusChange,
                onCharacterClick = { id -> navController.navigate("detail/$id") },
                onRetry = viewModel::retry,
                onLoadMore = viewModel::loadNextPage,
                onFavouriteClick = viewModel::onFavouriteClick
            )
        }

        composable(
            route = "detail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("characterId") ?: return@composable

            val listEntry = remember(backStackEntry) {
                navController.getBackStackEntry("list")
            }
            val viewModel: CharacterViewModel = hiltViewModel(listEntry)

            LaunchedEffect(id) {
                viewModel.loadCharacter(id)
            }

            CharacterDetailScreen(
                uiState = viewModel.detailState,
                onRetry = { viewModel.loadCharacter(id) },
                onBack = { navController.navigateUp() },
                onFavouriteClick = viewModel::onFavouriteClick
            )
        }
    }
}