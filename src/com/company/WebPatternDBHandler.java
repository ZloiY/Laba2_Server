package com.company;

import com.company.thrift.InvalidRequest;
import com.company.thrift.Operation;
import com.company.thrift.WebPatternDB;
import com.company.thrift.WorkWithClient;
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
            e.printStackTrace();
        }
    }
    @Override
    public void ping() {
        System.out.println("ping()");
    }

    @Override
    public int workWithRequest(int id, Operation operation, WorkWithClient work1, WorkWithClient work2) throws InvalidRequest {
        System.out.println("Operation: " + operation.toString());
        switch (operation){
            case EDIT: workWithEditRequest(work1, work2); break;
            case INSERT: workWithInsertRequest(work1); break;
            case DELETE: workWithDelRequest(work1); break;
        }
        return 0;
    }

    @Override
    public void zip() {
        System.out.println("zip()");
    }

    private int workWithInsertRequest(WorkWithClient work){
        try{
            Blob blob = connection.createBlob();
            blob.setBytes(1,work.schema.array());
            Statement statement = connection.createStatement();
            statement.execute("insert into patterns(pattern_id, pattern_description, pattern_name, pattern_schema) values('"+work.id+"','"+work.description+"','"+work.name+"','"+blob+"');");
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private int workWithEditRequest(WorkWithClient work1, WorkWithClient work2){
        try{
            Blob blob2 = connection.createBlob();
            blob2.setBytes(1,work2.schema.array());
            Statement statement = connection.createStatement();
            statement.execute("update patterns set pattern_id='"+work2.id+"',pattern_description='"+work2.description+"',pattern_name='"+work2.name+"',pattern_schema='"+blob2+"' where pattern_id='"+work1.id+"'");
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private int workWithDelRequest(WorkWithClient work){
        try{
            Statement statement = connection.createStatement();
            statement.execute("delete from patterns where pattern_id='"+work.id+"'");
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public List<WorkWithClient> workWithSearchRequest(int id, WorkWithClient work) throws TException {
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(new SQLSearchRequestFabric(work).getSearchRequest());
            return createLists(resultSet);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<WorkWithClient> createLists(ResultSet resultSet)throws SQLException{
        ArrayList<WorkWithClient> returnList = new ArrayList<>();
        while (resultSet.next()) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[10]);
            InputStream inputStream = resultSet.getBlob(4).getBinaryStream();
            try {
                byteBuffer.clear();
                byteBuffer = ByteBuffer.allocate(inputStream.read());
            }catch (IOException e){
                e.printStackTrace();
            }
            WorkWithClient withClient = new WorkWithClient();
            withClient.id = resultSet.getInt(1);
            withClient.name = resultSet.getString(2);
            withClient.description = resultSet.getString(3);
            withClient.schema = byteBuffer;
            returnList.add(withClient);
        }
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
