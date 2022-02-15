package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatCarbonIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Carbon";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatCarbonIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Carbon", FlatCarbonIJTheme.class);
  }
  
  public FlatCarbonIJTheme() {
    super(Utils.loadTheme("Carbon.theme.json"));
  }

  
  public String getName() {
    return "Carbon";
  }
}
