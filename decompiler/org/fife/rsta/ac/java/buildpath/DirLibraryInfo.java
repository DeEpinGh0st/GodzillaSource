package org.fife.rsta.ac.java.buildpath;

import java.io.File;
import java.io.IOException;
import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.classreader.ClassFile;
























public class DirLibraryInfo
  extends LibraryInfo
{
  private File dir;
  
  public DirLibraryInfo(File dir) {
    this(dir, (SourceLocation)null);
  }

  
  public DirLibraryInfo(String dir) {
    this(new File(dir));
  }

  
  public DirLibraryInfo(File dir, SourceLocation sourceLoc) {
    setDirectory(dir);
    setSourceLocation(sourceLoc);
  }

  
  public DirLibraryInfo(String dir, SourceLocation sourceLoc) {
    this(new File(dir), sourceLoc);
  }






  
  public void bulkClassFileCreationEnd() {}






  
  public void bulkClassFileCreationStart() {}





  
  public int compareTo(LibraryInfo info) {
    if (info == this) {
      return 0;
    }
    int result = -1;
    if (info instanceof DirLibraryInfo) {
      return this.dir.compareTo(((DirLibraryInfo)info).dir);
    }
    return result;
  }


  
  public ClassFile createClassFile(String entryName) throws IOException {
    return createClassFileBulk(entryName);
  }


  
  public ClassFile createClassFileBulk(String entryName) throws IOException {
    File file = new File(this.dir, entryName);
    if (!file.isFile()) {
      System.err.println("ERROR: Invalid class file: " + file.getAbsolutePath());
      return null;
    } 
    return new ClassFile(file);
  }


  
  public PackageMapNode createPackageMap() {
    PackageMapNode root = new PackageMapNode();
    getPackageMapImpl(this.dir, (String)null, root);
    return root;
  }


  
  public long getLastModified() {
    return this.dir.lastModified();
  }


  
  public String getLocationAsString() {
    return this.dir.getAbsolutePath();
  }









  
  private void getPackageMapImpl(File dir, String pkg, PackageMapNode root) {
    File[] children = dir.listFiles();
    
    for (File child : children) {
      if (child.isFile() && child.getName().endsWith(".class")) {
        if (pkg != null) {

          
          root.add(pkg + "/" + child.getName());
        } else {
          
          root.add(child.getName());
        }
      
      } else if (child.isDirectory()) {
        
        String subpkg = (pkg == null) ? child.getName() : (pkg + "/" + child.getName());
        getPackageMapImpl(child, subpkg, root);
      } 
    } 
  }



  
  public int hashCode() {
    return this.dir.hashCode();
  }






  
  private void setDirectory(File dir) {
    if (dir == null || !dir.isDirectory()) {
      String name = (dir == null) ? "null" : dir.getAbsolutePath();
      throw new IllegalArgumentException("Directory does not exist: " + name);
    } 
    this.dir = dir;
  }








  
  public String toString() {
    return "[DirLibraryInfo: jar=" + this.dir
      .getAbsolutePath() + "; source=" + 
      getSourceLocation() + "]";
  }
}
