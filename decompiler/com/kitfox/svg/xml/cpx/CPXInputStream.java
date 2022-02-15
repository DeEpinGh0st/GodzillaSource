package com.kitfox.svg.xml.cpx;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;















































public class CPXInputStream
  extends FilterInputStream
  implements CPXConsts
{
  SecureRandom sec = new SecureRandom();
  
  Inflater inflater = new Inflater();

  
  int xlateMode;
  
  byte[] head = new byte[4];
  int headSize = 0;
  int headPtr = 0;
  
  boolean reachedEOF = false;
  byte[] inBuffer = new byte[2048];
  byte[] decryptBuffer = new byte[2048];





  
  public CPXInputStream(InputStream in) throws IOException {
    super(in);

    
    for (int i = 0; i < 4; i++) {
      
      int val = in.read();
      this.head[i] = (byte)val;
      if (val == -1 || this.head[i] != MAGIC_NUMBER[i]) {
        
        this.headSize = i + 1;
        this.xlateMode = 0;
        
        return;
      } 
    } 
    this.xlateMode = 1;
  }



  
  public boolean markSupported() {
    return false;
  }









  
  public void close() throws IOException {
    this.reachedEOF = true;
    this.in.close();
  }



















  
  public int read() throws IOException {
    byte[] b = new byte[1];
    int retVal = read(b, 0, 1);
    if (retVal == -1) return -1; 
    return b[0];
  }






















  
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }



















  
  public int read(byte[] b, int off, int len) throws IOException {
    if (this.reachedEOF) return -1;
    
    if (this.xlateMode == 0) {
      
      int count = 0;
      
      while (this.headPtr < this.headSize && len > 0) {
        
        b[off++] = this.head[this.headPtr++];
        count++;
        len--;
      } 
      
      return (len == 0) ? count : (count + this.in.read(b, off, len));
    } 

    
    if (this.inflater.needsInput() && !decryptChunk()) {
      int numRead;
      this.reachedEOF = true;


      
      try {
        numRead = this.inflater.inflate(b, off, len);
      }
      catch (Exception e) {
        
        Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, (String)null, e);
        return -1;
      } 
      
      if (!this.inflater.finished())
      {
        Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, "Inflation imncomplete");
      }

      
      return (numRead == 0) ? -1 : numRead;
    } 

    
    try {
      return this.inflater.inflate(b, off, len);
    }
    catch (DataFormatException e) {
      DataFormatException dataFormatException1;
      Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, (String)null, dataFormatException1);
      return -1;
    } 
  }








  
  protected boolean decryptChunk() throws IOException {
    while (this.inflater.needsInput()) {
      
      int numInBytes = this.in.read(this.inBuffer);
      if (numInBytes == -1) return false;

      
      this.inflater.setInput(this.inBuffer, 0, numInBytes);
    } 
    
    return true;
  }





  
  public int available() {
    return this.reachedEOF ? 0 : 1;
  }




  
  public long skip(long n) throws IOException {
    int skipSize = (int)n;
    if (skipSize > this.inBuffer.length) skipSize = this.inBuffer.length; 
    return read(this.inBuffer, 0, skipSize);
  }
}
