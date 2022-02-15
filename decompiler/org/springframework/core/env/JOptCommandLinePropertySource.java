package org.springframework.core.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;























































public class JOptCommandLinePropertySource
  extends CommandLinePropertySource<OptionSet>
{
  public JOptCommandLinePropertySource(OptionSet options) {
    super(options);
  }




  
  public JOptCommandLinePropertySource(String name, OptionSet options) {
    super(name, options);
  }


  
  protected boolean containsOption(String name) {
    return this.source.has(name);
  }

  
  public String[] getPropertyNames() {
    List<String> names = new ArrayList<>();
    for (OptionSpec<?> spec : (Iterable<OptionSpec<?>>)this.source.specs()) {
      String lastOption = (String)CollectionUtils.lastElement(spec.options());
      if (lastOption != null)
      {
        names.add(lastOption);
      }
    } 
    return StringUtils.toStringArray(names);
  }

  
  @Nullable
  public List<String> getOptionValues(String name) {
    List<?> argValues = this.source.valuesOf(name);
    List<String> stringArgValues = new ArrayList<>();
    for (Object argValue : argValues) {
      stringArgValues.add(argValue.toString());
    }
    if (stringArgValues.isEmpty()) {
      return this.source.has(name) ? Collections.<String>emptyList() : null;
    }
    return Collections.unmodifiableList(stringArgValues);
  }

  
  protected List<String> getNonOptionArgs() {
    List<?> argValues = this.source.nonOptionArguments();
    List<String> stringArgValues = new ArrayList<>();
    for (Object argValue : argValues) {
      stringArgValues.add(argValue.toString());
    }
    return stringArgValues.isEmpty() ? Collections.<String>emptyList() : 
      Collections.<String>unmodifiableList(stringArgValues);
  }
}
