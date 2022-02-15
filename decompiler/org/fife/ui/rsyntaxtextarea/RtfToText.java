package org.fife.ui.rsyntaxtextarea;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

























final class RtfToText
{
  private Reader r;
  private StringBuilder sb;
  private StringBuilder controlWord;
  private int blockCount;
  private boolean inControlWord;
  
  private RtfToText(Reader r) {
    this.r = r;
    this.sb = new StringBuilder();
    this.controlWord = new StringBuilder();
    this.blockCount = 0;
    this.inControlWord = false;
  }











  
  private String convert() throws IOException {
    int i = this.r.read();
    if (i != 123) {
      throw new IOException("Invalid RTF file");
    }
    
    while ((i = this.r.read()) != -1) {
      
      char ch = (char)i;
      switch (ch) {
        case '{':
          if (this.inControlWord && this.controlWord.length() == 0) {
            this.sb.append('{');
            this.controlWord.setLength(0);
            this.inControlWord = false;
            continue;
          } 
          this.blockCount++;
          continue;
        
        case '}':
          if (this.inControlWord && this.controlWord.length() == 0) {
            this.sb.append('}');
            this.controlWord.setLength(0);
            this.inControlWord = false;
            continue;
          } 
          this.blockCount--;
          continue;
        
        case '\\':
          if (this.blockCount == 0) {
            if (this.inControlWord) {
              if (this.controlWord.length() == 0) {
                this.sb.append('\\');
                this.controlWord.setLength(0);
                this.inControlWord = false;
                continue;
              } 
              endControlWord();
              this.inControlWord = true;
              
              continue;
            } 
            this.inControlWord = true;
          } 
          continue;
        
        case ' ':
          if (this.blockCount == 0) {
            if (this.inControlWord) {
              endControlWord();
              continue;
            } 
            this.sb.append(' ');
          } 
          continue;
        
        case '\n':
        case '\r':
          if (this.blockCount == 0 && 
            this.inControlWord) {
            endControlWord();
          }
          continue;
      } 

      
      if (this.blockCount == 0) {
        if (this.inControlWord) {
          this.controlWord.append(ch);
          continue;
        } 
        this.sb.append(ch);
      } 
    } 




    
    return this.sb.toString();
  }







  
  private void endControlWord() {
    String word = this.controlWord.toString();
    if ("par".equals(word) || "line".equals(word)) {
      this.sb.append('\n');
    }
    else if ("tab".equals(word)) {
      this.sb.append('\t');
    }
    else if (isUnicodeEscape(word)) {
      this.sb.append((char)Integer.valueOf(word.substring(1)).intValue());
    } 
    this.controlWord.setLength(0);
    this.inControlWord = false;
  }

  
  private static boolean isUnicodeEscape(String controlWord) {
    if (controlWord.startsWith("u") && controlWord.length() > 1) {
      for (int i = 1; i < controlWord.length(); i++) {
        char ch = controlWord.charAt(i);
        if (ch < '0' || ch > '9') {
          return false;
        }
      } 
      return true;
    } 
    return false;
  }









  
  public static String getPlainText(byte[] rtf) throws IOException {
    return getPlainText(new ByteArrayInputStream(rtf));
  }








  
  public static String getPlainText(File file) throws IOException {
    return getPlainText(new BufferedReader(new FileReader(file)));
  }










  
  public static String getPlainText(InputStream in) throws IOException {
    return getPlainText(new InputStreamReader(in, StandardCharsets.US_ASCII));
  }









  
  private static String getPlainText(Reader r) throws IOException {
    try {
      RtfToText converter = new RtfToText(r);
      return converter.convert();
    } finally {
      r.close();
    } 
  }








  
  public static String getPlainText(String rtf) throws IOException {
    return getPlainText(new StringReader(rtf));
  }
}
