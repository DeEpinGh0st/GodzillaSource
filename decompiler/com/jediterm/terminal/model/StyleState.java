package com.jediterm.terminal.model;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import org.jetbrains.annotations.NotNull;

public class StyleState {
  private TextStyle myCurrentStyle = TextStyle.EMPTY;
  private TextStyle myDefaultStyle = TextStyle.EMPTY;
  
  private TextStyle myMergedStyle = null;



  
  public TextStyle getCurrent() {
    return TextStyle.getCanonicalStyle(getMergedStyle());
  }
  
  @NotNull
  private static TextStyle merge(@NotNull TextStyle style, @NotNull TextStyle defaultStyle) {
    if (style == null) $$$reportNull$$$0(0);  if (defaultStyle == null) $$$reportNull$$$0(1);  TextStyle.Builder builder = style.toBuilder();
    if (style.getBackground() == null && defaultStyle.getBackground() != null) {
      builder.setBackground(defaultStyle.getBackground());
    }
    if (style.getForeground() == null && defaultStyle.getForeground() != null) {
      builder.setForeground(defaultStyle.getForeground());
    }
    if (builder.build() == null) $$$reportNull$$$0(2);  return builder.build();
  }
  
  public void reset() {
    this.myCurrentStyle = this.myDefaultStyle;
    this.myMergedStyle = null;
  }
  
  public void set(StyleState styleState) {
    setCurrent(styleState.getCurrent());
  }
  
  public void setDefaultStyle(TextStyle defaultStyle) {
    this.myDefaultStyle = defaultStyle;
    this.myMergedStyle = null;
  }
  
  public TerminalColor getBackground() {
    return getBackground(null);
  }
  
  public TerminalColor getBackground(TerminalColor color) {
    return (color != null) ? color : this.myDefaultStyle.getBackground();
  }
  
  public TerminalColor getForeground() {
    return getForeground(null);
  }
  
  public TerminalColor getForeground(TerminalColor color) {
    return (color != null) ? color : this.myDefaultStyle.getForeground();
  }
  
  public void setCurrent(TextStyle current) {
    this.myCurrentStyle = current;
    this.myMergedStyle = null;
  }
  
  private TextStyle getMergedStyle() {
    if (this.myMergedStyle == null) {
      this.myMergedStyle = merge(this.myCurrentStyle, this.myDefaultStyle);
    }
    return this.myMergedStyle;
  }
}
