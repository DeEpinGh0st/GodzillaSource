package org.fife.rsta.ac.java.buildpath;

import java.io.IOException;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;

public interface SourceLocation {
  CompilationUnit getCompilationUnit(ClassFile paramClassFile) throws IOException;
  
  String getLocationAsString();
}
