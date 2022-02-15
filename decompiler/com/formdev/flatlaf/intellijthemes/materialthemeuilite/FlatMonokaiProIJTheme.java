package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMonokaiProIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Monokai Pro (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMonokaiProIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Monokai Pro (Material)", FlatMonokaiProIJTheme.class);
  }
  
  public FlatMonokaiProIJTheme() {
    super(Utils.loadTheme("Monokai Pro.theme.json"));
  }

  
  public String getName() {
    return "Monokai Pro (Material)";
  }
}
