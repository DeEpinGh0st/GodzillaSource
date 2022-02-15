package org.bouncycastle.jcajce;

import java.security.cert.CertPathParameters;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.x509.GeneralName;

public class PKIXExtendedParameters implements CertPathParameters {
  public static final int PKIX_VALIDITY_MODEL = 0;
  
  public static final int CHAIN_VALIDITY_MODEL = 1;
  
  private final PKIXParameters baseParameters;
  
  private final PKIXCertStoreSelector targetConstraints;
  
  private final Date date;
  
  private final List<PKIXCertStore> extraCertStores;
  
  private final Map<GeneralName, PKIXCertStore> namedCertificateStoreMap;
  
  private final List<PKIXCRLStore> extraCRLStores;
  
  private final Map<GeneralName, PKIXCRLStore> namedCRLStoreMap;
  
  private final boolean revocationEnabled;
  
  private final boolean useDeltas;
  
  private final int validityModel;
  
  private final Set<TrustAnchor> trustAnchors;
  
  private PKIXExtendedParameters(Builder paramBuilder) {
    this.baseParameters = paramBuilder.baseParameters;
    this.date = paramBuilder.date;
    this.extraCertStores = Collections.unmodifiableList(paramBuilder.extraCertStores);
    this.namedCertificateStoreMap = Collections.unmodifiableMap(new HashMap<GeneralName, PKIXCertStore>(paramBuilder.namedCertificateStoreMap));
    this.extraCRLStores = Collections.unmodifiableList(paramBuilder.extraCRLStores);
    this.namedCRLStoreMap = Collections.unmodifiableMap(new HashMap<GeneralName, PKIXCRLStore>(paramBuilder.namedCRLStoreMap));
    this.targetConstraints = paramBuilder.targetConstraints;
    this.revocationEnabled = paramBuilder.revocationEnabled;
    this.useDeltas = paramBuilder.useDeltas;
    this.validityModel = paramBuilder.validityModel;
    this.trustAnchors = Collections.unmodifiableSet(paramBuilder.trustAnchors);
  }
  
  public List<PKIXCertStore> getCertificateStores() {
    return this.extraCertStores;
  }
  
  public Map<GeneralName, PKIXCertStore> getNamedCertificateStoreMap() {
    return this.namedCertificateStoreMap;
  }
  
  public List<PKIXCRLStore> getCRLStores() {
    return this.extraCRLStores;
  }
  
  public Map<GeneralName, PKIXCRLStore> getNamedCRLStoreMap() {
    return this.namedCRLStoreMap;
  }
  
  public Date getDate() {
    return new Date(this.date.getTime());
  }
  
  public boolean isUseDeltasEnabled() {
    return this.useDeltas;
  }
  
  public int getValidityModel() {
    return this.validityModel;
  }
  
  public Object clone() {
    return this;
  }
  
  public PKIXCertStoreSelector getTargetConstraints() {
    return this.targetConstraints;
  }
  
  public Set getTrustAnchors() {
    return this.trustAnchors;
  }
  
  public Set getInitialPolicies() {
    return this.baseParameters.getInitialPolicies();
  }
  
  public String getSigProvider() {
    return this.baseParameters.getSigProvider();
  }
  
  public boolean isExplicitPolicyRequired() {
    return this.baseParameters.isExplicitPolicyRequired();
  }
  
  public boolean isAnyPolicyInhibited() {
    return this.baseParameters.isAnyPolicyInhibited();
  }
  
  public boolean isPolicyMappingInhibited() {
    return this.baseParameters.isPolicyMappingInhibited();
  }
  
  public List getCertPathCheckers() {
    return this.baseParameters.getCertPathCheckers();
  }
  
  public List<CertStore> getCertStores() {
    return this.baseParameters.getCertStores();
  }
  
  public boolean isRevocationEnabled() {
    return this.revocationEnabled;
  }
  
  public static class Builder {
    private final PKIXParameters baseParameters;
    
    private final Date date;
    
    private PKIXCertStoreSelector targetConstraints;
    
    private List<PKIXCertStore> extraCertStores = new ArrayList<PKIXCertStore>();
    
