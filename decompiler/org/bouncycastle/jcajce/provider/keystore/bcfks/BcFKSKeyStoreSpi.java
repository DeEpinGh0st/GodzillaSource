package org.bouncycastle.jcajce.provider.keystore.bcfks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bc.EncryptedObjectStoreData;
import org.bouncycastle.asn1.bc.EncryptedPrivateKeyData;
import org.bouncycastle.asn1.bc.EncryptedSecretKeyData;
import org.bouncycastle.asn1.bc.ObjectData;
import org.bouncycastle.asn1.bc.ObjectDataSequence;
import org.bouncycastle.asn1.bc.ObjectStore;
import org.bouncycastle.asn1.bc.ObjectStoreData;
import org.bouncycastle.asn1.bc.ObjectStoreIntegrityCheck;
import org.bouncycastle.asn1.bc.PbkdMacIntegrityCheck;
import org.bouncycastle.asn1.bc.SecretKeyData;
import org.bouncycastle.asn1.cms.CCMParameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

class BcFKSKeyStoreSpi extends KeyStoreSpi {
  private static final Map<String, ASN1ObjectIdentifier> oidMap = new HashMap<String, ASN1ObjectIdentifier>();
  
  private static final Map<ASN1ObjectIdentifier, String> publicAlgMap = new HashMap<ASN1ObjectIdentifier, String>();
  
  private static final BigInteger CERTIFICATE = BigInteger.valueOf(0L);
  
  private static final BigInteger PRIVATE_KEY = BigInteger.valueOf(1L);
  
  private static final BigInteger SECRET_KEY = BigInteger.valueOf(2L);
  
  private static final BigInteger PROTECTED_PRIVATE_KEY = BigInteger.valueOf(3L);
  
  private static final BigInteger PROTECTED_SECRET_KEY = BigInteger.valueOf(4L);
  
  private final BouncyCastleProvider provider;
  
  private final Map<String, ObjectData> entries = new HashMap<String, ObjectData>();
  
  private final Map<String, PrivateKey> privateKeyCache = new HashMap<String, PrivateKey>();
  
  private AlgorithmIdentifier hmacAlgorithm;
  
  private KeyDerivationFunc hmacPkbdAlgorithm;
  
  private Date creationDate;
  
  private Date lastModifiedDate;
  
  private static String getPublicKeyAlg(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str = publicAlgMap.get(paramASN1ObjectIdentifier);
    return (str != null) ? str : paramASN1ObjectIdentifier.getId();
  }
  
  BcFKSKeyStoreSpi(BouncyCastleProvider paramBouncyCastleProvider) {
    this.provider = paramBouncyCastleProvider;
  }
  
