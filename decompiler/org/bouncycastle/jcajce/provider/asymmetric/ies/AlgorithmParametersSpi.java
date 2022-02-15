package org.bouncycastle.jcajce.provider.asymmetric.ies;

import java.io.IOException;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.jce.spec.IESParameterSpec;

public class AlgorithmParametersSpi extends AlgorithmParametersSpi {
  IESParameterSpec currentSpec;
  
  protected boolean isASN1FormatString(String paramString) {
    return (paramString == null || paramString.equals("ASN.1"));
  }
  
  protected AlgorithmParameterSpec engineGetParameterSpec(Class paramClass) throws InvalidParameterSpecException {
    if (paramClass == null)
      throw new NullPointerException("argument to getParameterSpec must not be null"); 
    return localEngineGetParameterSpec(paramClass);
  }
  
  protected byte[] engineGetEncoded() {
    try {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      if (this.currentSpec.getDerivationV() != null)
        aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)new DEROctetString(this.currentSpec.getDerivationV()))); 
      if (this.currentSpec.getEncodingV() != null)
        aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)new DEROctetString(this.currentSpec.getEncodingV()))); 
      aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.currentSpec.getMacKeySize()));
      if (this.currentSpec.getNonce() != null) {
        ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
        aSN1EncodableVector1.add((ASN1Encodable)new ASN1Integer(this.currentSpec.getCipherKeySize()));
        aSN1EncodableVector1.add((ASN1Encodable)new ASN1Integer(this.currentSpec.getNonce()));
        aSN1EncodableVector.add((ASN1Encodable)new DERSequence(aSN1EncodableVector1));
      } 
      return (new DERSequence(aSN1EncodableVector)).getEncoded("DER");
    } catch (IOException iOException) {
      throw new RuntimeException("Error encoding IESParameters");
    } 
  }
  
  protected byte[] engineGetEncoded(String paramString) {
    return (isASN1FormatString(paramString) || paramString.equalsIgnoreCase("X.509")) ? engineGetEncoded() : null;
  }
  
  protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<IESParameterSpec> paramClass) throws InvalidParameterSpecException {
    if (paramClass == IESParameterSpec.class || paramClass == AlgorithmParameterSpec.class)
      return (AlgorithmParameterSpec)this.currentSpec; 
    throw new InvalidParameterSpecException("unknown parameter spec passed to ElGamal parameters object.");
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException {
    if (!(paramAlgorithmParameterSpec instanceof IESParameterSpec))
      throw new InvalidParameterSpecException("IESParameterSpec required to initialise a IES algorithm parameters object"); 
    this.currentSpec = (IESParameterSpec)paramAlgorithmParameterSpec;
  }
  
  protected void engineInit(byte[] paramArrayOfbyte) throws IOException {
    try {
      ASN1Sequence aSN1Sequence = (ASN1Sequence)ASN1Primitive.fromByteArray(paramArrayOfbyte);
      if (aSN1Sequence.size() == 1) {
        this.currentSpec = new IESParameterSpec(null, null, ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).getValue().intValue());
      } else if (aSN1Sequence.size() == 2) {
        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(0));
        if (aSN1TaggedObject.getTagNo() == 0) {
          this.currentSpec = new IESParameterSpec(ASN1OctetString.getInstance(aSN1TaggedObject, false).getOctets(), null, ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1)).getValue().intValue());
        } else {
          this.currentSpec = new IESParameterSpec(null, ASN1OctetString.getInstance(aSN1TaggedObject, false).getOctets(), ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1)).getValue().intValue());
        } 
      } else if (aSN1Sequence.size() == 3) {
        ASN1TaggedObject aSN1TaggedObject1 = ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(0));
        ASN1TaggedObject aSN1TaggedObject2 = ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(1));
        this.currentSpec = new IESParameterSpec(ASN1OctetString.getInstance(aSN1TaggedObject1, false).getOctets(), ASN1OctetString.getInstance(aSN1TaggedObject2, false).getOctets(), ASN1Integer.getInstance(aSN1Sequence.getObjectAt(2)).getValue().intValue());
      } else if (aSN1Sequence.size() == 4) {
        ASN1TaggedObject aSN1TaggedObject1 = ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(0));
        ASN1TaggedObject aSN1TaggedObject2 = ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(1));
        ASN1Sequence aSN1Sequence1 = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(3));
        this.currentSpec = new IESParameterSpec(ASN1OctetString.getInstance(aSN1TaggedObject1, false).getOctets(), ASN1OctetString.getInstance(aSN1TaggedObject2, false).getOctets(), ASN1Integer.getInstance(aSN1Sequence.getObjectAt(2)).getValue().intValue(), ASN1Integer.getInstance(aSN1Sequence1.getObjectAt(0)).getValue().intValue(), ASN1OctetString.getInstance(aSN1Sequence1.getObjectAt(1)).getOctets());
      } 
    } catch (ClassCastException classCastException) {
      throw new IOException("Not a valid IES Parameter encoding.");
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new IOException("Not a valid IES Parameter encoding.");
    } 
  }
  
  protected void engineInit(byte[] paramArrayOfbyte, String paramString) throws IOException {
    if (isASN1FormatString(paramString) || paramString.equalsIgnoreCase("X.509")) {
      engineInit(paramArrayOfbyte);
    } else {
      throw new IOException("Unknown parameter format " + paramString);
    } 
  }
  
  protected String engineToString() {
    return "IES Parameters";
  }
}
