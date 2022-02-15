package org.fife.ui.autocomplete;

import java.awt.Color;































public class AutoCompletionStyleContext
{
  private Color parameterizedCompletionCursorPositionColor;
  private Color parameterCopyColor;
  private Color parameterOutlineColor;
  
  public AutoCompletionStyleContext() {
    setParameterOutlineColor(Color.gray);
    setParameterCopyColor(new Color(11851775));
    setParameterizedCompletionCursorPositionColor(new Color(46080));
  }








  
  public Color getParameterCopyColor() {
    return this.parameterCopyColor;
  }








  
  public Color getParameterizedCompletionCursorPositionColor() {
    return this.parameterizedCompletionCursorPositionColor;
  }








  
  public Color getParameterOutlineColor() {
    return this.parameterOutlineColor;
  }








  
  public void setParameterCopyColor(Color color) {
    this.parameterCopyColor = color;
  }








  
  public void setParameterizedCompletionCursorPositionColor(Color color) {
    this.parameterizedCompletionCursorPositionColor = color;
  }








  
  public void setParameterOutlineColor(Color color) {
    this.parameterOutlineColor = color;
  }
}
