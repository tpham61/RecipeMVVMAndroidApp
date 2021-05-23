package com.example.recipemvvmandroidapp.viewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.recipemvvmandroidapp.domain.useCase.GetRecipeListUseCase
import com.example.recipemvvmandroidapp.domain.useCase.UseCaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchRecipeViewModel @Inject constructor(
    private val getRecipeListUseCase: GetRecipeListUseCase
): ViewModel() {
    data class RecipeForCardView(
        val id: Int,
        val title: String,
        val featuredImage: String
    )

    //data for search bar
    private val _searchBarText = MutableLiveData("")
    val searchBarText: LiveData<String> = _searchBarText

    //data for lazy list
    var recipeListForCardView: MutableState<List<RecipeForCardView>> = mutableStateOf(listOf())

    private var pageIndex: Int = 1

    //event for search bar
    val onSearchTextChanged: (String) -> Unit = {
        _searchBarText.value = it
    }

    val onSearch: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            val searchResult = getRecipeListUseCase
                .execute(
                    page = pageIndex,
                    query = searchBarText.value ?: ""
                )
            when(searchResult)
            {
                is UseCaseResult.Success -> {
                    recipeListForCardView.value = searchResult.resultValue.map{
                        RecipeForCardView(
                            id = it.id,
                            title = it.title,
                            featuredImage = it.featuredImage
                        )
                    }
                }
                is UseCaseResult.Error -> Log.d("Debug: SearchRecipeViewModel",
                    searchResult.exception.toString()
                )
            }
        }
    }
}

//class SearchRecipeViewModelFactory(
//    private val getRecipeListUseCase: GetRecipeListUseCase
//): ViewModelProvider.Factory{
//    override fun <T : ViewModel?> create(
//        modelClass: Class<T>
//    ): T {
//        return SearchRecipeViewModel(getRecipeListUseCase) as T
//    }
//}