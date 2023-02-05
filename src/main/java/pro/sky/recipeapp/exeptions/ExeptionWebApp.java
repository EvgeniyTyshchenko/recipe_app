package pro.sky.recipeapp.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ExeptionWebApp extends Exception {

    public ExeptionWebApp(String message) {
        super(message);
    }
}
