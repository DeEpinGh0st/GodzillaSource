package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatSolarizedLightIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Solarized Light (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatSolarizedLightIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Solarized Light (Material)", FlatSolarizedLightIJTheme.class);
  }
  
  public FlatSolarizedLightIJTheme() {
    super(Utils.loadTheme("Solarized Light.theme.json"));
  }

  
  public String getName() {
    return "Solarized Light (Material)";
  }
}
