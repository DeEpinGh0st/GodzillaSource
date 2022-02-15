package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatHiberbeeDarkIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Hiberbee Dark";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatHiberbeeDarkIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Hiberbee Dark", FlatHiberbeeDarkIJTheme.class);
  }
  
  public FlatHiberbeeDarkIJTheme() {
    super(Utils.loadTheme("HiberbeeDark.theme.json"));
  }

  
  public String getName() {
    return "Hiberbee Dark";
  }
}
