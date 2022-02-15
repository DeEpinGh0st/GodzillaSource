package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPublicKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.util.Strings;

public class JDKDSAPublicKey implements DSAPublicKey {
  private static final long serialVersionUID = 1752452449903495175L;
  
  private BigInteger y;
  
  private DSAParams dsaSpec;
  
  JDKDSAPublicKey(DSAPublicKeySpec paramDSAPublicKeySpec) {
    this.y = paramDSAPublicKeySpec.getY();
    this.dsaSpec = new DSAParameterSpec(paramDSAPublicKeySpec.getP(), paramDSAPublicKeySpec.getQ(), paramDSAPublicKeySpec.getG());
  }
  
  JDKDSAPublicKey(DSAPublicKey paramDSAPublicKey) {
    this.y = paramDSAPublicKey.getY();
    this.dsaSpec = paramDSAPublicKey.getParams();
  }
  
  JDKDSAPublicKey(DSAPublicKeyParameters paramDSAPublicKeyParameters) {
    this.y = paramDSAPublicKeyParameters.getY();
    this.dsaSpec = new DSAParameterSpec(paramDSAPublicKeyParameters.getParameters().getP(), paramDSAPublicKeyParameters.getParameters().getQ(), paramDSAPublicKeyParameters.getParameters().getG());
  }
  
  JDKDSAPublicKey(BigInteger paramBigInteger, DSAParameterSpec paramDSAParameterSpec) {
    this.y = paramBigInteger;
    this.dsaSpec = paramDSAParameterSpec;
  }
  
  JDKDSAPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    ASN1Integer aSN1Integer;
    try {
      aSN1Integer = (ASN1Integer)paramSubjectPublicKeyInfo.parsePublicKey();
    } catch (IOException iOException) {
      throw new IllegalArgumentException("invalid info structure in DSA public key");
    } 
    this.y = aSN1Integer.getValue();
    if (isNotNull(paramSubjectPublicKeyInfo.getAlgorithm().getParameters())) {
      DSAParameter dSAParameter = DSAParameter.getInstance(paramSubjectPublicKeyInfo.getAlgorithm().getParameters());
      this.dsaSpec = new DSAParameterSpec(dSAParameter.getP(), dSAParameter.getQ(), dSAParameter.getG());
    } 
  }
  
  private boolean isNotNull(ASN1Encodable paramASN1Encodable) {
    return (paramASN1Encodable != null && !DERNull.INSTANCE.equals(paramASN1Encodable));
  }
  
  public String getAlgorithm() {
    return "DSA";
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  public byte[] getEncoded() {
    try {
      return (this.dsaSpec == null) ? (new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa), (ASN1Encodable)new ASN1Integer(this.y))).getEncoded("DER") : (new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG())), (ASN1Encodable)new ASN1Integer(this.y))).getEncoded("DER");
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public DSAParams getParams() {
    return this.dsaSpec;
  }
  
  public BigInteger getY() {
    return this.y;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("DSA Public Key").append(str);
    stringBuffer.append("            y: ").append(getY().toString(16)).append(str);
    return stringBuffer.toString();
  }
  
  public int hashCode() {
    return getY().hashCode() ^ getParams().getG().hashCode() ^ getParams().getP().hashCode() ^ getParams().getQ().hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DSAPublicKey))
      return false; 
    DSAPublicKey dSAPublicKey = (DSAPublicKey)paramObject;
    return (getY().equals(dSAPublicKey.getY()) && getParams().getG().equals(dSAPublicKey.getParams().getG()) && getParams().getP().equals(dSAPublicKey.getParams().getP()) && getParams().getQ().equals(dSAPublicKey.getParams().getQ()));
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.y = (BigInteger)paramObjectInputStream.readObject();
    this.dsaSpec = new DSAParameterSpec((BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject());
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeObject(this.y);
    paramObjectOutputStream.writeObject(this.dsaSpec.getP());
    paramObjectOutputStream.writeObject(this.dsaSpec.getQ());
    paramObjectOutputStream.writeObject(this.dsaSpec.getG());
  }
}
