package com.company;

import javafx.scene.image.Image;

/**
 * Created by ZloiY on 3/8/2017.
 */
public class DataBaseModel {
    private int patternID;
    private String patternName;
    private String patternDescription;
    private Image patternSchema;

    public DataBaseModel(int id, String name, String description, Image schema){
        patternID = id;
        patternName = name;
        patternDescription = description;
        patternSchema = schema;
    }

    public int getPatternID() {
        return patternID;
    }

    public String getPatternName() {
        return patternName;
    }

    public String getPatternDescription() {
        return patternDescription;
    }

    public Image getPatternSchema() {
        return patternSchema;
    }
}
