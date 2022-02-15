package org.springframework.core;

import org.springframework.lang.Nullable;







































public final class SpringVersion
{
  @Nullable
  public static String getVersion() {
    Package pkg = SpringVersion.class.getPackage();
    return (pkg != null) ? pkg.getImplementationVersion() : null;
  }
}
