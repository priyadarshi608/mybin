package org.apache.log4j.spi;

import java.io.Serializable;
import org.apache.log4j.Category;
import org.apache.log4j.DefaultThrowableRenderer;






























public class ThrowableInformation
  implements Serializable
{
  static final long serialVersionUID = -4748765566864322735L;
  private transient Throwable throwable;
  private transient Category category;
  private String[] rep;
  
  public ThrowableInformation(Throwable throwable)
  {
    this.throwable = throwable;
  }
  





  public ThrowableInformation(Throwable throwable, Category category)
  {
    this.throwable = throwable;
    this.category = category;
  }
  




  public ThrowableInformation(String[] r)
  {
    if (r != null) {
      rep = ((String[])r.clone());
    }
  }
  

  public Throwable getThrowable()
  {
    return throwable;
  }
  
  public synchronized String[] getThrowableStrRep() {
    if (rep == null) {
      ThrowableRenderer renderer = null;
      if (category != null) {
        LoggerRepository repo = category.getLoggerRepository();
        if ((repo instanceof ThrowableRendererSupport)) {
          renderer = ((ThrowableRendererSupport)repo).getThrowableRenderer();
        }
      }
      if (renderer == null) {
        rep = DefaultThrowableRenderer.render(throwable);
      } else {
        rep = renderer.doRender(throwable);
      }
    }
    return (String[])rep.clone();
  }
}
