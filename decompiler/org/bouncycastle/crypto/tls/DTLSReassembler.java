package org.bouncycastle.crypto.tls;

import java.util.Vector;

class DTLSReassembler {
  private short msg_type;
  
  private byte[] body;
  
  private Vector missing = new Vector();
  
  DTLSReassembler(short paramShort, int paramInt) {
    this.msg_type = paramShort;
    this.body = new byte[paramInt];
    this.missing.addElement(new Range(0, paramInt));
  }
  
  short getMsgType() {
    return this.msg_type;
  }
  
  byte[] getBodyIfComplete() {
    return this.missing.isEmpty() ? this.body : null;
  }
  
  void contributeFragment(short paramShort, int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt3 + paramInt4;
    if (this.msg_type != paramShort || this.body.length != paramInt1 || i > paramInt1)
      return; 
    if (paramInt4 == 0) {
      if (paramInt3 == 0 && !this.missing.isEmpty()) {
        Range range = this.missing.firstElement();
        if (range.getEnd() == 0)
          this.missing.removeElementAt(0); 
      } 
      return;
    } 
    for (byte b = 0; b < this.missing.size(); b++) {
      Range range = this.missing.elementAt(b);
      if (range.getStart() >= i)
        break; 
      if (range.getEnd() > paramInt3) {
        int j = Math.max(range.getStart(), paramInt3);
        int k = Math.min(range.getEnd(), i);
        int m = k - j;
        System.arraycopy(paramArrayOfbyte, paramInt2 + j - paramInt3, this.body, j, m);
        if (j == range.getStart()) {
          if (k == range.getEnd()) {
            this.missing.removeElementAt(b--);
          } else {
            range.setStart(k);
          } 
        } else {
          if (k != range.getEnd())
            this.missing.insertElementAt(new Range(k, range.getEnd()), ++b); 
          range.setEnd(j);
        } 
      } 
    } 
  }
  
  void reset() {
    this.missing.removeAllElements();
    this.missing.addElement(new Range(0, this.body.length));
  }
  
  private static class Range {
    private int start;
    
    private int end;
    
    Range(int param1Int1, int param1Int2) {
      this.start = param1Int1;
      this.end = param1Int2;
    }
    
    public int getStart() {
      return this.start;
    }
    
    public void setStart(int param1Int) {
      this.start = param1Int;
    }
    
    public int getEnd() {
      return this.end;
    }
    
    public void setEnd(int param1Int) {
      this.end = param1Int;
    }
  }
}
