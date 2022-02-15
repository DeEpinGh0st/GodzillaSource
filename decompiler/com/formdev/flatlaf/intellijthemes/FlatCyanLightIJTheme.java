package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatCyanLightIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Cyan light";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatCyanLightIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Cyan light", FlatCyanLightIJTheme.class);
  }
  
  public FlatCyanLightIJTheme() {
    super(Utils.loadTheme("Cyan.theme.json"));
  }

  
  public String getName() {
    return "Cyan light";
  }
}
