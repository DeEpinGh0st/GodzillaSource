package org.fife.rsta.ac.java.buildpath;

import java.io.File;
import java.io.IOException;
import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.classreader.ClassFile;









































































public abstract class LibraryInfo
  implements Comparable<LibraryInfo>, Cloneable
{
  private SourceLocation sourceLoc;
  
  public abstract void bulkClassFileCreationEnd() throws IOException;
  
  public abstract void bulkClassFileCreationStart() throws IOException;
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cnse) {
      throw new IllegalStateException("Doesn't support cloning, but should! - " + 
          getClass().getName());
    } 
  }













  
  public abstract ClassFile createClassFile(String paramString) throws IOException;













  
  public abstract ClassFile createClassFileBulk(String paramString) throws IOException;













  
  public abstract PackageMapNode createPackageMap() throws IOException;













  
  public boolean equals(Object o) {
    return (o instanceof LibraryInfo && 
      compareTo((LibraryInfo)o) == 0);
  }











  
  public static LibraryInfo getJreJarInfo(File jreHome) {
    File sourceZip;
    LibraryInfo info = null;
    
    File mainJar = new File(jreHome, "lib/rt.jar");

    
    if (mainJar.isFile()) {
      sourceZip = new File(jreHome, "src.zip");
      if (!sourceZip.isFile())
      {
        sourceZip = new File(jreHome, "../src.zip");
      }
    }
    else {
      
      mainJar = new File(jreHome, "../Classes/classes.jar");
      
      sourceZip = new File(jreHome, "src.jar");
    } 
    
    if (mainJar.isFile()) {
      info = new JarLibraryInfo(mainJar);
      if (sourceZip.isFile()) {
        info.setSourceLocation(new ZipSourceLocation(sourceZip));
      }
    } else {
      
      System.err.println("[ERROR]: Cannot locate JRE jar in " + jreHome
          .getAbsolutePath());
      mainJar = null;
    } 
    
    return info;
  }











  
  public abstract long getLastModified();











  
  public abstract String getLocationAsString();










  
  public static LibraryInfo getMainJreJarInfo() {
    String javaHome = System.getProperty("java.home");
    return getJreJarInfo(new File(javaHome));
  }







  
  public SourceLocation getSourceLocation() {
    return this.sourceLoc;
  }









  
  public abstract int hashCode();








  
  public void setSourceLocation(SourceLocation sourceLoc) {
    this.sourceLoc = sourceLoc;
  }
}
