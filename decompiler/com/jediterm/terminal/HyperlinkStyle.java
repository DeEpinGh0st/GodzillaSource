package com.jediterm.terminal;

import com.jediterm.terminal.model.hyperlinks.LinkInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;





public class HyperlinkStyle
  extends TextStyle
{
  @NotNull
  private final LinkInfo myLinkInfo;
  @NotNull
  private final TextStyle myHighlightStyle;
  @Nullable
  private final TextStyle myPrevTextStyle;
  @NotNull
  private final HighlightMode myHighlightMode;
  
  public HyperlinkStyle(@NotNull TextStyle prevTextStyle, @NotNull LinkInfo hyperlinkInfo) {
    this(prevTextStyle.getForeground(), prevTextStyle.getBackground(), hyperlinkInfo, HighlightMode.HOVER, prevTextStyle);
  }




  
  public HyperlinkStyle(@Nullable TerminalColor foreground, @Nullable TerminalColor background, @NotNull LinkInfo hyperlinkInfo, @NotNull HighlightMode mode, @Nullable TextStyle prevTextStyle) {
    this(false, foreground, background, hyperlinkInfo, mode, prevTextStyle);
  }





  
  private HyperlinkStyle(boolean keepColors, @Nullable TerminalColor foreground, @Nullable TerminalColor background, @NotNull LinkInfo hyperlinkInfo, @NotNull HighlightMode mode, @Nullable TextStyle prevTextStyle) {
    super(keepColors ? foreground : null, keepColors ? background : null);
    this


      
      .myHighlightStyle = (new TextStyle.Builder()).setBackground(background).setForeground(foreground).setOption(TextStyle.Option.UNDERLINED, true).build();
    this.myLinkInfo = hyperlinkInfo;
    this.myHighlightMode = mode;
    this.myPrevTextStyle = prevTextStyle;
  }
  
  @Nullable
  public TextStyle getPrevTextStyle() {
    return this.myPrevTextStyle;
  }
  
  @NotNull
  public TextStyle getHighlightStyle() {
    if (this.myHighlightStyle == null) $$$reportNull$$$0(6);  return this.myHighlightStyle;
  }
  
  @NotNull
  public LinkInfo getLinkInfo() {
    if (this.myLinkInfo == null) $$$reportNull$$$0(7);  return this.myLinkInfo;
  }
  
  @NotNull
  public HighlightMode getHighlightMode() {
    if (this.myHighlightMode == null) $$$reportNull$$$0(8);  return this.myHighlightMode;
  }

  
  @NotNull
  public Builder toBuilder() {
    return new Builder(this);
  }
  
  public enum HighlightMode {
    ALWAYS, NEVER, HOVER;
  }

  
  public static class Builder
    extends TextStyle.Builder
  {
    @NotNull
    private LinkInfo myLinkInfo;
    
    @NotNull
    private TextStyle myHighlightStyle;
    @Nullable
    private TextStyle myPrevTextStyle;
    @NotNull
    private HyperlinkStyle.HighlightMode myHighlightMode;
    
    private Builder(@NotNull HyperlinkStyle style) {
      this.myLinkInfo = style.myLinkInfo;
      this.myHighlightStyle = style.myHighlightStyle;
      this.myPrevTextStyle = style.myPrevTextStyle;
      this.myHighlightMode = style.myHighlightMode;
    }
    
    @NotNull
    public HyperlinkStyle build() {
      if (build(false) == null) $$$reportNull$$$0(1);  return build(false);
    }
    
    @NotNull
    public HyperlinkStyle build(boolean keepColors) {
      TerminalColor foreground = this.myHighlightStyle.getForeground();
      TerminalColor background = this.myHighlightStyle.getBackground();
      if (keepColors) {
        TextStyle style = super.build();
        foreground = (style.getForeground() != null) ? style.getForeground() : this.myHighlightStyle.getForeground();
        background = (style.getBackground() != null) ? style.getBackground() : this.myHighlightStyle.getBackground();
      } 
      return new HyperlinkStyle(keepColors, foreground, background, this.myLinkInfo, this.myHighlightMode, this.myPrevTextStyle);
    }
  }
}
