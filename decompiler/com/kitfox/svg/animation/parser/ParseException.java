package com.kitfox.svg.animation.parser;













public class ParseException
  extends Exception
{
  protected static final String EOL = System.getProperty("line.separator", "\n");

  
  public Token currentToken;

  
  public int[][] expectedTokenSequences;
  
  public String[] tokenImage;

  
  public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
    super(_initialise(currentTokenVal, expectedTokenSequencesVal, tokenImageVal));
    this.currentToken = currentTokenVal;
    this.expectedTokenSequences = expectedTokenSequencesVal;
    this.tokenImage = tokenImageVal;
  }







  
  public ParseException() {}






  
  public ParseException(String message) {
    super(message);
  }
































  
  private static String _initialise(Token currentToken, int[][] expectedTokenSequences, String[] tokenImage) {
    StringBuilder expected = new StringBuilder();
    int maxSize = 0;
    for (int i = 0; i < expectedTokenSequences.length; i++) {
      if (maxSize < (expectedTokenSequences[i]).length)
        maxSize = (expectedTokenSequences[i]).length; 
      for (int k = 0; k < (expectedTokenSequences[i]).length; k++) {
        expected.append(tokenImage[expectedTokenSequences[i][k]]).append(' ');
      }
      if (expectedTokenSequences[i][(expectedTokenSequences[i]).length - 1] != 0)
        expected.append("..."); 
      expected.append(EOL).append("    ");
    } 
    
    StringBuilder sb = new StringBuilder();
    sb.append("Encountered \"");
    
    Token tok = currentToken.next;
    for (int j = 0; j < maxSize; j++) {
      String tokenText = tok.image;
      String escapedTokenText = add_escapes(tokenText);
      if (j != 0)
        sb.append(' '); 
      if (tok.kind == 0) {
        sb.append(tokenImage[0]);
        break;
      } 
      sb.append(" " + tokenImage[tok.kind]);
      sb.append(" \"");
      sb.append(escapedTokenText);
      sb.append("\"");
      tok = tok.next;
    } 
    sb.append("\" at line ")
      .append(currentToken.next.beginLine)
      .append(", column ")
      .append(currentToken.next.beginColumn);
    sb.append(".").append(EOL);
    
    if (expectedTokenSequences.length != 0)
    {
      
      sb.append(EOL)
        .append("Was expecting")
        .append((expectedTokenSequences.length == 1) ? ":" : " one of:")
        .append(EOL)
        .append(EOL)
        .append(expected);
    }
    
    return sb.toString();
  }






  
  static String add_escapes(String str) {
    StringBuilder retval = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      switch (ch) {
        
        case '\b':
          retval.append("\\b");
          break;
        case '\t':
          retval.append("\\t");
          break;
        case '\n':
          retval.append("\\n");
          break;
        case '\f':
          retval.append("\\f");
          break;
        case '\r':
          retval.append("\\r");
          break;
        case '"':
          retval.append("\\\"");
          break;
        case '\'':
          retval.append("\\'");
          break;
        case '\\':
          retval.append("\\\\");
          break;
        default:
          if (ch < ' ' || ch > '~') {
            String s = "0000" + Integer.toString(ch, 16);
            retval.append("\\u").append(s.substring(s.length() - 4, s.length())); break;
          } 
          retval.append(ch);
          break;
      } 
    
    } 
    return retval.toString();
  }
}
