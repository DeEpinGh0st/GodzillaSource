package org.bouncycastle.openssl;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectParser;
import org.bouncycastle.util.io.pem.PemReader;

public class PEMParser extends PemReader {
  private final Map parsers = new HashMap<Object, Object>();
  
  public PEMParser(Reader paramReader) {
    super(paramReader);
    this.parsers.put("CERTIFICATE REQUEST", new PKCS10CertificationRequestParser());
    this.parsers.put("NEW CERTIFICATE REQUEST", new PKCS10CertificationRequestParser());
    this.parsers.put("CERTIFICATE", new X509CertificateParser());
    this.parsers.put("TRUSTED CERTIFICATE", new X509TrustedCertificateParser());
    this.parsers.put("X509 CERTIFICATE", new X509CertificateParser());
    this.parsers.put("X509 CRL", new X509CRLParser());
    this.parsers.put("PKCS7", new PKCS7Parser());
    this.parsers.put("CMS", new PKCS7Parser());
    this.parsers.put("ATTRIBUTE CERTIFICATE", new X509AttributeCertificateParser());
    this.parsers.put("EC PARAMETERS", new ECCurveParamsParser());
    this.parsers.put("PUBLIC KEY", new PublicKeyParser());
    this.parsers.put("RSA PUBLIC KEY", new RSAPublicKeyParser());
    this.parsers.put("RSA PRIVATE KEY", new KeyPairParser(new RSAKeyPairParser()));
    this.parsers.put("DSA PRIVATE KEY", new KeyPairParser(new DSAKeyPairParser()));
    this.parsers.put("EC PRIVATE KEY", new KeyPairParser(new ECDSAKeyPairParser()));
    this.parsers.put("ENCRYPTED PRIVATE KEY", new EncryptedPrivateKeyParser());
    this.parsers.put("PRIVATE KEY", new PrivateKeyParser());
  }
  
  public Object readObject() throws IOException {
    PemObject pemObject = readPemObject();
    if (pemObject != null) {
      String str = pemObject.getType();
      if (this.parsers.containsKey(str))
        return ((PemObjectParser)this.parsers.get(str)).parseObject(pemObject); 
      throw new IOException("unrecognised object: " + str);
    } 
    return null;
  }
  
  private class DSAKeyPairParser implements PEMKeyPairParser {
    private DSAKeyPairParser() {}
    
