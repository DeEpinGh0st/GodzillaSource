package org.fife.ui.rsyntaxtextarea;

import javax.swing.text.Segment;
































public class TokenMap
{
  private int size;
  private TokenMapToken[] tokenMap;
  private boolean ignoreCase;
  private static final int DEFAULT_TOKEN_MAP_SIZE = 52;
  
  public TokenMap() {
    this(52);
  }






  
  public TokenMap(int size) {
    this(size, false);
  }







  
  public TokenMap(boolean ignoreCase) {
    this(52, ignoreCase);
  }








  
  public TokenMap(int size, boolean ignoreCase) {
    this.size = size;
    this.tokenMap = new TokenMapToken[size];
    this.ignoreCase = ignoreCase;
  }







  
  private void addTokenToBucket(int bucket, TokenMapToken token) {
    TokenMapToken old = this.tokenMap[bucket];
    token.nextToken = old;
    this.tokenMap[bucket] = token;
  }











  
  public int get(Segment text, int start, int end) {
    return get(text.array, start, end);
  }












  
  public int get(char[] array1, int start, int end) {
    int length1 = end - start + 1;
    
    int hash = getHashCode(array1, start, length1);
    TokenMapToken token = this.tokenMap[hash];











    
    if (!this.ignoreCase) {

      
      while (token != null) {
        if (token.length == length1) {
          char[] array2 = token.text;
          int offset2 = token.offset;
          int offset1 = start;
          int length = length1;
          while (length-- > 0) {
            if (array1[offset1++] != array2[offset2++]) {
              token = token.nextToken;
            }
          } 
          
          return token.tokenType;
        } 
        token = token.nextToken;

      
      }


    
    }
    else {

      
      while (token != null) {
        if (token.length == length1) {
          char[] array2 = token.text;
          int offset2 = token.offset;
          int offset1 = start;
          int length = length1;
          while (length-- > 0) {
            if (RSyntaxUtilities.toLowerCase(array1[offset1++]) != array2[offset2++])
            {
              token = token.nextToken;
            }
          } 
          
          return token.tokenType;
        } 
        token = token.nextToken;
      } 
    } 


    
    return -1;
  }










  
  private int getHashCode(char[] text, int offset, int length) {
    return (RSyntaxUtilities.toLowerCase(text[offset]) + 
      RSyntaxUtilities.toLowerCase(text[offset + length - 1])) % this.size;
  }








  
  protected boolean isIgnoringCase() {
    return this.ignoreCase;
  }







  
  public void put(String string, int tokenType) {
    if (isIgnoringCase()) {
      put(string.toLowerCase().toCharArray(), tokenType);
    } else {
      
      put(string.toCharArray(), tokenType);
    } 
  }











  
  private void put(char[] string, int tokenType) {
    int hashCode = getHashCode(string, 0, string.length);
    addTokenToBucket(hashCode, new TokenMapToken(string, tokenType));
  }

  
  private static final class TokenMapToken
  {
    private char[] text;
    
    private int offset;
    
    private int length;
    
    private int tokenType;
    
    private TokenMapToken nextToken;

    
    private TokenMapToken(char[] text, int tokenType) {
      this.text = text;
      this.offset = 0;
      this.length = text.length;
      this.tokenType = tokenType;
    }

    
    public String toString() {
      return "[TokenMapToken: " + new String(this.text, this.offset, this.length) + "]";
    }
  }
}
