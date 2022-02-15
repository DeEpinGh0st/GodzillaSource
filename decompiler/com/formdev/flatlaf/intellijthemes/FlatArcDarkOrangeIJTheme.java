package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatArcDarkOrangeIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Arc Dark - Orange";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatArcDarkOrangeIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Arc Dark - Orange", FlatArcDarkOrangeIJTheme.class);
  }
  
  public FlatArcDarkOrangeIJTheme() {
    super(Utils.loadTheme("arc_theme_dark_orange.theme.json"));
  }

  
  public String getName() {
    return "Arc Dark - Orange";
  }
}
