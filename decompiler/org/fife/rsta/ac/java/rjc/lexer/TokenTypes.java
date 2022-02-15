package org.fife.rsta.ac.java.rjc.lexer;

public interface TokenTypes {
  public static final int KEYWORD = 65536;
  
  public static final int DATA_TYPE = 131072;
  
  public static final int IDENTIFIER = 262144;
  
  public static final int COMMENT = 524288;
  
  public static final int DOC_COMMENT = 1048576;
  
  public static final int WHITESPACE = 2097152;
  
  public static final int LITERAL = 4194304;
  
  public static final int SEPARATOR = 8388608;
  
  public static final int OPERATOR = 16777216;
  
  public static final int ASSIGNMENT_OPERATOR = 33554432;
  
  public static final int ANNOTATION_START = 67108864;
  
  public static final int ELIPSIS = 134217728;
  
  public static final int KEYWORD_ABSTRACT = 65537;
  
  public static final int KEYWORD_ASSERT = 65538;
  
  public static final int KEYWORD_BOOLEAN = 131075;
  
  public static final int KEYWORD_BREAK = 65540;
  
  public static final int KEYWORD_BYTE = 131077;
  
  public static final int KEYWORD_CASE = 65542;
  
  public static final int KEYWORD_CATCH = 65543;
  
  public static final int KEYWORD_CHAR = 131080;
  
  public static final int KEYWORD_CLASS = 65545;
  
  public static final int KEYWORD_CONST = 65546;
  
  public static final int KEYWORD_CONTINUE = 65547;
  
  public static final int KEYWORD_DEFAULT = 65548;
  
  public static final int KEYWORD_DO = 65549;
  
  public static final int KEYWORD_DOUBLE = 131086;
  
  public static final int KEYWORD_ELSE = 65551;
  
  public static final int KEYWORD_ENUM = 65552;
  
  public static final int KEYWORD_EXTENDS = 65553;
  
  public static final int KEYWORD_FINAL = 65554;
  
  public static final int KEYWORD_FINALLY = 65555;
  
  public static final int KEYWORD_FLOAT = 131092;
  
  public static final int KEYWORD_FOR = 65557;
  
  public static final int KEYWORD_GOTO = 65558;
  
  public static final int KEYWORD_IF = 65559;
  
  public static final int KEYWORD_IMPLEMENTS = 65560;
  
  public static final int KEYWORD_IMPORT = 65561;
  
  public static final int KEYWORD_INSTANCEOF = 65562;
  
  public static final int KEYWORD_INT = 131099;
  
  public static final int KEYWORD_INTERFACE = 65564;
  
  public static final int KEYWORD_LONG = 131101;
  
  public static final int KEYWORD_NATIVE = 65566;
  
  public static final int KEYWORD_NEW = 65567;
  
  public static final int KEYWORD_PACKAGE = 65568;
  
  public static final int KEYWORD_PRIVATE = 65569;
  
  public static final int KEYWORD_PROTECTED = 65570;
  
  public static final int KEYWORD_PUBLIC = 65571;
  
  public static final int KEYWORD_RETURN = 65572;
  
  public static final int KEYWORD_SHORT = 131109;
  
  public static final int KEYWORD_STATIC = 65574;
  
  public static final int KEYWORD_STRICTFP = 65575;
  
  public static final int KEYWORD_SUPER = 65576;
  
  public static final int KEYWORD_SWITCH = 65577;
  
  public static final int KEYWORD_SYNCHRONIZED = 65578;
  
  public static final int KEYWORD_THIS = 65579;
  
  public static final int KEYWORD_THROW = 65580;
  
  public static final int KEYWORD_THROWS = 65581;
  
  public static final int KEYWORD_TRANSIENT = 65582;
  
  public static final int KEYWORD_TRY = 65583;
  
  public static final int KEYWORD_VOID = 65584;
  
  public static final int KEYWORD_VOLATILE = 65585;
  
  public static final int KEYWORD_WHILE = 65586;
  
  public static final int LITERAL_INT = 4194305;
  
  public static final int LITERAL_FP = 4194306;
  
  public static final int LITERAL_BOOLEAN = 4194307;
  
  public static final int LITERAL_CHAR = 4194308;
  
  public static final int LITERAL_STRING = 4194309;
  
  public static final int LITERAL_NULL = 4194310;
  
  public static final int SEPARATOR_LPAREN = 8388609;
  
  public static final int SEPARATOR_RPAREN = 8388610;
  
  public static final int SEPARATOR_LBRACE = 8388611;
  
  public static final int SEPARATOR_RBRACE = 8388612;
  
  public static final int SEPARATOR_LBRACKET = 8388613;
  
  public static final int SEPARATOR_RBRACKET = 8388614;
  
  public static final int SEPARATOR_SEMICOLON = 8388615;
  
  public static final int SEPARATOR_COMMA = 8388616;
  
  public static final int SEPARATOR_DOT = 8388617;
  
  public static final int OPERATOR_EQUALS = 33554433;
  
  public static final int OPERATOR_GT = 16777218;
  
  public static final int OPERATOR_LT = 16777219;
  
  public static final int OPERATOR_LOGICAL_NOT = 16777220;
  
  public static final int OPERATOR_BITWISE_NOT = 16777221;
  
  public static final int OPERATOR_QUESTION = 16777222;
  
  public static final int OPERATOR_COLON = 16777223;
  
  public static final int OPERATOR_EQUALS_EQUALS = 16777224;
  
  public static final int OPERATOR_LTE = 16777225;
  
  public static final int OPERATOR_GTE = 16777226;
  
  public static final int OPERATOR_NE = 16777227;
  
  public static final int OPERATOR_LOGICAL_AND = 16777228;
  
  public static final int OPERATOR_LOGICAL_OR = 16777229;
  
  public static final int OPERATOR_INCREMENT = 16777230;
  
  public static final int OPERATOR_DECREMENT = 16777231;
  
  public static final int OPERATOR_PLUS = 16777232;
  
  public static final int OPERATOR_MINUS = 16777233;
  
  public static final int OPERATOR_TIMES = 16777234;
  
  public static final int OPERATOR_DIVIDE = 16777235;
  
  public static final int OPERATOR_BITWISE_AND = 16777236;
  
  public static final int OPERATOR_BITWISE_OR = 16777237;
  
  public static final int OPERATOR_BITWISE_XOR = 16777238;
  
  public static final int OPERATOR_MOD = 16777239;
  
  public static final int OPERATOR_LSHIFT = 16777240;
  
  public static final int OPERATOR_RSHIFT = 16777241;
  
  public static final int OPERATOR_RSHIFT2 = 16777242;
  
  public static final int OPERATOR_PLUS_EQUALS = 33554459;
  
  public static final int OPERATOR_MINUS_EQUALS = 33554460;
  
  public static final int OPERATOR_TIMES_EQUALS = 33554461;
  
  public static final int OPERATOR_DIVIDE_EQUALS = 33554462;
  
  public static final int OPERATOR_BITWISE_AND_EQUALS = 33554463;
  
  public static final int OPERATOR_BITWISE_OR_EQUALS = 33554464;
  
  public static final int OPERATOR_BITWISE_XOR_EQUALS = 33554465;
  
  public static final int OPERATOR_MOD_EQUALS = 33554466;
  
  public static final int OPERATOR_LSHIFT_EQUALS = 33554467;
  
  public static final int OPERATOR_RSHIFT_EQUALS = 33554468;
  
  public static final int OPERATOR_RSHIFT2_EQUALS = 33554469;
}
