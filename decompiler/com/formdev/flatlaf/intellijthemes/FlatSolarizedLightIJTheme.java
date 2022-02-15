package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatSolarizedLightIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Solarized Light";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatSolarizedLightIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Solarized Light", FlatSolarizedLightIJTheme.class);
  }
  
  public FlatSolarizedLightIJTheme() {
    super(Utils.loadTheme("SolarizedLight.theme.json"));
  }

  
  public String getName() {
    return "Solarized Light";
  }
}
