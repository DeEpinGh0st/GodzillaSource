package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public final class DERMacData {
  private final byte[] macData;
  
  private DERMacData(byte[] paramArrayOfbyte) {
    this.macData = paramArrayOfbyte;
  }
  
  public byte[] getMacData() {
    return Arrays.clone(this.macData);
  }
  
  public static final class Builder {
    private final DERMacData.Type type;
    
    private ASN1OctetString idU;
    
    private ASN1OctetString idV;
    
    private ASN1OctetString ephemDataU;
    
    private ASN1OctetString ephemDataV;
    
    private byte[] text;
    
    public Builder(DERMacData.Type param1Type, byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2, byte[] param1ArrayOfbyte3, byte[] param1ArrayOfbyte4) {
      this.type = param1Type;
      this.idU = DerUtil.getOctetString(param1ArrayOfbyte1);
      this.idV = DerUtil.getOctetString(param1ArrayOfbyte2);
      this.ephemDataU = DerUtil.getOctetString(param1ArrayOfbyte3);
      this.ephemDataV = DerUtil.getOctetString(param1ArrayOfbyte4);
    }
    
    public Builder withText(byte[] param1ArrayOfbyte) {
      this.text = DerUtil.toByteArray((ASN1Primitive)new DERTaggedObject(false, 0, (ASN1Encodable)DerUtil.getOctetString(param1ArrayOfbyte)));
      return this;
    }
    
    public DERMacData build() {
      switch (this.type) {
        case UNILATERALU:
        case BILATERALU:
          return new DERMacData(concatenate(this.type.getHeader(), DerUtil.toByteArray((ASN1Primitive)this.idU), DerUtil.toByteArray((ASN1Primitive)this.idV), DerUtil.toByteArray((ASN1Primitive)this.ephemDataU), DerUtil.toByteArray((ASN1Primitive)this.ephemDataV), this.text));
        case UNILATERALV:
        case BILATERALV:
          return new DERMacData(concatenate(this.type.getHeader(), DerUtil.toByteArray((ASN1Primitive)this.idV), DerUtil.toByteArray((ASN1Primitive)this.idU), DerUtil.toByteArray((ASN1Primitive)this.ephemDataV), DerUtil.toByteArray((ASN1Primitive)this.ephemDataU), this.text));
      } 
      throw new IllegalStateException("Unknown type encountered in build");
    }
    
    private byte[] concatenate(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2, byte[] param1ArrayOfbyte3, byte[] param1ArrayOfbyte4, byte[] param1ArrayOfbyte5, byte[] param1ArrayOfbyte6) {
      return Arrays.concatenate(Arrays.concatenate(param1ArrayOfbyte1, param1ArrayOfbyte2, param1ArrayOfbyte3), Arrays.concatenate(param1ArrayOfbyte4, param1ArrayOfbyte5, param1ArrayOfbyte6));
    }
  }
  
  public enum Type {
    UNILATERALU("KC_1_U"),
    UNILATERALV("KC_1_V"),
    BILATERALU("KC_2_U"),
    BILATERALV("KC_2_V");
    
    private final String enc;
    
    Type(String param1String1) {
      this.enc = param1String1;
    }
    
    public byte[] getHeader() {
      return Strings.toByteArray(this.enc);
    }
  }
}
