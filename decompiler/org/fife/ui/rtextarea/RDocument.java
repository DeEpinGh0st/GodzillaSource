package org.fife.ui.rtextarea;

import javax.swing.text.BadLocationException;
import javax.swing.text.GapContent;
import javax.swing.text.PlainDocument;



















public class RDocument
  extends PlainDocument
{
  public RDocument() {
    super(new RGapContent(null));
  }








  
  public char charAt(int offset) throws BadLocationException {
    return ((RGapContent)getContent()).charAt(offset);
  }

  
  private static class RGapContent
    extends GapContent
  {
    private RGapContent() {}
    
    public char charAt(int offset) throws BadLocationException {
      if (offset < 0 || offset >= length()) {
        throw new BadLocationException("Invalid offset", offset);
      }
      int g0 = getGapStart();
      char[] array = (char[])getArray();
      if (offset < g0) {
        return array[offset];
      }
      return array[getGapEnd() + offset - g0];
    }
  }
}
