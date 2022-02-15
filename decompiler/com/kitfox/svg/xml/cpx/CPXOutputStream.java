package com.kitfox.svg.xml.cpx;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;





































public class CPXOutputStream
  extends FilterOutputStream
  implements CPXConsts
{
  Deflater deflater = new Deflater(9);

  
  byte[] deflateBuffer;


  
  public CPXOutputStream(OutputStream os) throws IOException {
    super(os);












































    
    this.deflateBuffer = new byte[2048];
    os.write(MAGIC_NUMBER);
  }




  
  public void write(int b) throws IOException {
    byte[] buf = new byte[1];
    buf[0] = (byte)b;
    write(buf, 0, 1);
  }



  
  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }



  
  public void write(byte[] b, int off, int len) throws IOException {
    this.deflater.setInput(b, off, len);
    
    processAllData();
  }










  
  protected void processAllData() throws IOException {
    int numDeflatedBytes;
    while ((numDeflatedBytes = this.deflater.deflate(this.deflateBuffer)) != 0)
    {

      
      this.out.write(this.deflateBuffer, 0, numDeflatedBytes);
    }
  }











  
  public void flush() throws IOException {
    this.out.flush();
  }













  
  public void close() throws IOException {
    this.deflater.finish();
    processAllData();
    
    try {
      flush();
    } catch (IOException iOException) {}
    
    this.out.close();
  }
}
