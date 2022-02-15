package javassist.compiler;



















public interface TokenId
{
  public static final int ABSTRACT = 300;
  public static final int BOOLEAN = 301;
  public static final int BREAK = 302;
  public static final int BYTE = 303;
  public static final int CASE = 304;
  public static final int CATCH = 305;
  public static final int CHAR = 306;
  public static final int CLASS = 307;
  public static final int CONST = 308;
  public static final int CONTINUE = 309;
  public static final int DEFAULT = 310;
  public static final int DO = 311;
  public static final int DOUBLE = 312;
  public static final int ELSE = 313;
  public static final int EXTENDS = 314;
  public static final int FINAL = 315;
  public static final int FINALLY = 316;
  public static final int FLOAT = 317;
  public static final int FOR = 318;
  public static final int GOTO = 319;
  public static final int IF = 320;
  public static final int IMPLEMENTS = 321;
  public static final int IMPORT = 322;
  public static final int INSTANCEOF = 323;
  public static final int INT = 324;
  public static final int INTERFACE = 325;
  public static final int LONG = 326;
  public static final int NATIVE = 327;
  public static final int NEW = 328;
  public static final int PACKAGE = 329;
  public static final int PRIVATE = 330;
  public static final int PROTECTED = 331;
  public static final int PUBLIC = 332;
  public static final int RETURN = 333;
  public static final int SHORT = 334;
  public static final int STATIC = 335;
  public static final int SUPER = 336;
  public static final int SWITCH = 337;
  public static final int SYNCHRONIZED = 338;
  public static final int THIS = 339;
  public static final int THROW = 340;
  public static final int THROWS = 341;
  public static final int TRANSIENT = 342;
  public static final int TRY = 343;
  public static final int VOID = 344;
  public static final int VOLATILE = 345;
  public static final int WHILE = 346;
  public static final int STRICT = 347;
  public static final int NEQ = 350;
  public static final int MOD_E = 351;
  public static final int AND_E = 352;
  public static final int MUL_E = 353;
  public static final int PLUS_E = 354;
  public static final int MINUS_E = 355;
  public static final int DIV_E = 356;
  public static final int LE = 357;
  public static final int EQ = 358;
  public static final int GE = 359;
  public static final int EXOR_E = 360;
  public static final int OR_E = 361;
  public static final int PLUSPLUS = 362;
  public static final int MINUSMINUS = 363;
  public static final int LSHIFT = 364;
  public static final int LSHIFT_E = 365;
  public static final int RSHIFT = 366;
  public static final int RSHIFT_E = 367;
  public static final int OROR = 368;
  public static final int ANDAND = 369;
  public static final int ARSHIFT = 370;
  public static final int ARSHIFT_E = 371;
  public static final String[] opNames = new String[] { "!=", "%=", "&=", "*=", "+=", "-=", "/=", "<=", "==", ">=", "^=", "|=", "++", "--", "<<", "<<=", ">>", ">>=", "||", "&&", ">>>", ">>>=" };




  
  public static final int[] assignOps = new int[] { 37, 38, 42, 43, 45, 47, 0, 0, 0, 94, 124, 0, 0, 0, 364, 0, 366, 0, 0, 0, 370 };
  public static final int Identifier = 400;
  public static final int CharConstant = 401;
  public static final int IntConstant = 402;
  public static final int LongConstant = 403;
  public static final int FloatConstant = 404;
  public static final int DoubleConstant = 405;
  public static final int StringL = 406;
  public static final int TRUE = 410;
  public static final int FALSE = 411;
  public static final int NULL = 412;
  public static final int CALL = 67;
  public static final int ARRAY = 65;
  public static final int MEMBER = 35;
  public static final int EXPR = 69;
  public static final int LABEL = 76;
  public static final int BLOCK = 66;
  public static final int DECL = 68;
  public static final int BadToken = 500;
}
