package com.jediterm.terminal.ui;

import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.Terminal;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PreConnectHandler
  implements Questioner, KeyListener {
  private Object mySync = new Object();
  private Terminal myTerminal;
  private StringBuffer myAnswer;
  private boolean myVisible;
  
  public PreConnectHandler(Terminal terminal) {
    this.myTerminal = terminal;
    this.myVisible = true;
  }


  
  public String questionHidden(String question) {
    this.myVisible = false;
    String answer = questionVisible(question, null);
    this.myVisible = true;
    return answer;
  }
  
  public String questionVisible(String question, String defValue) {
    synchronized (this.mySync) {
      this.myTerminal.writeUnwrappedString(question);
      this.myAnswer = new StringBuffer();
      if (defValue != null) {
        this.myAnswer.append(defValue);
        this.myTerminal.writeUnwrappedString(defValue);
      } 
      try {
        this.mySync.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } 
      String answerStr = this.myAnswer.toString();
      this.myAnswer = null;
      return answerStr;
    } 
  }
  
  public void showMessage(String message) {
    this.myTerminal.writeUnwrappedString(message);
    this.myTerminal.nextLine();
  }
  
  public void keyPressed(KeyEvent e) {
    if (this.myAnswer == null)
      return;  synchronized (this.mySync) {
      boolean release = false;
      
      switch (e.getKeyCode()) {
        case 8:
          if (this.myAnswer.length() > 0) {
            this.myTerminal.backspace();
            this.myTerminal.eraseInLine(0);
            this.myAnswer.deleteCharAt(this.myAnswer.length() - 1);
          } 
          break;
        case 10:
          this.myTerminal.nextLine();
          release = true;
          break;
      } 
      
      if (release) this.mySync.notifyAll();
    
    } 
  }

  
  public void keyReleased(KeyEvent e) {}

  
  public void keyTyped(KeyEvent e) {
    if (this.myAnswer == null)
      return;  char c = e.getKeyChar();
    if (Character.getType(c) != 15) {
      if (this.myVisible) this.myTerminal.writeCharacters(Character.toString(c)); 
      this.myAnswer.append(c);
    } 
  }
}
