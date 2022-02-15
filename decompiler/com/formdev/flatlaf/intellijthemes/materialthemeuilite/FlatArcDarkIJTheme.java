package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatArcDarkIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Arc Dark (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatArcDarkIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Arc Dark (Material)", FlatArcDarkIJTheme.class);
  }
  
  public FlatArcDarkIJTheme() {
    super(Utils.loadTheme("Arc Dark.theme.json"));
  }

  
  public String getName() {
    return "Arc Dark (Material)";
  }
}
