package org.mozilla.javascript.regexp;








































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































class REGlobalData
{
  boolean multiline;
  RECompiled regexp;
  int skipped;
  int cp;
  long[] parens;
  REProgState stateStackTop;
  REBackTrackData backTrackStackTop;
  
  int parensIndex(int i) {
    return (int)this.parens[i];
  }




  
  int parensLength(int i) {
    return (int)(this.parens[i] >>> 32L);
  }


  
  void setParens(int i, int index, int length) {
    if (this.backTrackStackTop != null && this.backTrackStackTop.parens == this.parens) {
      this.parens = (long[])this.parens.clone();
    }
    this.parens[i] = index & 0xFFFFFFFFL | length << 32L;
  }
}