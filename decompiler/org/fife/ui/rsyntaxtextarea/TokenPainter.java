package org.fife.ui.rsyntaxtextarea;

import java.awt.Graphics2D;
import javax.swing.text.TabExpander;

interface TokenPainter {
  float paint(Token paramToken, Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2, RSyntaxTextArea paramRSyntaxTextArea, TabExpander paramTabExpander);
  
  float paint(Token paramToken, Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2, RSyntaxTextArea paramRSyntaxTextArea, TabExpander paramTabExpander, float paramFloat3);
  
  float paint(Token paramToken, Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2, RSyntaxTextArea paramRSyntaxTextArea, TabExpander paramTabExpander, float paramFloat3, boolean paramBoolean);
  
  float paintSelected(Token paramToken, Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2, RSyntaxTextArea paramRSyntaxTextArea, TabExpander paramTabExpander, boolean paramBoolean);
  
  float paintSelected(Token paramToken, Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2, RSyntaxTextArea paramRSyntaxTextArea, TabExpander paramTabExpander, float paramFloat3, boolean paramBoolean);
}
