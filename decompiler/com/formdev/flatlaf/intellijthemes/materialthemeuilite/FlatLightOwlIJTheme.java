package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatLightOwlIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Light Owl (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatLightOwlIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Light Owl (Material)", FlatLightOwlIJTheme.class);
  }
  
  public FlatLightOwlIJTheme() {
    super(Utils.loadTheme("Light Owl.theme.json"));
  }

  
  public String getName() {
    return "Light Owl (Material)";
  }
}
