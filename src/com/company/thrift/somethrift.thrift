namespace java patterns

enum Operation{
    INSERT = 1,
    EDIT = 2,
    DELETE = 3
}

struct WorkWithClient {
    1: i32 id =1,
    2: string name,
    3: string description,
    4: binary schema,
}

exception InvalidRequest{
    1: i32 whatO,
    2: string whatHappens
}

service WebPatternDB{
    void ping(),

    i32 workWithRequest(1:i32 id, 2:Operation op, 3:WorkWithClient work1, 4:WorkWithClient work2) throws (1: InvalidRequest trouble),

    oneway void zip()
}