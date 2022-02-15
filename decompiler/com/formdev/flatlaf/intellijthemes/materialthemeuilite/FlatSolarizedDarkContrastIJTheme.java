package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatSolarizedDarkContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Solarized Dark Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatSolarizedDarkContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Solarized Dark Contrast (Material)", FlatSolarizedDarkContrastIJTheme.class);
  }
  
  public FlatSolarizedDarkContrastIJTheme() {
    super(Utils.loadTheme("Solarized Dark Contrast.theme.json"));
  }

  
  public String getName() {
    return "Solarized Dark Contrast (Material)";
  }
}
