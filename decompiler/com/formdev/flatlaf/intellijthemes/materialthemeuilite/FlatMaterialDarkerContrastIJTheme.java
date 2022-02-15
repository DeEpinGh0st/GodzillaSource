package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialDarkerContrastIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Darker Contrast (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialDarkerContrastIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Darker Contrast (Material)", FlatMaterialDarkerContrastIJTheme.class);
  }
  
  public FlatMaterialDarkerContrastIJTheme() {
    super(Utils.loadTheme("Material Darker Contrast.theme.json"));
  }

  
  public String getName() {
    return "Material Darker Contrast (Material)";
  }
}
