package org.fife.rsta.ac.java.buildpath;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.classreader.ClassFile;

















































public class ClasspathLibraryInfo
  extends LibraryInfo
{
  private Map<String, ClassFile> classNameToClassFile;
  
  public ClasspathLibraryInfo(String[] classes) {
    this(Arrays.asList(classes), null);
  }







  
  public ClasspathLibraryInfo(List<String> classes) {
    this(classes, null);
  }









  
  public ClasspathLibraryInfo(List<String> classes, SourceLocation sourceLoc) {
    setSourceLocation(sourceLoc);
    this.classNameToClassFile = new HashMap<>();
    int count = (classes == null) ? 0 : classes.size();
    for (int i = 0; i < count; i++) {

      
      String entryName = classes.get(i);
      entryName = entryName.replace('.', '/') + ".class";
      this.classNameToClassFile.put(entryName, null);
    } 
  }




  
  public void bulkClassFileCreationEnd() {}




  
  public void bulkClassFileCreationStart() {}



  
  public int compareTo(LibraryInfo info) {
    if (info == this) {
      return 0;
    }
    int res = -1;
    
    if (info instanceof ClasspathLibraryInfo) {
      ClasspathLibraryInfo other = (ClasspathLibraryInfo)info;
      
      res = this.classNameToClassFile.size() - other.classNameToClassFile.size();
      if (res == 0) {
        for (String key : this.classNameToClassFile.keySet()) {
          if (!other.classNameToClassFile.containsKey(key)) {
            res = -1;
            
            break;
          } 
        } 
      }
    } 
    return res;
  }



  
  public ClassFile createClassFile(String entryName) throws IOException {
    return createClassFileBulk(entryName);
  }




  
  public ClassFile createClassFileBulk(String entryName) throws IOException {
    ClassFile cf = null;
    if (this.classNameToClassFile.containsKey(entryName)) {
      cf = this.classNameToClassFile.get(entryName);
      if (cf == null) {
        cf = createClassFileImpl(entryName);
        this.classNameToClassFile.put(entryName, cf);
      } 
    } 
    return cf;
  }


  
  private ClassFile createClassFileImpl(String res) throws IOException {
    ClassFile cf = null;
    
    InputStream in = getClass().getClassLoader().getResourceAsStream(res);
    if (in != null) {
      
      try {
        BufferedInputStream bin = new BufferedInputStream(in);
        DataInputStream din = new DataInputStream(bin);
        cf = new ClassFile(din);
      } finally {
        in.close();
      } 
    }
    
    return cf;
  }



  
  public PackageMapNode createPackageMap() {
    PackageMapNode root = new PackageMapNode();
    for (String className : this.classNameToClassFile.keySet()) {
      root.add(className);
    }
    return root;
  }








  
  public long getLastModified() {
    return 0L;
  }


  
  public String getLocationAsString() {
    return null;
  }


  
  public int hashCode() {
    return this.classNameToClassFile.hashCode();
  }
}
