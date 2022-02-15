package org.bouncycastle.cms;

public class PasswordRecipientId extends RecipientId {
  public PasswordRecipientId() {
    super(3);
  }
  
  public int hashCode() {
    return 3;
  }
  
  public boolean equals(Object paramObject) {
    return !!(paramObject instanceof PasswordRecipientId);
  }
  
  public Object clone() {
    return new PasswordRecipientId();
  }
  
  public boolean match(Object paramObject) {
    return (paramObject instanceof PasswordRecipientInformation);
  }
}