  public Key engineGetKey(String paramString, char[] paramArrayOfchar) throws NoSuchAlgorithmException, UnrecoverableKeyException {
    ObjectData objectData = this.entries.get(paramString);
    if (objectData != null) {
      if (objectData.getType().equals(PRIVATE_KEY) || objectData.getType().equals(PROTECTED_PRIVATE_KEY)) {
        PrivateKey privateKey = this.privateKeyCache.get(paramString);
        if (privateKey != null)
          return privateKey; 
        EncryptedPrivateKeyData encryptedPrivateKeyData = EncryptedPrivateKeyData.getInstance(objectData.getData());
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = EncryptedPrivateKeyInfo.getInstance(encryptedPrivateKeyData.getEncryptedPrivateKeyInfo());
        try {
          KeyFactory keyFactory;
          PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(decryptData("PRIVATE_KEY_ENCRYPTION", encryptedPrivateKeyInfo.getEncryptionAlgorithm(), paramArrayOfchar, encryptedPrivateKeyInfo.getEncryptedData()));
          if (this.provider != null) {
            keyFactory = KeyFactory.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm().getId(), (Provider)this.provider);
          } else {
            keyFactory = KeyFactory.getInstance(getPublicKeyAlg(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm()));
          } 
          PrivateKey privateKey1 = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded()));
          this.privateKeyCache.put(paramString, privateKey1);
          return privateKey1;
        } catch (Exception exception) {
          throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover private key (" + paramString + "): " + exception.getMessage());
        } 
      } 
      if (objectData.getType().equals(SECRET_KEY) || objectData.getType().equals(PROTECTED_SECRET_KEY)) {
        EncryptedSecretKeyData encryptedSecretKeyData = EncryptedSecretKeyData.getInstance(objectData.getData());
        try {
          SecretKeyFactory secretKeyFactory;
          SecretKeyData secretKeyData = SecretKeyData.getInstance(decryptData("SECRET_KEY_ENCRYPTION", encryptedSecretKeyData.getKeyEncryptionAlgorithm(), paramArrayOfchar, encryptedSecretKeyData.getEncryptedKeyData()));
          if (this.provider != null) {
            secretKeyFactory = SecretKeyFactory.getInstance(secretKeyData.getKeyAlgorithm().getId(), (Provider)this.provider);
          } else {
            secretKeyFactory = SecretKeyFactory.getInstance(secretKeyData.getKeyAlgorithm().getId());
          } 
          return secretKeyFactory.generateSecret(new SecretKeySpec(secretKeyData.getKeyBytes(), secretKeyData.getKeyAlgorithm().getId()));
        } catch (Exception exception) {
          throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + paramString + "): " + exception.getMessage());
        } 
      } 
      throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + paramString + "): type not recognized");
    } 
    return null;
  }
  
  public Certificate[] engineGetCertificateChain(String paramString) {
    ObjectData objectData = this.entries.get(paramString);
    if (objectData != null && (objectData.getType().equals(PRIVATE_KEY) || objectData.getType().equals(PROTECTED_PRIVATE_KEY))) {
      EncryptedPrivateKeyData encryptedPrivateKeyData = EncryptedPrivateKeyData.getInstance(objectData.getData());
      Certificate[] arrayOfCertificate = encryptedPrivateKeyData.getCertificateChain();
      X509Certificate[] arrayOfX509Certificate = new X509Certificate[arrayOfCertificate.length];
      for (byte b = 0; b != arrayOfX509Certificate.length; b++)
        arrayOfX509Certificate[b] = (X509Certificate)decodeCertificate(arrayOfCertificate[b]); 
      return (Certificate[])arrayOfX509Certificate;
    } 
    return null;
  }
  
  public Certificate engineGetCertificate(String paramString) {
    ObjectData objectData = this.entries.get(paramString);
    if (objectData != null) {
      if (objectData.getType().equals(PRIVATE_KEY) || objectData.getType().equals(PROTECTED_PRIVATE_KEY)) {
        EncryptedPrivateKeyData encryptedPrivateKeyData = EncryptedPrivateKeyData.getInstance(objectData.getData());
        Certificate[] arrayOfCertificate = encryptedPrivateKeyData.getCertificateChain();
        return decodeCertificate(arrayOfCertificate[0]);
      } 
      if (objectData.getType().equals(CERTIFICATE))
        return decodeCertificate(objectData.getData()); 
    } 
    return null;
  }
  
  private Certificate decodeCertificate(Object paramObject) {
    if (this.provider != null)
      try {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", (Provider)this.provider);
        return certificateFactory.generateCertificate(new ByteArrayInputStream(Certificate.getInstance(paramObject).getEncoded()));
      } catch (Exception exception) {
        return null;
      }  
    try {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      return certificateFactory.generateCertificate(new ByteArrayInputStream(Certificate.getInstance(paramObject).getEncoded()));
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Date engineGetCreationDate(String paramString) {
    ObjectData objectData = this.entries.get(paramString);
    if (objectData != null)
      try {
        return objectData.getLastModifiedDate().getDate();
      } catch (ParseException parseException) {
        return new Date();
      }  
    return null;
  }
  
  public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfchar, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    Date date1 = new Date();
    Date date2 = date1;
    ObjectData objectData = this.entries.get(paramString);
    if (objectData != null)
      date1 = extractCreationDate(objectData, date1); 
    this.privateKeyCache.remove(paramString);
    if (paramKey instanceof PrivateKey) {
      if (paramArrayOfCertificate == null)
        throw new KeyStoreException("BCFKS KeyStore requires a certificate chain for private key storage."); 
      try {
        Cipher cipher;
        byte[] arrayOfByte1 = paramKey.getEncoded();
        KeyDerivationFunc keyDerivationFunc = generatePkbdAlgorithmIdentifier(32);
        byte[] arrayOfByte2 = generateKey(keyDerivationFunc, "PRIVATE_KEY_ENCRYPTION", (paramArrayOfchar != null) ? paramArrayOfchar : new char[0]);
        if (this.provider == null) {
          cipher = Cipher.getInstance("AES/CCM/NoPadding");
        } else {
          cipher = Cipher.getInstance("AES/CCM/NoPadding", (Provider)this.provider);
        } 
        cipher.init(1, new SecretKeySpec(arrayOfByte2, "AES"));
        byte[] arrayOfByte3 = cipher.doFinal(arrayOfByte1);
        AlgorithmParameters algorithmParameters = cipher.getParameters();
        PBES2Parameters pBES2Parameters = new PBES2Parameters(keyDerivationFunc, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, (ASN1Encodable)CCMParameters.getInstance(algorithmParameters.getEncoded())));
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, (ASN1Encodable)pBES2Parameters), arrayOfByte3);
        EncryptedPrivateKeyData encryptedPrivateKeyData = createPrivateKeySequence(encryptedPrivateKeyInfo, paramArrayOfCertificate);
        this.entries.put(paramString, new ObjectData(PRIVATE_KEY, paramString, date1, date2, encryptedPrivateKeyData.getEncoded(), null));
      } catch (Exception exception) {
        throw new ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + exception.toString(), exception);
      } 
    } else if (paramKey instanceof javax.crypto.SecretKey) {
      if (paramArrayOfCertificate != null)
        throw new KeyStoreException("BCFKS KeyStore cannot store certificate chain with secret key."); 
      try {
        Cipher cipher;
        byte[] arrayOfByte3;
        byte[] arrayOfByte1 = paramKey.getEncoded();
        KeyDerivationFunc keyDerivationFunc = generatePkbdAlgorithmIdentifier(32);
        byte[] arrayOfByte2 = generateKey(keyDerivationFunc, "SECRET_KEY_ENCRYPTION", (paramArrayOfchar != null) ? paramArrayOfchar : new char[0]);
        if (this.provider == null) {
          cipher = Cipher.getInstance("AES/CCM/NoPadding");
        } else {
          cipher = Cipher.getInstance("AES/CCM/NoPadding", (Provider)this.provider);
        } 
        cipher.init(1, new SecretKeySpec(arrayOfByte2, "AES"));
        String str = Strings.toUpperCase(paramKey.getAlgorithm());
        if (str.indexOf("AES") > -1) {
          arrayOfByte3 = cipher.doFinal((new SecretKeyData(NISTObjectIdentifiers.aes, arrayOfByte1)).getEncoded());
        } else {
          ASN1ObjectIdentifier aSN1ObjectIdentifier = oidMap.get(str);
          if (aSN1ObjectIdentifier != null) {
            arrayOfByte3 = cipher.doFinal((new SecretKeyData(aSN1ObjectIdentifier, arrayOfByte1)).getEncoded());
          } else {
            throw new KeyStoreException("BCFKS KeyStore cannot recognize secret key (" + str + ") for storage.");
          } 
        } 
        AlgorithmParameters algorithmParameters = cipher.getParameters();
        PBES2Parameters pBES2Parameters = new PBES2Parameters(keyDerivationFunc, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, (ASN1Encodable)CCMParameters.getInstance(algorithmParameters.getEncoded())));
        EncryptedSecretKeyData encryptedSecretKeyData = new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, (ASN1Encodable)pBES2Parameters), arrayOfByte3);
        this.entries.put(paramString, new ObjectData(SECRET_KEY, paramString, date1, date2, encryptedSecretKeyData.getEncoded(), null));
      } catch (Exception exception) {
        throw new ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + exception.toString(), exception);
      } 
    } else {
      throw new KeyStoreException("BCFKS KeyStore unable to recognize key.");
    } 
    this.lastModifiedDate = date2;
  }
  
  private SecureRandom getDefaultSecureRandom() {
    return new SecureRandom();
  }
  
  private EncryptedPrivateKeyData createPrivateKeySequence(EncryptedPrivateKeyInfo paramEncryptedPrivateKeyInfo, Certificate[] paramArrayOfCertificate) throws CertificateEncodingException {
    Certificate[] arrayOfCertificate = new Certificate[paramArrayOfCertificate.length];
    for (byte b = 0; b != paramArrayOfCertificate.length; b++)
      arrayOfCertificate[b] = Certificate.getInstance(paramArrayOfCertificate[b].getEncoded()); 
    return new EncryptedPrivateKeyData(paramEncryptedPrivateKeyInfo, arrayOfCertificate);
  }
  
  public void engineSetKeyEntry(String paramString, byte[] paramArrayOfbyte, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    Date date1 = new Date();
    Date date2 = date1;
    ObjectData objectData = this.entries.get(paramString);
    if (objectData != null)
      date1 = extractCreationDate(objectData, date1); 
    if (paramArrayOfCertificate != null) {
      EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
      try {
        encryptedPrivateKeyInfo = EncryptedPrivateKeyInfo.getInstance(paramArrayOfbyte);
      } catch (Exception exception) {
        throw new ExtKeyStoreException("BCFKS KeyStore private key encoding must be an EncryptedPrivateKeyInfo.", exception);
      } 
      try {
        this.privateKeyCache.remove(paramString);
        this.entries.put(paramString, new ObjectData(PROTECTED_PRIVATE_KEY, paramString, date1, date2, createPrivateKeySequence(encryptedPrivateKeyInfo, paramArrayOfCertificate).getEncoded(), null));
      } catch (Exception exception) {
        throw new ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + exception.toString(), exception);
      } 
    } else {
      try {
        this.entries.put(paramString, new ObjectData(PROTECTED_SECRET_KEY, paramString, date1, date2, paramArrayOfbyte, null));
      } catch (Exception exception) {
        throw new ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + exception.toString(), exception);
      } 
    } 
    this.lastModifiedDate = date2;
  }
  
  public void engineSetCertificateEntry(String paramString, Certificate paramCertificate) throws KeyStoreException {
    ObjectData objectData = this.entries.get(paramString);
    Date date1 = new Date();
    Date date2 = date1;
    if (objectData != null) {
      if (!objectData.getType().equals(CERTIFICATE))
        throw new KeyStoreException("BCFKS KeyStore already has a key entry with alias " + paramString); 
      date1 = extractCreationDate(objectData, date1);
    } 
    try {
      this.entries.put(paramString, new ObjectData(CERTIFICATE, paramString, date1, date2, paramCertificate.getEncoded(), null));
    } catch (CertificateEncodingException certificateEncodingException) {
      throw new ExtKeyStoreException("BCFKS KeyStore unable to handle certificate: " + certificateEncodingException.getMessage(), certificateEncodingException);
    } 
    this.lastModifiedDate = date2;
  }
  
  private Date extractCreationDate(ObjectData paramObjectData, Date paramDate) {
    try {
      paramDate = paramObjectData.getCreationDate().getDate();
    } catch (ParseException parseException) {}
    return paramDate;
  }
  
  public void engineDeleteEntry(String paramString) throws KeyStoreException {
    ObjectData objectData = this.entries.get(paramString);
    if (objectData == null)
      return; 
    this.privateKeyCache.remove(paramString);
    this.entries.remove(paramString);
    this.lastModifiedDate = new Date();
  }
  
  public Enumeration<String> engineAliases() {
    final Iterator<?> it = (new HashSet(this.entries.keySet())).iterator();
    return new Enumeration<String>() {
        public boolean hasMoreElements() {
          return it.hasNext();
        }
        
        public Object nextElement() {
          return it.next();
        }
      };
  }
  
  public boolean engineContainsAlias(String paramString) {
    if (paramString == null)
      throw new NullPointerException("alias value is null"); 
    return this.entries.containsKey(paramString);
  }
  
  public int engineSize() {
    return this.entries.size();
  }
  
  public boolean engineIsKeyEntry(String paramString) {
    ObjectData objectData = this.entries.get(paramString);
    if (objectData != null) {
      BigInteger bigInteger = objectData.getType();
      return (bigInteger.equals(PRIVATE_KEY) || bigInteger.equals(SECRET_KEY) || bigInteger.equals(PROTECTED_PRIVATE_KEY) || bigInteger.equals(PROTECTED_SECRET_KEY));
    } 
    return false;
  }
  
  public boolean engineIsCertificateEntry(String paramString) {
    ObjectData objectData = this.entries.get(paramString);
    return (objectData != null) ? objectData.getType().equals(CERTIFICATE) : false;
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate) {
    byte[] arrayOfByte;
    if (paramCertificate == null)
      return null; 
    try {
      arrayOfByte = paramCertificate.getEncoded();
    } catch (CertificateEncodingException certificateEncodingException) {
      return null;
    } 
    for (String str : this.entries.keySet()) {
      ObjectData objectData = this.entries.get(str);
      if (objectData.getType().equals(CERTIFICATE)) {
        if (Arrays.areEqual(objectData.getData(), arrayOfByte))
          return str; 
        continue;
      } 
      if (objectData.getType().equals(PRIVATE_KEY) || objectData.getType().equals(PROTECTED_PRIVATE_KEY))
        try {
          EncryptedPrivateKeyData encryptedPrivateKeyData = EncryptedPrivateKeyData.getInstance(objectData.getData());
          if (Arrays.areEqual(encryptedPrivateKeyData.getCertificateChain()[0].toASN1Primitive().getEncoded(), arrayOfByte))
            return str; 
        } catch (IOException iOException) {} 
    } 
    return null;
  }
  
  private byte[] generateKey(KeyDerivationFunc paramKeyDerivationFunc, String paramString, char[] paramArrayOfchar) throws IOException {
    int i;
    byte[] arrayOfByte1 = PBEParametersGenerator.PKCS12PasswordToBytes(paramArrayOfchar);
    byte[] arrayOfByte2 = PBEParametersGenerator.PKCS12PasswordToBytes(paramString.toCharArray());
    PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator((Digest)new SHA512Digest());
    if (paramKeyDerivationFunc.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBKDF2)) {
      PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(paramKeyDerivationFunc.getParameters());
      if (pBKDF2Params.getPrf().getAlgorithm().equals(PKCSObjectIdentifiers.id_hmacWithSHA512)) {
        pKCS5S2ParametersGenerator.init(Arrays.concatenate(arrayOfByte1, arrayOfByte2), pBKDF2Params.getSalt(), pBKDF2Params.getIterationCount().intValue());
        i = pBKDF2Params.getKeyLength().intValue();
      } else {
        throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD PRF.");
      } 
    } else {
      throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD.");
    } 
    return ((KeyParameter)pKCS5S2ParametersGenerator.generateDerivedParameters(i * 8)).getKey();
  }
  
  private void verifyMac(byte[] paramArrayOfbyte, PbkdMacIntegrityCheck paramPbkdMacIntegrityCheck, char[] paramArrayOfchar) throws NoSuchAlgorithmException, IOException {
    byte[] arrayOfByte = calculateMac(paramArrayOfbyte, paramPbkdMacIntegrityCheck.getMacAlgorithm(), paramPbkdMacIntegrityCheck.getPbkdAlgorithm(), paramArrayOfchar);
    if (!Arrays.constantTimeAreEqual(arrayOfByte, paramPbkdMacIntegrityCheck.getMac()))
      throw new IOException("BCFKS KeyStore corrupted: MAC calculation failed."); 
  }
  
  private byte[] calculateMac(byte[] paramArrayOfbyte, AlgorithmIdentifier paramAlgorithmIdentifier, KeyDerivationFunc paramKeyDerivationFunc, char[] paramArrayOfchar) throws NoSuchAlgorithmException, IOException {
    Mac mac;
    String str = paramAlgorithmIdentifier.getAlgorithm().getId();
    if (this.provider != null) {
      mac = Mac.getInstance(str, (Provider)this.provider);
    } else {
      mac = Mac.getInstance(str);
    } 
    try {
      mac.init(new SecretKeySpec(generateKey(paramKeyDerivationFunc, "INTEGRITY_CHECK", (paramArrayOfchar != null) ? paramArrayOfchar : new char[0]), str));
    } catch (InvalidKeyException invalidKeyException) {
      throw new IOException("Cannot set up MAC calculation: " + invalidKeyException.getMessage());
    } 
    return mac.doFinal(paramArrayOfbyte);
  }
  
  public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfchar) throws IOException, NoSuchAlgorithmException, CertificateException {
    EncryptedObjectStoreData encryptedObjectStoreData;
    ObjectData[] arrayOfObjectData = (ObjectData[])this.entries.values().toArray((Object[])new ObjectData[this.entries.size()]);
    KeyDerivationFunc keyDerivationFunc = generatePkbdAlgorithmIdentifier(32);
    byte[] arrayOfByte1 = generateKey(keyDerivationFunc, "STORE_ENCRYPTION", (paramArrayOfchar != null) ? paramArrayOfchar : new char[0]);
    ObjectStoreData objectStoreData = new ObjectStoreData(this.hmacAlgorithm, this.creationDate, this.lastModifiedDate, new ObjectDataSequence(arrayOfObjectData), null);
    try {
      Cipher cipher;
      if (this.provider == null) {
        cipher = Cipher.getInstance("AES/CCM/NoPadding");
      } else {
        cipher = Cipher.getInstance("AES/CCM/NoPadding", (Provider)this.provider);
      } 
      cipher.init(1, new SecretKeySpec(arrayOfByte1, "AES"));
      byte[] arrayOfByte = cipher.doFinal(objectStoreData.getEncoded());
      AlgorithmParameters algorithmParameters = cipher.getParameters();
      PBES2Parameters pBES2Parameters = new PBES2Parameters(keyDerivationFunc, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, (ASN1Encodable)CCMParameters.getInstance(algorithmParameters.getEncoded())));
      encryptedObjectStoreData = new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, (ASN1Encodable)pBES2Parameters), arrayOfByte);
    } catch (NoSuchPaddingException noSuchPaddingException) {
      throw new NoSuchAlgorithmException(noSuchPaddingException.toString());
    } catch (BadPaddingException badPaddingException) {
      throw new IOException(badPaddingException.toString());
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      throw new IOException(illegalBlockSizeException.toString());
    } catch (InvalidKeyException invalidKeyException) {
      throw new IOException(invalidKeyException.toString());
    } 
    PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(this.hmacPkbdAlgorithm.getParameters());
    byte[] arrayOfByte2 = new byte[(pBKDF2Params.getSalt()).length];
    getDefaultSecureRandom().nextBytes(arrayOfByte2);
    this.hmacPkbdAlgorithm = new KeyDerivationFunc(this.hmacPkbdAlgorithm.getAlgorithm(), (ASN1Encodable)new PBKDF2Params(arrayOfByte2, pBKDF2Params.getIterationCount().intValue(), pBKDF2Params.getKeyLength().intValue(), pBKDF2Params.getPrf()));
    byte[] arrayOfByte3 = calculateMac(encryptedObjectStoreData.getEncoded(), this.hmacAlgorithm, this.hmacPkbdAlgorithm, paramArrayOfchar);
    ObjectStore objectStore = new ObjectStore(encryptedObjectStoreData, new ObjectStoreIntegrityCheck(new PbkdMacIntegrityCheck(this.hmacAlgorithm, this.hmacPkbdAlgorithm, arrayOfByte3)));
    paramOutputStream.write(objectStore.getEncoded());
    paramOutputStream.flush();
  }
  
  public void engineLoad(InputStream paramInputStream, char[] paramArrayOfchar) throws IOException, NoSuchAlgorithmException, CertificateException {
    ObjectStoreData objectStoreData;
    this.entries.clear();
    this.privateKeyCache.clear();
    this.lastModifiedDate = this.creationDate = null;
    this.hmacAlgorithm = null;
    if (paramInputStream == null) {
      this.lastModifiedDate = this.creationDate = new Date();
      this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, (ASN1Encodable)DERNull.INSTANCE);
      this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(64);
      return;
    } 
    ASN1InputStream aSN1InputStream = new ASN1InputStream(paramInputStream);
    ObjectStore objectStore = ObjectStore.getInstance(aSN1InputStream.readObject());
    ObjectStoreIntegrityCheck objectStoreIntegrityCheck = objectStore.getIntegrityCheck();
    if (objectStoreIntegrityCheck.getType() == 0) {
      PbkdMacIntegrityCheck pbkdMacIntegrityCheck = PbkdMacIntegrityCheck.getInstance(objectStoreIntegrityCheck.getIntegrityCheck());
      this.hmacAlgorithm = pbkdMacIntegrityCheck.getMacAlgorithm();
      this.hmacPkbdAlgorithm = pbkdMacIntegrityCheck.getPbkdAlgorithm();
      verifyMac(objectStore.getStoreData().toASN1Primitive().getEncoded(), pbkdMacIntegrityCheck, paramArrayOfchar);
    } else {
      throw new IOException("BCFKS KeyStore unable to recognize integrity check.");
    } 
    ASN1Encodable aSN1Encodable = objectStore.getStoreData();
    if (aSN1Encodable instanceof EncryptedObjectStoreData) {
      EncryptedObjectStoreData encryptedObjectStoreData = (EncryptedObjectStoreData)aSN1Encodable;
      AlgorithmIdentifier algorithmIdentifier = encryptedObjectStoreData.getEncryptionAlgorithm();
      objectStoreData = ObjectStoreData.getInstance(decryptData("STORE_ENCRYPTION", algorithmIdentifier, paramArrayOfchar, encryptedObjectStoreData.getEncryptedContent().getOctets()));
    } else {
      objectStoreData = ObjectStoreData.getInstance(aSN1Encodable);
    } 
    try {
      this.creationDate = objectStoreData.getCreationDate().getDate();
      this.lastModifiedDate = objectStoreData.getLastModifiedDate().getDate();
    } catch (ParseException parseException) {
      throw new IOException("BCFKS KeyStore unable to parse store data information.");
    } 
    if (!objectStoreData.getIntegrityAlgorithm().equals(this.hmacAlgorithm))
      throw new IOException("BCFKS KeyStore storeData integrity algorithm does not match store integrity algorithm."); 
    Iterator iterator = objectStoreData.getObjectDataSequence().iterator();
    while (iterator.hasNext()) {
      ObjectData objectData = ObjectData.getInstance(iterator.next());
      this.entries.put(objectData.getIdentifier(), objectData);
    } 
  }
  
  private byte[] decryptData(String paramString, AlgorithmIdentifier paramAlgorithmIdentifier, char[] paramArrayOfchar, byte[] paramArrayOfbyte) throws IOException {
    if (!paramAlgorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBES2))
      throw new IOException("BCFKS KeyStore cannot recognize protection algorithm."); 
    PBES2Parameters pBES2Parameters = PBES2Parameters.getInstance(paramAlgorithmIdentifier.getParameters());
    EncryptionScheme encryptionScheme = pBES2Parameters.getEncryptionScheme();
    if (!encryptionScheme.getAlgorithm().equals(NISTObjectIdentifiers.id_aes256_CCM))
      throw new IOException("BCFKS KeyStore cannot recognize protection encryption algorithm."); 
    try {
      Cipher cipher;
      AlgorithmParameters algorithmParameters;
      CCMParameters cCMParameters = CCMParameters.getInstance(encryptionScheme.getParameters());
      if (this.provider == null) {
        cipher = Cipher.getInstance("AES/CCM/NoPadding");
        algorithmParameters = AlgorithmParameters.getInstance("CCM");
      } else {
        cipher = Cipher.getInstance("AES/CCM/NoPadding", (Provider)this.provider);
        algorithmParameters = AlgorithmParameters.getInstance("CCM", (Provider)this.provider);
      } 
      algorithmParameters.init(cCMParameters.getEncoded());
      byte[] arrayOfByte = generateKey(pBES2Parameters.getKeyDerivationFunc(), paramString, (paramArrayOfchar != null) ? paramArrayOfchar : new char[0]);
      cipher.init(2, new SecretKeySpec(arrayOfByte, "AES"), algorithmParameters);
      return cipher.doFinal(paramArrayOfbyte);
    } catch (Exception exception) {
      throw new IOException(exception.toString());
    } 
  }
  
  private KeyDerivationFunc generatePkbdAlgorithmIdentifier(int paramInt) {
    byte[] arrayOfByte = new byte[64];
    getDefaultSecureRandom().nextBytes(arrayOfByte);
    return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(arrayOfByte, 1024, paramInt, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, (ASN1Encodable)DERNull.INSTANCE)));
  }
  
  static {
    oidMap.put("DESEDE", OIWObjectIdentifiers.desEDE);
    oidMap.put("TRIPLEDES", OIWObjectIdentifiers.desEDE);
    oidMap.put("TDEA", OIWObjectIdentifiers.desEDE);
    oidMap.put("HMACSHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
    oidMap.put("HMACSHA224", PKCSObjectIdentifiers.id_hmacWithSHA224);
    oidMap.put("HMACSHA256", PKCSObjectIdentifiers.id_hmacWithSHA256);
    oidMap.put("HMACSHA384", PKCSObjectIdentifiers.id_hmacWithSHA384);
    oidMap.put("HMACSHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
    publicAlgMap.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
    publicAlgMap.put(X9ObjectIdentifiers.id_ecPublicKey, "EC");
    publicAlgMap.put(OIWObjectIdentifiers.elGamalAlgorithm, "DH");
    publicAlgMap.put(PKCSObjectIdentifiers.dhKeyAgreement, "DH");
    publicAlgMap.put(X9ObjectIdentifiers.id_dsa, "DSA");
  }
  
  public static class Def extends BcFKSKeyStoreSpi {
    public Def() {
      super(null);
    }
  }
  
  private static class ExtKeyStoreException extends KeyStoreException {
    private final Throwable cause;
    
    ExtKeyStoreException(String param1String, Throwable param1Throwable) {
      super(param1String);
      this.cause = param1Throwable;
    }
    
    public Throwable getCause() {
      return this.cause;
    }
  }
  
  public static class Std extends BcFKSKeyStoreSpi {
    public Std() {
      super(new BouncyCastleProvider());
    }
  }
}
