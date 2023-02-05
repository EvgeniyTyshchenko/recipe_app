package pro.sky.recipeapp.services;

import pro.sky.recipeapp.exeptions.ExeptionWebApp;
import pro.sky.recipeapp.model.Recipe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface RecipeService {

    int addRecipe(Recipe recipe);

    Recipe updateRecipe(int id, Recipe recipe) throws ExeptionWebApp;

    boolean deleteRecipe(int id);

    void deleteAllRecipe();

    Recipe getRecipe(int id);

    Map<Integer, Recipe> getAllRecipe();

    List<Recipe> findRecipeByIngredientId(int id);

    Path createReport() throws IOException, ExeptionWebApp;
}
