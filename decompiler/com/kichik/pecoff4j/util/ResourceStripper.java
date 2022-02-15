package com.kichik.pecoff4j.util;

import com.kichik.pecoff4j.PE;
import com.kichik.pecoff4j.io.PEParser;
import java.io.File;










public class ResourceStripper
{
  public static void remove(File pecoff, File output) throws Exception {
    PE pe = PEParser.parse(pecoff);
  }
}
