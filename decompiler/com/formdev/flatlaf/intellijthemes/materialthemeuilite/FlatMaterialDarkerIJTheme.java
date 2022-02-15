package com.formdev.flatlaf.intellijthemes.materialthemeuilite;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialDarkerIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Darker (Material)";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialDarkerIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Darker (Material)", FlatMaterialDarkerIJTheme.class);
  }
  
  public FlatMaterialDarkerIJTheme() {
    super(Utils.loadTheme("Material Darker.theme.json"));
  }

  
  public String getName() {
    return "Material Darker (Material)";
  }
}
