package tests;

import com.company.ConfigReader;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by ZloiY on 04.05.17.
 */
public class TestConfigReader {
    private static String path = "src\\test_res\\test_config.cfg";
    @Test
    public void TestGetPort()throws Exception{
        ConfigReader configReader = new ConfigReader(new File(path));
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        try {
            JsonReader jsonReader = Json.createReader(fileInputStream);
            JsonObject jsonObject = jsonReader.readObject();
            JsonObject serverCfg = jsonObject.getJsonObject("server");
            int port = serverCfg.getInt("port");
            assertEquals(port, configReader.getPort());
        }finally {
            fileInputStream.close();
        }
    }
    @Test
    public void TestGetUserName()throws Exception{
        ConfigReader configReader = new ConfigReader(new File(path));
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        try{
            JsonReader jsonReader = Json.createReader(fileInputStream);
            JsonObject jsonObject = jsonReader.readObject();
            JsonObject serverCfg = jsonObject.getJsonObject("server");
            String userName = serverCfg.getString("DBUserName");
            assertEquals(userName, configReader.getUserName());
        }finally {
            fileInputStream.close();
        }
    }
    @Test
    public void TestGetUserPass()throws Exception{
        ConfigReader configReader = new ConfigReader(new File(path));
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        try{
            JsonReader jsonReader = Json.createReader(fileInputStream);
            JsonObject jsonObject = jsonReader.readObject();
            JsonObject serverCfg = jsonObject.getJsonObject("server");
            String userName = serverCfg.getString("DBUserPass");
            assertEquals(userName, configReader.getUserPass());
        }finally {
            fileInputStream.close();
        }
    }
}
