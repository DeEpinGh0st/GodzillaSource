package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.DSABase;
import org.bouncycastle.jcajce.provider.asymmetric.util.DSAEncoder;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.util.Arrays;

public class GMSignatureSpi extends DSABase {
  GMSignatureSpi(Digest paramDigest, DSA paramDSA, DSAEncoder paramDSAEncoder) {
    super(paramDigest, paramDSA, paramDSAEncoder);
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    AsymmetricKeyParameter asymmetricKeyParameter = ECUtils.generatePublicKeyParameter(paramPublicKey);
    this.digest.reset();
    this.signer.init(false, (CipherParameters)asymmetricKeyParameter);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    AsymmetricKeyParameter asymmetricKeyParameter = ECUtil.generatePrivateKeyParameter(paramPrivateKey);
    this.digest.reset();
    if (this.appRandom != null) {
      this.signer.init(true, (CipherParameters)new ParametersWithRandom((CipherParameters)asymmetricKeyParameter, this.appRandom));
    } else {
      this.signer.init(true, (CipherParameters)asymmetricKeyParameter);
    } 
  }
  
  private static class StdDSAEncoder implements DSAEncoder {
    private StdDSAEncoder() {}
    
    public byte[] encode(BigInteger param1BigInteger1, BigInteger param1BigInteger2) throws IOException {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(param1BigInteger1));
      aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(param1BigInteger2));
      return (new DERSequence(aSN1EncodableVector)).getEncoded("DER");
    }
    
    public BigInteger[] decode(byte[] param1ArrayOfbyte) throws IOException {
      ASN1Sequence aSN1Sequence = (ASN1Sequence)ASN1Primitive.fromByteArray(param1ArrayOfbyte);
      if (aSN1Sequence.size() != 2)
        throw new IOException("malformed signature"); 
      if (!Arrays.areEqual(param1ArrayOfbyte, aSN1Sequence.getEncoded("DER")))
        throw new IOException("malformed signature"); 
      BigInteger[] arrayOfBigInteger = new BigInteger[2];
      arrayOfBigInteger[0] = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).getValue();
      arrayOfBigInteger[1] = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1)).getValue();
      return arrayOfBigInteger;
    }
  }
  
  public static class sm3WithSM2 extends GMSignatureSpi {
    public sm3WithSM2() {
      super((Digest)new SM3Digest(), (DSA)new SM2Signer(), new GMSignatureSpi.StdDSAEncoder(null));
    }
  }
}
