package com.kichik.pecoff4j.io;










public class DataEntry
{
  public boolean isSection;
  public int index;
  public int pointer;
  public boolean isDebugRawData;
  public int baseAddress;
  
  public DataEntry() {}
  
  public DataEntry(int index, int pointer) {
    this.index = index;
    this.pointer = pointer;
  }
}
