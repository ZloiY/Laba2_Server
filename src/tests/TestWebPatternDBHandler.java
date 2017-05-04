package tests;


import com.company.WebPatternDBHandler;
import com.company.thrift.PatternGroup;
import com.company.thrift.PatternModel;
import com.company.thrift.WebPatternDB;
import junit.framework.TestCase;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by ZloiY on 04.05.17.
 */
public class TestWebPatternDBHandler {
    @Test
    public void TestAddPattern()throws Exception{
        WebPatternDBHandler dbHandler = new WebPatternDBHandler("user", "user");
        dbHandler.getConnection().setAutoCommit(false);
        try{
            PatternModel insertPattern = new PatternModel();
            insertPattern.setId(0);
            insertPattern.setName("unitTest");
            insertPattern.setDescription("unitDescription");
            insertPattern.setPatternGroup(PatternGroup.MV_PATTERNS.getValue());
            dbHandler.addPattern(insertPattern);
            List<PatternModel> testList =dbHandler.findPattern(insertPattern);
            if (testList.size() == 1) {
                assertEquals(insertPattern.getName(), testList.get(0).getName());
                assertEquals(insertPattern.getDescription(), testList.get(0).getDescription());
                assertEquals(insertPattern.getPatternGroup(), testList.get(0).getPatternGroup());
            }
        }finally {
            dbHandler.getConnection().rollback();
            dbHandler.closeConnection();
        }
    }
    @Test
    public void TestReplacePattern()throws Exception{
        WebPatternDBHandler dbHandler = new WebPatternDBHandler("user", "user");
        dbHandler.getConnection().setAutoCommit(false);
        try{
            PatternModel updatePattern = new PatternModel();
            updatePattern.setName("UnitTest");
            updatePattern.setDescription("UnitDescription");
            updatePattern.setPatternGroup(PatternGroup.MV_PATTERNS.getValue());
            dbHandler.addPattern(updatePattern);
            PatternModel oldPattern = dbHandler.findPattern(updatePattern).get(0);
            updatePattern.setId(oldPattern.getId());
            updatePattern.setName("updateUnitTest");
            updatePattern.setDescription("updateUnitDescription");
            updatePattern.setPatternGroup(PatternGroup.STRUCT_PATTERNS.getValue());
            dbHandler.replacePattern(oldPattern, updatePattern);
            PatternModel newUpdatePattern = dbHandler.findPattern(updatePattern).get(0);
            assertEquals(updatePattern.getId(), newUpdatePattern.getId());
            assertEquals(updatePattern.getName(), newUpdatePattern.getName());
            assertEquals(updatePattern.getDescription(), newUpdatePattern.getDescription());
            assertEquals(updatePattern.getPatternGroup(), newUpdatePattern.getPatternGroup());
        }finally {
            dbHandler.getConnection().rollback();
            dbHandler.closeConnection();
        }
    }
    @Test
    public void TestDeletePattern()throws Exception{
        WebPatternDBHandler dbHandler = new WebPatternDBHandler("user", "user");
        dbHandler.getConnection().setAutoCommit(false);
        try {
            PatternModel addPattern = new PatternModel();
            addPattern.setName("UnitTest");
            addPattern.setDescription("UnitDescription");
            addPattern.setPatternGroup(PatternGroup.BEHAVE_PATTERNS.getValue());
            dbHandler.addPattern(addPattern);
            PatternModel delPattern = dbHandler.findPattern(addPattern).get(0);
            dbHandler.deletePattern(delPattern);
            List<PatternModel> emptyList = new ArrayList<>();
            assertEquals(emptyList,dbHandler.findPattern(delPattern));
        }finally {
            dbHandler.getConnection().rollback();
            dbHandler.closeConnection();
        }
    }
    @Test
    public void TestFindPatternsGroups() throws Exception{
        WebPatternDBHandler dbHandler = new WebPatternDBHandler("user", "user");
        dbHandler.getConnection().setAutoCommit(false);
        try{
            Statement statement = dbHandler.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select pattern_group from patterns");
            List<String> testGroups = new ArrayList<>();
            while(resultSet.next())
                testGroups.add(resultSet.getString(1));
            assertEquals(testGroups,dbHandler.findPatternGroups());
        }finally {
            dbHandler.getConnection().rollback();
            dbHandler.closeConnection();
        }
    }
    @Test
    public void TestFindPattern() throws Exception{
        WebPatternDBHandler dbHandler = new WebPatternDBHandler("user", "user");
        dbHandler.getConnection().setAutoCommit(false);
        try{
            PatternModel addPattern1 = new PatternModel();
            addPattern1.setName("UnitTest");
            addPattern1.setDescription("UnitDescription");
            addPattern1.setPatternGroup(PatternGroup.BEHAVE_PATTERNS.getValue());
            PatternModel addPattern2 = new PatternModel();
            addPattern2.setName("UnitTest");
            addPattern2.setDescription("UnitDescription");
            addPattern2.setPatternGroup(PatternGroup.BEHAVE_PATTERNS.getValue());
            dbHandler.addPattern(addPattern1);
            dbHandler.addPattern(addPattern2);
            addPattern1.setId(dbHandler.findPattern(addPattern1).get(0).getId());
            addPattern2.setId(addPattern1.getId()+1);
            List<PatternModel> testList = new ArrayList<>();
            testList.add(addPattern1);
            testList.add(addPattern2);
            assertEquals(testList,dbHandler.findPattern(addPattern1));
        }finally {
            dbHandler.getConnection().rollback();
            dbHandler.closeConnection();
        }
    }
    @Test
    public void TestFindPatternById()throws Exception{
        WebPatternDBHandler dbHandler = new WebPatternDBHandler("user", "user");
        dbHandler.getConnection().setAutoCommit(false);
        try{
            PatternModel addPattern = new PatternModel();
            addPattern.setName("UnitTest");
            addPattern.setDescription("UnitDescription");
            addPattern.setPatternGroup(PatternGroup.MV_PATTERNS.getValue());
            dbHandler.addPattern(addPattern);
            int id = dbHandler.findPattern(addPattern).get(0).getId();
            addPattern.setId(id);
            assertEquals(addPattern, dbHandler.findPatternById(id));
        }finally {
            dbHandler.getConnection().rollback();
            dbHandler.closeConnection();
        }
    }
    @Test
    public void TestCloseConnection()throws Exception{
        WebPatternDBHandler dbHandler = new WebPatternDBHandler("user", "user");
        dbHandler.getConnection().setAutoCommit(false);
        try{
            dbHandler.closeConnection();
            assertEquals(dbHandler.getConnection().isClosed(), true);
        }finally {
            if (!dbHandler.getConnection().isClosed()) {
                dbHandler.getConnection().rollback();
                dbHandler.closeConnection();
            }
        }
    }
    @Test
    public void TestCreateLists()throws Exception{
        WebPatternDBHandler dbHandler = new WebPatternDBHandler("user", "user");
        dbHandler.getConnection().setAutoCommit(false);
        try {
            PatternModel addPattern = new PatternModel();
            addPattern.setName("UnitTest");
            addPattern.setDescription("UnitDescription");
            addPattern.setPatternGroup(PatternGroup.BEHAVE_PATTERNS.getValue());
            dbHandler.addPattern(addPattern);
            dbHandler.addPattern(addPattern);
            dbHandler.addPattern(addPattern);
            int id = dbHandler.findPattern(addPattern).get(0).getId()-1;
            Statement statement = dbHandler.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from patterns where pattern_id > "+"'"+id+"'");
            List<PatternModel> testList = new ArrayList<>();
            while(resultSet.next()){
                PatternModel addedPattern = new PatternModel();
                addedPattern.setId(resultSet.getInt(1));
                addedPattern.setName(resultSet.getString(2));
                addedPattern.setDescription(resultSet.getString(3));
                addedPattern.setPatternGroup(resultSet.getInt(5));
                testList.add(addedPattern);
            }
            resultSet.beforeFirst();
            assertEquals(testList, dbHandler.createLists(resultSet));
        }finally {
            dbHandler.getConnection().rollback();
            dbHandler.closeConnection();
        }
    }
}
