package com.jgoodies.forms.util;

import com.jgoodies.common.base.SystemUtils;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;























































public final class FormUtils
{
  private static LookAndFeel cachedLookAndFeel;
  private static Boolean cachedIsLafAqua;
  
  public static boolean isLafAqua() {
    ensureValidCache();
    if (cachedIsLafAqua == null) {
      cachedIsLafAqua = Boolean.valueOf(SystemUtils.isLafAqua());
    }
    return cachedIsLafAqua.booleanValue();
  }
















  
  public static void clearLookAndFeelBasedCaches() {
    cachedIsLafAqua = null;
    DefaultUnitConverter.getInstance().clearCache();
  }

















  
  static void ensureValidCache() {
    LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
    if (currentLookAndFeel != cachedLookAndFeel) {
      clearLookAndFeelBasedCaches();
      cachedLookAndFeel = currentLookAndFeel;
    } 
  }
}
