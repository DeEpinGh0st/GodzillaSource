package com.jgoodies.forms.layout;

import com.jgoodies.common.base.Preconditions;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;































































public final class RowSpec
  extends FormSpec
{
  public static final FormSpec.DefaultAlignment TOP = FormSpec.TOP_ALIGN;



  
  public static final FormSpec.DefaultAlignment CENTER = FormSpec.CENTER_ALIGN;



  
  public static final FormSpec.DefaultAlignment BOTTOM = FormSpec.BOTTOM_ALIGN;



  
  public static final FormSpec.DefaultAlignment FILL = FormSpec.FILL_ALIGN;



  
  public static final FormSpec.DefaultAlignment DEFAULT = CENTER;






  
  private static final Map<String, RowSpec> CACHE = new HashMap<String, RowSpec>();



















  
  public RowSpec(FormSpec.DefaultAlignment defaultAlignment, Size size, double resizeWeight) {
    super(defaultAlignment, size, resizeWeight);
  }







  
  public RowSpec(Size size) {
    super(DEFAULT, size, 0.0D);
  }









  
  private RowSpec(String encodedDescription) {
    super(DEFAULT, encodedDescription);
  }














  
  public static RowSpec createGap(ConstantSize gapHeight) {
    return new RowSpec(DEFAULT, gapHeight, 0.0D);
  }
















  
  public static RowSpec decode(String encodedRowSpec) {
    return decode(encodedRowSpec, LayoutMap.getRoot());
  }

















  
  public static RowSpec decode(String encodedRowSpec, LayoutMap layoutMap) {
    Preconditions.checkNotBlank(encodedRowSpec, "The encoded row specification must not be null, empty or whitespace.");
    
    Preconditions.checkNotNull(layoutMap, "The LayoutMap must not be null.");
    
    String trimmed = encodedRowSpec.trim();
    String lower = trimmed.toLowerCase(Locale.ENGLISH);
    return decodeExpanded(layoutMap.expand(lower, false));
  }










  
  static RowSpec decodeExpanded(String expandedTrimmedLowerCaseSpec) {
    RowSpec spec = CACHE.get(expandedTrimmedLowerCaseSpec);
    if (spec == null) {
      spec = new RowSpec(expandedTrimmedLowerCaseSpec);
      CACHE.put(expandedTrimmedLowerCaseSpec, spec);
    } 
    return spec;
  }













  
  public static RowSpec[] decodeSpecs(String encodedRowSpecs) {
    return decodeSpecs(encodedRowSpecs, LayoutMap.getRoot());
  }
















  
  public static RowSpec[] decodeSpecs(String encodedRowSpecs, LayoutMap layoutMap) {
    return FormSpecParser.parseRowSpecs(encodedRowSpecs, layoutMap);
  }










  
  protected boolean isHorizontal() {
    return false;
  }
}
