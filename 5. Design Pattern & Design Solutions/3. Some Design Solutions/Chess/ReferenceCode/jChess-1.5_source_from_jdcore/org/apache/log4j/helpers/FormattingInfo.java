package org.apache.log4j.helpers;













public class FormattingInfo
{
  public FormattingInfo() {}
  












  int min = -1;
  int max = Integer.MAX_VALUE;
  boolean leftAlign = false;
  
  void reset() {
    min = -1;
    max = Integer.MAX_VALUE;
    leftAlign = false;
  }
  
  void dump() {
    LogLog.debug("min=" + min + ", max=" + max + ", leftAlign=" + leftAlign);
  }
}
