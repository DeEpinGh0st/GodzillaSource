package org.fife.rsta.ac.css;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.TemplateCompletion;




















class ColorCompletionGenerator
  implements CompletionGenerator
{
  private List<Completion> defaults;
  private static final String FUNC_ICON_KEY = "css_propertyvalue_function";
  private static final String ICON_KEY = "css_propertyvalue_identifier";
  private static final Pattern DIGITS = Pattern.compile("\\d*");
  
  public ColorCompletionGenerator(CompletionProvider provider) {
    this.defaults = createDefaults(provider);
  }



  
  private static List<Completion> createDefaults(CompletionProvider provider) {
    List<Completion> completions = new ArrayList<>();

    
    completions.add(new ColorCompletion(provider, "black"));
    completions.add(new ColorCompletion(provider, "silver"));
    completions.add(new ColorCompletion(provider, "gray"));
    completions.add(new ColorCompletion(provider, "white"));
    completions.add(new ColorCompletion(provider, "maroon"));
    completions.add(new ColorCompletion(provider, "red"));
    completions.add(new ColorCompletion(provider, "purple"));
    completions.add(new ColorCompletion(provider, "fuchsia"));
    completions.add(new ColorCompletion(provider, "green"));
    completions.add(new ColorCompletion(provider, "lime"));
    completions.add(new ColorCompletion(provider, "olive"));
    completions.add(new ColorCompletion(provider, "yellow"));
    completions.add(new ColorCompletion(provider, "navy"));
    completions.add(new ColorCompletion(provider, "blue"));
    completions.add(new ColorCompletion(provider, "teal"));
    completions.add(new ColorCompletion(provider, "aqua"));
    completions.add(new ColorCompletion(provider, "orange"));
    
    completions.add(new ColorCompletion(provider, "currentColor"));
    completions.add(new ColorCompletion(provider, "transparent"));
    completions.add(new ColorTemplateCompletion(provider, "#", "#${rgb}${cursor}", "#RGB"));
    
    completions.add(new ColorTemplateCompletion(provider, "#", "#${rrggbb}${cursor}", "#RRGGBB"));
    
    completions.add(new ColorTemplateCompletion(provider, "rgb", "rgb(${red}, ${green}, ${blue})${cursor}", "rgb(r, g, b)"));
    
    completions.add(new ColorTemplateCompletion(provider, "rgba", "rgba(${red}, ${green}, ${blue}, ${alpha})${cursor}", "rgba(r, g, b, a)"));
    
    completions.add(new ColorTemplateCompletion(provider, "hsl", "hsl(${hue}, ${saturation}, ${brightness})${cursor}", "hsl(h, s, b)"));
    
    completions.add(new ColorTemplateCompletion(provider, "hsla", "hsla(${hue}, ${saturation}, ${brightness}, ${alpha})${cursor}", "hsla(h, s, b, a)"));

    
    return completions;
  }







  
  public List<Completion> generate(CompletionProvider provider, String input) {
    List<Completion> completions = new ArrayList<>(this.defaults);
    
    if (DIGITS.matcher(input).matches()) {
      completions.add(new ColorCompletion(provider, input + "s"));
      completions.add(new ColorCompletion(provider, input + "ms"));
    } 
    
    return completions;
  }




  
  private static class ColorTemplateCompletion
    extends TemplateCompletion
  {
    public ColorTemplateCompletion(CompletionProvider provider, String input, String template, String desc) {
      super(provider, input, desc, template, desc, null);
      boolean function = (template.indexOf('(') > -1);
      setIcon(IconFactory.get().getIcon(function ? "css_propertyvalue_function" : "css_propertyvalue_identifier"));
    }
  }





  
  private static class ColorCompletion
    extends BasicCssCompletion
  {
    public ColorCompletion(CompletionProvider provider, String value) {
      super(provider, value, "css_propertyvalue_identifier");
    }
  }
}
