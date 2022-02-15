package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatNightOwlIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Night Owl (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatNightOwlIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Night Owl (Material)", FlatNightOwlIJTheme.class);
  }
  
  public FlatNightOwlIJTheme() {
    super(Utils.loadTheme("Night Owl.theme.json"));
  }

  
  public String getName() {
    return "Night Owl (Material)";
  }
}
