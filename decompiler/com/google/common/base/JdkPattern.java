package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;












@GwtIncompatible
final class JdkPattern
  extends CommonPattern
  implements Serializable
{
  private final Pattern pattern;
  private static final long serialVersionUID = 0L;
  
  JdkPattern(Pattern pattern) {
    this.pattern = Preconditions.<Pattern>checkNotNull(pattern);
  }

  
  public CommonMatcher matcher(CharSequence t) {
    return new JdkMatcher(this.pattern.matcher(t));
  }

  
  public String pattern() {
    return this.pattern.pattern();
  }

  
  public int flags() {
    return this.pattern.flags();
  }

  
  public String toString() {
    return this.pattern.toString();
  }
  
  private static final class JdkMatcher extends CommonMatcher {
    final Matcher matcher;
    
    JdkMatcher(Matcher matcher) {
      this.matcher = Preconditions.<Matcher>checkNotNull(matcher);
    }

    
    public boolean matches() {
      return this.matcher.matches();
    }

    
    public boolean find() {
      return this.matcher.find();
    }

    
    public boolean find(int index) {
      return this.matcher.find(index);
    }

    
    public String replaceAll(String replacement) {
      return this.matcher.replaceAll(replacement);
    }

    
    public int end() {
      return this.matcher.end();
    }

    
    public int start() {
      return this.matcher.start();
    }
  }
}
