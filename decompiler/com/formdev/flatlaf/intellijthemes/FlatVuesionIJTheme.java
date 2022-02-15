package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatVuesionIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Vuesion";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatVuesionIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Vuesion", FlatVuesionIJTheme.class);
  }
  
  public FlatVuesionIJTheme() {
    super(Utils.loadTheme("vuesion_theme.theme.json"));
  }

  
  public String getName() {
    return "Vuesion";
  }
}
