package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatSolarizedLightContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Solarized Light Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatSolarizedLightContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Solarized Light Contrast (Material)", FlatSolarizedLightContrastIJTheme.class);
  }
  
  public FlatSolarizedLightContrastIJTheme() {
    super(Utils.loadTheme("Solarized Light Contrast.theme.json"));
  }

  
  public String getName() {
    return "Solarized Light Contrast (Material)";
  }
}
