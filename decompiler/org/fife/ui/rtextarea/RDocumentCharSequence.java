package org.fife.ui.rtextarea;

import javax.swing.text.BadLocationException;


























class RDocumentCharSequence
  implements CharSequence
{
  private RDocument doc;
  private int start;
  private int end;
  
  RDocumentCharSequence(RDocument doc, int start) {
    this(doc, start, doc.getLength());
  }








  
  RDocumentCharSequence(RDocument doc, int start, int end) {
    this.doc = doc;
    this.start = start;
    this.end = end;
  }


  
  public char charAt(int index) {
    if (index < 0 || index >= length()) {
      throw new IndexOutOfBoundsException("Index " + index + " is not in range [0-" + 
          length() + ")");
    }
    try {
      return this.doc.charAt(this.start + index);
    } catch (BadLocationException ble) {
      throw new IndexOutOfBoundsException(ble.toString());
    } 
  }


  
  public int length() {
    return this.end - this.start;
  }


  
  public CharSequence subSequence(int start, int end) {
    if (start < 0) {
      throw new IndexOutOfBoundsException("start must be >= 0 (" + start + ")");
    }
    
    if (end < 0) {
      throw new IndexOutOfBoundsException("end must be >= 0 (" + end + ")");
    }
    
    if (end > length()) {
      throw new IndexOutOfBoundsException("end must be <= " + 
          length() + " (" + end + ")");
    }
    if (start > end) {
      throw new IndexOutOfBoundsException("start (" + start + ") cannot be > end (" + end + ")");
    }
    
    int newStart = this.start + start;
    int newEnd = this.start + end;
    return new RDocumentCharSequence(this.doc, newStart, newEnd);
  }


  
  public String toString() {
    try {
      return this.doc.getText(this.start, length());
    } catch (BadLocationException ble) {
      ble.printStackTrace();
      return "";
    } 
  }
}
