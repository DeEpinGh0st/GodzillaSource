package org.bouncycastle.cert.dane.fetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.bouncycastle.cert.dane.DANEEntry;
import org.bouncycastle.cert.dane.DANEEntryFetcher;
import org.bouncycastle.cert.dane.DANEEntryFetcherFactory;
import org.bouncycastle.cert.dane.DANEException;

public class JndiDANEFetcherFactory implements DANEEntryFetcherFactory {
  private static final String DANE_TYPE = "53";
  
  private List dnsServerList = new ArrayList();
  
  private boolean isAuthoritative;
  
  public JndiDANEFetcherFactory usingDNSServer(String paramString) {
    this.dnsServerList.add(paramString);
    return this;
  }
  
  public JndiDANEFetcherFactory setAuthoritative(boolean paramBoolean) {
    this.isAuthoritative = paramBoolean;
    return this;
  }
  
  public DANEEntryFetcher build(final String domainName) {
    final Hashtable<Object, Object> env = new Hashtable<Object, Object>();
    hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
    hashtable.put("java.naming.authoritative", this.isAuthoritative ? "true" : "false");
    if (this.dnsServerList.size() > 0) {
      StringBuffer stringBuffer = new StringBuffer();
      Iterator<String> iterator = this.dnsServerList.iterator();
      while (iterator.hasNext()) {
        if (stringBuffer.length() > 0)
          stringBuffer.append(" "); 
        stringBuffer.append("dns://" + iterator.next());
      } 
      hashtable.put("java.naming.provider.url", stringBuffer.toString());
    } 
    return new DANEEntryFetcher() {
        public List getEntries() throws DANEException {
          ArrayList arrayList = new ArrayList();
          try {
            InitialDirContext initialDirContext = new InitialDirContext(env);
            if (domainName.indexOf("_smimecert.") > 0) {
              Attributes attributes = initialDirContext.getAttributes(domainName, new String[] { "53" });
              Attribute attribute = attributes.get("53");
              if (attribute != null)
                JndiDANEFetcherFactory.this.addEntries(arrayList, domainName, attribute); 
            } else {
              NamingEnumeration<Binding> namingEnumeration = initialDirContext.listBindings("_smimecert." + domainName);
              while (namingEnumeration.hasMore()) {
                Binding binding = namingEnumeration.next();
                DirContext dirContext = (DirContext)binding.getObject();
                String str = dirContext.getNameInNamespace().substring(1, dirContext.getNameInNamespace().length() - 1);
                Attributes attributes = initialDirContext.getAttributes(str, new String[] { "53" });
                Attribute attribute = attributes.get("53");
                if (attribute != null) {
                  String str1 = dirContext.getNameInNamespace();
                  String str2 = str1.substring(1, str1.length() - 1);
                  JndiDANEFetcherFactory.this.addEntries(arrayList, str2, attribute);
                } 
              } 
            } 
            return arrayList;
          } catch (NamingException namingException) {
            throw new DANEException("Exception dealing with DNS: " + namingException.getMessage(), namingException);
          } 
        }
      };
  }
  
  private void addEntries(List<DANEEntry> paramList, String paramString, Attribute paramAttribute) throws NamingException, DANEException {
    for (byte b = 0; b != paramAttribute.size(); b++) {
      byte[] arrayOfByte = (byte[])paramAttribute.get(b);
      if (DANEEntry.isValidCertificate(arrayOfByte))
        try {
          paramList.add(new DANEEntry(paramString, arrayOfByte));
        } catch (IOException iOException) {
          throw new DANEException("Exception parsing entry: " + iOException.getMessage(), iOException);
        }  
    } 
  }
}
