package com.kichik.pecoff4j.util;









public class DataObject
{
  private byte[] buffer;
  
  public void set(byte[] buffer) {
    this.buffer = buffer;
  }
  
  public byte[] get() {
    return this.buffer;
  }
}
