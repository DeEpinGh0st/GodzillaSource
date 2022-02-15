package org.bouncycastle.crypto.signers;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class DSADigestSigner implements Signer {
  private final Digest digest;
  
  private final DSA dsaSigner;
  
  private boolean forSigning;
  
  public DSADigestSigner(DSA paramDSA, Digest paramDigest) {
    this.digest = paramDigest;
    this.dsaSigner = paramDSA;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    AsymmetricKeyParameter asymmetricKeyParameter;
    this.forSigning = paramBoolean;
    if (paramCipherParameters instanceof ParametersWithRandom) {
      asymmetricKeyParameter = (AsymmetricKeyParameter)((ParametersWithRandom)paramCipherParameters).getParameters();
    } else {
      asymmetricKeyParameter = (AsymmetricKeyParameter)paramCipherParameters;
    } 
    if (paramBoolean && !asymmetricKeyParameter.isPrivate())
      throw new IllegalArgumentException("Signing Requires Private Key."); 
    if (!paramBoolean && asymmetricKeyParameter.isPrivate())
      throw new IllegalArgumentException("Verification Requires Public Key."); 
    reset();
    this.dsaSigner.init(paramBoolean, paramCipherParameters);
  }
  
  public void update(byte paramByte) {
    this.digest.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public byte[] generateSignature() {
    if (!this.forSigning)
      throw new IllegalStateException("DSADigestSigner not initialised for signature generation."); 
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    BigInteger[] arrayOfBigInteger = this.dsaSigner.generateSignature(arrayOfByte);
    try {
      return derEncode(arrayOfBigInteger[0], arrayOfBigInteger[1]);
    } catch (IOException iOException) {
      throw new IllegalStateException("unable to encode signature");
    } 
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte) {
    if (this.forSigning)
      throw new IllegalStateException("DSADigestSigner not initialised for verification"); 
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    try {
      BigInteger[] arrayOfBigInteger = derDecode(paramArrayOfbyte);
      return this.dsaSigner.verifySignature(arrayOfByte, arrayOfBigInteger[0], arrayOfBigInteger[1]);
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  public void reset() {
    this.digest.reset();
  }
  
  private byte[] derEncode(BigInteger paramBigInteger1, BigInteger paramBigInteger2) throws IOException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(paramBigInteger1));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(paramBigInteger2));
    return (new DERSequence(aSN1EncodableVector)).getEncoded("DER");
  }
  
  private BigInteger[] derDecode(byte[] paramArrayOfbyte) throws IOException {
    ASN1Sequence aSN1Sequence = (ASN1Sequence)ASN1Primitive.fromByteArray(paramArrayOfbyte);
    return new BigInteger[] { ((ASN1Integer)aSN1Sequence.getObjectAt(0)).getValue(), ((ASN1Integer)aSN1Sequence.getObjectAt(1)).getValue() };
  }
}
