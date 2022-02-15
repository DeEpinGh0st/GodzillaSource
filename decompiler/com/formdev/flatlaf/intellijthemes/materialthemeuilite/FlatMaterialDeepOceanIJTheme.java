package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialDeepOceanIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Deep Ocean (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialDeepOceanIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Deep Ocean (Material)", FlatMaterialDeepOceanIJTheme.class);
  }
  
  public FlatMaterialDeepOceanIJTheme() {
    super(Utils.loadTheme("Material Deep Ocean.theme.json"));
  }

  
  public String getName() {
    return "Material Deep Ocean (Material)";
  }
}
