package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialOceanicIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Oceanic (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialOceanicIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Oceanic (Material)", FlatMaterialOceanicIJTheme.class);
  }
  
  public FlatMaterialOceanicIJTheme() {
    super(Utils.loadTheme("Material Oceanic.theme.json"));
  }

  
  public String getName() {
    return "Material Oceanic (Material)";
  }
}
