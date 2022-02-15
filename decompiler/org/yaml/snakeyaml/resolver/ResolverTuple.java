package org.yaml.snakeyaml.resolver;

import java.util.regex.Pattern;
import org.yaml.snakeyaml.nodes.Tag;
















final class ResolverTuple
{
  private final Tag tag;
  private final Pattern regexp;
  
  public ResolverTuple(Tag tag, Pattern regexp) {
    this.tag = tag;
    this.regexp = regexp;
  }
  
  public Tag getTag() {
    return this.tag;
  }
  
  public Pattern getRegexp() {
    return this.regexp;
  }

  
  public String toString() {
    return "Tuple tag=" + this.tag + " regexp=" + this.regexp;
  }
}
