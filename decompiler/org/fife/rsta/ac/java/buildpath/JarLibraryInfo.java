package org.fife.rsta.ac.java.buildpath;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.classreader.ClassFile;





















public class JarLibraryInfo
  extends LibraryInfo
{
  private File jarFile;
  private JarFile bulkCreateJar;
  
  public JarLibraryInfo(String jarFile) {
    this(new File(jarFile));
  }

  
  public JarLibraryInfo(File jarFile) {
    this(jarFile, (SourceLocation)null);
  }

  
  public JarLibraryInfo(File jarFile, SourceLocation sourceLoc) {
    setJarFile(jarFile);
    setSourceLocation(sourceLoc);
  }


  
  public void bulkClassFileCreationEnd() {
    try {
      this.bulkCreateJar.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } 
  }


  
  public void bulkClassFileCreationStart() {
    try {
      this.bulkCreateJar = new JarFile(this.jarFile);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } 
  }









  
  public int compareTo(LibraryInfo info) {
    if (info == this) {
      return 0;
    }
    int result = -1;
    if (info instanceof JarLibraryInfo) {
      result = this.jarFile.compareTo(((JarLibraryInfo)info).jarFile);
    }
    return result;
  }


  
  public ClassFile createClassFile(String entryName) throws IOException {
    try (JarFile jar = new JarFile(this.jarFile)) {
      return createClassFileImpl(jar, entryName);
    } 
  }


  
  public ClassFile createClassFileBulk(String entryName) throws IOException {
    return createClassFileImpl(this.bulkCreateJar, entryName);
  }

  
  private static ClassFile createClassFileImpl(JarFile jar, String entryName) throws IOException {
    ClassFile cf;
    JarEntry entry = (JarEntry)jar.getEntry(entryName);
    if (entry == null) {
      System.err.println("ERROR: Invalid entry: " + entryName);
      return null;
    } 
    
    DataInputStream in = new DataInputStream(new BufferedInputStream(jar.getInputStream(entry)));
    
    try {
      cf = new ClassFile(in);
    } finally {
      in.close();
    } 
    return cf;
  }



  
  public PackageMapNode createPackageMap() throws IOException {
    PackageMapNode root = new PackageMapNode();
    
    try (JarFile jar = new JarFile(this.jarFile)) {
      
      Enumeration<JarEntry> e = jar.entries();
      while (e.hasMoreElements()) {
        ZipEntry entry = e.nextElement();
        String entryName = entry.getName();
        if (entryName.endsWith(".class")) {
          root.add(entryName);
        }
      } 
    } 

    
    return root;
  }



  
  public long getLastModified() {
    return this.jarFile.lastModified();
  }


  
  public String getLocationAsString() {
    return this.jarFile.getAbsolutePath();
  }






  
  public File getJarFile() {
    return this.jarFile;
  }


  
  public int hashCode() {
    return this.jarFile.hashCode();
  }






  
  private void setJarFile(File jarFile) {
    if (jarFile == null || !jarFile.exists()) {
      String name = (jarFile == null) ? "null" : jarFile.getAbsolutePath();
      throw new IllegalArgumentException("Jar does not exist: " + name);
    } 
    this.jarFile = jarFile;
  }








  
  public String toString() {
    return "[JarLibraryInfo: jar=" + this.jarFile
      .getAbsolutePath() + "; source=" + 
      getSourceLocation() + "]";
  }
}
