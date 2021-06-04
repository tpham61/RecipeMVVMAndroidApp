package com.example.recipemvvmandroidapp.ui.viewModel

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.recipemvvmandroidapp.domain.model.RecipeDTO
import com.example.recipemvvmandroidapp.dependency.Dependency
import com.example.recipemvvmandroidapp.domain.useCase.GetRecipeListUseCase
import com.example.recipemvvmandroidapp.domain.useCase.UseCaseResult
import com.example.recipemvvmandroidapp.domain.useCase.getRecipeListUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

class SearchRecipeViewModel(
    private val getRecipeListUseCase: GetRecipeListUseCase
): ViewModel() {
    private val uiMutableState = MutableStateFlow(SearchRecipeViewStates.empty)
    val uiState: StateFlow<SearchRecipeViewStates> = uiMutableState

    private var mostRecentlyLoadedPage = 0

    private var currentNetworkJob: Job = Job()

    //event for search bar
    val onSearchBarTextChanged: (String) -> Unit = { newString: String ->
        try{
            currentNetworkJob.cancel()
        } catch (exception: Exception){
            Log.d("Exception in SearchRecipeViewModel: onSearchBarTextChanged", "$exception")
        }
        uiMutableState.value = SearchRecipeViewStates(
            searchBarText = newString,
            recipeList = listOf(),
            lazyListState = LazyListState(),
            isLoading = false,
            loadError = false
        )
        mostRecentlyLoadedPage = 0
    }

    val checkIfNewPageIsNeeded: () -> Unit = {
        //this check will let pagination always load 1 page a head
        //ex: firstVisibleItemIndex = 1, page = 1 => 1 + 30 > 1 * 30 => load page 2
        if(uiMutableState.value.lazyListState.firstVisibleItemIndex + API_PAGE_SIZE > mostRecentlyLoadedPage * API_PAGE_SIZE){
            if(!uiMutableState.value.isLoading){
                uiMutableState.value = uiMutableState.value.copy(
                    isLoading = true
                )
                mostRecentlyLoadedPage += 1
                currentNetworkJob = viewModelScope.launch(Dispatchers.IO){
                    delay(1000)
                    println("Job launch with query: ${uiMutableState.value.searchBarText}")
                    when(val useCaseResult = getRecipeListUseCase.execute(
                        page = mostRecentlyLoadedPage,
                        query = uiMutableState.value.searchBarText
                    ))
                    {
                        is UseCaseResult.Success -> {
                            uiMutableState.value = uiMutableState.value.copy(
                                loadError = false
                            )
                            appendNewPage(useCaseResult.resultValue)
                        }
                        is UseCaseResult.Error -> {
                            uiMutableState.value = uiMutableState.value.copy(
                                loadError = true
                            )
                            Log.d("Exception in SearchRecipeViewmodel: checkIfNewPageIsNeeded", "${useCaseResult.exception}")
                        }
                    }
                    uiMutableState.value = uiMutableState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun appendNewPage(newRecipeList: List<RecipeDTO>){
        val currentRecipeList = ArrayList(uiMutableState.value.recipeList)
        currentRecipeList.addAll(newRecipeList)
        uiMutableState.value = uiMutableState.value.copy(
            recipeList = currentRecipeList
        )
    }
}

data class SearchRecipeViewStates(
    val searchBarText: String,
    val recipeList: List<RecipeDTO>,
    val lazyListState: LazyListState,
    val isLoading: Boolean,
    val loadError: Boolean
){
    companion object{
        val empty = SearchRecipeViewStates(
            searchBarText = "",
            recipeList = listOf(),
            lazyListState = LazyListState(),
            isLoading = false,
            loadError = false
        )
    }
}


class SearchRecipeViewModelFactory(
    private val getRecipeListUseCase: GetRecipeListUseCase
): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        modelClass: Class<T>
    ): T {
        return SearchRecipeViewModel(getRecipeListUseCase) as T
    }
}

@Composable
fun Dependency.ViewModel.searchRecipeViewModel(): SearchRecipeViewModel {
    return viewModel(
        key = "SearchRecipeViewModel",
        factory = SearchRecipeViewModelFactory(
            useCase.getRecipeListUseCase()
        )
    )
}