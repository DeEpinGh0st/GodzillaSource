package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.GOST3410Params;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PrivateKeySpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public class BCGOST3410PrivateKey implements GOST3410PrivateKey, PKCS12BagAttributeCarrier {
  static final long serialVersionUID = 8581661527592305464L;
  
  private BigInteger x;
  
  private transient GOST3410Params gost3410Spec;
  
  private transient PKCS12BagAttributeCarrier attrCarrier = (PKCS12BagAttributeCarrier)new PKCS12BagAttributeCarrierImpl();
  
  protected BCGOST3410PrivateKey() {}
  
  BCGOST3410PrivateKey(GOST3410PrivateKey paramGOST3410PrivateKey) {
    this.x = paramGOST3410PrivateKey.getX();
    this.gost3410Spec = paramGOST3410PrivateKey.getParameters();
  }
  
  BCGOST3410PrivateKey(GOST3410PrivateKeySpec paramGOST3410PrivateKeySpec) {
    this.x = paramGOST3410PrivateKeySpec.getX();
    this.gost3410Spec = (GOST3410Params)new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec(paramGOST3410PrivateKeySpec.getP(), paramGOST3410PrivateKeySpec.getQ(), paramGOST3410PrivateKeySpec.getA()));
  }
  
  BCGOST3410PrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = new GOST3410PublicKeyAlgParameters((ASN1Sequence)paramPrivateKeyInfo.getAlgorithmId().getParameters());
    ASN1OctetString aSN1OctetString = ASN1OctetString.getInstance(paramPrivateKeyInfo.parsePrivateKey());
    byte[] arrayOfByte1 = aSN1OctetString.getOctets();
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length];
    for (byte b = 0; b != arrayOfByte1.length; b++)
      arrayOfByte2[b] = arrayOfByte1[arrayOfByte1.length - 1 - b]; 
    this.x = new BigInteger(1, arrayOfByte2);
    this.gost3410Spec = (GOST3410Params)GOST3410ParameterSpec.fromPublicKeyAlg(gOST3410PublicKeyAlgParameters);
  }
  
  BCGOST3410PrivateKey(GOST3410PrivateKeyParameters paramGOST3410PrivateKeyParameters, GOST3410ParameterSpec paramGOST3410ParameterSpec) {
    this.x = paramGOST3410PrivateKeyParameters.getX();
    this.gost3410Spec = (GOST3410Params)paramGOST3410ParameterSpec;
    if (paramGOST3410ParameterSpec == null)
      throw new IllegalArgumentException("spec is null"); 
  }
  
  public String getAlgorithm() {
    return "GOST3410";
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public byte[] getEncoded() {
    byte[] arrayOfByte2;
    byte[] arrayOfByte1 = getX().toByteArray();
    if (arrayOfByte1[0] == 0) {
      arrayOfByte2 = new byte[arrayOfByte1.length - 1];
    } else {
      arrayOfByte2 = new byte[arrayOfByte1.length];
    } 
    for (byte b = 0; b != arrayOfByte2.length; b++)
      arrayOfByte2[b] = arrayOfByte1[arrayOfByte1.length - 1 - b]; 
    try {
      PrivateKeyInfo privateKeyInfo;
      if (this.gost3410Spec instanceof GOST3410ParameterSpec) {
        privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94, (ASN1Encodable)new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.gost3410Spec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getDigestParamSetOID()))), (ASN1Encodable)new DEROctetString(arrayOfByte2));
      } else {
        privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94), (ASN1Encodable)new DEROctetString(arrayOfByte2));
      } 
      return privateKeyInfo.getEncoded("DER");
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public GOST3410Params getParameters() {
    return this.gost3410Spec;
  }
  
  public BigInteger getX() {
    return this.x;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof GOST3410PrivateKey))
      return false; 
    GOST3410PrivateKey gOST3410PrivateKey = (GOST3410PrivateKey)paramObject;
    return (getX().equals(gOST3410PrivateKey.getX()) && getParameters().getPublicKeyParameters().equals(gOST3410PrivateKey.getParameters().getPublicKeyParameters()) && getParameters().getDigestParamSetOID().equals(gOST3410PrivateKey.getParameters().getDigestParamSetOID()) && compareObj(getParameters().getEncryptionParamSetOID(), gOST3410PrivateKey.getParameters().getEncryptionParamSetOID()));
  }
  
  private boolean compareObj(Object paramObject1, Object paramObject2) {
    return (paramObject1 == paramObject2) ? true : ((paramObject1 == null) ? false : paramObject1.equals(paramObject2));
  }
  
  public int hashCode() {
    return getX().hashCode() ^ this.gost3410Spec.hashCode();
  }
  
  public void setBagAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.attrCarrier.setBagAttribute(paramASN1ObjectIdentifier, paramASN1Encodable);
  }
  
  public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return this.attrCarrier.getBagAttribute(paramASN1ObjectIdentifier);
  }
  
  public Enumeration getBagAttributeKeys() {
    return this.attrCarrier.getBagAttributeKeys();
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
    this.attrCarrier = (PKCS12BagAttributeCarrier)new PKCS12BagAttributeCarrierImpl();
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
