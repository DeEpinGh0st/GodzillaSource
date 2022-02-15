package org.bouncycastle.pqc.jcajce.provider.newhope;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.jcajce.interfaces.NHPrivateKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class BCNHPrivateKey implements NHPrivateKey {
  private static final long serialVersionUID = 1L;
  
  private final NHPrivateKeyParameters params;
  
  public BCNHPrivateKey(NHPrivateKeyParameters paramNHPrivateKeyParameters) {
    this.params = paramNHPrivateKeyParameters;
  }
  
  public BCNHPrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    this.params = new NHPrivateKeyParameters(convert(ASN1OctetString.getInstance(paramPrivateKeyInfo.parsePrivateKey()).getOctets()));
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof BCNHPrivateKey))
      return false; 
    BCNHPrivateKey bCNHPrivateKey = (BCNHPrivateKey)paramObject;
    return Arrays.areEqual(this.params.getSecData(), bCNHPrivateKey.params.getSecData());
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.params.getSecData());
  }
  
  public final String getAlgorithm() {
    return "NH";
  }
  
  public byte[] getEncoded() {
    try {
      AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
      short[] arrayOfShort = this.params.getSecData();
      byte[] arrayOfByte = new byte[arrayOfShort.length * 2];
      for (byte b = 0; b != arrayOfShort.length; b++)
        Pack.shortToLittleEndian(arrayOfShort[b], arrayOfByte, b * 2); 
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)new DEROctetString(arrayOfByte));
      return privateKeyInfo.getEncoded();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public short[] getSecretData() {
    return this.params.getSecData();
  }
  
  CipherParameters getKeyParams() {
    return (CipherParameters)this.params;
  }
  
  private static short[] convert(byte[] paramArrayOfbyte) {
    short[] arrayOfShort = new short[paramArrayOfbyte.length / 2];
    for (byte b = 0; b != arrayOfShort.length; b++)
      arrayOfShort[b] = Pack.littleEndianToShort(paramArrayOfbyte, b * 2); 
    return arrayOfShort;
  }
}