    public PEMKeyPair parse(byte[] param1ArrayOfbyte) throws IOException {
      try {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(param1ArrayOfbyte);
        if (aSN1Sequence.size() != 6)
          throw new PEMException("malformed sequence in DSA private key"); 
        ASN1Integer aSN1Integer1 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1));
        ASN1Integer aSN1Integer2 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(2));
        ASN1Integer aSN1Integer3 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(3));
        ASN1Integer aSN1Integer4 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(4));
        ASN1Integer aSN1Integer5 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(5));
        return new PEMKeyPair(new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)new DSAParameter(aSN1Integer1.getValue(), aSN1Integer2.getValue(), aSN1Integer3.getValue())), (ASN1Encodable)aSN1Integer4), new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)new DSAParameter(aSN1Integer1.getValue(), aSN1Integer2.getValue(), aSN1Integer3.getValue())), (ASN1Encodable)aSN1Integer5));
      } catch (IOException iOException) {
        throw iOException;
      } catch (Exception exception) {
        throw new PEMException("problem creating DSA private key: " + exception.toString(), exception);
      } 
    }
  }
  
  private class ECCurveParamsParser implements PemObjectParser {
    private ECCurveParamsParser() {}
    
    public Object parseObject(PemObject param1PemObject) throws IOException {
      try {
        ASN1Primitive aSN1Primitive = ASN1Primitive.fromByteArray(param1PemObject.getContent());
        return (aSN1Primitive instanceof org.bouncycastle.asn1.ASN1ObjectIdentifier) ? ASN1Primitive.fromByteArray(param1PemObject.getContent()) : ((aSN1Primitive instanceof ASN1Sequence) ? X9ECParameters.getInstance(aSN1Primitive) : null);
      } catch (IOException iOException) {
        throw iOException;
      } catch (Exception exception) {
        throw new PEMException("exception extracting EC named curve: " + exception.toString());
      } 
    }
  }
  
  private class ECDSAKeyPairParser implements PEMKeyPairParser {
    private ECDSAKeyPairParser() {}
    
    public PEMKeyPair parse(byte[] param1ArrayOfbyte) throws IOException {
      try {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(param1ArrayOfbyte);
        ECPrivateKey eCPrivateKey = ECPrivateKey.getInstance(aSN1Sequence);
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, (ASN1Encodable)eCPrivateKey.getParameters());
        PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)eCPrivateKey);
        SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier, eCPrivateKey.getPublicKey().getBytes());
        return new PEMKeyPair(subjectPublicKeyInfo, privateKeyInfo);
      } catch (IOException iOException) {
        throw iOException;
      } catch (Exception exception) {
        throw new PEMException("problem creating EC private key: " + exception.toString(), exception);
      } 
    }
  }
  
  private class EncryptedPrivateKeyParser implements PemObjectParser {
    public Object parseObject(PemObject param1PemObject) throws IOException {
      try {
        return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance(param1PemObject.getContent()));
      } catch (Exception exception) {
        throw new PEMException("problem parsing ENCRYPTED PRIVATE KEY: " + exception.toString(), exception);
      } 
    }
  }
  
  private class KeyPairParser implements PemObjectParser {
    private final PEMKeyPairParser pemKeyPairParser;
    
    public KeyPairParser(PEMKeyPairParser param1PEMKeyPairParser) {
      this.pemKeyPairParser = param1PEMKeyPairParser;
    }
    
    public Object parseObject(PemObject param1PemObject) throws IOException {
      boolean bool = false;
      String str = null;
      List list = param1PemObject.getHeaders();
      for (PemHeader pemHeader : list) {
        if (pemHeader.getName().equals("Proc-Type") && pemHeader.getValue().equals("4,ENCRYPTED")) {
          bool = true;
          continue;
        } 
        if (pemHeader.getName().equals("DEK-Info"))
          str = pemHeader.getValue(); 
      } 
      byte[] arrayOfByte = param1PemObject.getContent();
      try {
        if (bool) {
          StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
          String str1 = stringTokenizer.nextToken();
          byte[] arrayOfByte1 = Hex.decode(stringTokenizer.nextToken());
          return new PEMEncryptedKeyPair(str1, arrayOfByte1, arrayOfByte, this.pemKeyPairParser);
        } 
        return this.pemKeyPairParser.parse(arrayOfByte);
      } catch (IOException iOException) {
        if (bool)
          throw new PEMException("exception decoding - please check password and data.", iOException); 
        throw new PEMException(iOException.getMessage(), iOException);
      } catch (IllegalArgumentException illegalArgumentException) {
        if (bool)
          throw new PEMException("exception decoding - please check password and data.", illegalArgumentException); 
        throw new PEMException(illegalArgumentException.getMessage(), illegalArgumentException);
      } 
    }
  }
  
  private class PKCS10CertificationRequestParser implements PemObjectParser {
    private PKCS10CertificationRequestParser() {}
    
    public Object parseObject(PemObject param1PemObject) throws IOException {
      try {
        return new PKCS10CertificationRequest(param1PemObject.getContent());
      } catch (Exception exception) {
        throw new PEMException("problem parsing certrequest: " + exception.toString(), exception);
      } 
    }
  }
  
  private class PKCS7Parser implements PemObjectParser {
    private PKCS7Parser() {}
    
    public Object parseObject(PemObject param1PemObject) throws IOException {
      try {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(param1PemObject.getContent());
        return ContentInfo.getInstance(aSN1InputStream.readObject());
      } catch (Exception exception) {
        throw new PEMException("problem parsing PKCS7 object: " + exception.toString(), exception);
      } 
    }
  }
  
  private class PrivateKeyParser implements PemObjectParser {
    public Object parseObject(PemObject param1PemObject) throws IOException {
      try {
        return PrivateKeyInfo.getInstance(param1PemObject.getContent());
      } catch (Exception exception) {
        throw new PEMException("problem parsing PRIVATE KEY: " + exception.toString(), exception);
      } 
    }
  }
  
  private class PublicKeyParser implements PemObjectParser {
    public Object parseObject(PemObject param1PemObject) throws IOException {
      return SubjectPublicKeyInfo.getInstance(param1PemObject.getContent());
    }
  }
  
  private class RSAKeyPairParser implements PEMKeyPairParser {
    private RSAKeyPairParser() {}
    
    public PEMKeyPair parse(byte[] param1ArrayOfbyte) throws IOException {
      try {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(param1ArrayOfbyte);
        if (aSN1Sequence.size() != 9)
          throw new PEMException("malformed sequence in RSA private key"); 
        RSAPrivateKey rSAPrivateKey = RSAPrivateKey.getInstance(aSN1Sequence);
        RSAPublicKey rSAPublicKey = new RSAPublicKey(rSAPrivateKey.getModulus(), rSAPrivateKey.getPublicExponent());
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE);
        return new PEMKeyPair(new SubjectPublicKeyInfo(algorithmIdentifier, (ASN1Encodable)rSAPublicKey), new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)rSAPrivateKey));
      } catch (IOException iOException) {
        throw iOException;
      } catch (Exception exception) {
        throw new PEMException("problem creating RSA private key: " + exception.toString(), exception);
      } 
    }
  }
  
  private class RSAPublicKeyParser implements PemObjectParser {
    public Object parseObject(PemObject param1PemObject) throws IOException {
      try {
        RSAPublicKey rSAPublicKey = RSAPublicKey.getInstance(param1PemObject.getContent());
        return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE), (ASN1Encodable)rSAPublicKey);
      } catch (IOException iOException) {
        throw iOException;
      } catch (Exception exception) {
        throw new PEMException("problem extracting key: " + exception.toString(), exception);
      } 
    }
  }
  
  private class X509AttributeCertificateParser implements PemObjectParser {
    private X509AttributeCertificateParser() {}
    
    public Object parseObject(PemObject param1PemObject) throws IOException {
      return new X509AttributeCertificateHolder(param1PemObject.getContent());
    }
  }
  
  private class X509CRLParser implements PemObjectParser {
    private X509CRLParser() {}
    
    public Object parseObject(PemObject param1PemObject) throws IOException {
      try {
        return new X509CRLHolder(param1PemObject.getContent());
      } catch (Exception exception) {
        throw new PEMException("problem parsing cert: " + exception.toString(), exception);
      } 
    }
  }
  
  private class X509CertificateParser implements PemObjectParser {
    private X509CertificateParser() {}
    
    public Object parseObject(PemObject param1PemObject) throws IOException {
      try {
        return new X509CertificateHolder(param1PemObject.getContent());
      } catch (Exception exception) {
        throw new PEMException("problem parsing cert: " + exception.toString(), exception);
      } 
    }
  }
  
  private class X509TrustedCertificateParser implements PemObjectParser {
    private X509TrustedCertificateParser() {}
    
    public Object parseObject(PemObject param1PemObject) throws IOException {
      try {
        return new X509TrustedCertificateBlock(param1PemObject.getContent());
      } catch (Exception exception) {
        throw new PEMException("problem parsing cert: " + exception.toString(), exception);
      } 
    }
  }
}
