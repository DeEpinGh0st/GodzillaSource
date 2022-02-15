package org.springframework.cglib.transform;

public interface MethodFilter {
  boolean accept(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);
}
