package org.bouncycastle.crypto.agreement.kdf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Pack;

public class DHKEKGenerator implements DerivationFunction {
  private final Digest digest;
  
  private ASN1ObjectIdentifier algorithm;
  
  private int keySize;
  
  private byte[] z;
  
  private byte[] partyAInfo;
  
  public DHKEKGenerator(Digest paramDigest) {
    this.digest = paramDigest;
  }
  
  public void init(DerivationParameters paramDerivationParameters) {
    DHKDFParameters dHKDFParameters = (DHKDFParameters)paramDerivationParameters;
    this.algorithm = dHKDFParameters.getAlgorithm();
    this.keySize = dHKDFParameters.getKeySize();
    this.z = dHKDFParameters.getZ();
    this.partyAInfo = dHKDFParameters.getExtraInfo();
  }
  
  public Digest getDigest() {
    return this.digest;
  }
  
  public int generateBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalArgumentException {
    if (paramArrayOfbyte.length - paramInt2 < paramInt1)
      throw new OutputLengthException("output buffer too small"); 
    long l = paramInt2;
    int i = this.digest.getDigestSize();
    if (l > 8589934591L)
      throw new IllegalArgumentException("Output length too large"); 
    int j = (int)((l + i - 1L) / i);
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    byte b1 = 1;
    for (byte b2 = 0; b2 < j; b2++) {
      this.digest.update(this.z, 0, this.z.length);
      ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
      ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
      aSN1EncodableVector2.add((ASN1Encodable)this.algorithm);
      aSN1EncodableVector2.add((ASN1Encodable)new DEROctetString(Pack.intToBigEndian(b1)));
      aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector2));
      if (this.partyAInfo != null)
        aSN1EncodableVector1.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DEROctetString(this.partyAInfo))); 
      aSN1EncodableVector1.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)new DEROctetString(Pack.intToBigEndian(this.keySize))));
      try {
        byte[] arrayOfByte1 = (new DERSequence(aSN1EncodableVector1)).getEncoded("DER");
        this.digest.update(arrayOfByte1, 0, arrayOfByte1.length);
      } catch (IOException iOException) {
        throw new IllegalArgumentException("unable to encode parameter info: " + iOException.getMessage());
      } 
      this.digest.doFinal(arrayOfByte, 0);
      if (paramInt2 > i) {
        System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt1, i);
        paramInt1 += i;
        paramInt2 -= i;
      } else {
        System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt1, paramInt2);
      } 
      b1++;
    } 
    this.digest.reset();
    return (int)l;
  }
}
