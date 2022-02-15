package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatNightOwlContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Night Owl Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatNightOwlContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Night Owl Contrast (Material)", FlatNightOwlContrastIJTheme.class);
  }
  
  public FlatNightOwlContrastIJTheme() {
    super(Utils.loadTheme("Night Owl Contrast.theme.json"));
  }

  
  public String getName() {
    return "Night Owl Contrast (Material)";
  }
}
