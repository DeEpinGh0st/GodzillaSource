package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatGradiantoDeepOceanIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Gradianto Deep Ocean";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatGradiantoDeepOceanIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Gradianto Deep Ocean", FlatGradiantoDeepOceanIJTheme.class);
  }
  
  public FlatGradiantoDeepOceanIJTheme() {
    super(Utils.loadTheme("Gradianto_deep_ocean.theme.json"));
  }

  
  public String getName() {
    return "Gradianto Deep Ocean";
  }
}
