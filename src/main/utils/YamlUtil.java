package main.utils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

public class YamlUtil {

    public static Object getYaml(String name){
        File file = new File("config.yaml");
        try {
            LinkedHashMap<String,Object> yamlPro;
            Yaml yaml = new Yaml();
            InputStream is =YamlUtil.class.getResourceAsStream("/main/config.yaml");
            FileUtil.inputStreamToFile(is,file);
            yamlPro = (LinkedHashMap) yaml.load(new FileInputStream(file));
            return yamlPro.get(name);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            file.delete();
        }
        return null;
    }

}
