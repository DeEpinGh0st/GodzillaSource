package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatSolarizedDarkIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Solarized Dark (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatSolarizedDarkIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Solarized Dark (Material)", FlatSolarizedDarkIJTheme.class);
  }
  
  public FlatSolarizedDarkIJTheme() {
    super(Utils.loadTheme("Solarized Dark.theme.json"));
  }

  
  public String getName() {
    return "Solarized Dark (Material)";
  }
}
