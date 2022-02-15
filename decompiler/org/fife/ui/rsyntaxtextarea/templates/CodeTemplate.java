package org.fife.ui.rsyntaxtextarea.templates;

import java.io.Serializable;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public interface CodeTemplate extends Cloneable, Comparable<CodeTemplate>, Serializable {
  Object clone();
  
  String getID();
  
  void invoke(RSyntaxTextArea paramRSyntaxTextArea) throws BadLocationException;
}
