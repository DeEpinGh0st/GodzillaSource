package com.kitfox.svg.animation.parser;

import java.io.IOException;

public interface CharStream {
  char readChar() throws IOException;
  
  int getBeginColumn();
  
  int getBeginLine();
  
  int getEndColumn();
  
  int getEndLine();
  
  void backup(int paramInt);
  
  char beginToken() throws IOException;
  
  String getImage();
  
  char[] getSuffix(int paramInt);
  
  void done();
  
  void setTabSize(int paramInt);
  
  int getTabSize();
  
  void setTrackLineColumn(boolean paramBoolean);
  
  boolean isTrackLineColumn();
}
