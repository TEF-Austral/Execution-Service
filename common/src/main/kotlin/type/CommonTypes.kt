package type

enum class CommonTypes {
    DELIMITERS, // ( ) { } , . ; : ?
    OPERATORS, // + - * / %
    ASSIGNMENT, // =
    COMPARISON, // == != < > <= >=
    LOGICAL_OPERATORS, // && ||
    CONDITIONALS, // if else switch case
    DATA_TYPES, // NUMBER STRING
    BOOLEAN_LITERAL,
    STRING_LITERAL, // "Hello" 'World'
    NUMBER_LITERAL, // 123 45.67
    IDENTIFIER, // variable names, function names
    LET,
    CONST,
    PRINT, // print
    NUMBER {
        override fun toString(): String = super.toString().lowercase()
    },
    STRING {
        override fun toString(): String = super.toString().lowercase()
    },
    BOOLEAN {
        override fun toString(): String = super.toString().lowercase()
    },
    EMPTY,
    READ_INPUT,
    READ_ENV,
}
