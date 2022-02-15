package org.bouncycastle.pkcs.bc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestProvider;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilderProvider;

public class BcPKCS12MacCalculatorBuilderProvider implements PKCS12MacCalculatorBuilderProvider {
  private BcDigestProvider digestProvider;
  
  public BcPKCS12MacCalculatorBuilderProvider(BcDigestProvider paramBcDigestProvider) {
    this.digestProvider = paramBcDigestProvider;
  }
  
  public PKCS12MacCalculatorBuilder get(final AlgorithmIdentifier algorithmIdentifier) {
    return new PKCS12MacCalculatorBuilder() {
        public MacCalculator build(char[] param1ArrayOfchar) throws OperatorCreationException {
          PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
          return PKCS12PBEUtils.createMacCalculator(algorithmIdentifier.getAlgorithm(), BcPKCS12MacCalculatorBuilderProvider.this.digestProvider.get(algorithmIdentifier), pKCS12PBEParams, param1ArrayOfchar);
        }
        
        public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
          return new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE);
        }
      };
  }
}