    private Map<GeneralName, PKIXCertStore> namedCertificateStoreMap = new HashMap<GeneralName, PKIXCertStore>();
    
    private List<PKIXCRLStore> extraCRLStores = new ArrayList<PKIXCRLStore>();
    
    private Map<GeneralName, PKIXCRLStore> namedCRLStoreMap = new HashMap<GeneralName, PKIXCRLStore>();
    
    private boolean revocationEnabled;
    
    private int validityModel = 0;
    
    private boolean useDeltas = false;
    
    private Set<TrustAnchor> trustAnchors;
    
    public Builder(PKIXParameters param1PKIXParameters) {
      this.baseParameters = (PKIXParameters)param1PKIXParameters.clone();
      CertSelector certSelector = param1PKIXParameters.getTargetCertConstraints();
      if (certSelector != null)
        this.targetConstraints = (new PKIXCertStoreSelector.Builder(certSelector)).build(); 
      Date date = param1PKIXParameters.getDate();
      this.date = (date == null) ? new Date() : date;
      this.revocationEnabled = param1PKIXParameters.isRevocationEnabled();
      this.trustAnchors = param1PKIXParameters.getTrustAnchors();
    }
    
    public Builder(PKIXExtendedParameters param1PKIXExtendedParameters) {
      this.baseParameters = param1PKIXExtendedParameters.baseParameters;
      this.date = param1PKIXExtendedParameters.date;
      this.targetConstraints = param1PKIXExtendedParameters.targetConstraints;
      this.extraCertStores = new ArrayList<PKIXCertStore>(param1PKIXExtendedParameters.extraCertStores);
      this.namedCertificateStoreMap = new HashMap<GeneralName, PKIXCertStore>(param1PKIXExtendedParameters.namedCertificateStoreMap);
      this.extraCRLStores = new ArrayList<PKIXCRLStore>(param1PKIXExtendedParameters.extraCRLStores);
      this.namedCRLStoreMap = new HashMap<GeneralName, PKIXCRLStore>(param1PKIXExtendedParameters.namedCRLStoreMap);
      this.useDeltas = param1PKIXExtendedParameters.useDeltas;
      this.validityModel = param1PKIXExtendedParameters.validityModel;
      this.revocationEnabled = param1PKIXExtendedParameters.isRevocationEnabled();
      this.trustAnchors = param1PKIXExtendedParameters.getTrustAnchors();
    }
    
    public Builder addCertificateStore(PKIXCertStore param1PKIXCertStore) {
      this.extraCertStores.add(param1PKIXCertStore);
      return this;
    }
    
    public Builder addNamedCertificateStore(GeneralName param1GeneralName, PKIXCertStore param1PKIXCertStore) {
      this.namedCertificateStoreMap.put(param1GeneralName, param1PKIXCertStore);
      return this;
    }
    
    public Builder addCRLStore(PKIXCRLStore param1PKIXCRLStore) {
      this.extraCRLStores.add(param1PKIXCRLStore);
      return this;
    }
    
    public Builder addNamedCRLStore(GeneralName param1GeneralName, PKIXCRLStore param1PKIXCRLStore) {
      this.namedCRLStoreMap.put(param1GeneralName, param1PKIXCRLStore);
      return this;
    }
    
    public Builder setTargetConstraints(PKIXCertStoreSelector param1PKIXCertStoreSelector) {
      this.targetConstraints = param1PKIXCertStoreSelector;
      return this;
    }
    
    public Builder setUseDeltasEnabled(boolean param1Boolean) {
      this.useDeltas = param1Boolean;
      return this;
    }
    
    public Builder setValidityModel(int param1Int) {
      this.validityModel = param1Int;
      return this;
    }
    
    public Builder setTrustAnchor(TrustAnchor param1TrustAnchor) {
      this.trustAnchors = Collections.singleton(param1TrustAnchor);
      return this;
    }
    
    public Builder setTrustAnchors(Set<TrustAnchor> param1Set) {
      this.trustAnchors = param1Set;
      return this;
    }
    
    public void setRevocationEnabled(boolean param1Boolean) {
      this.revocationEnabled = param1Boolean;
    }
    
    public PKIXExtendedParameters build() {
      return new PKIXExtendedParameters(this);
    }
  }
}
