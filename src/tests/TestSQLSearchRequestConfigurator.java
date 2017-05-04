import com.company.SQLSearchRequestConfigurator;
import com.company.thrift.PatternGroup;
import com.company.thrift.PatternModel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by ZloiY on 04.05.17.
 */
public class TestSQLSearchRequestConfigurator {
    @Test
    public void TestSearchStatementWith3ParametrsWith3Parametrs(){
        PatternModel patternModel = new PatternModel();
        patternModel.setName("UnitTest");
        patternModel.setDescription("UnitDescription");
        patternModel.setPatternGroup(PatternGroup.MV_PATTERNS.getValue());
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        String groupSearch = "pattern_group ='"+patternModel.getPatternGroup()+"' ";
        String nameSearch = "pattern_name like '%"+patternModel.getName()+"%' ";
        String descriptionSearch = "pattern_description like '%"+patternModel.getDescription()+"%' ";
        String testString = "select * from patterns where "+groupSearch+" and " +nameSearch +" and " + descriptionSearch;
        assertEquals(testString, sqlSearchRequestConfigurator.searchStatementWith3Parametres(groupSearch,descriptionSearch,nameSearch));
    }
    @Test
    public void TestSearchStatementWith3ParametrsWith2ParametrsNameDescription(){
        PatternModel patternModel = new PatternModel();
        patternModel.setName("UnitTest");
        patternModel.setDescription("UnitDescription");
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        String descriptionSearch = "pattern_description like '%"+patternModel.getDescription()+"%'";
        String nameSearch = "pattern_name like '%"+patternModel.getName()+"%' ";
        assertEquals(sqlSearchRequestConfigurator.searchStatementWith2Parametres(nameSearch, descriptionSearch), sqlSearchRequestConfigurator.searchStatementWith3Parametres("",nameSearch, descriptionSearch));
    }
    @Test
    public void TestSearchStatementWith3ParametrsWith2ParametrsNameGroup(){
        PatternModel patternModel = new PatternModel();
        patternModel.setName("UnitTest");
        patternModel.setPatternGroup(PatternGroup.MV_PATTERNS.getValue());
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        String groupSearch = "pattern_group ='"+patternModel.getPatternGroup()+"' ";
        String nameSearch = "pattern_name like '%"+patternModel.getName()+"%' ";
        assertEquals(sqlSearchRequestConfigurator.searchStatementWith2Parametres(groupSearch, nameSearch), sqlSearchRequestConfigurator.searchStatementWith3Parametres(groupSearch,nameSearch, ""));
    }
    @Test
    public void TestSearchStatementWith3ParametrsWith2ParametrsDescriptionGroup(){
        PatternModel patternModel = new PatternModel();
        patternModel.setDescription("UnitDescription");
        patternModel.setPatternGroup(PatternGroup.MV_PATTERNS.getValue());
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        String groupSearch = "pattern_group ='"+patternModel.getPatternGroup()+"' ";
        String descriptionSearch = "pattern_description like '%"+patternModel.getDescription()+"%' ";
        assertEquals(sqlSearchRequestConfigurator.searchStatementWith2Parametres(groupSearch, descriptionSearch), sqlSearchRequestConfigurator.searchStatementWith3Parametres(groupSearch,"", descriptionSearch));
    }
    @Test
    public void TestSearchStatementWith3ParametrsWith1ParametrsName(){
        PatternModel patternModel = new PatternModel();
        patternModel.setName("UnitTest");
        String nameSearch = "pattern_name like '%"+patternModel.getName()+"%' ";
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        assertEquals(sqlSearchRequestConfigurator.searchStatementWith1Parametr(nameSearch), sqlSearchRequestConfigurator.searchStatementWith1Parametr(nameSearch));
    }
    @Test
    public void TestSearchStatementWith3ParametrsWith1ParametrDescription(){
        PatternModel patternModel = new PatternModel();
        patternModel.setDescription("UnitDescription");
        String descriptionSearch = "pattern_description like '%"+patternModel.getDescription()+"%' ";
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        assertEquals(sqlSearchRequestConfigurator.searchStatementWith1Parametr(descriptionSearch), sqlSearchRequestConfigurator.searchStatementWith3Parametres("","",descriptionSearch));
    }
    @Test
    public void TestSearchStatementWith3ParametrsWith1ParametrPatternGroup(){
        PatternModel patternModel = new PatternModel();
        patternModel.setPatternGroup(PatternGroup.MV_PATTERNS.getValue());
        String groupSearch = "pattern_group ='"+patternModel.getPatternGroup()+"' ";
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        assertEquals(sqlSearchRequestConfigurator.searchStatementWith1Parametr(groupSearch), sqlSearchRequestConfigurator.searchStatementWith3Parametres(groupSearch,"",""));
    }
    @Test
    public void TestSearchStatementWith3ParametrsWith0Parametrs(){
        PatternModel patternModel = new PatternModel();
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        String testString = "select * from patterns";
        assertEquals(testString,sqlSearchRequestConfigurator.searchStatementWith3Parametres("","",""));
    }
    @Test
    public void TestSearchStatementWith2ParametrsWith2Parametrs(){
        PatternModel patternModel = new PatternModel();
        patternModel.setName("UnitTest");
        patternModel.setDescription("UnitDescription");
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        String descriptionSearch = "pattern_description like '%"+patternModel.getDescription()+"%'";
        String nameSearch = "pattern_name like '%"+patternModel.getName()+"%' ";
        String testString = "select * from patterns where "+descriptionSearch+" and " + nameSearch;
        assertEquals(testString, sqlSearchRequestConfigurator.searchStatementWith2Parametres(descriptionSearch,nameSearch));
    }
    @Test
    public void TestSearchStatementWith2ParametrsWith1Parametr(){
        PatternModel patternModel = new PatternModel();
        patternModel.setName("UnitTest");
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        String nameSearch = "pattern_name like '%"+patternModel.getName()+"%' ";
        assertEquals(sqlSearchRequestConfigurator.searchStatementWith1Parametr(nameSearch),sqlSearchRequestConfigurator.searchStatementWith2Parametres(nameSearch,""));
    }
    @Test
    public void TestSearchStatementWith1Parametr(){
        PatternModel patternModel = new PatternModel();
        patternModel.setName("UnitTest");
        String nameSearch = "pattern_name like '%"+patternModel.getName()+"%' ";
        String testString = "select * from patterns where "+nameSearch;
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        assertEquals(testString, sqlSearchRequestConfigurator.searchStatementWith1Parametr(nameSearch));
    }
    @Test
    public void TestCreateSearchRequestWith3Parametrs(){
        PatternModel patternModel = new PatternModel();
        patternModel.setName("UnitTest");
        patternModel.setDescription("UnitDescription");
        patternModel.setPatternGroup(PatternGroup.MV_PATTERNS.getValue());
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        String groupSearch = "pattern_group ='"+patternModel.getPatternGroup()+"' ";
        String nameSearch = "pattern_name like '%"+patternModel.getName()+"%' ";
        String descriptionSearch = "pattern_description like '%"+patternModel.getDescription()+"%' ";
        assertEquals(sqlSearchRequestConfigurator.createSearchRequest(), sqlSearchRequestConfigurator.searchStatementWith3Parametres(groupSearch, nameSearch, descriptionSearch));
    }

