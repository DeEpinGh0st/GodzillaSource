package com.jediterm.terminal.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;



public class JTextFieldLimit
  extends PlainDocument
{
  private int limit;
  
  public JTextFieldLimit(int limit) {
    this.limit = limit;
  }
  
  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
    if (str == null)
      return; 
    if (getLength() + str.length() <= this.limit)
      super.insertString(offset, str, attr); 
  }
}
