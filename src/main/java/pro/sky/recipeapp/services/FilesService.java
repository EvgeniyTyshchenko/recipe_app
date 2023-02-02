package pro.sky.recipeapp.services;

import java.io.File;
import java.nio.file.Path;

public interface FilesService {

    boolean saveToFile(String json, String dataFileName);

    String readFromFile(String dataFileName);

    boolean cleanDataFile(String dataFileName);

    File getDataFile(String dataFileName);

    Path createTempFile(String suffix);
}
