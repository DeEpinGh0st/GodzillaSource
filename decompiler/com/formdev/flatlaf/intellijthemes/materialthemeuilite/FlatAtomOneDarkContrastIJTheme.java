package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatAtomOneDarkContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Atom One Dark Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatAtomOneDarkContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Atom One Dark Contrast (Material)", FlatAtomOneDarkContrastIJTheme.class);
  }
  
  public FlatAtomOneDarkContrastIJTheme() {
    super(Utils.loadTheme("Atom One Dark Contrast.theme.json"));
  }

  
  public String getName() {
    return "Atom One Dark Contrast (Material)";
  }
}
