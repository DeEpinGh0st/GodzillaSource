package org.bouncycastle.crypto.tls;

import java.math.BigInteger;
import java.util.Vector;
import org.bouncycastle.crypto.agreement.srp.SRP6StandardGroups;
import org.bouncycastle.crypto.params.SRP6GroupParameters;

public class DefaultTlsSRPGroupVerifier implements TlsSRPGroupVerifier {
  protected static final Vector DEFAULT_GROUPS = new Vector();
  
  protected Vector groups;
  
  public DefaultTlsSRPGroupVerifier() {
    this(DEFAULT_GROUPS);
  }
  
  public DefaultTlsSRPGroupVerifier(Vector paramVector) {
    this.groups = paramVector;
  }
  
  public boolean accept(SRP6GroupParameters paramSRP6GroupParameters) {
    for (byte b = 0; b < this.groups.size(); b++) {
      if (areGroupsEqual(paramSRP6GroupParameters, this.groups.elementAt(b)))
        return true; 
    } 
    return false;
  }
  
  protected boolean areGroupsEqual(SRP6GroupParameters paramSRP6GroupParameters1, SRP6GroupParameters paramSRP6GroupParameters2) {
    return (paramSRP6GroupParameters1 == paramSRP6GroupParameters2 || (areParametersEqual(paramSRP6GroupParameters1.getN(), paramSRP6GroupParameters2.getN()) && areParametersEqual(paramSRP6GroupParameters1.getG(), paramSRP6GroupParameters2.getG())));
  }
  
  protected boolean areParametersEqual(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    return (paramBigInteger1 == paramBigInteger2 || paramBigInteger1.equals(paramBigInteger2));
  }
  
  static {
    DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_1024);
    DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_1536);
    DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_2048);
    DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_3072);
    DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_4096);
    DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_6144);
    DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_8192);
  }
}
