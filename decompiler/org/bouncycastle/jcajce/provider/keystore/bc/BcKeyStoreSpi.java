package org.bouncycastle.jcajce.provider.keystore.bc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.DigestInputStream;
import org.bouncycastle.crypto.io.DigestOutputStream;
import org.bouncycastle.crypto.io.MacInputStream;
import org.bouncycastle.crypto.io.MacOutputStream;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.BCKeyStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.util.io.TeeOutputStream;

public class BcKeyStoreSpi extends KeyStoreSpi implements BCKeyStore {
  private static final int STORE_VERSION = 2;
  
  private static final int STORE_SALT_SIZE = 20;
  
  private static final String STORE_CIPHER = "PBEWithSHAAndTwofish-CBC";
  
  private static final int KEY_SALT_SIZE = 20;
  
  private static final int MIN_ITERATIONS = 1024;
  
  private static final String KEY_CIPHER = "PBEWithSHAAnd3-KeyTripleDES-CBC";
  
  static final int NULL = 0;
  
  static final int CERTIFICATE = 1;
  
  static final int KEY = 2;
  
  static final int SECRET = 3;
  
  static final int SEALED = 4;
  
  static final int KEY_PRIVATE = 0;
  
  static final int KEY_PUBLIC = 1;
  
  static final int KEY_SECRET = 2;
  
  protected Hashtable table = new Hashtable<Object, Object>();
  
  protected SecureRandom random = new SecureRandom();
  
  protected int version;
  
  private final JcaJceHelper helper = (JcaJceHelper)new BCJcaJceHelper();
  
  public BcKeyStoreSpi(int paramInt) {
    this.version = paramInt;
  }
  
  private void encodeCertificate(Certificate paramCertificate, DataOutputStream paramDataOutputStream) throws IOException {
    try {
      byte[] arrayOfByte = paramCertificate.getEncoded();
      paramDataOutputStream.writeUTF(paramCertificate.getType());
      paramDataOutputStream.writeInt(arrayOfByte.length);
      paramDataOutputStream.write(arrayOfByte);
    } catch (CertificateEncodingException certificateEncodingException) {
      throw new IOException(certificateEncodingException.toString());
    } 
  }
  
  private Certificate decodeCertificate(DataInputStream paramDataInputStream) throws IOException {
    String str = paramDataInputStream.readUTF();
    byte[] arrayOfByte = new byte[paramDataInputStream.readInt()];
    paramDataInputStream.readFully(arrayOfByte);
    try {
      CertificateFactory certificateFactory = this.helper.createCertificateFactory(str);
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      return certificateFactory.generateCertificate(byteArrayInputStream);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new IOException(noSuchProviderException.toString());
    } catch (CertificateException certificateException) {
      throw new IOException(certificateException.toString());
    } 
  }
  
  private void encodeKey(Key paramKey, DataOutputStream paramDataOutputStream) throws IOException {
    byte[] arrayOfByte = paramKey.getEncoded();
    if (paramKey instanceof java.security.PrivateKey) {
      paramDataOutputStream.write(0);
    } else if (paramKey instanceof java.security.PublicKey) {
      paramDataOutputStream.write(1);
    } else {
      paramDataOutputStream.write(2);
    } 
    paramDataOutputStream.writeUTF(paramKey.getFormat());
    paramDataOutputStream.writeUTF(paramKey.getAlgorithm());
    paramDataOutputStream.writeInt(arrayOfByte.length);
    paramDataOutputStream.write(arrayOfByte);
  }
  
  private Key decodeKey(DataInputStream paramDataInputStream) throws IOException {
    X509EncodedKeySpec x509EncodedKeySpec;
    int i = paramDataInputStream.read();
    String str1 = paramDataInputStream.readUTF();
    String str2 = paramDataInputStream.readUTF();
    byte[] arrayOfByte = new byte[paramDataInputStream.readInt()];
    paramDataInputStream.readFully(arrayOfByte);
    if (str1.equals("PKCS#8") || str1.equals("PKCS8")) {
      PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(arrayOfByte);
    } else if (str1.equals("X.509") || str1.equals("X509")) {
      x509EncodedKeySpec = new X509EncodedKeySpec(arrayOfByte);
    } else {
      if (str1.equals("RAW"))
        return new SecretKeySpec(arrayOfByte, str2); 
      throw new IOException("Key format " + str1 + " not recognised!");
    } 
    try {
      switch (i) {
        case 0:
          return this.helper.createKeyFactory(str2).generatePrivate(x509EncodedKeySpec);
        case 1:
          return this.helper.createKeyFactory(str2).generatePublic(x509EncodedKeySpec);
        case 2:
          return this.helper.createSecretKeyFactory(str2).generateSecret(x509EncodedKeySpec);
      } 
      throw new IOException("Key type " + i + " not recognised!");
    } catch (Exception exception) {
      throw new IOException("Exception creating key: " + exception.toString());
    } 
  }
  
