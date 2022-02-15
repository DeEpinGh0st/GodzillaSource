package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatAtomOneDarkIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Atom One Dark (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatAtomOneDarkIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Atom One Dark (Material)", FlatAtomOneDarkIJTheme.class);
  }
  
  public FlatAtomOneDarkIJTheme() {
    super(Utils.loadTheme("Atom One Dark.theme.json"));
  }

  
  public String getName() {
    return "Atom One Dark (Material)";
  }
}
