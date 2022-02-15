package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialOceanicContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Oceanic Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialOceanicContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Oceanic Contrast (Material)", FlatMaterialOceanicContrastIJTheme.class);
  }
  
  public FlatMaterialOceanicContrastIJTheme() {
    super(Utils.loadTheme("Material Oceanic Contrast.theme.json"));
  }

  
  public String getName() {
    return "Material Oceanic Contrast (Material)";
  }
}
