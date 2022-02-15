package org.bouncycastle.x509;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class ExtendedPKIXParameters extends PKIXParameters {
  private List stores = new ArrayList();
  
  private Selector selector;
  
  private boolean additionalLocationsEnabled;
  
  private List additionalStores = new ArrayList();
  
  private Set trustedACIssuers = new HashSet();
  
  private Set necessaryACAttributes = new HashSet();
  
  private Set prohibitedACAttributes = new HashSet();
  
  private Set attrCertCheckers = new HashSet();
  
  public static final int PKIX_VALIDITY_MODEL = 0;
  
  public static final int CHAIN_VALIDITY_MODEL = 1;
  
  private int validityModel = 0;
  
  private boolean useDeltas = false;
  
  public ExtendedPKIXParameters(Set<TrustAnchor> paramSet) throws InvalidAlgorithmParameterException {
    super(paramSet);
  }
  
  public static ExtendedPKIXParameters getInstance(PKIXParameters paramPKIXParameters) {
    ExtendedPKIXParameters extendedPKIXParameters;
    try {
      extendedPKIXParameters = new ExtendedPKIXParameters(paramPKIXParameters.getTrustAnchors());
    } catch (Exception exception) {
      throw new RuntimeException(exception.getMessage());
    } 
    extendedPKIXParameters.setParams(paramPKIXParameters);
    return extendedPKIXParameters;
  }
  
  protected void setParams(PKIXParameters paramPKIXParameters) {
    setDate(paramPKIXParameters.getDate());
    setCertPathCheckers(paramPKIXParameters.getCertPathCheckers());
    setCertStores(paramPKIXParameters.getCertStores());
    setAnyPolicyInhibited(paramPKIXParameters.isAnyPolicyInhibited());
    setExplicitPolicyRequired(paramPKIXParameters.isExplicitPolicyRequired());
    setPolicyMappingInhibited(paramPKIXParameters.isPolicyMappingInhibited());
    setRevocationEnabled(paramPKIXParameters.isRevocationEnabled());
    setInitialPolicies(paramPKIXParameters.getInitialPolicies());
    setPolicyQualifiersRejected(paramPKIXParameters.getPolicyQualifiersRejected());
    setSigProvider(paramPKIXParameters.getSigProvider());
    setTargetCertConstraints(paramPKIXParameters.getTargetCertConstraints());
    try {
      setTrustAnchors(paramPKIXParameters.getTrustAnchors());
    } catch (Exception exception) {
      throw new RuntimeException(exception.getMessage());
    } 
    if (paramPKIXParameters instanceof ExtendedPKIXParameters) {
      ExtendedPKIXParameters extendedPKIXParameters = (ExtendedPKIXParameters)paramPKIXParameters;
      this.validityModel = extendedPKIXParameters.validityModel;
      this.useDeltas = extendedPKIXParameters.useDeltas;
      this.additionalLocationsEnabled = extendedPKIXParameters.additionalLocationsEnabled;
      this.selector = (extendedPKIXParameters.selector == null) ? null : (Selector)extendedPKIXParameters.selector.clone();
      this.stores = new ArrayList(extendedPKIXParameters.stores);
      this.additionalStores = new ArrayList(extendedPKIXParameters.additionalStores);
      this.trustedACIssuers = new HashSet(extendedPKIXParameters.trustedACIssuers);
      this.prohibitedACAttributes = new HashSet(extendedPKIXParameters.prohibitedACAttributes);
      this.necessaryACAttributes = new HashSet(extendedPKIXParameters.necessaryACAttributes);
      this.attrCertCheckers = new HashSet(extendedPKIXParameters.attrCertCheckers);
    } 
  }
  
  public boolean isUseDeltasEnabled() {
    return this.useDeltas;
  }
  
  public void setUseDeltasEnabled(boolean paramBoolean) {
    this.useDeltas = paramBoolean;
  }
  
  public int getValidityModel() {
    return this.validityModel;
  }
  
  public void setCertStores(List paramList) {
    if (paramList != null) {
      Iterator<CertStore> iterator = paramList.iterator();
      while (iterator.hasNext())
        addCertStore(iterator.next()); 
    } 
  }
  
  public void setStores(List<?> paramList) {
    if (paramList == null) {
      this.stores = new ArrayList();
    } else {
      Iterator iterator = paramList.iterator();
      while (iterator.hasNext()) {
        if (!(iterator.next() instanceof Store))
          throw new ClassCastException("All elements of list must be of type org.bouncycastle.util.Store."); 
      } 
      this.stores = new ArrayList(paramList);
    } 
  }
  
  public void addStore(Store paramStore) {
    if (paramStore != null)
      this.stores.add(paramStore); 
  }
  
  public void addAdditionalStore(Store paramStore) {
    if (paramStore != null)
      this.additionalStores.add(paramStore); 
  }
  
  public void addAddionalStore(Store paramStore) {
    addAdditionalStore(paramStore);
  }
  
  public List getAdditionalStores() {
    return Collections.unmodifiableList(this.additionalStores);
  }
  
  public List getStores() {
    return Collections.unmodifiableList(new ArrayList(this.stores));
  }
  
  public void setValidityModel(int paramInt) {
    this.validityModel = paramInt;
  }
  
  public Object clone() {
    ExtendedPKIXParameters extendedPKIXParameters;
    try {
      extendedPKIXParameters = new ExtendedPKIXParameters(getTrustAnchors());
    } catch (Exception exception) {
      throw new RuntimeException(exception.getMessage());
    } 
    extendedPKIXParameters.setParams(this);
    return extendedPKIXParameters;
  }
  
  public boolean isAdditionalLocationsEnabled() {
    return this.additionalLocationsEnabled;
  }
  
  public void setAdditionalLocationsEnabled(boolean paramBoolean) {
    this.additionalLocationsEnabled = paramBoolean;
  }
  
  public Selector getTargetConstraints() {
    return (this.selector != null) ? (Selector)this.selector.clone() : null;
  }
  
  public void setTargetConstraints(Selector paramSelector) {
    if (paramSelector != null) {
      this.selector = (Selector)paramSelector.clone();
    } else {
      this.selector = null;
    } 
  }
  
  public void setTargetCertConstraints(CertSelector paramCertSelector) {
    super.setTargetCertConstraints(paramCertSelector);
    if (paramCertSelector != null) {
      this.selector = X509CertStoreSelector.getInstance((X509CertSelector)paramCertSelector);
    } else {
      this.selector = null;
    } 
  }
  
  public Set getTrustedACIssuers() {
    return Collections.unmodifiableSet(this.trustedACIssuers);
  }
  
  public void setTrustedACIssuers(Set paramSet) {
    if (paramSet == null) {
      this.trustedACIssuers.clear();
      return;
    } 
    Iterator iterator = paramSet.iterator();
    while (iterator.hasNext()) {
      if (!(iterator.next() instanceof TrustAnchor))
        throw new ClassCastException("All elements of set must be of type " + TrustAnchor.class.getName() + "."); 
    } 
    this.trustedACIssuers.clear();
    this.trustedACIssuers.addAll(paramSet);
  }
  
  public Set getNecessaryACAttributes() {
    return Collections.unmodifiableSet(this.necessaryACAttributes);
  }
  
  public void setNecessaryACAttributes(Set paramSet) {
    if (paramSet == null) {
      this.necessaryACAttributes.clear();
      return;
    } 
    Iterator iterator = paramSet.iterator();
    while (iterator.hasNext()) {
      if (!(iterator.next() instanceof String))
        throw new ClassCastException("All elements of set must be of type String."); 
    } 
    this.necessaryACAttributes.clear();
    this.necessaryACAttributes.addAll(paramSet);
  }
  
  public Set getProhibitedACAttributes() {
    return Collections.unmodifiableSet(this.prohibitedACAttributes);
  }
  
  public void setProhibitedACAttributes(Set paramSet) {
    if (paramSet == null) {
      this.prohibitedACAttributes.clear();
      return;
    } 
    Iterator iterator = paramSet.iterator();
    while (iterator.hasNext()) {
      if (!(iterator.next() instanceof String))
        throw new ClassCastException("All elements of set must be of type String."); 
    } 
    this.prohibitedACAttributes.clear();
    this.prohibitedACAttributes.addAll(paramSet);
  }
  
  public Set getAttrCertCheckers() {
    return Collections.unmodifiableSet(this.attrCertCheckers);
  }
  
  public void setAttrCertCheckers(Set paramSet) {
    if (paramSet == null) {
      this.attrCertCheckers.clear();
      return;
    } 
    Iterator iterator = paramSet.iterator();
    while (iterator.hasNext()) {
      if (!(iterator.next() instanceof PKIXAttrCertChecker))
        throw new ClassCastException("All elements of set must be of type " + PKIXAttrCertChecker.class.getName() + "."); 
    } 
    this.attrCertCheckers.clear();
    this.attrCertCheckers.addAll(paramSet);
  }
}
