package pro.sky.recipeapp.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.recipeapp.exeptions.ExeptionWebApp;
import pro.sky.recipeapp.model.Ingredient;
import pro.sky.recipeapp.services.FilesService;
import pro.sky.recipeapp.services.IngredientService;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final FilesService filesService;

    @Value("${name.of.ingredients.data.file}")
    private String ingredientsDataFileName;
    private static Integer id = 0;
    private static Map<Integer, Ingredient> ingredients = new LinkedHashMap<>();

    public IngredientServiceImpl(FilesService filesService) {
        this.filesService = filesService;
    }

    public static Map<Integer, Ingredient> getIngredients() {
        return ingredients;
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
    public int addIngredient(Ingredient ingredient) {
        ingredients.put(id ++, ingredient);
        saveToFile();
        return id;
    }

    @Override
    public void addIngredientsToTheRecipe(List<Ingredient> ingredientList) {
        for (Ingredient ingredient : ingredientList) {
            if (!ingredients.containsValue(ingredient)) {
                ingredients.put(id++, ingredient);
            }
        }
        saveToFile();
    }

    @Override
    public Ingredient updateIngredient(int id, Ingredient ingredient) throws ExeptionWebApp {
        if (ingredients.containsKey(id)) {
            saveToFile();
            return ingredients.put(id, ingredient);
        } else {
            throw new ExeptionWebApp("Ингредиент не найден");
        }
    }

    @Override
    public boolean deleteIngredient(int id) {
        if (ingredients.containsKey(id)) {
            ingredients.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAllIngredients() {
        ingredients = new LinkedHashMap<>();
        filesService.cleanDataFile(ingredientsDataFileName);
    }

    @Override
    public Ingredient getIngredient(int id) {
        return ingredients.get(id);
    }

    @Override
    public Map<Integer, Ingredient> getAllIngredients() {
        if (!ingredients.isEmpty()) {
            return ingredients;
        }
        return new LinkedHashMap<>();
    }

    private void saveToFile() {
        try {
           String json = new ObjectMapper().writeValueAsString(ingredients);
           filesService.saveToFile(json, ingredientsDataFileName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() throws ExeptionWebApp {
        String json = filesService.readFromFile(ingredientsDataFileName);
        try {
            ingredients = new ObjectMapper().readValue(json, new TypeReference<LinkedHashMap<Integer, Ingredient>>() {
            });
        } catch (JsonProcessingException e) {
            throw new ExeptionWebApp("Ошибка чтения файла");
        }
    }
}