package org.springframework.core.type.filter;

import java.util.regex.Pattern;
import org.springframework.core.type.ClassMetadata;
import org.springframework.util.Assert;

























public class RegexPatternTypeFilter
  extends AbstractClassTestingTypeFilter
{
  private final Pattern pattern;
  
  public RegexPatternTypeFilter(Pattern pattern) {
    Assert.notNull(pattern, "Pattern must not be null");
    this.pattern = pattern;
  }


  
  protected boolean match(ClassMetadata metadata) {
    return this.pattern.matcher(metadata.getClassName()).matches();
  }
}
