package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatArcDarkIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Arc Dark";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatArcDarkIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Arc Dark", FlatArcDarkIJTheme.class);
  }
  
  public FlatArcDarkIJTheme() {
    super(Utils.loadTheme("arc_theme_dark.theme.json"));
  }

  
  public String getName() {
    return "Arc Dark";
  }
}
