package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMoonlightContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Moonlight Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMoonlightContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Moonlight Contrast (Material)", FlatMoonlightContrastIJTheme.class);
  }
  
  public FlatMoonlightContrastIJTheme() {
    super(Utils.loadTheme("Moonlight Contrast.theme.json"));
  }

  
  public String getName() {
    return "Moonlight Contrast (Material)";
  }
}
