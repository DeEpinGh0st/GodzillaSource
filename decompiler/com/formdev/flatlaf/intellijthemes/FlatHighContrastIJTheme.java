package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatHighContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "High contrast";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatHighContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("High contrast", FlatHighContrastIJTheme.class);
  }
  
  public FlatHighContrastIJTheme() {
    super(Utils.loadTheme("HighContrast.theme.json"));
  }

  
  public String getName() {
    return "High contrast";
  }
}
