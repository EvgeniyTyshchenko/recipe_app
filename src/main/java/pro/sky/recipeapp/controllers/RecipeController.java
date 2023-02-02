package pro.sky.recipeapp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pro.sky.recipeapp.exeptions.ExeptionWebApp;
import pro.sky.recipeapp.model.Recipe;
import pro.sky.recipeapp.services.RecipeService;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recipe")
@Tag(name = "Рецепты", description = "CRUD-операции для работы с рецептами")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Operation(summary = "Добавление рецепта",
            description = "При добавлении рецепта требуется заполнить поля в формате JSON")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепт добавлен"
            )
    })
    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> addRecipe(@RequestBody Recipe recipe) {
        int id = recipeService.addRecipe(recipe);
        return ResponseEntity.ok(id);
    }

    @Operation(summary = "Получение рецепта по ID",
            description = "Требуется вводить целое число")
    @Parameters(value = {
            @Parameter(
                    name = "id",
                    example = "0"
            )}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепт найден!",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Recipe.class)
                            )
                    }
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable int id) {
        Recipe recipe = recipeService.getRecipe(id);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    @Operation(summary = "Поиск рецепта по id ингредиента",
            description = "Требуется вводить целое число")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепт найден"
            )
    })
    @GetMapping(value = "/findRecipeById", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Recipe>> findRecipeByIngredientId(@RequestParam int id) {
        if (recipeService.findRecipeByIngredientId(id) != null) {
            return ResponseEntity.ok(recipeService.findRecipeByIngredientId(id));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Обновление рецепта по ID",
            description = "Требуется вводить целое число")
    @Parameters(value = {
            @Parameter(
                    name = "id",
                    example = "0"
            )}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепт обновлён!"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Рецепт не найден!"
            )
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateRecipe(@PathVariable int id, @RequestBody Recipe recipe) throws ExeptionWebApp {
        Recipe result = recipeService.updateRecipe(id, recipe);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe.getTitle() + " обновлён!");
    }

    @Operation(summary = "Удаление рецепта по ID",
            description = "Требуется вводить целое число")
    @Parameters(value = {
            @Parameter(
                    name = "id",
                    example = "0"
            )}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепт удалён!"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Рецепт не найден!"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable int id) {
        if (recipeService.deleteRecipe(id)) {
            return ResponseEntity.ok("Рецепт №" + id + " удалён!");
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удаление всех рецептов")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllRecipe() {
        recipeService.deleteAllRecipe();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение списка всех рецептов",
            description = "Входные данные не нужны!<br>" +
                    "Список рецептов будет получен в теле ответа.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Recipe.class))
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Список пуст!"
            )
    })
    @GetMapping("/getAll")
    public ResponseEntity<Map<Integer, Recipe>> getAllRecipe() {
        Map<Integer, Recipe> allRecipe = recipeService.getAllRecipe();
        if (allRecipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allRecipe);
    }

    @Operation(summary = "Скачать рецепты",
            description = "Входные данные не нужны!")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Всё хорошо, запрос выполнился"
                    ), @ApiResponse(
                    responseCode = "400",
                    description = "Есть ошибка в параметрах запроса"
            ), @ApiResponse(
                    responseCode = "500",
                    description = "Во время выполнения запроса произошла ошибка на сервере"
            )
            }
    )
    @GetMapping(value = "/report")
    public ResponseEntity<Object> downloadRecipe() {
        try {
            Path path = recipeService.createReport();
            if (Files.size(path) == 0) {
                return ResponseEntity.noContent().build();
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .contentLength(Files.size(path))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"recipes.txt\"")
                        .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.toString());
        }
    }
}
