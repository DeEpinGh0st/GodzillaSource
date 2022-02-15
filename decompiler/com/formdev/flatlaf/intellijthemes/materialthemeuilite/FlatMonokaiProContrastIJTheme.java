package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMonokaiProContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Monokai Pro Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMonokaiProContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Monokai Pro Contrast (Material)", FlatMonokaiProContrastIJTheme.class);
  }
  
  public FlatMonokaiProContrastIJTheme() {
    super(Utils.loadTheme("Monokai Pro Contrast.theme.json"));
  }

  
  public String getName() {
    return "Monokai Pro Contrast (Material)";
  }
}
