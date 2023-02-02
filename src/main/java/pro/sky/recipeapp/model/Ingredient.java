package pro.sky.recipeapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    @NotBlank(message = "The title should not be empty")
    private String title;
    @Positive
    private int quantity;
    @NotBlank(message = "Units of measurement must be filled")
    private String unitsOfMeasurement;
}
