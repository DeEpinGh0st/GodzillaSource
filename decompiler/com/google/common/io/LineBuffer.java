package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;


























@GwtIncompatible
abstract class LineBuffer
{
  private StringBuilder line = new StringBuilder();





  
  private boolean sawReturn;





  
  protected void add(char[] cbuf, int off, int len) throws IOException {
    int pos = off;
    if (this.sawReturn && len > 0)
    {
      if (finishLine((cbuf[pos] == '\n'))) {
        pos++;
      }
    }
    
    int start = pos;
    for (int end = off + len; pos < end; pos++) {
      switch (cbuf[pos]) {
        case '\r':
          this.line.append(cbuf, start, pos - start);
          this.sawReturn = true;
          if (pos + 1 < end && 
            finishLine((cbuf[pos + 1] == '\n'))) {
            pos++;
          }
          
          start = pos + 1;
          break;
        
        case '\n':
          this.line.append(cbuf, start, pos - start);
          finishLine(true);
          start = pos + 1;
          break;
      } 


    
    } 
    this.line.append(cbuf, start, off + len - start);
  }

  
  @CanIgnoreReturnValue
  private boolean finishLine(boolean sawNewline) throws IOException {
    String separator = this.sawReturn ? (sawNewline ? "\r\n" : "\r") : (sawNewline ? "\n" : "");
    handleLine(this.line.toString(), separator);
    this.line = new StringBuilder();
    this.sawReturn = false;
    return sawNewline;
  }






  
  protected void finish() throws IOException {
    if (this.sawReturn || this.line.length() > 0)
      finishLine(false); 
  }
  
  protected abstract void handleLine(String paramString1, String paramString2) throws IOException;
}
