package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;





























@Beta
@GwtIncompatible
public final class PatternFilenameFilter
  implements FilenameFilter
{
  private final Pattern pattern;
  
  public PatternFilenameFilter(String patternStr) {
    this(Pattern.compile(patternStr));
  }





  
  public PatternFilenameFilter(Pattern pattern) {
    this.pattern = (Pattern)Preconditions.checkNotNull(pattern);
  }

  
  public boolean accept(File dir, String fileName) {
    return this.pattern.matcher(fileName).matches();
  }
}
