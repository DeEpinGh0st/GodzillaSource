package org.fife.ui.rsyntaxtextarea;

import java.awt.Rectangle;
import javax.swing.text.BadLocationException;

interface RSTAView {
  int yForLine(Rectangle paramRectangle, int paramInt) throws BadLocationException;
  
  int yForLineContaining(Rectangle paramRectangle, int paramInt) throws BadLocationException;
}
