package org.springframework.core.env;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;






















































































public class SimpleCommandLinePropertySource
  extends CommandLinePropertySource<CommandLineArgs>
{
  public SimpleCommandLinePropertySource(String... args) {
    super((new SimpleCommandLineArgsParser()).parse(args));
  }




  
  public SimpleCommandLinePropertySource(String name, String[] args) {
    super(name, (new SimpleCommandLineArgsParser()).parse(args));
  }




  
  public String[] getPropertyNames() {
    return StringUtils.toStringArray(this.source.getOptionNames());
  }

  
  protected boolean containsOption(String name) {
    return this.source.containsOption(name);
  }

  
  @Nullable
  protected List<String> getOptionValues(String name) {
    return this.source.getOptionValues(name);
  }

  
  protected List<String> getNonOptionArgs() {
    return this.source.getNonOptionArgs();
  }
}
