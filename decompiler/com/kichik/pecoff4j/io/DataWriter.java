package com.kichik.pecoff4j.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;








public class DataWriter
  implements IDataWriter
{
  private BufferedOutputStream out;
  private int position;
  
  public DataWriter(File output) throws FileNotFoundException {
    this(new FileOutputStream(output));
  }
  
  public DataWriter(OutputStream out) {
    this.out = new BufferedOutputStream(out);
  }

  
  public void writeByte(int b) throws IOException {
    this.out.write(b);
    this.position++;
  }

  
  public void writeByte(int b, int count) throws IOException {
    for (int i = 0; i < count; i++) {
      this.out.write(b);
    }
    this.position += count;
  }

  
  public void writeBytes(byte[] b) throws IOException {
    this.out.write(b);
    this.position += b.length;
  }

  
  public void writeDoubleWord(int dw) throws IOException {
    this.out.write(dw & 0xFF);
    this.out.write(dw >> 8 & 0xFF);
    this.out.write(dw >> 16 & 0xFF);
    this.out.write(dw >> 24 & 0xFF);
    this.position += 4;
  }

  
  public void writeWord(int w) throws IOException {
    this.out.write(w & 0xFF);
    this.out.write(w >> 8 & 0xFF);
    this.position += 2;
  }

  
  public void writeLong(long l) throws IOException {
    writeDoubleWord((int)l);
    writeDoubleWord((int)(l >> 32L));
  }
  
  public void flush() throws IOException {
    this.out.flush();
  }
  
  public void close() throws IOException {
    this.out.close();
  }

  
  public int getPosition() {
    return this.position;
  }

  
  public void writeUtf(String s, int len) throws IOException {
    byte[] b = s.getBytes();
    int i = 0;
    for (; i < b.length && i < len; i++) {
      this.out.write(b[i]);
    }
    for (; i < len; i++) {
      this.out.write(0);
    }
    this.position += len;
  }

  
  public void writeUtf(String s) throws IOException {
    byte[] b = s.getBytes();
    this.out.write(b);
    this.out.write(0);
    this.position += b.length + 1;
  }
}
