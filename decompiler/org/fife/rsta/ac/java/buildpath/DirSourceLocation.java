package org.fife.rsta.ac.java.buildpath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
























public class DirSourceLocation
  implements SourceLocation
{
  private File dir;
  
  public DirSourceLocation(String dir) {
    this(new File(dir));
  }






  
  public DirSourceLocation(File dir) {
    this.dir = dir;
  }






  
  public CompilationUnit getCompilationUnit(ClassFile cf) throws IOException {
    CompilationUnit cu = null;
    
    String entryName = cf.getClassName(true);
    entryName = entryName.replace('.', '/');
    entryName = entryName + ".java";
    
    File file = new File(this.dir, entryName);
    if (!file.isFile())
    {
      file = new File(this.dir, "src/" + entryName);
    }
    
    if (file.isFile()) {
      try (BufferedReader r = new BufferedReader(new FileReader(file))) {
        Scanner s = new Scanner(r);
        cu = (new ASTFactory()).getCompilationUnit(entryName, s);
      } 
    }

    
    return cu;
  }






  
  public String getLocationAsString() {
    return this.dir.getAbsolutePath();
  }
}
