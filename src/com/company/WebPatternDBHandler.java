package com.company;

import com.company.thrift.PatternModel;
import com.company.thrift.WebPatternDB;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс имплементирующий интерфейс сгенерированного класса WebPatternDB
 * и реализующего выполнение операций на стороне сервера.
 */

public class WebPatternDBHandler implements WebPatternDB.Iface {
    /**
     * Соединенение с базой данной
     */
    private Connection connection;
    private DriverManager driverManager;
    /**
     * Поток логгера
     */
    private  LogThread log;

    /**
     * Конструктор. Выполняет подключение к базе данных, запускает поток логгера.
     */
    public WebPatternDBHandler(LogThread log){
        try{
            Driver driver = new com.mysql.cj.jdbc.Driver();
            driverManager.registerDriver(driver);
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/web_apps_patterns", "user", "user");
            this.log = log;
            this.log.log("Connect to data base");
        }catch (SQLException e){
            log.log("Cannot connect to SQL server.");
        }
    }

    /**
     * Добавляет новый паттер в базу данных.
     * @param pattern содержит наименование, описание и графическую схему паттерна необходимых для заполнения базы данных
     */
    public void addPattern(PatternModel pattern){
         log.log("New insert request from client.");
         log.log("Adding new pattern " + pattern.getName());
        try{
            Statement statement = connection.createStatement();
            if (pattern.schema!=null) {
                byte[] schemaBytes = new byte[pattern.schema.capacity()];
                for (int i=0; i<pattern.schema.capacity(); i++){
                    schemaBytes[i] = pattern.schema.get(i);
                }
                PreparedStatement preparedStatement = connection.prepareStatement("insert into patterns(pattern_description, pattern_name, pattern_schema) values(?,?,?)");
                preparedStatement.setString(1,pattern.description);
                preparedStatement.setString(2,pattern.name);
                preparedStatement.setBytes(3,schemaBytes);
                preparedStatement.execute();
                preparedStatement.close();
            }else{
                statement.execute("insert into patterns(pattern_description, pattern_name) values('" + pattern.description + "','" + pattern.name + "')");
            }
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Заменяет существующую паттерн в базе данных на новую.
     * @param oldPattern старый паттерн в базе данных, который заменят.
     * @param newPattern новый паттерн в базе данных, которым заменят.
     */
    public void replacePattern(PatternModel oldPattern, PatternModel newPattern){
        log.log("Replace request from client.");
        log.log("Replace this pattern " + oldPattern.getId() + " to this " + newPattern.getId());
        try{
            if (newPattern.getSchema() != null) {
                PreparedStatement statement = connection.prepareStatement("update patterns set pattern_name=?,pattern_description=?,pattern_name=?,pattern_schema=? where pattern_id=?");
                statement.setInt(1, newPattern.id);
                statement.setString(2, newPattern.description);
                statement.setString(3, newPattern.name);
                statement.setBytes(4, newPattern.schema.array());
                statement.setInt(5, oldPattern.id);
                statement.execute();
            }else{
                PreparedStatement statement = connection.prepareStatement("update patterns set pattern_name=?,pattern_description=?,pattern_name=? where pattern_id=?");
                statement.setInt(1, newPattern.id);
                statement.setString(2, newPattern.description);
                statement.setString(3, newPattern.name);
                statement.setInt(4, oldPattern.id);
                statement.execute();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Удаляет паттерн из базы данных.
     * @param delPattern паттрен для удаления из базы данных.
     */
    public void deletePattern(PatternModel delPattern){
        log.log("Delete request from client.");
        log.log("Deleting pattern " + delPattern.getId());
        try{
            Statement statement = connection.createStatement();
            statement.execute("delete from patterns where pattern_id='"+delPattern.id+"'");
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Ищет паттерны согласно данной модели
     * @param pattern модель для поиска паттернов
     * @return список найденных паттернов в базе данных
     * @throws TException выбрасывается при наличии неполадок в RPC
     */
    public List<PatternModel> findPattern(PatternModel pattern) throws TException {
        log.log("Search request from client.");
        try{
            Statement statement = connection.createStatement();
            SQLSearchRequestFabric sqlSearchRequestFabric = new SQLSearchRequestFabric(pattern);
            ResultSet resultSet = statement.executeQuery(sqlSearchRequestFabric.getSearchRequest());
            return createLists(resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ищет паттерн согласно его id в базе данных.
     * @param id id паттерна
     * @return найденный паттерн
     * @throws TException выбрасывается при наличии неполадок в RPC
     */
    public PatternModel findPatternById(int id) throws TException {
        log.log("Search by id request from client.");
        log.log("Searching id: "+id);
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from patterns where pattern_id ='"+id+"'");
            PatternModel pattern = new PatternModel();
            if (resultSet.next()){
                pattern.setId(resultSet.getInt(1));
                pattern.setName(resultSet.getString(2));
                pattern.setDescription(resultSet.getString(3));
                if (resultSet.getBlob(4) != null) {
                    Blob blob = resultSet.getBlob(4);
                    ByteBuffer buffer = ByteBuffer.wrap(blob.getBytes(1,(int)blob.length()));
                    pattern.setSchema(buffer);
                }
            }
            log.log("Find pattern "+pattern.getName());
            return pattern;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Используется в findPattern для формирования списка паттернов из найденных данных в базе данных.
     * @param resultSet данные из базы данных
     * @return список найденных паттернов
     * @throws SQLException выбрасывается при сбоях вработе с базой данных
     */
    private ArrayList<PatternModel> createLists(ResultSet resultSet)throws SQLException{
        ArrayList<PatternModel> returnList = new ArrayList<>();
        while (resultSet.next()) {
            PatternModel pattern = new PatternModel();
            if (resultSet.getBinaryStream(4) != null){
                Blob blob = resultSet.getBlob(4);
                ByteBuffer byteBuffer = ByteBuffer.wrap(blob.getBytes(1,(int)blob.length()));
                pattern.setSchema(byteBuffer.array());
            }
            pattern.setId(resultSet.getInt(1));
            pattern.setName(resultSet.getString(2));
            pattern.setDescription(resultSet.getString(3));
            returnList.add(pattern);
            log.log("Find pattern " + pattern.getName());
        }
        resultSet.close();
        return returnList;
    }

    /**
     * Закрвает соединение с базой данных.
     * Закрывает поток логгера.
     */
    public void closeConnection(){
        try{
            log.log("Closing connection");
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
