package com.kichik.pecoff4j;

import com.kichik.pecoff4j.util.IntMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;













public class SectionTable
{
  public static final String RESOURCE_TABLE = ".rsrc";
  public static final String EXPORT_TABLE = ".edata";
  public static final String IMPORT_TABLE = ".idata";
  public static final String LOAD_CONFIG_TABLE = ".rdata";
  private List<SectionHeader> headers = new ArrayList<>();
  private IntMap sections = new IntMap();
  private RVAConverter rvaConverter;
  
  public void add(SectionHeader header) {
    this.headers.add(header);
  }
  
  public int getNumberOfSections() {
    return this.headers.size();
  }
  
  public SectionHeader getHeader(int index) {
    return this.headers.get(index);
  }
  
  public SectionData getSection(int index) {
    return (SectionData)this.sections.get(index);
  }
  
  public void put(int index, SectionData data) {
    this.sections.put(index, data);
  }
  
  public RVAConverter getRVAConverter() {
    return this.rvaConverter;
  }
  
  public void setRvaConverter(RVAConverter rvaConverter) {
    this.rvaConverter = rvaConverter;
  }
  
  public int getFirstSectionRawDataPointer() {
    int pointer = 0;
    for (int i = 0; i < this.headers.size(); i++) {
      SectionHeader sh = this.headers.get(i);
      if (sh.getVirtualSize() > 0 && (pointer == 0 || sh
        .getPointerToRawData() < pointer)) {
        pointer = sh.getPointerToRawData();
      }
    } 
    
    return pointer;
  }
  
  public SectionHeader getLastSectionRawPointerSorted() {
    SectionHeader[] headers = getHeadersPointerSorted();
    if (headers == null || headers.length == 0)
      return null; 
    return headers[headers.length - 1];
  }
  
  public SectionHeader[] getHeadersPointerSorted() {
    List<SectionHeader> headers = new ArrayList<>();
    for (int i = 0; i < getNumberOfSections(); i++) {
      headers.add(getHeader(i));
    }
    
    SectionHeader[] sorted = headers.<SectionHeader>toArray(new SectionHeader[0]);
    Arrays.sort(sorted, new Comparator<SectionHeader>()
        {
          public int compare(SectionHeader o1, SectionHeader o2) {
            return o1.getVirtualAddress() - o2.getVirtualAddress();
          }
        });
    
    return sorted;
  }
  
  public SectionHeader findHeader(String name) {
    for (SectionHeader sh : this.headers) {
      if (sh.getName().equals(name)) {
        return sh;
      }
    } 
    return null;
  }
  
  public SectionData findSection(String name) {
    for (int i = 0; i < this.headers.size(); i++) {
      SectionHeader sh = this.headers.get(i);
      if (sh.getName().equals(name)) {
        return (SectionData)this.sections.get(i);
      }
    } 
    return null;
  }
}
