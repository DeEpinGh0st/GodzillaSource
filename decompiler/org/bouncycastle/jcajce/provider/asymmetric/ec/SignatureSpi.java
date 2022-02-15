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
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.ECNRSigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.DSABase;
import org.bouncycastle.jcajce.provider.asymmetric.util.DSAEncoder;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.util.Arrays;

public class SignatureSpi extends DSABase {
  SignatureSpi(Digest paramDigest, DSA paramDSA, DSAEncoder paramDSAEncoder) {
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
  
  private static class PlainDSAEncoder implements DSAEncoder {
    private PlainDSAEncoder() {}
    
    public byte[] encode(BigInteger param1BigInteger1, BigInteger param1BigInteger2) throws IOException {
      byte[] arrayOfByte3;
      byte[] arrayOfByte1 = makeUnsigned(param1BigInteger1);
      byte[] arrayOfByte2 = makeUnsigned(param1BigInteger2);
      if (arrayOfByte1.length > arrayOfByte2.length) {
        arrayOfByte3 = new byte[arrayOfByte1.length * 2];
      } else {
        arrayOfByte3 = new byte[arrayOfByte2.length * 2];
      } 
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, arrayOfByte3.length / 2 - arrayOfByte1.length, arrayOfByte1.length);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte3.length - arrayOfByte2.length, arrayOfByte2.length);
      return arrayOfByte3;
    }
    
    private byte[] makeUnsigned(BigInteger param1BigInteger) {
      byte[] arrayOfByte = param1BigInteger.toByteArray();
      if (arrayOfByte[0] == 0) {
        byte[] arrayOfByte1 = new byte[arrayOfByte.length - 1];
        System.arraycopy(arrayOfByte, 1, arrayOfByte1, 0, arrayOfByte1.length);
        return arrayOfByte1;
      } 
      return arrayOfByte;
    }
    
