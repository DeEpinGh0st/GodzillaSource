package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialDeepOceanContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Deep Ocean Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialDeepOceanContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Deep Ocean Contrast (Material)", FlatMaterialDeepOceanContrastIJTheme.class);
  }
  
  public FlatMaterialDeepOceanContrastIJTheme() {
    super(Utils.loadTheme("Material Deep Ocean Contrast.theme.json"));
  }

  
  public String getName() {
    return "Material Deep Ocean Contrast (Material)";
  }
}
