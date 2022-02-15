package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.util.Arrays;

public class GOST28147ParameterSpec implements AlgorithmParameterSpec {
  private byte[] iv = null;
  
  private byte[] sBox = null;
  
  private static Map oidMappings = new HashMap<Object, Object>();
  
  public GOST28147ParameterSpec(byte[] paramArrayOfbyte) {
    this.sBox = new byte[paramArrayOfbyte.length];
    System.arraycopy(paramArrayOfbyte, 0, this.sBox, 0, paramArrayOfbyte.length);
  }
  
  public GOST28147ParameterSpec(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this(paramArrayOfbyte1);
    this.iv = new byte[paramArrayOfbyte2.length];
    System.arraycopy(paramArrayOfbyte2, 0, this.iv, 0, paramArrayOfbyte2.length);
  }
  
  public GOST28147ParameterSpec(String paramString) {
    this.sBox = GOST28147Engine.getSBox(paramString);
  }
  
  public GOST28147ParameterSpec(String paramString, byte[] paramArrayOfbyte) {
    this(paramString);
    this.iv = new byte[paramArrayOfbyte.length];
    System.arraycopy(paramArrayOfbyte, 0, this.iv, 0, paramArrayOfbyte.length);
  }
  
  public GOST28147ParameterSpec(ASN1ObjectIdentifier paramASN1ObjectIdentifier, byte[] paramArrayOfbyte) {
    this(getName(paramASN1ObjectIdentifier));
    this.iv = Arrays.clone(paramArrayOfbyte);
  }
  
  public byte[] getSbox() {
    return Arrays.clone(this.sBox);
  }
  
  public byte[] getSBox() {
    return Arrays.clone(this.sBox);
  }
  
  public byte[] getIV() {
    return Arrays.clone(this.iv);
  }
  
  private static String getName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str = (String)oidMappings.get(paramASN1ObjectIdentifier);
    if (str == null)
      throw new IllegalArgumentException("unknown OID: " + paramASN1ObjectIdentifier); 
    return str;
  }
  
  static {
    oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, "E-A");
    oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet, "E-B");
    oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet, "E-C");
    oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet, "E-D");
  }
}
