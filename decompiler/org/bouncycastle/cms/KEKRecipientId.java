package org.bouncycastle.cms;

import org.bouncycastle.util.Arrays;

public class KEKRecipientId extends RecipientId {
  private byte[] keyIdentifier;
  
  public KEKRecipientId(byte[] paramArrayOfbyte) {
    super(1);
    this.keyIdentifier = paramArrayOfbyte;
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.keyIdentifier);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof KEKRecipientId))
      return false; 
    KEKRecipientId kEKRecipientId = (KEKRecipientId)paramObject;
    return Arrays.areEqual(this.keyIdentifier, kEKRecipientId.keyIdentifier);
  }
  
  public byte[] getKeyIdentifier() {
    return Arrays.clone(this.keyIdentifier);
  }
  
  public Object clone() {
    return new KEKRecipientId(this.keyIdentifier);
  }
  
  public boolean match(Object paramObject) {
    return (paramObject instanceof byte[]) ? Arrays.areEqual(this.keyIdentifier, (byte[])paramObject) : ((paramObject instanceof KEKRecipientInformation) ? ((KEKRecipientInformation)paramObject).getRID().equals(this) : false);
  }
}
