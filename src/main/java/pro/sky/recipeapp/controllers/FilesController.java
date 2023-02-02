package pro.sky.recipeapp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.recipeapp.services.FilesService;

import java.io.*;

@RestController
@RequestMapping("/files")
@Tag(name = "Работа с файлами",
        description = "Реализована возможность скачивать и загружать рецепты / ингредиенты")
public class FilesController {

private final FilesService filesService;

    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @Operation(summary = "Скачать файл",
            description = """
                    Необходимо вводить dataFileName, в зависимости от того, какой файл Вам нужен.
                    Возможны два варианта:
                    1) recipes
                    2) ingredients""")
    @GetMapping("/export/{dataFileName}")
    public ResponseEntity<InputStreamResource> downloadDataFile(@PathVariable String dataFileName) throws FileNotFoundException {
        File file = filesService.getDataFile(dataFileName);
        if(file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + dataFileName + ".json")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "Загрузить файл",
            description = """
                    Необходимо вводить dataFileName, в зависимости от того, какой файл Вам нужно загрузить.
                    Возможны два варианта:
                    1) recipes
                    2) ingredients""")
    @PostMapping(value = "/import/{dataFileName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadDataFile(@PathVariable String dataFileName, @RequestParam MultipartFile file) {
        filesService.cleanDataFile(dataFileName);
        File dataFile = filesService.getDataFile(dataFileName);
        try(FileOutputStream fos = new FileOutputStream(dataFile)) {
            IOUtils.copy(file.getInputStream(), fos);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
