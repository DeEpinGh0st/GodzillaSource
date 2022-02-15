package org.springframework.expression;

























































public interface ParserContext
{
  public static final ParserContext TEMPLATE_EXPRESSION = new ParserContext()
    {
      public boolean isTemplate()
      {
        return true;
      }

      
      public String getExpressionPrefix() {
        return "#{";
      }

      
      public String getExpressionSuffix() {
        return "}";
      }
    };
  
  boolean isTemplate();
  
  String getExpressionPrefix();
  
  String getExpressionSuffix();
}
