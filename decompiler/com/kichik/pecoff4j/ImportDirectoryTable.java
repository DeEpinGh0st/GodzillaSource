package com.kichik.pecoff4j;

import java.util.ArrayList;









public class ImportDirectoryTable
{
  private ArrayList imports = new ArrayList();
  
  public void add(ImportEntry entry) {
    this.imports.add(entry);
  }
  
  public int size() {
    return this.imports.size();
  }
  
  public ImportEntry getEntry(int index) {
    return this.imports.get(index);
  }
}
