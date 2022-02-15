package org.springframework.core.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;


























class CommandLineArgs
{
  private final Map<String, List<String>> optionArgs = new HashMap<>();
  private final List<String> nonOptionArgs = new ArrayList<>();






  
  public void addOptionArg(String optionName, @Nullable String optionValue) {
    if (!this.optionArgs.containsKey(optionName)) {
      this.optionArgs.put(optionName, new ArrayList<>());
    }
    if (optionValue != null) {
      ((List<String>)this.optionArgs.get(optionName)).add(optionValue);
    }
  }



  
  public Set<String> getOptionNames() {
    return Collections.unmodifiableSet(this.optionArgs.keySet());
  }



  
  public boolean containsOption(String optionName) {
    return this.optionArgs.containsKey(optionName);
  }





  
  @Nullable
  public List<String> getOptionValues(String optionName) {
    return this.optionArgs.get(optionName);
  }



  
  public void addNonOptionArg(String value) {
    this.nonOptionArgs.add(value);
  }



  
  public List<String> getNonOptionArgs() {
    return Collections.unmodifiableList(this.nonOptionArgs);
  }
}
