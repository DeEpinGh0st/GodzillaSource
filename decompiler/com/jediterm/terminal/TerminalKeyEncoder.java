package com.jediterm.terminal;

import com.jediterm.terminal.ui.UIUtil;
import com.jediterm.terminal.util.CharUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;









public class TerminalKeyEncoder
{
  private static final int ESC = 27;
  private final Map<KeyCodeAndModifier, byte[]> myKeyCodes = (Map)new HashMap<>();
  
  private boolean myAltSendsEscape = true;
  private boolean myMetaSendsEscape = false;
  
  public TerminalKeyEncoder() {
    setAutoNewLine(false);
    arrowKeysApplicationSequences();
    keypadAnsiSequences();
    putCode(8, new int[] { 127 });
    putCode(112, new int[] { 27, 79, 80 });
    putCode(113, new int[] { 27, 79, 81 });
    putCode(114, new int[] { 27, 79, 82 });
    putCode(115, new int[] { 27, 79, 83 });
    putCode(116, new int[] { 27, 91, 49, 53, 126 });
    putCode(117, new int[] { 27, 91, 49, 55, 126 });
    putCode(118, new int[] { 27, 91, 49, 56, 126 });
    putCode(119, new int[] { 27, 91, 49, 57, 126 });
    putCode(120, new int[] { 27, 91, 50, 48, 126 });
    putCode(121, new int[] { 27, 91, 50, 49, 126 });
    putCode(122, new int[] { 27, 91, 50, 51, 126, 27 });
    putCode(123, new int[] { 27, 91, 50, 52, 126, 8 });
    
    putCode(155, new int[] { 27, 91, 50, 126 });
    putCode(127, new int[] { 27, 91, 51, 126 });
    
    putCode(33, new int[] { 27, 91, 53, 126 });
    putCode(34, new int[] { 27, 91, 54, 126 });
    
    putCode(36, new int[] { 27, 91, 72 });
    putCode(35, new int[] { 27, 91, 70 });
  }
  
  public void arrowKeysApplicationSequences() {
    putCode(38, new int[] { 27, 79, 65 });
    putCode(40, new int[] { 27, 79, 66 });
    putCode(39, new int[] { 27, 79, 67 });
    putCode(37, new int[] { 27, 79, 68 });
    
    if (UIUtil.isLinux) {
      putCode(new KeyCodeAndModifier(39, 2), new int[] { 27, 91, 49, 59, 53, 67 });
      putCode(new KeyCodeAndModifier(37, 2), new int[] { 27, 91, 49, 59, 53, 68 });
      putCode(new KeyCodeAndModifier(39, 8), new int[] { 27, 91, 49, 59, 51, 67 });
      putCode(new KeyCodeAndModifier(37, 8), new int[] { 27, 91, 49, 59, 51, 68 });
    } else {
      
      putCode(new KeyCodeAndModifier(39, 8), new int[] { 27, 102 });
      putCode(new KeyCodeAndModifier(37, 8), new int[] { 27, 98 });
    } 
  }
  
  public void arrowKeysAnsiCursorSequences() {
    putCode(38, new int[] { 27, 91, 65 });
    putCode(40, new int[] { 27, 91, 66 });
    putCode(39, new int[] { 27, 91, 67 });
    putCode(37, new int[] { 27, 91, 68 });
    if (UIUtil.isMac) {
      putCode(new KeyCodeAndModifier(39, 8), new int[] { 27, 102 });
      putCode(new KeyCodeAndModifier(37, 8), new int[] { 27, 98 });
    } 
  }
  
  public void keypadApplicationSequences() {
    putCode(225, new int[] { 27, 79, 66 });
    putCode(226, new int[] { 27, 79, 68 });
    putCode(227, new int[] { 27, 79, 67 });
    putCode(224, new int[] { 27, 79, 65 });
    
    putCode(36, new int[] { 27, 79, 72 });
    putCode(35, new int[] { 27, 79, 70 });
  }
  
  public void keypadAnsiSequences() {
    putCode(225, new int[] { 27, 91, 66 });
    putCode(226, new int[] { 27, 91, 68 });
    putCode(227, new int[] { 27, 91, 67 });
    putCode(224, new int[] { 27, 91, 65 });
    
    putCode(36, new int[] { 27, 91, 72 });
    putCode(35, new int[] { 27, 91, 70 });
  }
  
