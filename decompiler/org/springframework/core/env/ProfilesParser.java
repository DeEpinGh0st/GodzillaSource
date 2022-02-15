package org.springframework.core.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;





























final class ProfilesParser
{
  static Profiles parse(String... expressions) {
    Assert.notEmpty((Object[])expressions, "Must specify at least one profile");
    Profiles[] parsed = new Profiles[expressions.length];
    for (int i = 0; i < expressions.length; i++) {
      parsed[i] = parseExpression(expressions[i]);
    }
    return new ParsedProfiles(expressions, parsed);
  }
  
  private static Profiles parseExpression(String expression) {
    Assert.hasText(expression, () -> "Invalid profile expression [" + expression + "]: must contain text");
    StringTokenizer tokens = new StringTokenizer(expression, "()&|!", true);
    return parseTokens(expression, tokens);
  }
  
  private static Profiles parseTokens(String expression, StringTokenizer tokens) {
    return parseTokens(expression, tokens, Context.NONE);
  }
  
  private static Profiles parseTokens(String expression, StringTokenizer tokens, Context context) {
    List<Profiles> elements = new ArrayList<>();
    Operator operator = null;
    while (tokens.hasMoreTokens()) {
      Profiles contents, merged; String token = tokens.nextToken().trim();
      if (token.isEmpty()) {
        continue;
      }
      switch (token) {
        case "(":
          contents = parseTokens(expression, tokens, Context.BRACKET);
          if (context == Context.INVERT) {
            return contents;
          }
          elements.add(contents);
          continue;
        case "&":
          assertWellFormed(expression, (operator == null || operator == Operator.AND));
          operator = Operator.AND;
          continue;
        case "|":
          assertWellFormed(expression, (operator == null || operator == Operator.OR));
          operator = Operator.OR;
          continue;
        case "!":
          elements.add(not(parseTokens(expression, tokens, Context.INVERT)));
          continue;
        case ")":
          merged = merge(expression, elements, operator);
          if (context == Context.BRACKET) {
            return merged;
          }
          elements.clear();
          elements.add(merged);
          operator = null;
          continue;
      } 
      Profiles value = equals(token);
      if (context == Context.INVERT) {
        return value;
      }
      elements.add(value);
    } 
    
    return merge(expression, elements, operator);
  }
  
  private static Profiles merge(String expression, List<Profiles> elements, @Nullable Operator operator) {
    assertWellFormed(expression, !elements.isEmpty());
    if (elements.size() == 1) {
      return elements.get(0);
    }
    Profiles[] profiles = elements.<Profiles>toArray(new Profiles[0]);
    return (operator == Operator.AND) ? and(profiles) : or(profiles);
  }
  
  private static void assertWellFormed(String expression, boolean wellFormed) {
    Assert.isTrue(wellFormed, () -> "Malformed profile expression [" + expression + "]");
  }
  
  private static Profiles or(Profiles... profiles) {
    return activeProfile -> Arrays.<Profiles>stream(profiles).anyMatch(isMatch(activeProfile));
  }
  
  private static Profiles and(Profiles... profiles) {
    return activeProfile -> Arrays.<Profiles>stream(profiles).allMatch(isMatch(activeProfile));
  }
  
  private static Profiles not(Profiles profiles) {
    return activeProfile -> !profiles.matches(activeProfile);
  }
  
  private static Profiles equals(String profile) {
    return activeProfile -> activeProfile.test(profile);
  }
  
  private static Predicate<Profiles> isMatch(Predicate<String> activeProfile) {
    return profiles -> profiles.matches(activeProfile);
  }
  
  private enum Operator {
    AND, OR; }
  
  private enum Context {
    NONE, INVERT, BRACKET;
  }
  
  private static class ParsedProfiles
    implements Profiles {
    private final Set<String> expressions = new LinkedHashSet<>();
    
    private final Profiles[] parsed;
    
    ParsedProfiles(String[] expressions, Profiles[] parsed) {
      Collections.addAll(this.expressions, expressions);
      this.parsed = parsed;
    }

    
    public boolean matches(Predicate<String> activeProfiles) {
      for (Profiles candidate : this.parsed) {
        if (candidate.matches(activeProfiles)) {
          return true;
        }
      } 
      return false;
    }

    
    public int hashCode() {
      return this.expressions.hashCode();
    }

    
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      ParsedProfiles that = (ParsedProfiles)obj;
      return this.expressions.equals(that.expressions);
    }

    
    public String toString() {
      return StringUtils.collectionToDelimitedString(this.expressions, " or ");
    }
  }
}
