package com.company;

import com.company.thrift.PatternModel;

import java.nio.ByteBuffer;

/**
 * Класс формирующий поисковый запрос для базы данных SQL
 */
public class SQLSearchRequestConfigurator {
    /**
     * id паттерна в базе данных
     */
    private int id;
    /**
     * Название паттерна в базе данных
     */
    private String name;
    /**
     * Описание паттерна в базе данных
     */
    private String description;
    /**
     * Поисковый запрос к базе данных
     */
    private String searchRequest;
    /**
     * Название таблицы гед находится паттерн
     */
    private String patternGroup;

    /**
     * @param pattern модель для поиска нужного паттерна
     */
    public SQLSearchRequestConfigurator(PatternModel pattern){
        id = pattern.id;
        name = pattern.name;
        description = pattern.description;
        patternGroup = pattern.getPatternGroup();
        searchRequest = createSearchRequest();
    }

    /**
     * Формирует основные параметры для поиска
     * @return готовый поисковый запрос
     */
    private String createSearchRequest(){
        String idSearch = "";
        String nameSearch = "";
        String descriptionSearch = "";
        if (!(id < 1))
            idSearch = "pattern_id ='"+id+"' ";
        if (name != null)
            nameSearch = "pattern_name like '%"+name+"%' ";
        if (description != null)
            descriptionSearch = "pattern_description like '%"+description+"%' ";
        return searchStatementWith3Parametres(idSearch, nameSearch, descriptionSearch);
    }

    /**
     * Формирует поисковый запрос из трёх параметров поиска.
     * @param idSearch первый параметр поиска
     * @param nameSearch второй параметр поиска
     * @param descriptionSearch третий параметр поиска
     * @return готовый поисковый запрос
     */
    private String searchStatementWith3Parametres(String idSearch, String nameSearch, String descriptionSearch){
        if (idSearch.isEmpty() && nameSearch.isEmpty() && descriptionSearch.isEmpty())
            return "select * from "+patternGroup;
        if (idSearch.isEmpty() || nameSearch.isEmpty() || descriptionSearch.isEmpty()){
            if (idSearch.isEmpty() && !nameSearch.isEmpty() && !descriptionSearch.isEmpty())
                return searchStatementWith2Parametres(nameSearch, descriptionSearch);
            else if (nameSearch.isEmpty() && !idSearch.isEmpty() && !descriptionSearch.isEmpty())
                return searchStatementWith2Parametres(idSearch, descriptionSearch);
            else if (descriptionSearch.isEmpty() && !nameSearch.isEmpty() && !idSearch.isEmpty())
                return searchStatementWith2Parametres(idSearch, nameSearch);
            if (idSearch.isEmpty() && nameSearch.isEmpty() && !descriptionSearch.isEmpty())
                return searchStatementWith1Parametr(descriptionSearch);
            else if (idSearch.isEmpty() && descriptionSearch.isEmpty() && !nameSearch.isEmpty())
                return searchStatementWith1Parametr(nameSearch);
            else if (descriptionSearch.isEmpty() && nameSearch.isEmpty() && !idSearch.isEmpty())
                return searchStatementWith1Parametr(idSearch);
        }else return "select * from "+patternGroup+" where "+idSearch+" and "+descriptionSearch+" and "+nameSearch;
        return null;
    }

    /**
     * Формирует поисквый запрос из двух параметров поиска.
     * @param firstParametr первый парметр поиска
     * @param secondParametr второй параметр поиска
     * @return готовый поисковый запрос
     */
    private String searchStatementWith2Parametres(String firstParametr, String secondParametr){
        if (firstParametr.isEmpty() || secondParametr.isEmpty())
            if (firstParametr.isEmpty())
            return searchStatementWith1Parametr(secondParametr);
            else return searchStatementWith1Parametr(secondParametr);
        else return "select * from "+patternGroup+" where "+firstParametr+" and "+secondParametr;
    }

    /**
     * Формирует поисковый запрос из одного параметра
     * @param searchParametr парметр поиска
     * @return готовый поисковый запрос
     */
    private String searchStatementWith1Parametr(String searchParametr){
        return "select * from "+patternGroup+" where "+searchParametr;
    }

    /**
     * Возвращает поисковый запрос.
     * @return поисковый запрос
     */
    public String getSearchRequest(){
        return searchRequest;
    }
}
