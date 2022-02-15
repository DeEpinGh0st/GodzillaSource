package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatLightOwlContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Light Owl Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatLightOwlContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Light Owl Contrast (Material)", FlatLightOwlContrastIJTheme.class);
  }
  
  public FlatLightOwlContrastIJTheme() {
    super(Utils.loadTheme("Light Owl Contrast.theme.json"));
  }

  
  public String getName() {
    return "Light Owl Contrast (Material)";
  }
}
