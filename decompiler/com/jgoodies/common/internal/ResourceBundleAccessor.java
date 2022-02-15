package com.jgoodies.common.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.Icon;

















































public final class ResourceBundleAccessor
  implements StringAndIconResourceAccessor
{
  private final ResourceBundle bundle;
  
  public ResourceBundleAccessor(ResourceBundle bundle) {
    this.bundle = (ResourceBundle)Preconditions.checkNotNull(bundle, "The %1$s must not be null.", new Object[] { "resource bundle" });
  }




  
  public Icon getIcon(String key) {
    return (Icon)this.bundle.getObject(key);
  }














  
  public String getString(String key, Object... args) {
    try {
      return Strings.get(this.bundle.getString(key), args);
    } catch (MissingResourceException mre) {
      return key;
    } 
  }
}
