package org.fife.ui.rsyntaxtextarea;

import java.awt.Rectangle;
import javax.swing.text.TabExpander;

public interface Token extends TokenTypes {
  StringBuilder appendHTMLRepresentation(StringBuilder paramStringBuilder, RSyntaxTextArea paramRSyntaxTextArea, boolean paramBoolean);
  
  StringBuilder appendHTMLRepresentation(StringBuilder paramStringBuilder, RSyntaxTextArea paramRSyntaxTextArea, boolean paramBoolean1, boolean paramBoolean2);
  
  char charAt(int paramInt);
  
  boolean containsPosition(int paramInt);
  
  int documentToToken(int paramInt);
  
  boolean endsWith(char[] paramArrayOfchar);
  
  int getEndOffset();
  
  String getHTMLRepresentation(RSyntaxTextArea paramRSyntaxTextArea);
  
  int getLanguageIndex();
  
  Token getLastNonCommentNonWhitespaceToken();
  
  Token getLastPaintableToken();
  
  String getLexeme();
  
  int getListOffset(RSyntaxTextArea paramRSyntaxTextArea, TabExpander paramTabExpander, float paramFloat1, float paramFloat2);
  
  Token getNextToken();
  
  int getOffset();
  
  int getOffsetBeforeX(RSyntaxTextArea paramRSyntaxTextArea, TabExpander paramTabExpander, float paramFloat1, float paramFloat2);
  
  char[] getTextArray();
  
  int getTextOffset();
  
  int getType();
  
  float getWidth(RSyntaxTextArea paramRSyntaxTextArea, TabExpander paramTabExpander, float paramFloat);
  
  float getWidthUpTo(int paramInt, RSyntaxTextArea paramRSyntaxTextArea, TabExpander paramTabExpander, float paramFloat);
  
  boolean is(char[] paramArrayOfchar);
  
  boolean is(int paramInt, char[] paramArrayOfchar);
  
  boolean is(int paramInt, String paramString);
  
  boolean isComment();
  
  boolean isCommentOrWhitespace();
  
  boolean isHyperlink();
  
  boolean isIdentifier();
  
  boolean isLeftCurly();
  
  boolean isRightCurly();
  
  boolean isPaintable();
  
  boolean isSingleChar(char paramChar);
  
  boolean isSingleChar(int paramInt, char paramChar);
  
  boolean isWhitespace();
  
  int length();
  
  Rectangle listOffsetToView(RSyntaxTextArea paramRSyntaxTextArea, TabExpander paramTabExpander, int paramInt1, int paramInt2, Rectangle paramRectangle);
  
  void setHyperlink(boolean paramBoolean);
  
  void setLanguageIndex(int paramInt);
  
  void setType(int paramInt);
  
  boolean startsWith(char[] paramArrayOfchar);
  
  int tokenToDocument(int paramInt);
}
