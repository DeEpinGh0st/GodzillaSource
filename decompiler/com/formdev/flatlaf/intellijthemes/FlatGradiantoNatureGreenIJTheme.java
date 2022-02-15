package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatGradiantoNatureGreenIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Gradianto Nature Green";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatGradiantoNatureGreenIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Gradianto Nature Green", FlatGradiantoNatureGreenIJTheme.class);
  }
  
  public FlatGradiantoNatureGreenIJTheme() {
    super(Utils.loadTheme("Gradianto_Nature_Green.theme.json"));
  }

  
  public String getName() {
    return "Gradianto Nature Green";
  }
}
