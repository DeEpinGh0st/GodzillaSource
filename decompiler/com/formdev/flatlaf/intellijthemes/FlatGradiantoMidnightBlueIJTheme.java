package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatGradiantoMidnightBlueIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Gradianto Midnight Blue";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatGradiantoMidnightBlueIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Gradianto Midnight Blue", FlatGradiantoMidnightBlueIJTheme.class);
  }
  
  public FlatGradiantoMidnightBlueIJTheme() {
    super(Utils.loadTheme("Gradianto_midnight_blue.theme.json"));
  }

  
  public String getName() {
    return "Gradianto Midnight Blue";
  }
}
