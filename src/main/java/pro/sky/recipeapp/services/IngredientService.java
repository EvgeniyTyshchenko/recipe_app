package pro.sky.recipeapp.services;

import pro.sky.recipeapp.exeptions.ExeptionWebApp;
import pro.sky.recipeapp.model.Ingredient;

import java.util.List;
import java.util.Map;

public interface IngredientService {

    int addIngredient(Ingredient ingredient);

    void addIngredientsToTheRecipe(List<Ingredient> ingredientList);

    Ingredient updateIngredient(int id, Ingredient ingredient) throws ExeptionWebApp;

    boolean deleteIngredient(int id);

    void deleteAllIngredients();

    Ingredient getIngredient(int id);

    Map<Integer, Ingredient> getAllIngredients();
}
