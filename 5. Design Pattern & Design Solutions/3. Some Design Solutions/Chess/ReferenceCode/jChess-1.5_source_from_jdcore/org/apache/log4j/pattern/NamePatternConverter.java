package org.apache.log4j.pattern;


















public abstract class NamePatternConverter
  extends LoggingEventPatternConverter
{
  private final NameAbbreviator abbreviator;
  

















  protected NamePatternConverter(String name, String style, String[] options)
  {
    super(name, style);
    
    if ((options != null) && (options.length > 0)) {
      abbreviator = NameAbbreviator.getAbbreviator(options[0]);
    } else {
      abbreviator = NameAbbreviator.getDefaultAbbreviator();
    }
  }
  




  protected final void abbreviate(int nameStart, StringBuffer buf)
  {
    abbreviator.abbreviate(nameStart, buf);
  }
}
