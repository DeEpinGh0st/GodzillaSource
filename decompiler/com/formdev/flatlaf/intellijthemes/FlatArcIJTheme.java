package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatArcIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Arc";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatArcIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Arc", FlatArcIJTheme.class);
  }
  
  public FlatArcIJTheme() {
    super(Utils.loadTheme("arc-theme.theme.json"));
  }

  
  public String getName() {
    return "Arc";
  }
}
