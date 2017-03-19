package com.company;


import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import java.io.*;

/**
 * Created by ZloiY on 3/19/2017.
 */
public class ConfigReader {
    private int port;
    private String userName;
    private String userPass;

    public ConfigReader(){
        JsonReader jsonReader;
        try{
            FileInputStream fileInputStream = new FileInputStream(new File("cfg.txt"));
            jsonReader = Json.createReader(fileInputStream);
            JsonObject jsonObject = jsonReader.readObject();
            JsonObject serverCfg = jsonObject.getJsonObject("server");
            port = serverCfg.getInt("port");
            userName = serverCfg.getString("DBUserName");
            userPass = serverCfg.getString("DBUserPass");
            fileInputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPass() {
        return userPass;
    }
}
