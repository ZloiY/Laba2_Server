package com.company;


import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;

/**
 * Класс для прочтения файла конфигурации сервера.
 */
public class ConfigReader {
    /**
     * Порт на котором будет запущен сервер.
     */
    private int port;
    /**
     * Имя пользователя базы данных.
     */
    private String userName;
    /**
     * Пароль пользователя базы данных.
     */
    private String userPass;

    /**
     * Читает из файла конфигурации порт сервера, имя пользователя базы данных, пароль базы данных.
     */
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

    /**
     * Возвращает порт сервера.
     * @return порт сервера
     */
    public int getPort() { return port;  }

    /**
     * Возвращает имя пользователя базы данных.
     * @return имя пользователя базы данных
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Возвращает пароль пользователя базы данных.
     * @return пароль пользователя базы данных
     */
    public String getUserPass() {
        return userPass;
    }
}
