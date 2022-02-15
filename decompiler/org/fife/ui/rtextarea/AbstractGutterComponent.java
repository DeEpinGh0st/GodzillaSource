package org.fife.ui.rtextarea;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.View;





























abstract class AbstractGutterComponent
  extends JPanel
{
  protected RTextArea textArea;
  protected int currentLineCount;
  
  AbstractGutterComponent(RTextArea textArea) {
    init();
    setTextArea(textArea);
  }












  
  protected static Rectangle getChildViewBounds(View parent, int line, Rectangle editorRect) {
    Shape alloc = parent.getChildAllocation(line, editorRect);
    if (alloc == null)
    {
      
      return new Rectangle();
    }
    return (alloc instanceof Rectangle) ? (Rectangle)alloc : alloc
      .getBounds();
  }






  
  protected Gutter getGutter() {
    Container parent = getParent();
    return (parent instanceof Gutter) ? (Gutter)parent : null;
  }







  
  abstract void handleDocumentEvent(DocumentEvent paramDocumentEvent);







  
  protected void init() {}






  
  abstract void lineHeightsChanged();






  
  public void setTextArea(RTextArea textArea) {
    this.textArea = textArea;
    int lineCount = (textArea == null) ? 0 : textArea.getLineCount();
    if (this.currentLineCount != lineCount) {
      this.currentLineCount = lineCount;
      repaint();
    } 
  }
}
