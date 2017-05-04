namespace java patterns
/*
 * Модель паттерна.
 * 1 - id паттерна в базе данных;
 * 2 - номер группы к которой принадлежит паттерн(Порождающий, Структурный, Поведенческий, MV-паттерны);
 * 3 - название паттерна;
 * 4 - описание паттерна;
 * 5 - схема паттерна.
 */
struct PatternModel {
    1: optional i32 id,
    2: optional i32 PatternGroup,
    3: optional string name,
    4: optional string description,
    5: optional binary schema,
}
/*
 * Список групп к которой может принадлежать паттерн.
 */
enum PatternGroup{
    MV_PATTERNS = 1,
    STRUCT_PATTERNS = 2,
    CREAT_PATTERNS = 3,
    BEHAVE_PATTERNS = 4
}
/*
 * Исключение возникающие при ошибках сервера.
 */
exception InvalidRequest{
    1: string whatHappens
}
/*
 * Сервис содержащий методы для взаимодействия с запросами клиента.
 * Реализация методов {@link com.company.WebPatternDBHandler}
 */
service WebPatternDB{

    void addPattern(1:PatternModel newPattern) throws (1:InvalidRequest oups),

    void replacePattern(1:PatternModel oldPattern, 2: PatternModel newPattern) throws (1:InvalidRequest oups),

    void deletePattern(1:PatternModel delPattern) throws (1:InvalidRequest oups),

    list<PatternModel> findPattern (1:PatternModel pattern) throws (1:InvalidRequest oups),

    PatternModel findPatternById (1:i32 id) throws (1:InvalidRequest oups),

    list<string> findPatternGroups() throws (1:InvalidRequest oups)
}