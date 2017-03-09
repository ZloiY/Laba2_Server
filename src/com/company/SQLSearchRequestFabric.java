package com.company;

import com.company.thrift.WorkWithClient;

import java.nio.ByteBuffer;

/**
 * Created by ZloiY on 3/9/2017.
 */
public class SQLSearchRequestFabric {
    private int id;
    private String name;
    private String description;
    private String searchRequest;

    public SQLSearchRequestFabric(WorkWithClient workWithClient){
        id = workWithClient.id;
        name = workWithClient.name;
        description = workWithClient.description;
        searchRequest = createSearchRequest();
    }

    private String createSearchRequest(){
        String idSearch = "";
        String nameSearch = "";
        String descriptionSearch = "";
        if (!(id < 1))
            idSearch = "pattern_id ='"+id+"' ";
        if (name != null)
            nameSearch = "pattern_name ='"+name+"' ";
        if (description != null)
            descriptionSearch = "pattern_description ='"+description+"' ";
        return searchStatementWith3Parametres(idSearch, nameSearch, descriptionSearch);
    }

    private String searchStatementWith3Parametres(String idSearch, String nameSearch, String descriptionSearch){
        if (idSearch.isEmpty() && nameSearch.isEmpty() && descriptionSearch.isEmpty())
            return "select * from patterns";
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
        }else return "select * from patterns where "+idSearch+", "+descriptionSearch+", "+nameSearch;
        return null;
    }

    private String searchStatementWith2Parametres(String firstParametr, String secondParametr){
        if (firstParametr.isEmpty() || secondParametr.isEmpty())
            if (firstParametr.isEmpty())
            return searchStatementWith1Parametr(secondParametr);
            else return searchStatementWith1Parametr(secondParametr);
        else return "select * from patterns where "+firstParametr+", "+secondParametr;
    }

    private String searchStatementWith1Parametr(String searchParametr){
        return "select * from patterns where "+searchParametr;
    }

    public String getSearchRequest(){
        return searchRequest;
    }
}