  protected Cipher makePBECipher(String paramString, int paramInt1, char[] paramArrayOfchar, byte[] paramArrayOfbyte, int paramInt2) throws IOException {
    try {
      PBEKeySpec pBEKeySpec = new PBEKeySpec(paramArrayOfchar);
      SecretKeyFactory secretKeyFactory = this.helper.createSecretKeyFactory(paramString);
      PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(paramArrayOfbyte, paramInt2);
      Cipher cipher = this.helper.createCipher(paramString);
      cipher.init(paramInt1, secretKeyFactory.generateSecret(pBEKeySpec), pBEParameterSpec);
      return cipher;
    } catch (Exception exception) {
      throw new IOException("Error initialising store of key store: " + exception);
    } 
  }
  
  public void setRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
  }
  
  public Enumeration engineAliases() {
    return this.table.keys();
  }
  
  public boolean engineContainsAlias(String paramString) {
    return (this.table.get(paramString) != null);
  }
  
  public void engineDeleteEntry(String paramString) throws KeyStoreException {
    Object object = this.table.get(paramString);
    if (object == null)
      return; 
    this.table.remove(paramString);
  }
  
  public Certificate engineGetCertificate(String paramString) {
    StoreEntry storeEntry = (StoreEntry)this.table.get(paramString);
    if (storeEntry != null) {
      if (storeEntry.getType() == 1)
        return (Certificate)storeEntry.getObject(); 
      Certificate[] arrayOfCertificate = storeEntry.getCertificateChain();
      if (arrayOfCertificate != null)
        return arrayOfCertificate[0]; 
    } 
    return null;
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate) {
    Enumeration<StoreEntry> enumeration = this.table.elements();
    while (enumeration.hasMoreElements()) {
      StoreEntry storeEntry = enumeration.nextElement();
      if (storeEntry.getObject() instanceof Certificate) {
        Certificate certificate = (Certificate)storeEntry.getObject();
        if (certificate.equals(paramCertificate))
          return storeEntry.getAlias(); 
        continue;
      } 
      Certificate[] arrayOfCertificate = storeEntry.getCertificateChain();
      if (arrayOfCertificate != null && arrayOfCertificate[0].equals(paramCertificate))
        return storeEntry.getAlias(); 
    } 
    return null;
  }
  
  public Certificate[] engineGetCertificateChain(String paramString) {
    StoreEntry storeEntry = (StoreEntry)this.table.get(paramString);
    return (storeEntry != null) ? storeEntry.getCertificateChain() : null;
  }
  
  public Date engineGetCreationDate(String paramString) {
    StoreEntry storeEntry = (StoreEntry)this.table.get(paramString);
    return (storeEntry != null) ? storeEntry.getDate() : null;
  }
  
  public Key engineGetKey(String paramString, char[] paramArrayOfchar) throws NoSuchAlgorithmException, UnrecoverableKeyException {
    StoreEntry storeEntry = (StoreEntry)this.table.get(paramString);
    return (storeEntry == null || storeEntry.getType() == 1) ? null : (Key)storeEntry.getObject(paramArrayOfchar);
  }
  
  public boolean engineIsCertificateEntry(String paramString) {
    StoreEntry storeEntry = (StoreEntry)this.table.get(paramString);
    return (storeEntry != null && storeEntry.getType() == 1);
  }
  
  public boolean engineIsKeyEntry(String paramString) {
    StoreEntry storeEntry = (StoreEntry)this.table.get(paramString);
    return (storeEntry != null && storeEntry.getType() != 1);
  }
  
  public void engineSetCertificateEntry(String paramString, Certificate paramCertificate) throws KeyStoreException {
    StoreEntry storeEntry = (StoreEntry)this.table.get(paramString);
    if (storeEntry != null && storeEntry.getType() != 1)
      throw new KeyStoreException("key store already has a key entry with alias " + paramString); 
    this.table.put(paramString, new StoreEntry(paramString, paramCertificate));
  }
  
  public void engineSetKeyEntry(String paramString, byte[] paramArrayOfbyte, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    this.table.put(paramString, new StoreEntry(paramString, paramArrayOfbyte, paramArrayOfCertificate));
  }
  
  public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfchar, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    if (paramKey instanceof java.security.PrivateKey && paramArrayOfCertificate == null)
      throw new KeyStoreException("no certificate chain for private key"); 
    try {
      this.table.put(paramString, new StoreEntry(paramString, paramKey, paramArrayOfchar, paramArrayOfCertificate));
    } catch (Exception exception) {
      throw new KeyStoreException(exception.toString());
    } 
  }
  
  public int engineSize() {
    return this.table.size();
  }
  
  protected void loadStore(InputStream paramInputStream) throws IOException {
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    for (int i = dataInputStream.read(); i > 0; i = dataInputStream.read()) {
      Certificate certificate;
      Key key;
      byte[] arrayOfByte;
      String str = dataInputStream.readUTF();
      Date date = new Date(dataInputStream.readLong());
      int j = dataInputStream.readInt();
      Certificate[] arrayOfCertificate = null;
      if (j != 0) {
        arrayOfCertificate = new Certificate[j];
        for (int k = 0; k != j; k++)
          arrayOfCertificate[k] = decodeCertificate(dataInputStream); 
      } 
      switch (i) {
        case 1:
          certificate = decodeCertificate(dataInputStream);
          this.table.put(str, new StoreEntry(str, date, 1, certificate));
          break;
        case 2:
          key = decodeKey(dataInputStream);
          this.table.put(str, new StoreEntry(str, date, 2, key, arrayOfCertificate));
          break;
        case 3:
        case 4:
          arrayOfByte = new byte[dataInputStream.readInt()];
          dataInputStream.readFully(arrayOfByte);
          this.table.put(str, new StoreEntry(str, date, i, arrayOfByte, arrayOfCertificate));
          break;
        default:
          throw new RuntimeException("Unknown object type in store.");
      } 
    } 
  }
  
  protected void saveStore(OutputStream paramOutputStream) throws IOException {
    Enumeration<StoreEntry> enumeration = this.table.elements();
    DataOutputStream dataOutputStream = new DataOutputStream(paramOutputStream);
    while (enumeration.hasMoreElements()) {
      byte[] arrayOfByte;
      StoreEntry storeEntry = enumeration.nextElement();
      dataOutputStream.write(storeEntry.getType());
      dataOutputStream.writeUTF(storeEntry.getAlias());
      dataOutputStream.writeLong(storeEntry.getDate().getTime());
      Certificate[] arrayOfCertificate = storeEntry.getCertificateChain();
      if (arrayOfCertificate == null) {
        dataOutputStream.writeInt(0);
      } else {
        dataOutputStream.writeInt(arrayOfCertificate.length);
        for (byte b = 0; b != arrayOfCertificate.length; b++)
          encodeCertificate(arrayOfCertificate[b], dataOutputStream); 
      } 
      switch (storeEntry.getType()) {
        case 1:
          encodeCertificate((Certificate)storeEntry.getObject(), dataOutputStream);
          continue;
        case 2:
          encodeKey((Key)storeEntry.getObject(), dataOutputStream);
          continue;
        case 3:
        case 4:
          arrayOfByte = (byte[])storeEntry.getObject();
          dataOutputStream.writeInt(arrayOfByte.length);
          dataOutputStream.write(arrayOfByte);
          continue;
      } 
      throw new RuntimeException("Unknown object type in store.");
    } 
    dataOutputStream.write(0);
  }
  
  public void engineLoad(InputStream paramInputStream, char[] paramArrayOfchar) throws IOException {
    this.table.clear();
    if (paramInputStream == null)
      return; 
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    int i = dataInputStream.readInt();
    if (i != 2 && i != 0 && i != 1)
      throw new IOException("Wrong version of key store."); 
    int j = dataInputStream.readInt();
    if (j <= 0)
      throw new IOException("Invalid salt detected"); 
    byte[] arrayOfByte = new byte[j];
    dataInputStream.readFully(arrayOfByte);
    int k = dataInputStream.readInt();
    HMac hMac = new HMac((Digest)new SHA1Digest());
    if (paramArrayOfchar != null && paramArrayOfchar.length != 0) {
      CipherParameters cipherParameters;
      byte[] arrayOfByte1 = PBEParametersGenerator.PKCS12PasswordToBytes(paramArrayOfchar);
      PKCS12ParametersGenerator pKCS12ParametersGenerator = new PKCS12ParametersGenerator((Digest)new SHA1Digest());
      pKCS12ParametersGenerator.init(arrayOfByte1, arrayOfByte, k);
      if (i != 2) {
        cipherParameters = pKCS12ParametersGenerator.generateDerivedMacParameters(hMac.getMacSize());
      } else {
        cipherParameters = pKCS12ParametersGenerator.generateDerivedMacParameters(hMac.getMacSize() * 8);
      } 
      Arrays.fill(arrayOfByte1, (byte)0);
      hMac.init(cipherParameters);
      MacInputStream macInputStream = new MacInputStream(dataInputStream, (Mac)hMac);
      loadStore((InputStream)macInputStream);
      byte[] arrayOfByte2 = new byte[hMac.getMacSize()];
      hMac.doFinal(arrayOfByte2, 0);
      byte[] arrayOfByte3 = new byte[hMac.getMacSize()];
      dataInputStream.readFully(arrayOfByte3);
      if (!Arrays.constantTimeAreEqual(arrayOfByte2, arrayOfByte3)) {
        this.table.clear();
        throw new IOException("KeyStore integrity check failed.");
      } 
    } else {
      loadStore(dataInputStream);
      byte[] arrayOfByte1 = new byte[hMac.getMacSize()];
      dataInputStream.readFully(arrayOfByte1);
    } 
  }
  
  public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfchar) throws IOException {
    DataOutputStream dataOutputStream = new DataOutputStream(paramOutputStream);
    byte[] arrayOfByte1 = new byte[20];
    int i = 1024 + (this.random.nextInt() & 0x3FF);
    this.random.nextBytes(arrayOfByte1);
    dataOutputStream.writeInt(this.version);
    dataOutputStream.writeInt(arrayOfByte1.length);
    dataOutputStream.write(arrayOfByte1);
    dataOutputStream.writeInt(i);
    HMac hMac = new HMac((Digest)new SHA1Digest());
    MacOutputStream macOutputStream = new MacOutputStream((Mac)hMac);
    PKCS12ParametersGenerator pKCS12ParametersGenerator = new PKCS12ParametersGenerator((Digest)new SHA1Digest());
    byte[] arrayOfByte2 = PBEParametersGenerator.PKCS12PasswordToBytes(paramArrayOfchar);
    pKCS12ParametersGenerator.init(arrayOfByte2, arrayOfByte1, i);
    if (this.version < 2) {
      hMac.init(pKCS12ParametersGenerator.generateDerivedMacParameters(hMac.getMacSize()));
    } else {
      hMac.init(pKCS12ParametersGenerator.generateDerivedMacParameters(hMac.getMacSize() * 8));
    } 
    for (byte b = 0; b != arrayOfByte2.length; b++)
      arrayOfByte2[b] = 0; 
    saveStore((OutputStream)new TeeOutputStream(dataOutputStream, (OutputStream)macOutputStream));
    byte[] arrayOfByte3 = new byte[hMac.getMacSize()];
    hMac.doFinal(arrayOfByte3, 0);
    dataOutputStream.write(arrayOfByte3);
    dataOutputStream.close();
  }
  
  static Provider getBouncyCastleProvider() {
    return (Provider)((Security.getProvider("BC") != null) ? Security.getProvider("BC") : new BouncyCastleProvider());
  }
  
  public static class BouncyCastleStore extends BcKeyStoreSpi {
    public BouncyCastleStore() {
      super(1);
    }
    
    public void engineLoad(InputStream param1InputStream, char[] param1ArrayOfchar) throws IOException {
      String str;
      this.table.clear();
      if (param1InputStream == null)
        return; 
      DataInputStream dataInputStream = new DataInputStream(param1InputStream);
      int i = dataInputStream.readInt();
      if (i != 2 && i != 0 && i != 1)
        throw new IOException("Wrong version of key store."); 
      byte[] arrayOfByte1 = new byte[dataInputStream.readInt()];
      if (arrayOfByte1.length != 20)
        throw new IOException("Key store corrupted."); 
      dataInputStream.readFully(arrayOfByte1);
      int j = dataInputStream.readInt();
      if (j < 0 || j > 65536)
        throw new IOException("Key store corrupted."); 
      if (i == 0) {
        str = "OldPBEWithSHAAndTwofish-CBC";
      } else {
        str = "PBEWithSHAAndTwofish-CBC";
      } 
      Cipher cipher = makePBECipher(str, 2, param1ArrayOfchar, arrayOfByte1, j);
      CipherInputStream cipherInputStream = new CipherInputStream(dataInputStream, cipher);
      SHA1Digest sHA1Digest = new SHA1Digest();
      DigestInputStream digestInputStream = new DigestInputStream(cipherInputStream, (Digest)sHA1Digest);
      loadStore((InputStream)digestInputStream);
      byte[] arrayOfByte2 = new byte[sHA1Digest.getDigestSize()];
      sHA1Digest.doFinal(arrayOfByte2, 0);
      byte[] arrayOfByte3 = new byte[sHA1Digest.getDigestSize()];
      Streams.readFully(cipherInputStream, arrayOfByte3);
      if (!Arrays.constantTimeAreEqual(arrayOfByte2, arrayOfByte3)) {
        this.table.clear();
        throw new IOException("KeyStore integrity check failed.");
      } 
    }
    
    public void engineStore(OutputStream param1OutputStream, char[] param1ArrayOfchar) throws IOException {
      DataOutputStream dataOutputStream = new DataOutputStream(param1OutputStream);
      byte[] arrayOfByte1 = new byte[20];
      int i = 1024 + (this.random.nextInt() & 0x3FF);
      this.random.nextBytes(arrayOfByte1);
      dataOutputStream.writeInt(this.version);
      dataOutputStream.writeInt(arrayOfByte1.length);
      dataOutputStream.write(arrayOfByte1);
      dataOutputStream.writeInt(i);
      Cipher cipher = makePBECipher("PBEWithSHAAndTwofish-CBC", 1, param1ArrayOfchar, arrayOfByte1, i);
      CipherOutputStream cipherOutputStream = new CipherOutputStream(dataOutputStream, cipher);
      DigestOutputStream digestOutputStream = new DigestOutputStream((Digest)new SHA1Digest());
      saveStore((OutputStream)new TeeOutputStream(cipherOutputStream, (OutputStream)digestOutputStream));
      byte[] arrayOfByte2 = digestOutputStream.getDigest();
      cipherOutputStream.write(arrayOfByte2);
      cipherOutputStream.close();
    }
  }
  
  public static class Std extends BcKeyStoreSpi {
    public Std() {
      super(2);
    }
  }
  
  private class StoreEntry {
    int type;
    
    String alias;
    
    Object obj;
    
    Certificate[] certChain;
    
    Date date = new Date();
    
    StoreEntry(String param1String, Certificate param1Certificate) {
      this.type = 1;
      this.alias = param1String;
      this.obj = param1Certificate;
      this.certChain = null;
    }
    
    StoreEntry(String param1String, byte[] param1ArrayOfbyte, Certificate[] param1ArrayOfCertificate) {
      this.type = 3;
      this.alias = param1String;
      this.obj = param1ArrayOfbyte;
      this.certChain = param1ArrayOfCertificate;
    }
    
    StoreEntry(String param1String, Key param1Key, char[] param1ArrayOfchar, Certificate[] param1ArrayOfCertificate) throws Exception {
      this.type = 4;
      this.alias = param1String;
      this.certChain = param1ArrayOfCertificate;
      byte[] arrayOfByte = new byte[20];
      BcKeyStoreSpi.this.random.setSeed(System.currentTimeMillis());
      BcKeyStoreSpi.this.random.nextBytes(arrayOfByte);
      int i = 1024 + (BcKeyStoreSpi.this.random.nextInt() & 0x3FF);
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      dataOutputStream.writeInt(arrayOfByte.length);
      dataOutputStream.write(arrayOfByte);
      dataOutputStream.writeInt(i);
      Cipher cipher = BcKeyStoreSpi.this.makePBECipher("PBEWithSHAAnd3-KeyTripleDES-CBC", 1, param1ArrayOfchar, arrayOfByte, i);
      CipherOutputStream cipherOutputStream = new CipherOutputStream(dataOutputStream, cipher);
      dataOutputStream = new DataOutputStream(cipherOutputStream);
      BcKeyStoreSpi.this.encodeKey(param1Key, dataOutputStream);
      dataOutputStream.close();
      this.obj = byteArrayOutputStream.toByteArray();
    }
    
    StoreEntry(String param1String, Date param1Date, int param1Int, Object param1Object) {
      this.alias = param1String;
      this.date = param1Date;
      this.type = param1Int;
      this.obj = param1Object;
    }
    
    StoreEntry(String param1String, Date param1Date, int param1Int, Object param1Object, Certificate[] param1ArrayOfCertificate) {
      this.alias = param1String;
      this.date = param1Date;
      this.type = param1Int;
      this.obj = param1Object;
      this.certChain = param1ArrayOfCertificate;
    }
    
    int getType() {
      return this.type;
    }
    
    String getAlias() {
      return this.alias;
    }
    
    Object getObject() {
      return this.obj;
    }
    
    Object getObject(char[] param1ArrayOfchar) throws NoSuchAlgorithmException, UnrecoverableKeyException {
      if ((param1ArrayOfchar == null || param1ArrayOfchar.length == 0) && this.obj instanceof Key)
        return this.obj; 
      if (this.type == 4) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream((byte[])this.obj);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        try {
          byte[] arrayOfByte = new byte[dataInputStream.readInt()];
          dataInputStream.readFully(arrayOfByte);
          int i = dataInputStream.readInt();
          Cipher cipher = BcKeyStoreSpi.this.makePBECipher("PBEWithSHAAnd3-KeyTripleDES-CBC", 2, param1ArrayOfchar, arrayOfByte, i);
          CipherInputStream cipherInputStream = new CipherInputStream(dataInputStream, cipher);
          try {
            return BcKeyStoreSpi.this.decodeKey(new DataInputStream(cipherInputStream));
          } catch (Exception exception) {
            byteArrayInputStream = new ByteArrayInputStream((byte[])this.obj);
            dataInputStream = new DataInputStream(byteArrayInputStream);
            arrayOfByte = new byte[dataInputStream.readInt()];
            dataInputStream.readFully(arrayOfByte);
            i = dataInputStream.readInt();
            cipher = BcKeyStoreSpi.this.makePBECipher("BrokenPBEWithSHAAnd3-KeyTripleDES-CBC", 2, param1ArrayOfchar, arrayOfByte, i);
            cipherInputStream = new CipherInputStream(dataInputStream, cipher);
            Key key = null;
            try {
              key = BcKeyStoreSpi.this.decodeKey(new DataInputStream(cipherInputStream));
            } catch (Exception exception1) {
              byteArrayInputStream = new ByteArrayInputStream((byte[])this.obj);
              dataInputStream = new DataInputStream(byteArrayInputStream);
              arrayOfByte = new byte[dataInputStream.readInt()];
              dataInputStream.readFully(arrayOfByte);
              i = dataInputStream.readInt();
              cipher = BcKeyStoreSpi.this.makePBECipher("OldPBEWithSHAAnd3-KeyTripleDES-CBC", 2, param1ArrayOfchar, arrayOfByte, i);
              cipherInputStream = new CipherInputStream(dataInputStream, cipher);
              key = BcKeyStoreSpi.this.decodeKey(new DataInputStream(cipherInputStream));
            } 
            if (key != null) {
              ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
              DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
              dataOutputStream.writeInt(arrayOfByte.length);
              dataOutputStream.write(arrayOfByte);
              dataOutputStream.writeInt(i);
              Cipher cipher1 = BcKeyStoreSpi.this.makePBECipher("PBEWithSHAAnd3-KeyTripleDES-CBC", 1, param1ArrayOfchar, arrayOfByte, i);
              CipherOutputStream cipherOutputStream = new CipherOutputStream(dataOutputStream, cipher1);
              dataOutputStream = new DataOutputStream(cipherOutputStream);
              BcKeyStoreSpi.this.encodeKey(key, dataOutputStream);
              dataOutputStream.close();
              this.obj = byteArrayOutputStream.toByteArray();
              return key;
            } 
            throw new UnrecoverableKeyException("no match");
          } 
        } catch (Exception exception) {
          throw new UnrecoverableKeyException("no match");
        } 
      } 
      throw new RuntimeException("forget something!");
    }
    
    Certificate[] getCertificateChain() {
      return this.certChain;
    }
    
    Date getDate() {
      return this.date;
    }
  }
  
  public static class Version1 extends BcKeyStoreSpi {
    public Version1() {
      super(1);
    }
  }
}
