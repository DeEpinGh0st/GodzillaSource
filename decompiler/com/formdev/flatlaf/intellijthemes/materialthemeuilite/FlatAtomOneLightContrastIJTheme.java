package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatAtomOneLightContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Atom One Light Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatAtomOneLightContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Atom One Light Contrast (Material)", FlatAtomOneLightContrastIJTheme.class);
  }
  
  public FlatAtomOneLightContrastIJTheme() {
    super(Utils.loadTheme("Atom One Light Contrast.theme.json"));
  }

  
  public String getName() {
    return "Atom One Light Contrast (Material)";
  }
}
