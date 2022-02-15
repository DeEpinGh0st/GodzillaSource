package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatSolarizedDarkIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Solarized Dark";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatSolarizedDarkIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Solarized Dark", FlatSolarizedDarkIJTheme.class);
  }
  
  public FlatSolarizedDarkIJTheme() {
    super(Utils.loadTheme("SolarizedDark.theme.json"));
  }

  
  public String getName() {
    return "Solarized Dark";
  }
}
