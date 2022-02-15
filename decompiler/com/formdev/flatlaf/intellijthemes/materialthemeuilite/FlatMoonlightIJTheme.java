package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMoonlightIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Moonlight (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMoonlightIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Moonlight (Material)", FlatMoonlightIJTheme.class);
  }
  
  public FlatMoonlightIJTheme() {
    super(Utils.loadTheme("Moonlight.theme.json"));
  }

  
  public String getName() {
    return "Moonlight (Material)";
  }
}