  void putCode(int code, int... bytesAsInt) {
    this.myKeyCodes.put(new KeyCodeAndModifier(code, 0), CharUtils.makeCode(bytesAsInt));
  }
  
  private void putCode(@NotNull KeyCodeAndModifier key, int... bytesAsInt) {
    if (key == null) $$$reportNull$$$0(0);  this.myKeyCodes.put(key, CharUtils.makeCode(bytesAsInt));
  }
  
  public byte[] getCode(int key, int modifiers) {
    byte[] bytes = this.myKeyCodes.get(new KeyCodeAndModifier(key, modifiers));
    if (bytes != null) {
      return bytes;
    }
    bytes = this.myKeyCodes.get(new KeyCodeAndModifier(key, 0));
    if (bytes == null) {
      return null;
    }
    
    if ((this.myAltSendsEscape || alwaysSendEsc(key)) && (modifiers & 0x8) != 0) {
      return insertCodeAt(bytes, CharUtils.makeCode(new int[] { 27 }, ), 0);
    }
    
    if ((this.myMetaSendsEscape || alwaysSendEsc(key)) && (modifiers & 0x4) != 0) {
      return insertCodeAt(bytes, CharUtils.makeCode(new int[] { 27 }, ), 0);
    }
    
    if (isCursorKey(key)) {
      return getCodeWithModifiers(bytes, modifiers);
    }
    
    return bytes;
  }
  
  private boolean alwaysSendEsc(int key) {
    return (isCursorKey(key) || key == 8);
  }
  
  private boolean isCursorKey(int key) {
    return (key == 40 || key == 38 || key == 37 || key == 39 || key == 36 || key == 35);
  }



  
  private byte[] getCodeWithModifiers(byte[] bytes, int modifiers) {
    int code = modifiersToCode(modifiers);
    
    if (code > 0) {
      return insertCodeAt(bytes, Integer.toString(code).getBytes(), bytes.length - 1);
    }
    return bytes;
  }
  
  private static byte[] insertCodeAt(byte[] bytes, byte[] code, int at) {
    byte[] res = new byte[bytes.length + code.length];
    System.arraycopy(bytes, 0, res, 0, bytes.length);
    System.arraycopy(bytes, at, res, at + code.length, bytes.length - at);
    System.arraycopy(code, 0, res, at, code.length);
    return res;
  }























  
  private static int modifiersToCode(int modifiers) {
    int code = 0;
    if ((modifiers & 0x1) != 0) {
      code |= 0x1;
    }
    if ((modifiers & 0x8) != 0) {
      code |= 0x2;
    }
    if ((modifiers & 0x2) != 0) {
      code |= 0x4;
    }
    if ((modifiers & 0x4) != 0) {
      code |= 0x8;
    }
    return (code != 0) ? (code + 1) : code;
  }
  
  public void setAutoNewLine(boolean enabled) {
    if (enabled) {
      putCode(10, new int[] { 13, 10 });
    } else {
      
      putCode(10, new int[] { 13 });
    } 
  }
  
  public void setAltSendsEscape(boolean altSendsEscape) {
    this.myAltSendsEscape = altSendsEscape;
  }
  
  public void setMetaSendsEscape(boolean metaSendsEscape) {
    this.myMetaSendsEscape = metaSendsEscape;
  }
  
  private static class KeyCodeAndModifier {
    private final int myCode;
    private final int myModifier;
    
    public KeyCodeAndModifier(int code, int modifier) {
      this.myCode = code;
      this.myModifier = modifier;
    }

    
    public boolean equals(Object o) {
      if (this == o) return true; 
      if (o == null || getClass() != o.getClass()) return false; 
      KeyCodeAndModifier that = (KeyCodeAndModifier)o;
      return (this.myCode == that.myCode && this.myModifier == that.myModifier);
    }

    
    public int hashCode() {
      return Objects.hash(new Object[] { Integer.valueOf(this.myCode), Integer.valueOf(this.myModifier) });
    }
  }
}
