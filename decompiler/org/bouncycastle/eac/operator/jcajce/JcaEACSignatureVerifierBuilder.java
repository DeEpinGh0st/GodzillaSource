package org.bouncycastle.eac.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.eac.operator.EACSignatureVerifier;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorStreamException;
import org.bouncycastle.operator.RuntimeOperatorException;

public class JcaEACSignatureVerifierBuilder {
  private EACHelper helper = new DefaultEACHelper();
  
  public JcaEACSignatureVerifierBuilder setProvider(String paramString) {
    this.helper = new NamedEACHelper(paramString);
    return this;
  }
  
  public JcaEACSignatureVerifierBuilder setProvider(Provider paramProvider) {
    this.helper = new ProviderEACHelper(paramProvider);
    return this;
  }
  
  public EACSignatureVerifier build(final ASN1ObjectIdentifier usageOid, PublicKey paramPublicKey) throws OperatorCreationException {
    Signature signature;
    try {
      signature = this.helper.getSignature(usageOid);
      signature.initVerify(paramPublicKey);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new OperatorCreationException("unable to find algorithm: " + noSuchAlgorithmException.getMessage(), noSuchAlgorithmException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new OperatorCreationException("unable to find provider: " + noSuchProviderException.getMessage(), noSuchProviderException);
    } catch (InvalidKeyException invalidKeyException) {
      throw new OperatorCreationException("invalid key: " + invalidKeyException.getMessage(), invalidKeyException);
    } 
    final SignatureOutputStream sigStream = new SignatureOutputStream(signature);
    return new EACSignatureVerifier() {
        public ASN1ObjectIdentifier getUsageIdentifier() {
          return usageOid;
        }
        
        public OutputStream getOutputStream() {
          return sigStream;
        }
        
        public boolean verify(byte[] param1ArrayOfbyte) {
          try {
            if (usageOid.on(EACObjectIdentifiers.id_TA_ECDSA))
              try {
                byte[] arrayOfByte = JcaEACSignatureVerifierBuilder.derEncode(param1ArrayOfbyte);
                return sigStream.verify(arrayOfByte);
              } catch (Exception exception) {
                return false;
              }  
            return sigStream.verify(param1ArrayOfbyte);
          } catch (SignatureException signatureException) {
            throw new RuntimeOperatorException("exception obtaining signature: " + signatureException.getMessage(), signatureException);
          } 
        }
      };
  }
  
  private static byte[] derEncode(byte[] paramArrayOfbyte) throws IOException {
    int i = paramArrayOfbyte.length / 2;
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte1, 0, i);
    System.arraycopy(paramArrayOfbyte, i, arrayOfByte2, 0, i);
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(new BigInteger(1, arrayOfByte1)));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(new BigInteger(1, arrayOfByte2)));
    DERSequence dERSequence = new DERSequence(aSN1EncodableVector);
    return dERSequence.getEncoded();
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
    
    boolean verify(byte[] param1ArrayOfbyte) throws SignatureException {
      return this.sig.verify(param1ArrayOfbyte);
    }
  }
}
