package org.springframework.expression;

public interface ExpressionParser {
  Expression parseExpression(String paramString) throws ParseException;
  
  Expression parseExpression(String paramString, ParserContext paramParserContext) throws ParseException;
}
