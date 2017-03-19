namespace java patterns

struct PatternModel {
    1: optional i32 id,
    2: optional string patternGroup,
    3: optional string name,
    4: optional string description,
    5: optional binary schema,
}

exception InvalidRequest{
    1: string whatHappens
}

service WebPatternDB{

    void addPattern(1:PatternModel newPattern) throws (1:InvalidRequest oups),

    void replacePattern(1:PatternModel oldPattern, 2: PatternModel newPattern) throws (1:InvalidRequest oups),

    void deletePattern(1:PatternModel delPattern) throws (1:InvalidRequest oups),

    list<PatternModel> findPattern (1:PatternModel pattern) throws (1:InvalidRequest oups),

    PatternModel findPatternById (1:i32 id, 2:string patternGroup) throws (1:InvalidRequest oups)
}