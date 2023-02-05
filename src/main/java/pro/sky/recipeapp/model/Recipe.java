package pro.sky.recipeapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    @NotBlank(message = "The title should not be empty")
    private String title;
    @Positive
    private int time;
    private List<Ingredient> ingredients;
    private List<String> cookingSteps;
}
