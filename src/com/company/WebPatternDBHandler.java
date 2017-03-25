package com.company;

import com.company.thrift.PatternModel;
import com.company.thrift.WebPatternDB;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    /**
     * Поток логгера
     */
    private Logger logger;
    /**
     * Конструктор. Выполняет подключение к базе данных, запускает поток логгера.
     */
    public WebPatternDBHandler(String userName, String userPass){
        try{
            Driver driver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/web_apps_patterns", userName, userPass);
            logger = LogManager.getLogger();
            logger.log(Level.INFO,"User name: " + userName + ", User password : " + userPass);
            logger.log(Level.INFO,"Connect to data base");
        }catch (SQLException e){
            logger.log(Level.INFO,"Cannot connect to SQL server.");
        }
    }

    /**
     * Добавляет новый паттер в базу данных.
     * @param pattern содержит наименование, описание и графическую схему паттерна необходимых для заполнения базы данных
     */
    public void addPattern(PatternModel pattern){
         logger.log(Level.INFO,"New insert request from client.");
         logger.log(Level.INFO,"Adding new pattern " + pattern.getName() + " pattern group " + pattern.getPatternGroup());
        try{
            Statement statement = connection.createStatement();
            if (pattern.schema!=null) {
                byte[] schemaBytes = new byte[pattern.schema.capacity()];
                for (int i=0; i<pattern.schema.capacity(); i++){
                    schemaBytes[i] = pattern.schema.get(i);
                }
                PreparedStatement preparedStatement = connection.prepareStatement("insert into patterns(pattern_description, pattern_name, pattern_schema, pattern_group) values(?,?,?,?)");
                preparedStatement.setString(1,pattern.description);
                preparedStatement.setString(2,pattern.name);
                preparedStatement.setBytes(3,schemaBytes);
                preparedStatement.setInt(4,pattern.PatternGroup);
                preparedStatement.execute();
                preparedStatement.close();
            }else{
                statement.execute("insert into patterns(pattern_description, pattern_name, pattern_group) values('" + pattern.description + "','" + pattern.name + "','"+pattern.PatternGroup+"')");
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
        logger.log(Level.INFO,"Replace request from client.");
        logger.log(Level.INFO,"Replace this pattern " + oldPattern.getId() + " to this " + newPattern.getId());
        try{
            if (newPattern.getSchema() != null) {
                PreparedStatement statement = connection.prepareStatement("update patterns set pattern_name=?,pattern_description=?,pattern_name=?,pattern_schema=?,pattern_group=? where pattern_id=?");
                statement.setInt(1, newPattern.id);
                statement.setString(2, newPattern.description);
                statement.setString(3, newPattern.name);
                statement.setBytes(4, newPattern.schema.array());
                statement.setInt(5,newPattern.PatternGroup);
                statement.setInt(6, oldPattern.id);
                statement.execute();
            }else{
                PreparedStatement statement = connection.prepareStatement("update patterns set pattern_name=?,pattern_description=?,pattern_name=?,pattern_group=? where pattern_id=?");
                statement.setInt(1, newPattern.id);
                statement.setString(2, newPattern.description);
                statement.setString(3, newPattern.name);
                statement.setInt(4,newPattern.PatternGroup);
                statement.setInt(5, oldPattern.id);
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
        logger.log(Level.INFO,"Delete request from client.");
        logger.log(Level.INFO,"Deleting pattern " + delPattern.getId());
        try{
            Statement statement = connection.createStatement();
            statement.execute("delete from patterns where pattern_id='"+delPattern.id+"'");
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Возвращает список таблиц с паттернами из базы данных.
     * @return список таблиц
     * @throws TException выбрасывается при наличии неполадок в RPC
     */
    public List<String> findPatternGroups() throws TException{
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select pattern_group from patterns");
            ArrayList<String> tables = new ArrayList<>();
            while (resultSet.next())
                tables.add(resultSet.getString(1));
            return tables;
        }catch (SQLException sql){
            sql.printStackTrace();
            return null;
        }
    }

    /**
     * Ищет паттерны согласно данной модели
     * @param pattern модель для поиска паттернов
     * @return список найденных паттернов в базе данных
     * @throws TException выбрасывается при наличии неполадок в RPC
     */
    public List<PatternModel> findPattern(PatternModel pattern) throws TException {
        logger.log(Level.INFO,"Search request from client.");
        try{
            Statement statement = connection.createStatement();
            SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(pattern);
            ResultSet resultSet = statement.executeQuery(sqlSearchRequestConfigurator.getSearchRequest());
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
    public PatternModel findPatternById(int id, String patternGroup) throws TException {
        logger.log(Level.INFO,"Search by id request from client.");
        logger.log(Level.INFO,"Searching id: "+id);
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
            logger.log(Level.INFO,"Find pattern "+pattern.getName());
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
            pattern.setPatternGroup(resultSet.getInt(5));
            returnList.add(pattern);
            logger.log(Level.INFO,"Find pattern " + pattern.getName());
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
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
