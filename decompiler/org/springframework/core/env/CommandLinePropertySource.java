package org.springframework.core.env;

import java.util.Collection;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;













































































































































































































public abstract class CommandLinePropertySource<T>
  extends EnumerablePropertySource<T>
{
  public static final String COMMAND_LINE_PROPERTY_SOURCE_NAME = "commandLineArgs";
  public static final String DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME = "nonOptionArgs";
  private String nonOptionArgsPropertyName = "nonOptionArgs";





  
  public CommandLinePropertySource(T source) {
    super("commandLineArgs", source);
  }




  
  public CommandLinePropertySource(String name, T source) {
    super(name, source);
  }





  
  public void setNonOptionArgsPropertyName(String nonOptionArgsPropertyName) {
    this.nonOptionArgsPropertyName = nonOptionArgsPropertyName;
  }








  
  public final boolean containsProperty(String name) {
    if (this.nonOptionArgsPropertyName.equals(name)) {
      return !getNonOptionArgs().isEmpty();
    }
    return containsOption(name);
  }










  
  @Nullable
  public final String getProperty(String name) {
    if (this.nonOptionArgsPropertyName.equals(name)) {
      Collection<String> nonOptionArguments = getNonOptionArgs();
      if (nonOptionArguments.isEmpty()) {
        return null;
      }
      
      return StringUtils.collectionToCommaDelimitedString(nonOptionArguments);
    } 
    
    Collection<String> optionValues = getOptionValues(name);
    if (optionValues == null) {
      return null;
    }
    
    return StringUtils.collectionToCommaDelimitedString(optionValues);
  }
  
  protected abstract boolean containsOption(String paramString);
  
  @Nullable
  protected abstract List<String> getOptionValues(String paramString);
  
  protected abstract List<String> getNonOptionArgs();
}
