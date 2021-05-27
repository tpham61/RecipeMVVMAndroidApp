package com.example.recipemvvmandroidapp.ui.view.tabView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.recipemvvmandroidapp.domain.model.Recipe
import com.example.recipemvvmandroidapp.ui.router.RouterController
import com.example.recipemvvmandroidapp.ui.view.viewComponent.RecipeCard
import com.example.recipemvvmandroidapp.ui.view.viewComponent.SearchBar
import com.example.recipemvvmandroidapp.ui.viewModel.SearchRecipeViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun CreateSearchRecipeView(
    searchBarText: String,
    onSearchTextChanged: (String) -> Unit,
    lazyPagingItems: LazyPagingItems<Recipe>,
    onSearch: () -> Unit,
    onClickRecipeCard: (Int) -> Unit
){
    Column(
        modifier = Modifier.padding(12.dp)
    )
    {
        SearchBar(
            textContent = searchBarText,
            onValueChange = onSearchTextChanged,
            labelContent = "Search recipe",
            onSearch = onSearch
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            //show a loading indicator while waiting for the list to load
            if(lazyPagingItems.loadState.refresh == LoadState.Loading){
                item{
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        CircularProgressIndicator()
                    }
                }
            }

            items(lazyPagingItems = lazyPagingItems){ recipe ->
                RecipeCard(
                    recipeName = recipe!!.title,
                    recipeImageUrl = recipe.featuredImage,
                    onClick = {
                        onClickRecipeCard(recipe.id)
                    }
                )
            }

            //show a loading indicator at the bottom of the list while appending new items to the list
            if(lazyPagingItems.loadState.append == LoadState.Loading){
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun SearchRecipeView(
    router: RouterController,
//    searchRecipeViewModel: SearchRecipeViewModel
)
{
    val searchRecipeViewModel: SearchRecipeViewModel = hiltViewModel()
    val searchBarText: String by searchRecipeViewModel.searchBarText.observeAsState(initial = "")
    val lazyPagingItems = searchRecipeViewModel.pagingFlow.value.collectAsLazyPagingItems()
    CreateSearchRecipeView(
        searchBarText = searchBarText,
        onSearchTextChanged = searchRecipeViewModel.onSearchTextChanged,
        lazyPagingItems = lazyPagingItems,
        onSearch = searchRecipeViewModel.onSearch,
        onClickRecipeCard = {recipeId: Int ->
            router.navigateToRecipeDetailView(recipeId)
        }
    )
}

@Preview
@Composable
fun PreviewSearchRecipeView()
{
    CreateSearchRecipeView(
        searchBarText = "chicken",
        onSearchTextChanged = { /*TODO*/ },
        lazyPagingItems = flowOf(PagingData.empty<Recipe>()).collectAsLazyPagingItems(),
        onSearch = { },
        onClickRecipeCard = {}
    )
}