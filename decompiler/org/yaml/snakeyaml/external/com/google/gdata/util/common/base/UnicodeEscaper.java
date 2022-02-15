package org.yaml.snakeyaml.external.com.google.gdata.util.common.base;

import java.io.IOException;














































































































public abstract class UnicodeEscaper
  implements Escaper
{
  private static final int DEST_PAD = 32;
  
  protected abstract char[] escape(int paramInt);
  
  protected int nextEscapeIndex(CharSequence csq, int start, int end) {
    int index = start;
    while (index < end) {
      int cp = codePointAt(csq, index, end);
      if (cp < 0 || escape(cp) != null) {
        break;
      }
      index += Character.isSupplementaryCodePoint(cp) ? 2 : 1;
    } 
    return index;
  }



























  
  public String escape(String string) {
    int end = string.length();
    int index = nextEscapeIndex(string, 0, end);
    return (index == end) ? string : escapeSlow(string, index);
  }





















  
  protected final String escapeSlow(String s, int index) {
    int end = s.length();

    
    char[] dest = DEST_TL.get();
    int destIndex = 0;
    int unescapedChunkStart = 0;
    
    while (index < end) {
      int cp = codePointAt(s, index, end);
      if (cp < 0) {
        throw new IllegalArgumentException("Trailing high surrogate at end of input");
      }
      char[] escaped = escape(cp);
      if (escaped != null) {
        int i = index - unescapedChunkStart;



        
        int sizeNeeded = destIndex + i + escaped.length;
        if (dest.length < sizeNeeded) {
          int destLength = sizeNeeded + end - index + 32;
          dest = growBuffer(dest, destIndex, destLength);
        } 
        
        if (i > 0) {
          s.getChars(unescapedChunkStart, index, dest, destIndex);
          destIndex += i;
        } 
        if (escaped.length > 0) {
          System.arraycopy(escaped, 0, dest, destIndex, escaped.length);
          destIndex += escaped.length;
        } 
      } 
      unescapedChunkStart = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
      index = nextEscapeIndex(s, unescapedChunkStart, end);
    } 



    
    int charsSkipped = end - unescapedChunkStart;
    if (charsSkipped > 0) {
      int endIndex = destIndex + charsSkipped;
      if (dest.length < endIndex) {
        dest = growBuffer(dest, destIndex, endIndex);
      }
      s.getChars(unescapedChunkStart, end, dest, destIndex);
      destIndex = endIndex;
    } 
    return new String(dest, 0, destIndex);
  }












































  
  public Appendable escape(final Appendable out) {
    assert out != null;
    
    return new Appendable() {
        int pendingHighSurrogate = -1;
        char[] decodedChars = new char[2];
        
        public Appendable append(CharSequence csq) throws IOException {
          return append(csq, 0, csq.length());
        }
        
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
          int index = start;
          if (index < end) {





            
            int unescapedChunkStart = index;
            if (this.pendingHighSurrogate != -1) {


              
              char c = csq.charAt(index++);
              if (!Character.isLowSurrogate(c)) {
                throw new IllegalArgumentException("Expected low surrogate character but got " + c);
              }
              
              char[] escaped = UnicodeEscaper.this.escape(Character.toCodePoint((char)this.pendingHighSurrogate, c));
              
              if (escaped != null) {


                
                outputChars(escaped, escaped.length);
                unescapedChunkStart++;
              
              }
              else {

                
                out.append((char)this.pendingHighSurrogate);
              } 
              this.pendingHighSurrogate = -1;
            } 

            
            while (true) {
              index = UnicodeEscaper.this.nextEscapeIndex(csq, index, end);
              if (index > unescapedChunkStart) {
                out.append(csq, unescapedChunkStart, index);
              }
              if (index == end) {
                break;
              }

              
              int cp = UnicodeEscaper.codePointAt(csq, index, end);
              if (cp < 0) {


                
                this.pendingHighSurrogate = -cp;
                
                break;
              } 
              char[] escaped = UnicodeEscaper.this.escape(cp);
              if (escaped != null) {
                outputChars(escaped, escaped.length);
              
              }
              else {
                
                int len = Character.toChars(cp, this.decodedChars, 0);
                outputChars(this.decodedChars, len);
              } 

              
              index += Character.isSupplementaryCodePoint(cp) ? 2 : 1;
              unescapedChunkStart = index;
            } 
          } 
          return this;
        }
        
        public Appendable append(char c) throws IOException {
          if (this.pendingHighSurrogate != -1) {


            
            if (!Character.isLowSurrogate(c)) {
              throw new IllegalArgumentException("Expected low surrogate character but got '" + c + "' with value " + c);
            }

            
            char[] escaped = UnicodeEscaper.this.escape(Character.toCodePoint((char)this.pendingHighSurrogate, c));
            if (escaped != null) {
              outputChars(escaped, escaped.length);
            } else {
              out.append((char)this.pendingHighSurrogate);
              out.append(c);
            } 
            this.pendingHighSurrogate = -1;
          } else if (Character.isHighSurrogate(c)) {
            
            this.pendingHighSurrogate = c;
          } else {
            if (Character.isLowSurrogate(c)) {
              throw new IllegalArgumentException("Unexpected low surrogate character '" + c + "' with value " + c);
            }

            
            char[] escaped = UnicodeEscaper.this.escape(c);
            if (escaped != null) {
              outputChars(escaped, escaped.length);
            } else {
              out.append(c);
            } 
          } 
          return this;
        }
        
        private void outputChars(char[] chars, int len) throws IOException {
          for (int n = 0; n < len; n++) {
            out.append(chars[n]);
          }
        }
      };
  }






































  
  protected static final int codePointAt(CharSequence seq, int index, int end) {
    if (index < end) {
      char c1 = seq.charAt(index++);
      if (c1 < '?' || c1 > '?')
      {
        return c1; } 
      if (c1 <= '?') {

        
        if (index == end) {
          return -c1;
        }
        
        char c2 = seq.charAt(index);
        if (Character.isLowSurrogate(c2)) {
          return Character.toCodePoint(c1, c2);
        }
        throw new IllegalArgumentException("Expected low surrogate but got char '" + c2 + "' with value " + c2 + " at index " + index);
      } 
      
      throw new IllegalArgumentException("Unexpected low surrogate character '" + c1 + "' with value " + c1 + " at index " + (index - 1));
    } 

    
    throw new IndexOutOfBoundsException("Index exceeds specified range");
  }





  
  private static final char[] growBuffer(char[] dest, int index, int size) {
    char[] copy = new char[size];
    if (index > 0) {
      System.arraycopy(dest, 0, copy, 0, index);
    }
    return copy;
  }





  
  private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>()
    {
      protected char[] initialValue() {
        return new char[1024];
      }
    };
}
