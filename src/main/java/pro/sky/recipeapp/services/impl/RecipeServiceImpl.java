package pro.sky.recipeapp.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.recipeapp.exeptions.ExeptionWebApp;
import pro.sky.recipeapp.model.Ingredient;
import pro.sky.recipeapp.model.Recipe;
import pro.sky.recipeapp.services.FilesService;
import pro.sky.recipeapp.services.IngredientService;
import pro.sky.recipeapp.services.RecipeService;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static pro.sky.recipeapp.services.impl.IngredientServiceImpl.getIngredients;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final FilesService filesService;
    private final IngredientService ingredientService;

    @Value("${name.of.recipes.data.file}")
    private String recipesDataFileName;
    private static Integer id = 0;
    private Map<Integer, Recipe> recipes = new LinkedHashMap<>();

    public RecipeServiceImpl(FilesService filesService, IngredientService ingredientService) {
        this.filesService = filesService;
        this.ingredientService = ingredientService;
    }

    @PostConstruct
    private void init() {
        try {
            readFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int addRecipe(Recipe recipe) {
        recipes.put(id ++, recipe);
        ingredientService.addIngredientsToTheRecipe(recipe.getIngredients());
        saveToFile();
        return id;
    }

    @Override
    public Recipe updateRecipe(int id, Recipe recipe) throws ExeptionWebApp {
        if (recipes.containsKey(id)) {
            saveToFile();
            return recipes.put(id, recipe);
        } else {
            throw new ExeptionWebApp("Рецепт не найден");
        }
    }

    @Override
    public boolean deleteRecipe(int id) {
        if (recipes.containsKey(id)) {
            recipes.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAllRecipe() {
        recipes = new LinkedHashMap<>();
        filesService.cleanDataFile(recipesDataFileName);
    }

    @Override
    public Recipe getRecipe(int id) {
        return recipes.get(id);
    }

    @Override
    public Map<Integer, Recipe> getAllRecipe() {
        if (!recipes.isEmpty()) {
            return recipes;
        }
        return new LinkedHashMap<>();
    }

    @Override
    public List<Recipe> findRecipeByIngredientId(int id) {
        Ingredient ingredient = getIngredients().get(id);
        if (ingredient != null) {
            return recipes.values()
                    .stream()
                    .filter(x -> x.getIngredients().contains(ingredient))
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(recipes);
            filesService.saveToFile(json, recipesDataFileName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() throws ExeptionWebApp {
        String json = filesService.readFromFile(recipesDataFileName);
        try {
            recipes = new ObjectMapper().readValue(json, new TypeReference<LinkedHashMap<Integer, Recipe>>() {
            });
        } catch (JsonProcessingException e) {
            throw new ExeptionWebApp("Ошибка чтения файла");
        }
    }

    @Override
    public Path createReport() throws IOException, ExeptionWebApp {
        recipes.getOrDefault(id, null);
        Path path = filesService.createTempFile("Recipes");
        for (Recipe recipe : recipes.values()) {
            try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                writer.append(recipe.getTitle()).append("\n")
                        .append("Время приготовления: ")
                        .append(String.valueOf(recipe.getTime()))
                        .append(" минут \n")
                        .append("Ингредиенты: \n");
                for (Ingredient ingredient : recipe.getIngredients()) {
                    writer.append(ingredient.getTitle())
                            .append(" - ")
                            .append(String.valueOf(ingredient.getQuantity()))
                            .append(" ")
                            .append(ingredient.getUnitsOfMeasurement())
                            .append("\n");
                }
                writer.append("Инструкция приготовления: \n");
                for (String str : recipe.getCookingSteps()) {
                    writer.append(str)
                            .append(";\n");
                }
                writer.append("\n\n");
            }
        }
        return path;
    }
}