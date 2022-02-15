package org.fife.rsta.ac.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.fife.rsta.ac.java.buildpath.JarLibraryInfo;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.Util;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;































public class JarManager
{
  private List<JarReader> classFileSources;
  private static boolean checkModified;
  
  public JarManager() {
    this.classFileSources = new ArrayList<>();
    setCheckModifiedDatestamps(true);
  }





















  
  public void addCompletions(CompletionProvider p, String text, Set<Completion> addTo) {
    if (text.length() == 0) {
      return;
    }

    
    if (text.indexOf('.') > -1) {
      String[] pkgNames = Util.splitOnChar(text, 46);
      for (int i = 0; i < this.classFileSources.size(); i++) {
        JarReader jar = this.classFileSources.get(i);
        jar.addCompletions(p, pkgNames, addTo);

      
      }

    
    }
    else {

      
      String lowerCaseText = text.toLowerCase();
      for (int i = 0; i < this.classFileSources.size(); i++) {
        JarReader jar = this.classFileSources.get(i);
        
        List<ClassFile> classFiles = jar.getClassesWithNamesStartingWith(lowerCaseText);
        if (classFiles != null) {
          for (ClassFile cf : classFiles) {
            if (Util.isPublic(cf.getAccessFlags())) {
              addTo.add(new ClassCompletion(p, cf));
            }
          } 
        }
      } 
    } 
  }















  
  public boolean addClassFileSource(File jarFile) throws IOException {
    if (jarFile == null) {
      throw new IllegalArgumentException("jarFile cannot be null");
    }
    return addClassFileSource((LibraryInfo)new JarLibraryInfo(jarFile));
  }


















  
  public boolean addClassFileSource(LibraryInfo info) throws IOException {
    if (info == null) {
      throw new IllegalArgumentException("info cannot be null");
    }

    
    for (int i = 0; i < this.classFileSources.size(); i++) {
      JarReader jar = this.classFileSources.get(i);
      LibraryInfo info2 = jar.getLibraryInfo();
      if (info2.equals(info)) {
        
        SourceLocation source = info.getSourceLocation();
        SourceLocation source2 = info2.getSourceLocation();
        if ((source == null && source2 != null) || (source != null && 
          !source.equals(source2))) {
          this.classFileSources.set(i, new JarReader((LibraryInfo)info.clone()));
          return true;
        } 
        return false;
      } 
    } 

    
    this.classFileSources.add(new JarReader(info));
    return true;
  }










  
  public void addCurrentJreClassFileSource() throws IOException {
    addClassFileSource(LibraryInfo.getMainJreJarInfo());
  }









  
  public void clearClassFileSources() {
    this.classFileSources.clear();
  }
















  
  public static boolean getCheckModifiedDatestamps() {
    return checkModified;
  }


  
  public ClassFile getClassEntry(String className) {
    String[] items = Util.splitOnChar(className, 46);
    
    for (int i = 0; i < this.classFileSources.size(); i++) {
      JarReader jar = this.classFileSources.get(i);
      ClassFile cf = jar.getClassEntry(items);
      if (cf != null) {
        return cf;
      }
    } 
    
    return null;
  }














  
  public List<ClassFile> getClassesWithUnqualifiedName(String name, List<ImportDeclaration> importDeclarations) {
    List<ClassFile> result = null;

    
    for (ImportDeclaration idec : importDeclarations) {

      
      if (!idec.isStatic()) {

        
        if (idec.isWildcard()) {
          
          String str = idec.getName();
          str = str.substring(0, str.indexOf('*'));
          str = str + name;
          ClassFile classFile = getClassEntry(str);
          if (classFile != null) {
            if (result == null) {
              result = new ArrayList<>(1);
            }
            result.add(classFile);
          } 

          
          continue;
        } 
        
        String name2 = idec.getName();
        String unqualifiedName2 = name2.substring(name2.lastIndexOf('.') + 1);
        if (unqualifiedName2.equals(name)) {
          ClassFile classFile = getClassEntry(name2);
          if (classFile != null) {
            if (result == null) {
              result = new ArrayList<>(1);
            }
            result.add(classFile);
            continue;
          } 
          System.err.println("ERROR: Class not found! - " + name2);
        } 
      } 
    } 





    
    String qualified = "java.lang." + name;
    ClassFile entry = getClassEntry(qualified);
    if (entry != null) {
      if (result == null) {
        result = new ArrayList<>(1);
      }
      result.add(entry);
    } 
    
    return result;
  }








  
  public List<ClassFile> getClassesInPackage(String pkgName, boolean inPkg) {
    List<ClassFile> list = new ArrayList<>();
    String[] pkgs = Util.splitOnChar(pkgName, 46);
    
    for (int i = 0; i < this.classFileSources.size(); i++) {
      JarReader jar = this.classFileSources.get(i);
      jar.getClassesInPackage(list, pkgs, inPkg);
    } 
    
    return list;
  }













  
  public List<LibraryInfo> getClassFileSources() {
    List<LibraryInfo> jarList = new ArrayList<>(this.classFileSources.size());
    for (JarReader reader : this.classFileSources) {
      jarList.add(reader.getLibraryInfo());
    }
    return jarList;
  }

  
  public SourceLocation getSourceLocForClass(String className) {
    SourceLocation sourceLoc = null;
    for (int i = 0; i < this.classFileSources.size(); i++) {
      JarReader jar = this.classFileSources.get(i);
      if (jar.containsClass(className)) {
        sourceLoc = jar.getLibraryInfo().getSourceLocation();
        break;
      } 
    } 
    return sourceLoc;
  }












  
  public boolean removeClassFileSource(File jar) {
    return removeClassFileSource((LibraryInfo)new JarLibraryInfo(jar));
  }











  
  public boolean removeClassFileSource(LibraryInfo toRemove) {
    for (Iterator<JarReader> i = this.classFileSources.iterator(); i.hasNext(); ) {
      JarReader reader = i.next();
      LibraryInfo info = reader.getLibraryInfo();
      if (info.equals(toRemove)) {
        i.remove();
        return true;
      } 
    } 
    return false;
  }
















  
  public static void setCheckModifiedDatestamps(boolean check) {
    checkModified = check;
  }
}
