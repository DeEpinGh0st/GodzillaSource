package com.jediterm.terminal;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.Objects;
import java.util.WeakHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextStyle
{
  private static final EnumSet<Option> NO_OPTIONS = EnumSet.noneOf(Option.class);
  
  public static final TextStyle EMPTY = new TextStyle();
  
  private static final WeakHashMap<TextStyle, WeakReference<TextStyle>> styles = new WeakHashMap<>();
  
  private final TerminalColor myForeground;
  private final TerminalColor myBackground;
  private final EnumSet<Option> myOptions;
  
  public TextStyle() {
    this(null, null, NO_OPTIONS);
  }
  
  public TextStyle(@Nullable TerminalColor foreground, @Nullable TerminalColor background) {
    this(foreground, background, NO_OPTIONS);
  }
  
  public TextStyle(@Nullable TerminalColor foreground, @Nullable TerminalColor background, @NotNull EnumSet<Option> options) {
    this.myForeground = foreground;
    this.myBackground = background;
    this.myOptions = options.clone();
  }
  
  @NotNull
  public static TextStyle getCanonicalStyle(TextStyle currentStyle) {
    if (currentStyle instanceof HyperlinkStyle) {
      if (currentStyle == null) $$$reportNull$$$0(1);  return currentStyle;
    } 
    WeakReference<TextStyle> canonRef = styles.get(currentStyle);
    if (canonRef != null) {
      TextStyle canonStyle = canonRef.get();
      if (canonStyle != null) {
        if (canonStyle == null) $$$reportNull$$$0(2);  return canonStyle;
      } 
    } 
    styles.put(currentStyle, new WeakReference<>(currentStyle));
    if (currentStyle == null) $$$reportNull$$$0(3);  return currentStyle;
  }
  
  @Nullable
  public TerminalColor getForeground() {
    return this.myForeground;
  }
  
  @Nullable
  public TerminalColor getBackground() {
    return this.myBackground;
  }
  
  public TextStyle createEmptyWithColors() {
    return new TextStyle(this.myForeground, this.myBackground);
  }
  
  public int getId() {
    return hashCode();
  }
  
  public boolean hasOption(Option option) {
    return this.myOptions.contains(option);
  }

  
  public boolean equals(Object o) {
    if (this == o) return true; 
    if (o == null || getClass() != o.getClass()) return false; 
    TextStyle textStyle = (TextStyle)o;
    return (Objects.equals(this.myForeground, textStyle.myForeground) && 
      Objects.equals(this.myBackground, textStyle.myBackground) && this.myOptions
      .equals(textStyle.myOptions));
  }

  
  public int hashCode() {
    return Objects.hash(new Object[] { this.myForeground, this.myBackground, this.myOptions });
  }
  
  public TerminalColor getBackgroundForRun() {
    return this.myOptions.contains(Option.INVERSE) ? this.myForeground : this.myBackground;
  }
  
  public TerminalColor getForegroundForRun() {
    return this.myOptions.contains(Option.INVERSE) ? this.myBackground : this.myForeground;
  }
  
  @NotNull
  public Builder toBuilder() {
    return new Builder(this);
  }
  
  public enum Option {
    BOLD,
    ITALIC,
    BLINK,
    DIM,
    INVERSE,
    UNDERLINED,
    HIDDEN;
    
    private void set(@NotNull EnumSet<Option> options, boolean val) {
      if (options == null) $$$reportNull$$$0(0);  if (val) {
        options.add(this);
      } else {
        
        options.remove(this);
      } 
    }
  }
  
  public static class Builder {
    private TerminalColor myForeground;
    private TerminalColor myBackground;
    private EnumSet<TextStyle.Option> myOptions;
    
    public Builder(@NotNull TextStyle textStyle) {
      this.myForeground = textStyle.myForeground;
      this.myBackground = textStyle.myBackground;
      this.myOptions = textStyle.myOptions.clone();
    }
    
    public Builder() {
      this.myForeground = null;
      this.myBackground = null;
      this.myOptions = EnumSet.noneOf(TextStyle.Option.class);
    }
    
    @NotNull
    public Builder setForeground(@Nullable TerminalColor foreground) {
      this.myForeground = foreground;
      if (this == null) $$$reportNull$$$0(1);  return this;
    }
    
    @NotNull
    public Builder setBackground(@Nullable TerminalColor background) {
      this.myBackground = background;
      if (this == null) $$$reportNull$$$0(2);  return this;
    }
    
    @NotNull
    public Builder setOption(@NotNull TextStyle.Option option, boolean val) {
      if (option == null) $$$reportNull$$$0(3);  option.set(this.myOptions, val);
      if (this == null) $$$reportNull$$$0(4);  return this;
    }
    
    @NotNull
    public TextStyle build() {
      return new TextStyle(this.myForeground, this.myBackground, this.myOptions);
    }
  }
}
