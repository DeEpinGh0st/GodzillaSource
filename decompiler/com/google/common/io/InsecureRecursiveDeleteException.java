package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import java.nio.file.FileSystemException;
































@Beta
@GwtIncompatible
public final class InsecureRecursiveDeleteException
  extends FileSystemException
{
  public InsecureRecursiveDeleteException(String file) {
    super(file, null, "unable to guarantee security of recursive delete");
  }
}
