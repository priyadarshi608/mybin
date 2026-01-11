package org.jdesktop.application;

import java.util.EventObject;












public class TaskEvent<T>
  extends EventObject
{
  private final T value;
  
  public final T getValue()
  {
    return value;
  }
  





  public TaskEvent(Task paramTask, T paramT)
  {
    super(paramTask);
    value = paramT;
  }
}
