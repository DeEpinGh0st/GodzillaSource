package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialLighterContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Lighter Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialLighterContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Lighter Contrast (Material)", FlatMaterialLighterContrastIJTheme.class);
  }
  
  public FlatMaterialLighterContrastIJTheme() {
    super(Utils.loadTheme("Material Lighter Contrast.theme.json"));
  }

  
  public String getName() {
    return "Material Lighter Contrast (Material)";
  }
}
