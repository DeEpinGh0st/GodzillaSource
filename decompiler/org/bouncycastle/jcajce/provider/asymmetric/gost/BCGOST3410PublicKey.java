package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jce.interfaces.GOST3410Params;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeySpec;
import org.bouncycastle.util.Strings;

public class BCGOST3410PublicKey implements GOST3410PublicKey {
  static final long serialVersionUID = -6251023343619275990L;
  
  private BigInteger y;
  
  private transient GOST3410Params gost3410Spec;
  
  BCGOST3410PublicKey(GOST3410PublicKeySpec paramGOST3410PublicKeySpec) {
    this.y = paramGOST3410PublicKeySpec.getY();
    this.gost3410Spec = (GOST3410Params)new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec(paramGOST3410PublicKeySpec.getP(), paramGOST3410PublicKeySpec.getQ(), paramGOST3410PublicKeySpec.getA()));
  }
  
  BCGOST3410PublicKey(GOST3410PublicKey paramGOST3410PublicKey) {
    this.y = paramGOST3410PublicKey.getY();
    this.gost3410Spec = paramGOST3410PublicKey.getParameters();
  }
  
  BCGOST3410PublicKey(GOST3410PublicKeyParameters paramGOST3410PublicKeyParameters, GOST3410ParameterSpec paramGOST3410ParameterSpec) {
    this.y = paramGOST3410PublicKeyParameters.getY();
    this.gost3410Spec = (GOST3410Params)paramGOST3410ParameterSpec;
  }
  
  BCGOST3410PublicKey(BigInteger paramBigInteger, GOST3410ParameterSpec paramGOST3410ParameterSpec) {
    this.y = paramBigInteger;
    this.gost3410Spec = (GOST3410Params)paramGOST3410ParameterSpec;
  }
  
  BCGOST3410PublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = new GOST3410PublicKeyAlgParameters((ASN1Sequence)paramSubjectPublicKeyInfo.getAlgorithmId().getParameters());
    try {
      DEROctetString dEROctetString = (DEROctetString)paramSubjectPublicKeyInfo.parsePublicKey();
      byte[] arrayOfByte1 = dEROctetString.getOctets();
      byte[] arrayOfByte2 = new byte[arrayOfByte1.length];
      for (byte b = 0; b != arrayOfByte1.length; b++)
        arrayOfByte2[b] = arrayOfByte1[arrayOfByte1.length - 1 - b]; 
      this.y = new BigInteger(1, arrayOfByte2);
    } catch (IOException iOException) {
      throw new IllegalArgumentException("invalid info structure in GOST3410 public key");
    } 
    this.gost3410Spec = (GOST3410Params)GOST3410ParameterSpec.fromPublicKeyAlg(gOST3410PublicKeyAlgParameters);
  }
  
  public String getAlgorithm() {
    return "GOST3410";
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  public byte[] getEncoded() {
    byte[] arrayOfByte2;
    byte[] arrayOfByte1 = getY().toByteArray();
    if (arrayOfByte1[0] == 0) {
      arrayOfByte2 = new byte[arrayOfByte1.length - 1];
    } else {
      arrayOfByte2 = new byte[arrayOfByte1.length];
    } 
    for (byte b = 0; b != arrayOfByte2.length; b++)
      arrayOfByte2[b] = arrayOfByte1[arrayOfByte1.length - 1 - b]; 
    try {
      SubjectPublicKeyInfo subjectPublicKeyInfo;
      if (this.gost3410Spec instanceof GOST3410ParameterSpec) {
        if (this.gost3410Spec.getEncryptionParamSetOID() != null) {
          subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94, (ASN1Encodable)new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.gost3410Spec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getDigestParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getEncryptionParamSetOID()))), (ASN1Encodable)new DEROctetString(arrayOfByte2));
        } else {
          subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94, (ASN1Encodable)new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.gost3410Spec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getDigestParamSetOID()))), (ASN1Encodable)new DEROctetString(arrayOfByte2));
        } 
      } else {
        subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94), (ASN1Encodable)new DEROctetString(arrayOfByte2));
      } 
      return KeyUtil.getEncodedSubjectPublicKeyInfo(subjectPublicKeyInfo);
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public GOST3410Params getParameters() {
    return this.gost3410Spec;
  }
  
  public BigInteger getY() {
    return this.y;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("GOST3410 Public Key").append(str);
    stringBuffer.append("            y: ").append(getY().toString(16)).append(str);
    return stringBuffer.toString();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof BCGOST3410PublicKey) {
      BCGOST3410PublicKey bCGOST3410PublicKey = (BCGOST3410PublicKey)paramObject;
      return (this.y.equals(bCGOST3410PublicKey.y) && this.gost3410Spec.equals(bCGOST3410PublicKey.gost3410Spec));
    } 
    return false;
  }
  
  public int hashCode() {
    return this.y.hashCode() ^ this.gost3410Spec.hashCode();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    String str = (String)paramObjectInputStream.readObject();
    if (str != null) {
      this.gost3410Spec = (GOST3410Params)new GOST3410ParameterSpec(str, (String)paramObjectInputStream.readObject(), (String)paramObjectInputStream.readObject());
    } else {
      this.gost3410Spec = (GOST3410Params)new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec((BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject()));
      paramObjectInputStream.readObject();
      paramObjectInputStream.readObject();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (this.gost3410Spec.getPublicKeyParamSetOID() != null) {
      paramObjectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParamSetOID());
      paramObjectOutputStream.writeObject(this.gost3410Spec.getDigestParamSetOID());
      paramObjectOutputStream.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
    } else {
      paramObjectOutputStream.writeObject(null);
      paramObjectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParameters().getP());
      paramObjectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParameters().getQ());
      paramObjectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParameters().getA());
      paramObjectOutputStream.writeObject(this.gost3410Spec.getDigestParamSetOID());
      paramObjectOutputStream.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
    } 
  }
}
