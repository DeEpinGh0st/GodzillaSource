package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialLighterIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Lighter (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialLighterIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Lighter (Material)", FlatMaterialLighterIJTheme.class);
  }
  
  public FlatMaterialLighterIJTheme() {
    super(Utils.loadTheme("Material Lighter.theme.json"));
  }

  
  public String getName() {
    return "Material Lighter (Material)";
  }
}
