package org.bouncycastle.jcajce.provider.asymmetric.dsa;

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
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.util.Strings;

public class BCDSAPublicKey implements DSAPublicKey {
  private static final long serialVersionUID = 1752452449903495175L;
  
  private static BigInteger ZERO = BigInteger.valueOf(0L);
  
  private BigInteger y;
  
  private transient DSAPublicKeyParameters lwKeyParams;
  
  private transient DSAParams dsaSpec;
  
  BCDSAPublicKey(DSAPublicKeySpec paramDSAPublicKeySpec) {
    this.y = paramDSAPublicKeySpec.getY();
    this.dsaSpec = new DSAParameterSpec(paramDSAPublicKeySpec.getP(), paramDSAPublicKeySpec.getQ(), paramDSAPublicKeySpec.getG());
    this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
  }
  
  BCDSAPublicKey(DSAPublicKey paramDSAPublicKey) {
    this.y = paramDSAPublicKey.getY();
    this.dsaSpec = paramDSAPublicKey.getParams();
    this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
  }
  
  BCDSAPublicKey(DSAPublicKeyParameters paramDSAPublicKeyParameters) {
    this.y = paramDSAPublicKeyParameters.getY();
    if (paramDSAPublicKeyParameters != null) {
      this.dsaSpec = new DSAParameterSpec(paramDSAPublicKeyParameters.getParameters().getP(), paramDSAPublicKeyParameters.getParameters().getQ(), paramDSAPublicKeyParameters.getParameters().getG());
    } else {
      this.dsaSpec = null;
    } 
    this.lwKeyParams = paramDSAPublicKeyParameters;
  }
  
  public BCDSAPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
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
    } else {
      this.dsaSpec = null;
    } 
    this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
  }
  
  private boolean isNotNull(ASN1Encodable paramASN1Encodable) {
    return (paramASN1Encodable != null && !DERNull.INSTANCE.equals(paramASN1Encodable.toASN1Primitive()));
  }
  
  public String getAlgorithm() {
    return "DSA";
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  DSAPublicKeyParameters engineGetKeyParameters() {
    return this.lwKeyParams;
  }
  
  public byte[] getEncoded() {
    return (this.dsaSpec == null) ? KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa), (ASN1Encodable)new ASN1Integer(this.y)) : KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)(new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG())).toASN1Primitive()), (ASN1Encodable)new ASN1Integer(this.y));
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
    stringBuffer.append("DSA Public Key [").append(DSAUtil.generateKeyFingerprint(this.y, getParams())).append("]").append(str);
    stringBuffer.append("            y: ").append(getY().toString(16)).append(str);
    return stringBuffer.toString();
  }
  
  public int hashCode() {
    return (this.dsaSpec != null) ? (getY().hashCode() ^ getParams().getG().hashCode() ^ getParams().getP().hashCode() ^ getParams().getQ().hashCode()) : getY().hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DSAPublicKey))
      return false; 
    DSAPublicKey dSAPublicKey = (DSAPublicKey)paramObject;
    return (this.dsaSpec != null) ? ((getY().equals(dSAPublicKey.getY()) && dSAPublicKey.getParams() != null && getParams().getG().equals(dSAPublicKey.getParams().getG()) && getParams().getP().equals(dSAPublicKey.getParams().getP()) && getParams().getQ().equals(dSAPublicKey.getParams().getQ()))) : ((getY().equals(dSAPublicKey.getY()) && dSAPublicKey.getParams() == null));
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    BigInteger bigInteger = (BigInteger)paramObjectInputStream.readObject();
    if (bigInteger.equals(ZERO)) {
      this.dsaSpec = null;
    } else {
      this.dsaSpec = new DSAParameterSpec(bigInteger, (BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject());
    } 
    this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (this.dsaSpec == null) {
      paramObjectOutputStream.writeObject(ZERO);
    } else {
      paramObjectOutputStream.writeObject(this.dsaSpec.getP());
      paramObjectOutputStream.writeObject(this.dsaSpec.getQ());
      paramObjectOutputStream.writeObject(this.dsaSpec.getG());
    } 
  }
}
