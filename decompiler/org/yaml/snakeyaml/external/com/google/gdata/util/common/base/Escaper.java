package org.yaml.snakeyaml.external.com.google.gdata.util.common.base;

public interface Escaper {
  String escape(String paramString);
  
  Appendable escape(Appendable paramAppendable);
}
