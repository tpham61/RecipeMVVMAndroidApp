package com.example.recipemvvmandroidapp.domain.useCase

import com.example.recipemvvmandroidapp.data.repositoryImplementation.RecipeRepository
import com.example.recipemvvmandroidapp.data.repositoryImplementation.recipeRepository
import com.example.recipemvvmandroidapp.dependency.Dependency

class GetRecipeListUseCase(
    private val recipeRepository: RecipeRepository
) {
    data class RecipeForCardView(
        val id: Int,
        val title: String,
        val featuredImage: String
    )

    suspend fun execute(
        page: Int,
        query: String
    ): UseCaseResult<List<RecipeForCardView>>
    {
        return try{
            UseCaseResult.Success(recipeRepository
                .searchForRecipes(page, query)
                .map{
                    RecipeForCardView(
                        it.id,
                        it.title,
                        it.featuredImage
                    )
                })
        } catch(exception: Exception){
            UseCaseResult.Error(exception)
        }
    }
}

fun Dependency.UseCase.getRecipeListUseCase(): GetRecipeListUseCase
{
    return GetRecipeListUseCase(repository.recipeRepository())
}