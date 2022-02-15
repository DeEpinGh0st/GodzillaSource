package org.bouncycastle.jcajce;

import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

public interface PKIXCRLStore<T extends java.security.cert.CRL> extends Store<T> {
  Collection<T> getMatches(Selector<T> paramSelector) throws StoreException;
}
