package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.util.MessageDigestUtils;

public abstract class AlgorithmParametersSpi extends AlgorithmParametersSpi {
  protected boolean isASN1FormatString(String paramString) {
    return (paramString == null || paramString.equals("ASN.1"));
  }
  
  protected AlgorithmParameterSpec engineGetParameterSpec(Class paramClass) throws InvalidParameterSpecException {
    if (paramClass == null)
      throw new NullPointerException("argument to getParameterSpec must not be null"); 
    return localEngineGetParameterSpec(paramClass);
  }
  
  protected abstract AlgorithmParameterSpec localEngineGetParameterSpec(Class paramClass) throws InvalidParameterSpecException;
  
  public static class OAEP extends AlgorithmParametersSpi {
    OAEPParameterSpec currentSpec;
    
    protected byte[] engineGetEncoded() {
      AlgorithmIdentifier algorithmIdentifier1 = new AlgorithmIdentifier(DigestFactory.getOID(this.currentSpec.getDigestAlgorithm()), (ASN1Encodable)DERNull.INSTANCE);
      MGF1ParameterSpec mGF1ParameterSpec = (MGF1ParameterSpec)this.currentSpec.getMGFParameters();
      AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)new AlgorithmIdentifier(DigestFactory.getOID(mGF1ParameterSpec.getDigestAlgorithm()), (ASN1Encodable)DERNull.INSTANCE));
      PSource.PSpecified pSpecified = (PSource.PSpecified)this.currentSpec.getPSource();
      AlgorithmIdentifier algorithmIdentifier3 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, (ASN1Encodable)new DEROctetString(pSpecified.getValue()));
      RSAESOAEPparams rSAESOAEPparams = new RSAESOAEPparams(algorithmIdentifier1, algorithmIdentifier2, algorithmIdentifier3);
      try {
        return rSAESOAEPparams.getEncoded("DER");
      } catch (IOException iOException) {
        throw new RuntimeException("Error encoding OAEPParameters");
      } 
    }
    
    protected byte[] engineGetEncoded(String param1String) {
      return (isASN1FormatString(param1String) || param1String.equalsIgnoreCase("X.509")) ? engineGetEncoded() : null;
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<OAEPParameterSpec> param1Class) throws InvalidParameterSpecException {
      if (param1Class == OAEPParameterSpec.class || param1Class == AlgorithmParameterSpec.class)
        return this.currentSpec; 
      throw new InvalidParameterSpecException("unknown parameter spec passed to OAEP parameters object.");
    }
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (!(param1AlgorithmParameterSpec instanceof OAEPParameterSpec))
        throw new InvalidParameterSpecException("OAEPParameterSpec required to initialise an OAEP algorithm parameters object"); 
      this.currentSpec = (OAEPParameterSpec)param1AlgorithmParameterSpec;
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte) throws IOException {
      try {
        RSAESOAEPparams rSAESOAEPparams = RSAESOAEPparams.getInstance(param1ArrayOfbyte);
        if (!rSAESOAEPparams.getMaskGenAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1))
          throw new IOException("unknown mask generation function: " + rSAESOAEPparams.getMaskGenAlgorithm().getAlgorithm()); 
        this.currentSpec = new OAEPParameterSpec(MessageDigestUtils.getDigestName(rSAESOAEPparams.getHashAlgorithm().getAlgorithm()), OAEPParameterSpec.DEFAULT.getMGFAlgorithm(), new MGF1ParameterSpec(MessageDigestUtils.getDigestName(AlgorithmIdentifier.getInstance(rSAESOAEPparams.getMaskGenAlgorithm().getParameters()).getAlgorithm())), new PSource.PSpecified(ASN1OctetString.getInstance(rSAESOAEPparams.getPSourceAlgorithm().getParameters()).getOctets()));
      } catch (ClassCastException classCastException) {
        throw new IOException("Not a valid OAEP Parameter encoding.");
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        throw new IOException("Not a valid OAEP Parameter encoding.");
      } 
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte, String param1String) throws IOException {
      if (param1String.equalsIgnoreCase("X.509") || param1String.equalsIgnoreCase("ASN.1")) {
        engineInit(param1ArrayOfbyte);
      } else {
        throw new IOException("Unknown parameter format " + param1String);
      } 
    }
    
    protected String engineToString() {
      return "OAEP Parameters";
    }
  }
  
  public static class PSS extends AlgorithmParametersSpi {
    PSSParameterSpec currentSpec;
    
    protected byte[] engineGetEncoded() throws IOException {
      PSSParameterSpec pSSParameterSpec = this.currentSpec;
      AlgorithmIdentifier algorithmIdentifier1 = new AlgorithmIdentifier(DigestFactory.getOID(pSSParameterSpec.getDigestAlgorithm()), (ASN1Encodable)DERNull.INSTANCE);
      MGF1ParameterSpec mGF1ParameterSpec = (MGF1ParameterSpec)pSSParameterSpec.getMGFParameters();
      AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)new AlgorithmIdentifier(DigestFactory.getOID(mGF1ParameterSpec.getDigestAlgorithm()), (ASN1Encodable)DERNull.INSTANCE));
      RSASSAPSSparams rSASSAPSSparams = new RSASSAPSSparams(algorithmIdentifier1, algorithmIdentifier2, new ASN1Integer(pSSParameterSpec.getSaltLength()), new ASN1Integer(pSSParameterSpec.getTrailerField()));
      return rSASSAPSSparams.getEncoded("DER");
    }
    
    protected byte[] engineGetEncoded(String param1String) throws IOException {
      return (param1String.equalsIgnoreCase("X.509") || param1String.equalsIgnoreCase("ASN.1")) ? engineGetEncoded() : null;
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class<PSSParameterSpec> param1Class) throws InvalidParameterSpecException {
      if (param1Class == PSSParameterSpec.class && this.currentSpec != null)
        return this.currentSpec; 
      throw new InvalidParameterSpecException("unknown parameter spec passed to PSS parameters object.");
    }
    
    protected void engineInit(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidParameterSpecException {
      if (!(param1AlgorithmParameterSpec instanceof PSSParameterSpec))
        throw new InvalidParameterSpecException("PSSParameterSpec required to initialise an PSS algorithm parameters object"); 
      this.currentSpec = (PSSParameterSpec)param1AlgorithmParameterSpec;
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte) throws IOException {
      try {
        RSASSAPSSparams rSASSAPSSparams = RSASSAPSSparams.getInstance(param1ArrayOfbyte);
        if (!rSASSAPSSparams.getMaskGenAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1))
          throw new IOException("unknown mask generation function: " + rSASSAPSSparams.getMaskGenAlgorithm().getAlgorithm()); 
        this.currentSpec = new PSSParameterSpec(MessageDigestUtils.getDigestName(rSASSAPSSparams.getHashAlgorithm().getAlgorithm()), PSSParameterSpec.DEFAULT.getMGFAlgorithm(), new MGF1ParameterSpec(MessageDigestUtils.getDigestName(AlgorithmIdentifier.getInstance(rSASSAPSSparams.getMaskGenAlgorithm().getParameters()).getAlgorithm())), rSASSAPSSparams.getSaltLength().intValue(), rSASSAPSSparams.getTrailerField().intValue());
      } catch (ClassCastException classCastException) {
        throw new IOException("Not a valid PSS Parameter encoding.");
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        throw new IOException("Not a valid PSS Parameter encoding.");
      } 
    }
    
    protected void engineInit(byte[] param1ArrayOfbyte, String param1String) throws IOException {
      if (isASN1FormatString(param1String) || param1String.equalsIgnoreCase("X.509")) {
        engineInit(param1ArrayOfbyte);
      } else {
        throw new IOException("Unknown parameter format " + param1String);
      } 
    }
    
    protected String engineToString() {
      return "PSS Parameters";
    }
  }
}
