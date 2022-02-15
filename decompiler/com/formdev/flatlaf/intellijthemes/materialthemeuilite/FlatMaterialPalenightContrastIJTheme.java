package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialPalenightContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Palenight Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialPalenightContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Palenight Contrast (Material)", FlatMaterialPalenightContrastIJTheme.class);
  }
  
  public FlatMaterialPalenightContrastIJTheme() {
    super(Utils.loadTheme("Material Palenight Contrast.theme.json"));
  }

  
  public String getName() {
    return "Material Palenight Contrast (Material)";
  }
}
