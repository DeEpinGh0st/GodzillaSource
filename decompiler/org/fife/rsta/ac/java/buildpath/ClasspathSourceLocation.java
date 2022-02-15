package org.fife.rsta.ac.java.buildpath;

import java.io.InputStream;
import java.io.InputStreamReader;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;



































public class ClasspathSourceLocation
  implements SourceLocation
{
  public CompilationUnit getCompilationUnit(ClassFile cf) {
    CompilationUnit cu = null;
    
    String res = cf.getClassName(true).replace('.', '/') + ".java";
    InputStream in = getClass().getClassLoader().getResourceAsStream(res);
    if (in != null) {
      Scanner s = new Scanner(new InputStreamReader(in));
      cu = (new ASTFactory()).getCompilationUnit(res, s);
    } 
    
    return cu;
  }






  
  public String getLocationAsString() {
    return null;
  }
}
