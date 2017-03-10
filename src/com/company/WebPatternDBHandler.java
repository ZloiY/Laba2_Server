package com.company;

import com.company.thrift.InvalidRequest;
import com.company.thrift.PatternModel;
import com.company.thrift.WebPatternDB;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import org.apache.thrift.TException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZloiY on 3/8/2017.
 */
public class WebPatternDBHandler implements WebPatternDB.Iface {

    private Connection connection;
    private DriverManager driverManager;
    public WebPatternDBHandler(){
        try{
            Driver driver = new com.mysql.cj.jdbc.Driver();
            driverManager.registerDriver(driver);
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/web_apps_patterns", "user", "user");
        }catch (SQLException e){
            System.err.println("Cannot connect to SQL server.");
        }
    }
    @Override
    public void ping() {
        System.out.println("ping()");
    }


    public void addPattern(PatternModel pattern){
        try{
            Statement statement = connection.createStatement();
            if (pattern.schema!=null) {
                byte[] schemaBytes = new byte[pattern.schema.capacity()];
                for (int i=0; i<pattern.schema.capacity(); i++){
                    schemaBytes[i] = pattern.schema.get(i);
                }
                PreparedStatement preparedStatement = connection.prepareStatement("insert into patterns(pattern_description, pattern_name, pattern_schema) values(?,?,?)");
                //statement.execute("insert into patterns(pattern_description, pattern_name, pattern_schema) values('" + pattern.description + "','" + pattern.name + "', load_file('" + blob + "'))");
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

    public void replacePattern(PatternModel oldPattern, PatternModel newPattern){
        try{
            PreparedStatement statement = connection.prepareStatement("update patterns set pattern_name=?,pattern_description=?,pattern_name=?,pattern_schema=? where pattern_id=?");
            statement.setInt(1,newPattern.id);
            statement.setString(2, newPattern.description);
            statement.setString(3, newPattern.name);
            statement.setBytes(4, newPattern.schema.array());
            statement.setInt(5,oldPattern.id);
            statement.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deletePattern(PatternModel delPattern){
        try{
            Statement statement = connection.createStatement();
            statement.execute("delete from patterns where pattern_id='"+delPattern.id+"'");
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<PatternModel> findPattern(PatternModel pattern) throws TException {
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(new SQLSearchRequestFabric(pattern).getSearchRequest());
            return createLists(resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PatternModel getLastPattern() throws TException {
        PatternModel lastPattern = new PatternModel();
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from patterns order by pattern_id desc limit 1;");
            if (result.next()) {
                if (result.getBlob(4) != null) {
                    Blob blob = result.getBlob(4);
                    ByteBuffer buffer = ByteBuffer.wrap(blob.getBytes(1,(int)blob.length()));
                    lastPattern.setSchema(buffer);
                }
                lastPattern.setId(result.getInt(1));
                lastPattern.setName(result.getString(2));
                lastPattern.setDescription(result.getString(3));
                return lastPattern;
            }
        }catch(SQLException e){
           e.printStackTrace();
           return null;
        }
        return null;
    }

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
        }
        resultSet.close();
        return returnList;
    }

    public void closeConnection(){
        try{
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
