package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatLightFlatIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Light Flat";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatLightFlatIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Light Flat", FlatLightFlatIJTheme.class);
  }
  
  public FlatLightFlatIJTheme() {
    super(Utils.loadTheme("LightFlatTheme.theme.json"));
  }

  
  public String getName() {
    return "Light Flat";
  }
}
