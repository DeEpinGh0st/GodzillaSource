package com.jgoodies.common.swing;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JLabel;

































































































































































public final class MnemonicUtils
{
  static final char MNEMONIC_MARKER = '&';
  
  public static void configure(AbstractButton target, String markedText) {
    Preconditions.checkNotNull(target, "The %1$s must not be null.", new Object[] { "target" });
    configure0(target, new MnemonicText(markedText, '&'));
  }












  
  public static void configure(Action target, String markedText) {
    Preconditions.checkNotNull(target, "The %1$s must not be null.", new Object[] { "target" });
    configure0(target, new MnemonicText(markedText, '&'));
  }












  
  public static void configure(JLabel target, String markedText) {
    Preconditions.checkNotNull(target, "The %1$s must not be null.", new Object[] { "target" });
    configure0(target, new MnemonicText(markedText, '&'));
  }























  
  public static String plainText(String markedText) {
    return (new MnemonicText(markedText, '&')).text;
  }









  
  static int mnemonic(String markedText) {
    return (new MnemonicText(markedText, '&')).key;
  }








  
  static int mnemonicIndex(String markedText) {
    return (new MnemonicText(markedText, '&')).index;
  }



  
  private static void configure0(AbstractButton button, MnemonicText mnemonicText) {
    button.setText(mnemonicText.text);
    button.setMnemonic(mnemonicText.key);
    button.setDisplayedMnemonicIndex(mnemonicText.index);
  }









  
  private static void configure0(Action action, MnemonicText mnemonicText) {
    Integer keyValue = Integer.valueOf(mnemonicText.key);
    Integer indexValue = (mnemonicText.index == -1) ? null : Integer.valueOf(mnemonicText.index);

    
    action.putValue("Name", mnemonicText.text);
    action.putValue("MnemonicKey", keyValue);
    action.putValue("SwingDisplayedMnemonicIndexKey", indexValue);
  }
  
  private static void configure0(JLabel label, MnemonicText mnemonicText) {
    label.setText(mnemonicText.text);
    label.setDisplayedMnemonic(mnemonicText.key);
    label.setDisplayedMnemonicIndex(mnemonicText.index);
  }

  
  private static final class MnemonicText
  {
    String text;
    
    int key;
    
    int index;
    
    private MnemonicText(String markedText, char marker) {
      int i;
      if (markedText == null || markedText.length() <= 1 || (i = markedText.indexOf(marker)) == -1) {

        
        this.text = markedText;
        this.key = 0;
        this.index = -1;
        return;
      } 
      boolean html = Strings.startsWithIgnoreCase(markedText, "<html>");
      StringBuilder builder = new StringBuilder();
      int begin = 0;
      int quotedMarkers = 0;
      int markerIndex = -1;
      boolean marked = false;
      char markedChar = Character.MIN_VALUE;
      CharacterIterator sci = new StringCharacterIterator(markedText);
      do {
        builder.append(markedText.substring(begin, i));
        char current = sci.setIndex(i);
        char next = sci.next();
        if (html) {
          int entityEnd = indexOfEntityEnd(markedText, i);
          if (entityEnd == -1) {
            marked = true;
            builder.append("<u>").append(next).append("</u>");
            begin = i + 2;
            markedChar = next;
          } else {
            builder.append(markedText.substring(i, entityEnd));
            begin = entityEnd;
          }
        
        } else if (next == marker) {
          builder.append(next);
          begin = i + 2;
          quotedMarkers++;
        } else if (Character.isWhitespace(next)) {
          builder.append(current).append(next);
          begin = i + 2;
        } else if (next == Character.MAX_VALUE) {
          builder.append(current);
          begin = i + 2;
        } else {
          builder.append(next);
          begin = i + 2;
          markerIndex = i - quotedMarkers;
          marked = true;
          markedChar = next;
        } 
        
        i = markedText.indexOf(marker, begin);
      } while (i != -1 && !marked);
      if (begin < markedText.length()) {
        builder.append(markedText.substring(begin));
      }
      this.text = builder.toString();
      this.index = markerIndex;
      if (marked) {
        this.key = mnemonicKey(markedChar);
      } else {
        this.key = 0;
      } 
    }
    
    private static int indexOfEntityEnd(String htmlText, int start) {
      CharacterIterator sci = new StringCharacterIterator(htmlText, start);
      
      while (true) {
        char c = sci.next();
        if (c == ';') {
          return sci.getIndex();
        }
        if (!Character.isLetterOrDigit(c)) {
          return -1;
        }
        if (c == Character.MAX_VALUE) {
          return -1;
        }
      } 
    }



    
    private static int mnemonicKey(char c) {
      int vk = c;
      if (vk >= 97 && vk <= 122) {
        vk -= 32;
      }
      return vk;
    }
  }
}
