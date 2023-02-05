package pro.sky.recipeapp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Информация", description = "Get запросы: 1) Активность приложения; 2) Данные")
public class InfoController {

    @Operation(
            summary = "Активность приложения",
            description = "Получение информации о запуске приложения")
    @GetMapping
    public String startApp() {
        return "Приложение запущено";
    }

    @Operation(
            summary = "Данные",
            description = "Получение краткой информации о разработчике и возможностях проектка")
    @GetMapping("/info")
    public String getInfo() {
        return """
                Автор: Тыщенко Евгений;
                Название проекта: Приложение рецептов;
                Дата создания проекта: 30.12.2022 г;
                
                В проекте реализована возможность отдельной работы как с рецептами, так и с ингредиентами.               
                При добавлении рецепта, автоматически добавляются ингредиенты.
                + имеется функционал по редактированию, удалению, получению списков рецептов / ингредиентов;
                + возможность получения рецепта / ингредиента по ID;
                + поиск рецепта по ID ингредиента;
                + скачать / загрузить список рецептов или ингредиентов.            
                """;
    }
}