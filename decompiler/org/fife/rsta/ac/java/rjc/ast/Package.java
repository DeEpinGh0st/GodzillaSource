package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lexer.Offset;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;











public class Package
  extends AbstractASTNode
{
  public Package(Scanner s, int offs, String pkg) {
    super(pkg, s.createOffset(offs), s.createOffset(offs + pkg.length()));
  }
}
