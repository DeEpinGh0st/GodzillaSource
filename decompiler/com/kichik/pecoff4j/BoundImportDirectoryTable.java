package com.kichik.pecoff4j;

import java.util.ArrayList;
import java.util.List;









public class BoundImportDirectoryTable
{
  private List<BoundImport> imports = new ArrayList<>();
  
  public void add(BoundImport bi) {
    this.imports.add(bi);
  }
  
  public int size() {
    return this.imports.size();
  }
  
  public BoundImport get(int index) {
    return this.imports.get(index);
  }
}
