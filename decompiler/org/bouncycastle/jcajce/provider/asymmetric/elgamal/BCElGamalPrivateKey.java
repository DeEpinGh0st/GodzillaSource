package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.jce.spec.ElGamalPrivateKeySpec;

public class BCElGamalPrivateKey implements ElGamalPrivateKey, DHPrivateKey, PKCS12BagAttributeCarrier {
  static final long serialVersionUID = 4819350091141529678L;
  
  private BigInteger x;
  
  private transient ElGamalParameterSpec elSpec;
  
  private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();
  
  protected BCElGamalPrivateKey() {}
  
  BCElGamalPrivateKey(ElGamalPrivateKey paramElGamalPrivateKey) {
    this.x = paramElGamalPrivateKey.getX();
    this.elSpec = paramElGamalPrivateKey.getParameters();
  }
  
  BCElGamalPrivateKey(DHPrivateKey paramDHPrivateKey) {
    this.x = paramDHPrivateKey.getX();
    this.elSpec = new ElGamalParameterSpec(paramDHPrivateKey.getParams().getP(), paramDHPrivateKey.getParams().getG());
  }
  
  BCElGamalPrivateKey(ElGamalPrivateKeySpec paramElGamalPrivateKeySpec) {
    this.x = paramElGamalPrivateKeySpec.getX();
    this.elSpec = new ElGamalParameterSpec(paramElGamalPrivateKeySpec.getParams().getP(), paramElGamalPrivateKeySpec.getParams().getG());
  }
  
  BCElGamalPrivateKey(DHPrivateKeySpec paramDHPrivateKeySpec) {
    this.x = paramDHPrivateKeySpec.getX();
    this.elSpec = new ElGamalParameterSpec(paramDHPrivateKeySpec.getP(), paramDHPrivateKeySpec.getG());
  }
  
  BCElGamalPrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ElGamalParameter elGamalParameter = ElGamalParameter.getInstance(paramPrivateKeyInfo.getPrivateKeyAlgorithm().getParameters());
    ASN1Integer aSN1Integer = ASN1Integer.getInstance(paramPrivateKeyInfo.parsePrivateKey());
    this.x = aSN1Integer.getValue();
    this.elSpec = new ElGamalParameterSpec(elGamalParameter.getP(), elGamalParameter.getG());
  }
  
  BCElGamalPrivateKey(ElGamalPrivateKeyParameters paramElGamalPrivateKeyParameters) {
    this.x = paramElGamalPrivateKeyParameters.getX();
    this.elSpec = new ElGamalParameterSpec(paramElGamalPrivateKeyParameters.getParameters().getP(), paramElGamalPrivateKeyParameters.getParameters().getG());
  }
  
  public String getAlgorithm() {
    return "ElGamal";
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public byte[] getEncoded() {
    try {
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, (ASN1Encodable)new ElGamalParameter(this.elSpec.getP(), this.elSpec.getG())), (ASN1Encodable)new ASN1Integer(getX()));
      return privateKeyInfo.getEncoded("DER");
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public ElGamalParameterSpec getParameters() {
    return this.elSpec;
  }
  
  public DHParameterSpec getParams() {
    return new DHParameterSpec(this.elSpec.getP(), this.elSpec.getG());
  }
  
  public BigInteger getX() {
    return this.x;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DHPrivateKey))
      return false; 
    DHPrivateKey dHPrivateKey = (DHPrivateKey)paramObject;
    return (getX().equals(dHPrivateKey.getX()) && getParams().getG().equals(dHPrivateKey.getParams().getG()) && getParams().getP().equals(dHPrivateKey.getParams().getP()) && getParams().getL() == dHPrivateKey.getParams().getL());
  }
  
  public int hashCode() {
    return getX().hashCode() ^ getParams().getG().hashCode() ^ getParams().getP().hashCode() ^ getParams().getL();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.elSpec = new ElGamalParameterSpec((BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject());
    this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(this.elSpec.getP());
    paramObjectOutputStream.writeObject(this.elSpec.getG());
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
}
