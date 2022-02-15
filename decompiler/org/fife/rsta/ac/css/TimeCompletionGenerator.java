package org.fife.rsta.ac.css;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;



















class TimeCompletionGenerator
  implements CompletionGenerator
{
  private static final String ICON_KEY = "css_propertyvalue_identifier";
  private static final Pattern DIGITS = Pattern.compile("\\d*");






  
  public List<Completion> generate(CompletionProvider provider, String input) {
    List<Completion> completions = new ArrayList<>();
    
    if (DIGITS.matcher(input).matches()) {
      completions.add(new TimeCompletion(provider, input + "s"));
      completions.add(new TimeCompletion(provider, input + "ms"));
    } 
    
    return completions;
  }




  
  private static class TimeCompletion
    extends BasicCssCompletion
  {
    public TimeCompletion(CompletionProvider provider, String value) {
      super(provider, value, "css_propertyvalue_identifier");
    }
  }
}
