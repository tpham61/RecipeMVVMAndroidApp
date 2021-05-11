package com.example.recipemvvmandroidapp.view.tabView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipemvvmandroidapp.dependency.Dependency
import com.example.recipemvvmandroidapp.router.RouterController
import com.example.recipemvvmandroidapp.router.routerController
import com.example.recipemvvmandroidapp.view.viewComponent.RecipeCard
import com.example.recipemvvmandroidapp.viewModel.searchRecipeViewModel
import com.example.recipemvvmandroidapp.view.viewComponent.SearchBar
import com.example.recipemvvmandroidapp.viewModel.SearchRecipeViewModel

@Composable
fun Dependency.View.CreateSearchRecipeView(
    searchRecipeViewModel: SearchRecipeViewModel,
    routerController: RouterController
)
{
    val searchBarText: String by searchRecipeViewModel.searchBarText.observeAsState(initial = "")

    val recipeList = searchRecipeViewModel.recipeListForCardView.value

    Scaffold(
        modifier = Modifier,
        topBar = {
        },
        content = {
            Column(
                modifier = Modifier
            )
            {
                SearchBar(
                    textContent = searchBarText,
                    onValueChange = searchRecipeViewModel.onSearchTextChange,
                    labelContent = "Search recipe",
                    onSearch = searchRecipeViewModel.onSearch
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn() {
                    itemsIndexed(items = recipeList){ index, recipe ->
                        RecipeCard(
                            recipeName = recipe.title,
                            recipeImageUrl = recipe.featuredImage,
                            onClick = {
                                routerController.navigateToRecipeDetailView(recipe.id)
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun Dependency.View.SearchRecipeView()
{
    val searchRecipeViewModel = viewModel.searchRecipeViewModel()
    val routerController = router.routerController()
    CreateSearchRecipeView(
        searchRecipeViewModel = searchRecipeViewModel,
        routerController = routerController
    )
}