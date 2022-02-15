package org.fife.rsta.ac.java.buildpath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;




























public class ZipSourceLocation
  implements SourceLocation
{
  private File archive;
  
  public ZipSourceLocation(String archive) {
    this(new File(archive));
  }






  
  public ZipSourceLocation(File archive) {
    this.archive = archive;
  }






  
  public CompilationUnit getCompilationUnit(ClassFile cf) throws IOException {
    CompilationUnit cu = null;
    
    try (ZipFile zipFile = new ZipFile(this.archive)) {
      
      String entryName = cf.getClassName(true).replaceAll("\\.", "/");
      entryName = entryName + ".java";
      
      ZipEntry entry = zipFile.getEntry(entryName);
      if (entry == null)
      {
        entry = zipFile.getEntry("src/" + entryName);
      }
      
      if (entry != null) {
        InputStream in = zipFile.getInputStream(entry);
        Scanner s = new Scanner(new InputStreamReader(in));
        cu = (new ASTFactory()).getCompilationUnit(entryName, s);
      } 
    } 


    
    return cu;
  }






  
  public String getLocationAsString() {
    return this.archive.getAbsolutePath();
  }
}