    public BigInteger[] decode(byte[] param1ArrayOfbyte) throws IOException {
      BigInteger[] arrayOfBigInteger = new BigInteger[2];
      byte[] arrayOfByte1 = new byte[param1ArrayOfbyte.length / 2];
      byte[] arrayOfByte2 = new byte[param1ArrayOfbyte.length / 2];
      System.arraycopy(param1ArrayOfbyte, 0, arrayOfByte1, 0, arrayOfByte1.length);
      System.arraycopy(param1ArrayOfbyte, arrayOfByte1.length, arrayOfByte2, 0, arrayOfByte2.length);
      arrayOfBigInteger[0] = new BigInteger(1, arrayOfByte1);
      arrayOfBigInteger[1] = new BigInteger(1, arrayOfByte2);
      return arrayOfBigInteger;
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
  
  public static class ecCVCDSA extends SignatureSpi {
    public ecCVCDSA() {
      super(DigestFactory.createSHA1(), (DSA)new ECDSASigner(), new SignatureSpi.PlainDSAEncoder(null));
    }
  }
  
  public static class ecCVCDSA224 extends SignatureSpi {
    public ecCVCDSA224() {
      super(DigestFactory.createSHA224(), (DSA)new ECDSASigner(), new SignatureSpi.PlainDSAEncoder(null));
    }
  }
  
  public static class ecCVCDSA256 extends SignatureSpi {
    public ecCVCDSA256() {
      super(DigestFactory.createSHA256(), (DSA)new ECDSASigner(), new SignatureSpi.PlainDSAEncoder(null));
    }
  }
  
  public static class ecCVCDSA384 extends SignatureSpi {
    public ecCVCDSA384() {
      super(DigestFactory.createSHA384(), (DSA)new ECDSASigner(), new SignatureSpi.PlainDSAEncoder(null));
    }
  }
  
  public static class ecCVCDSA512 extends SignatureSpi {
    public ecCVCDSA512() {
      super(DigestFactory.createSHA512(), (DSA)new ECDSASigner(), new SignatureSpi.PlainDSAEncoder(null));
    }
  }
  
  public static class ecDSA extends SignatureSpi {
    public ecDSA() {
      super(DigestFactory.createSHA1(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDSA224 extends SignatureSpi {
    public ecDSA224() {
      super(DigestFactory.createSHA224(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDSA256 extends SignatureSpi {
    public ecDSA256() {
      super(DigestFactory.createSHA256(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDSA384 extends SignatureSpi {
    public ecDSA384() {
      super(DigestFactory.createSHA384(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDSA512 extends SignatureSpi {
    public ecDSA512() {
      super(DigestFactory.createSHA512(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDSARipeMD160 extends SignatureSpi {
    public ecDSARipeMD160() {
      super((Digest)new RIPEMD160Digest(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDSASha3_224 extends SignatureSpi {
    public ecDSASha3_224() {
      super(DigestFactory.createSHA3_224(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDSASha3_256 extends SignatureSpi {
    public ecDSASha3_256() {
      super(DigestFactory.createSHA3_256(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDSASha3_384 extends SignatureSpi {
    public ecDSASha3_384() {
      super(DigestFactory.createSHA3_384(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDSASha3_512 extends SignatureSpi {
    public ecDSASha3_512() {
      super(DigestFactory.createSHA3_512(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDSAnone extends SignatureSpi {
    public ecDSAnone() {
      super((Digest)new NullDigest(), (DSA)new ECDSASigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDetDSA extends SignatureSpi {
    public ecDetDSA() {
      super(DigestFactory.createSHA1(), (DSA)new ECDSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA1())), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDetDSA224 extends SignatureSpi {
    public ecDetDSA224() {
      super(DigestFactory.createSHA224(), (DSA)new ECDSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA224())), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDetDSA256 extends SignatureSpi {
    public ecDetDSA256() {
      super(DigestFactory.createSHA256(), (DSA)new ECDSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA256())), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDetDSA384 extends SignatureSpi {
    public ecDetDSA384() {
      super(DigestFactory.createSHA384(), (DSA)new ECDSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA384())), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDetDSA512 extends SignatureSpi {
    public ecDetDSA512() {
      super(DigestFactory.createSHA512(), (DSA)new ECDSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA512())), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDetDSASha3_224 extends SignatureSpi {
    public ecDetDSASha3_224() {
      super(DigestFactory.createSHA3_224(), (DSA)new ECDSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA3_224())), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDetDSASha3_256 extends SignatureSpi {
    public ecDetDSASha3_256() {
      super(DigestFactory.createSHA3_256(), (DSA)new ECDSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA3_256())), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDetDSASha3_384 extends SignatureSpi {
    public ecDetDSASha3_384() {
      super(DigestFactory.createSHA3_384(), (DSA)new ECDSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA3_384())), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecDetDSASha3_512 extends SignatureSpi {
    public ecDetDSASha3_512() {
      super(DigestFactory.createSHA3_512(), (DSA)new ECDSASigner((DSAKCalculator)new HMacDSAKCalculator(DigestFactory.createSHA3_512())), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecNR extends SignatureSpi {
    public ecNR() {
      super(DigestFactory.createSHA1(), (DSA)new ECNRSigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecNR224 extends SignatureSpi {
    public ecNR224() {
      super(DigestFactory.createSHA224(), (DSA)new ECNRSigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecNR256 extends SignatureSpi {
    public ecNR256() {
      super(DigestFactory.createSHA256(), (DSA)new ECNRSigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecNR384 extends SignatureSpi {
    public ecNR384() {
      super(DigestFactory.createSHA384(), (DSA)new ECNRSigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecNR512 extends SignatureSpi {
    public ecNR512() {
      super(DigestFactory.createSHA512(), (DSA)new ECNRSigner(), new SignatureSpi.StdDSAEncoder(null));
    }
  }
  
  public static class ecPlainDSARP160 extends SignatureSpi {
    public ecPlainDSARP160() {
      super((Digest)new RIPEMD160Digest(), (DSA)new ECDSASigner(), new SignatureSpi.PlainDSAEncoder(null));
    }
  }
}
