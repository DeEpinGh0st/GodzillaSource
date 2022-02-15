package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatArcOrangeIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Arc - Orange";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatArcOrangeIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Arc - Orange", FlatArcOrangeIJTheme.class);
  }
  
  public FlatArcOrangeIJTheme() {
    super(Utils.loadTheme("arc-theme-orange.theme.json"));
  }

  
  public String getName() {
    return "Arc - Orange";
  }
}
