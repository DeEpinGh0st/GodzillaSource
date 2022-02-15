package com.jediterm.terminal;

public interface Questioner {
  String questionVisible(String paramString1, String paramString2);
  
  String questionHidden(String paramString);
  
  void showMessage(String paramString);
}
