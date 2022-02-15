package com.kitfox.svg.animation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;













































public abstract class TimeBase
{
  static final Matcher matchIndefinite = Pattern.compile("\\s*indefinite\\s*").matcher("");
  static final Matcher matchUnitTime = Pattern.compile("\\s*([-+]?((\\d*\\.\\d+)|(\\d+))([-+]?[eE]\\d+)?)\\s*(h|min|s|ms)?\\s*").matcher("");















  
  protected static TimeBase parseTimeComponent(String text) {
    matchIndefinite.reset(text);
    if (matchIndefinite.matches()) return new TimeIndefinite();
    
    matchUnitTime.reset(text);
    if (matchUnitTime.matches()) {
      
      String val = matchUnitTime.group(1);
      String units = matchUnitTime.group(6);
      
      double time = 0.0D; try {
        time = Double.parseDouble(val);
      } catch (Exception exception) {}
      
      if (units.equals("ms")) { time *= 0.001D; }
      else if (units.equals("min")) { time *= 60.0D; }
      else if (units.equals("h")) { time *= 3600.0D; }
      
      return new TimeDiscrete(time);
    } 
    
    return null;
  }
  
  public abstract double evalTime();
  
  public void setParentElement(AnimationElement ele) {}
}
