package org.bouncycastle.jce.provider;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PKIXPolicyNode implements PolicyNode {
  protected List children;
  
  protected int depth;
  
  protected Set expectedPolicies;
  
  protected PolicyNode parent;
  
  protected Set policyQualifiers;
  
  protected String validPolicy;
  
  protected boolean critical;
  
  public PKIXPolicyNode(List paramList, int paramInt, Set paramSet1, PolicyNode paramPolicyNode, Set paramSet2, String paramString, boolean paramBoolean) {
    this.children = paramList;
    this.depth = paramInt;
    this.expectedPolicies = paramSet1;
    this.parent = paramPolicyNode;
    this.policyQualifiers = paramSet2;
    this.validPolicy = paramString;
    this.critical = paramBoolean;
  }
  
  public void addChild(PKIXPolicyNode paramPKIXPolicyNode) {
    this.children.add(paramPKIXPolicyNode);
    paramPKIXPolicyNode.setParent(this);
  }
  
  public Iterator getChildren() {
    return this.children.iterator();
  }
  
  public int getDepth() {
    return this.depth;
  }
  
  public Set getExpectedPolicies() {
    return this.expectedPolicies;
  }
  
  public PolicyNode getParent() {
    return this.parent;
  }
  
  public Set getPolicyQualifiers() {
    return this.policyQualifiers;
  }
  
  public String getValidPolicy() {
    return this.validPolicy;
  }
  
  public boolean hasChildren() {
    return !this.children.isEmpty();
  }
  
  public boolean isCritical() {
    return this.critical;
  }
  
  public void removeChild(PKIXPolicyNode paramPKIXPolicyNode) {
    this.children.remove(paramPKIXPolicyNode);
  }
  
  public void setCritical(boolean paramBoolean) {
    this.critical = paramBoolean;
  }
  
  public void setParent(PKIXPolicyNode paramPKIXPolicyNode) {
    this.parent = paramPKIXPolicyNode;
  }
  
  public String toString() {
    return toString("");
  }
  
  public String toString(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(paramString);
    stringBuffer.append(this.validPolicy);
    stringBuffer.append(" {\n");
    for (byte b = 0; b < this.children.size(); b++)
      stringBuffer.append(((PKIXPolicyNode)this.children.get(b)).toString(paramString + "    ")); 
    stringBuffer.append(paramString);
    stringBuffer.append("}\n");
    return stringBuffer.toString();
  }
  
  public Object clone() {
    return copy();
  }
  
  public PKIXPolicyNode copy() {
    HashSet<String> hashSet1 = new HashSet();
    Iterator<String> iterator = this.expectedPolicies.iterator();
    while (iterator.hasNext())
      hashSet1.add(new String(iterator.next())); 
    HashSet<String> hashSet2 = new HashSet();
    iterator = this.policyQualifiers.iterator();
    while (iterator.hasNext())
      hashSet2.add(new String(iterator.next())); 
    PKIXPolicyNode pKIXPolicyNode = new PKIXPolicyNode(new ArrayList(), this.depth, hashSet1, null, hashSet2, new String(this.validPolicy), this.critical);
    iterator = this.children.iterator();
    while (iterator.hasNext()) {
      PKIXPolicyNode pKIXPolicyNode1 = ((PKIXPolicyNode)iterator.next()).copy();
      pKIXPolicyNode1.setParent(pKIXPolicyNode);
      pKIXPolicyNode.addChild(pKIXPolicyNode1);
    } 
    return pKIXPolicyNode;
  }
  
  public void setExpectedPolicies(Set paramSet) {
    this.expectedPolicies = paramSet;
  }
}
