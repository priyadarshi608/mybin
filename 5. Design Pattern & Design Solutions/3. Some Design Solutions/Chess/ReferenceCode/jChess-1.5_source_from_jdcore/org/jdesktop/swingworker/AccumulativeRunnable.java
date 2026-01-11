package org.jdesktop.swingworker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

abstract class AccumulativeRunnable<T>
  implements Runnable
{
  private List<T> arguments = null;
  
  AccumulativeRunnable() {}
  
  protected abstract void run(List<T> paramList);
  
  public final void run()
  {
    run(flush());
  }
  
  public final synchronized void add(T... paramVarArgs)
  {
    int i = 1;
    if (arguments == null)
    {
      i = 0;
      arguments = new ArrayList();
    }
    Collections.addAll(arguments, paramVarArgs);
    if (i == 0) {
      submit();
    }
  }
  
  protected void submit()
  {
    SwingUtilities.invokeLater(this);
  }
  
  private final synchronized List<T> flush()
  {
    List localList = arguments;
    arguments = null;
    return localList;
  }
}