    @Test
    public  void TestCreateSearchRequestWith2Parametrs(){
        PatternModel patternModel = new PatternModel();
        patternModel.setName("UnitTest");
        patternModel.setDescription("UnitDescription");
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        String nameSearch = "pattern_name like '%"+patternModel.getName()+"%' ";
        String descriptionSearch = "pattern_description like '%"+patternModel.getDescription()+"%' ";
        assertEquals(sqlSearchRequestConfigurator.createSearchRequest(), sqlSearchRequestConfigurator.searchStatementWith3Parametres("", nameSearch, descriptionSearch));
    }

    @Test
    public void TestCreateSearchRequestWith1Parametrs(){
        PatternModel patternModel = new PatternModel();
        patternModel.setName("UnitTest");
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        String nameSearch = "pattern_name like '%"+patternModel.getName()+"%' ";
        assertEquals(sqlSearchRequestConfigurator.createSearchRequest(), sqlSearchRequestConfigurator.searchStatementWith3Parametres("",nameSearch,""));
    }

    @Test
    public void TestCreateSearchRequestWith0Parametrs(){
        PatternModel patternModel = new PatternModel();
        SQLSearchRequestConfigurator sqlSearchRequestConfigurator = new SQLSearchRequestConfigurator(patternModel);
        assertEquals(sqlSearchRequestConfigurator.createSearchRequest(), sqlSearchRequestConfigurator.searchStatementWith3Parametres("","",""));
    }
}
