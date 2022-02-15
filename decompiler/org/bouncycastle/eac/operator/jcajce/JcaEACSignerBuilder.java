package org.bouncycastle.eac.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.eac.operator.EACSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorStreamException;
import org.bouncycastle.operator.RuntimeOperatorException;

public class JcaEACSignerBuilder {
  private static final Hashtable sigNames = new Hashtable<Object, Object>();
  
  private EACHelper helper = new DefaultEACHelper();
  
  public JcaEACSignerBuilder setProvider(String paramString) {
    this.helper = new NamedEACHelper(paramString);
    return this;
  }
  
  public JcaEACSignerBuilder setProvider(Provider paramProvider) {
    this.helper = new ProviderEACHelper(paramProvider);
    return this;
  }
  
  public EACSigner build(String paramString, PrivateKey paramPrivateKey) throws OperatorCreationException {
    return build((ASN1ObjectIdentifier)sigNames.get(paramString), paramPrivateKey);
  }
  
  public EACSigner build(final ASN1ObjectIdentifier usageOid, PrivateKey paramPrivateKey) throws OperatorCreationException {
    Signature signature;
    try {
      signature = this.helper.getSignature(usageOid);
      signature.initSign(paramPrivateKey);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new OperatorCreationException("unable to find algorithm: " + noSuchAlgorithmException.getMessage(), noSuchAlgorithmException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new OperatorCreationException("unable to find provider: " + noSuchProviderException.getMessage(), noSuchProviderException);
    } catch (InvalidKeyException invalidKeyException) {
      throw new OperatorCreationException("invalid key: " + invalidKeyException.getMessage(), invalidKeyException);
    } 
    final SignatureOutputStream sigStream = new SignatureOutputStream(signature);
    return new EACSigner() {
        public ASN1ObjectIdentifier getUsageIdentifier() {
          return usageOid;
        }
        
        public OutputStream getOutputStream() {
          return sigStream;
        }
        
        public byte[] getSignature() {
          try {
            byte[] arrayOfByte = sigStream.getSignature();
            return usageOid.on(EACObjectIdentifiers.id_TA_ECDSA) ? JcaEACSignerBuilder.reencode(arrayOfByte) : arrayOfByte;
          } catch (SignatureException signatureException) {
            throw new RuntimeOperatorException("exception obtaining signature: " + signatureException.getMessage(), signatureException);
          } 
        }
      };
  }
  
  public static int max(int paramInt1, int paramInt2) {
    return (paramInt1 > paramInt2) ? paramInt1 : paramInt2;
  }
  
  private static byte[] reencode(byte[] paramArrayOfbyte) {
    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramArrayOfbyte);
    BigInteger bigInteger1 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).getValue();
    BigInteger bigInteger2 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1)).getValue();
    byte[] arrayOfByte1 = bigInteger1.toByteArray();
    byte[] arrayOfByte2 = bigInteger2.toByteArray();
    int i = unsignedIntLength(arrayOfByte1);
    int j = unsignedIntLength(arrayOfByte2);
    int k = max(i, j);
    byte[] arrayOfByte3 = new byte[k * 2];
    Arrays.fill(arrayOfByte3, (byte)0);
    copyUnsignedInt(arrayOfByte1, arrayOfByte3, k - i);
    copyUnsignedInt(arrayOfByte2, arrayOfByte3, 2 * k - j);
    return arrayOfByte3;
  }
  
  private static int unsignedIntLength(byte[] paramArrayOfbyte) {
    int i = paramArrayOfbyte.length;
    if (paramArrayOfbyte[0] == 0)
      i--; 
    return i;
  }
  
  private static void copyUnsignedInt(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    int i = paramArrayOfbyte1.length;
    boolean bool = false;
    if (paramArrayOfbyte1[0] == 0) {
      i--;
      bool = true;
    } 
    System.arraycopy(paramArrayOfbyte1, bool, paramArrayOfbyte2, paramInt, i);
  }
  
  static {
    sigNames.put("SHA1withRSA", EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1);
    sigNames.put("SHA256withRSA", EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256);
    sigNames.put("SHA1withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1);
    sigNames.put("SHA256withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256);
    sigNames.put("SHA512withRSA", EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_512);
    sigNames.put("SHA512withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_512);
    sigNames.put("SHA1withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_1);
    sigNames.put("SHA224withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_224);
    sigNames.put("SHA256withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_256);
    sigNames.put("SHA384withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_384);
    sigNames.put("SHA512withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_512);
  }
  
  private class SignatureOutputStream extends OutputStream {
    private Signature sig;
    
    SignatureOutputStream(Signature param1Signature) {
      this.sig = param1Signature;
    }
    
    public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      try {
        this.sig.update(param1ArrayOfbyte, param1Int1, param1Int2);
      } catch (SignatureException signatureException) {
        throw new OperatorStreamException("exception in content signer: " + signatureException.getMessage(), signatureException);
      } 
    }
    
    public void write(byte[] param1ArrayOfbyte) throws IOException {
      try {
        this.sig.update(param1ArrayOfbyte);
      } catch (SignatureException signatureException) {
        throw new OperatorStreamException("exception in content signer: " + signatureException.getMessage(), signatureException);
      } 
    }
    
    public void write(int param1Int) throws IOException {
      try {
        this.sig.update((byte)param1Int);
      } catch (SignatureException signatureException) {
        throw new OperatorStreamException("exception in content signer: " + signatureException.getMessage(), signatureException);
      } 
    }
    
    byte[] getSignature() throws SignatureException {
      return this.sig.sign();
    }
  }
}
