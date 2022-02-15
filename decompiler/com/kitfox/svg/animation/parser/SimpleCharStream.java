package com.kitfox.svg.animation.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;


public class SimpleCharStream
  extends AbstractCharStream
{
  protected Reader inputStream;
  
  protected int streamRead(char[] buffer, int offset, int len) throws IOException {
    return this.inputStream.read(buffer, offset, len);
  }

  
  protected void streamClose() throws IOException {
    this.inputStream.close();
  }

  
  protected void fillBuff() throws IOException {
    if (this.maxNextCharInd == this.available)
    {
      if (this.available == this.bufsize) {
        
        if (this.tokenBegin > 2048) {
          
          this.maxNextCharInd = 0;
          this.bufpos = 0;
          this.available = this.tokenBegin;
        
        }
        else if (this.tokenBegin < 0) {
          this.maxNextCharInd = 0;
          this.bufpos = 0;
        } else {
          
          expandBuff(false);
        }
      
      } else if (this.available > this.tokenBegin) {
        this.available = this.bufsize;
      }
      else if (this.tokenBegin - this.available < 2048) {
        expandBuff(true);
      } else {
        this.available = this.tokenBegin;
      } 
    }
    try {
      int i = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd);
      if (i == -1) {
        
        this.inputStream.close();
        throw new IOException();
      } 
      this.maxNextCharInd += i;
      
      return;
    } catch (IOException e) {
      this.bufpos--;
      backup(0);
      if (this.tokenBegin == -1)
        this.tokenBegin = this.bufpos; 
      throw e;
    } 
  }


  
  public char readChar() throws IOException {
    if (this.inBuf > 0) {
      
      this.inBuf--;
      
      if (++this.bufpos == this.bufsize) {
        this.bufpos = 0;
      }
      return this.buffer[this.bufpos];
    } 
    
    this.bufpos++;
    if (this.bufpos >= this.maxNextCharInd) {
      fillBuff();
    }
    char c = this.buffer[this.bufpos];
    
    if (isTrackLineColumn())
      updateLineColumn(c); 
    return c;
  }





  
  public SimpleCharStream(Reader dstream, int startline, int startcolumn, int buffersize) {
    super(startline, startcolumn, buffersize);
    this.inputStream = dstream;
  }




  
  public SimpleCharStream(Reader dstream, int startline, int startcolumn) {
    this(dstream, startline, startcolumn, 4096);
  }


  
  public SimpleCharStream(Reader dstream) {
    this(dstream, 1, 1, 4096);
  }


  
  public void reInit(Reader dstream) {
    reInit(dstream, 1, 1, 4096);
  }




  
  public void reInit(Reader dstream, int startline, int startcolumn) {
    reInit(dstream, startline, startcolumn, 4096);
  }





  
  public void reInit(Reader dstream, int startline, int startcolumn, int buffersize) {
    this.inputStream = dstream;
    reInit(startline, startcolumn, buffersize);
  }






  
  public SimpleCharStream(InputStream dstream, Charset encoding, int startline, int startcolumn, int buffersize) {
    this(new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
  }





  
  public SimpleCharStream(InputStream dstream, Charset encoding, int startline, int startcolumn) {
    this(dstream, encoding, startline, startcolumn, 4096);
  }



  
  public SimpleCharStream(InputStream dstream, Charset encoding) {
    this(dstream, encoding, 1, 1, 4096);
  }



  
  public void reInit(InputStream dstream, Charset encoding) {
    reInit(dstream, encoding, 1, 1, 4096);
  }





  
  public void reInit(InputStream dstream, Charset encoding, int startline, int startcolumn) {
    reInit(dstream, encoding, startline, startcolumn, 4096);
  }






  
  public void reInit(InputStream dstream, Charset encoding, int startline, int startcolumn, int buffersize) {
    reInit(new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
  }
}
