package org.bouncycastle.cms;

import org.bouncycastle.util.Selector;

public abstract class RecipientId implements Selector {
  public static final int keyTrans = 0;
  
  public static final int kek = 1;
  
  public static final int keyAgree = 2;
  
  public static final int password = 3;
  
  private final int type;
  
  protected RecipientId(int paramInt) {
    this.type = paramInt;
  }
  
  public int getType() {
    return this.type;
  }
  
  public abstract Object clone();
}
