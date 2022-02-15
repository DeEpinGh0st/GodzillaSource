package com.intellij.uiDesigner.compiler;


















public class RecursiveFormNestingException
  extends Exception
{
  public RecursiveFormNestingException() {
    super("Recursive form nesting is not allowed");
  }
}
