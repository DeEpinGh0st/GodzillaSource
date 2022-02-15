package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialPalenightIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Palenight (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialPalenightIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Palenight (Material)", FlatMaterialPalenightIJTheme.class);
  }
  
  public FlatMaterialPalenightIJTheme() {
    super(Utils.loadTheme("Material Palenight.theme.json"));
  }

  
  public String getName() {
    return "Material Palenight (Material)";
  }
}
