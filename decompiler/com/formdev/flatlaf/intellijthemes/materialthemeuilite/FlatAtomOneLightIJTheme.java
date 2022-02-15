package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatAtomOneLightIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Atom One Light (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatAtomOneLightIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Atom One Light (Material)", FlatAtomOneLightIJTheme.class);
  }
  
  public FlatAtomOneLightIJTheme() {
    super(Utils.loadTheme("Atom One Light.theme.json"));
  }

  
  public String getName() {
    return "Atom One Light (Material)";
  }
}
