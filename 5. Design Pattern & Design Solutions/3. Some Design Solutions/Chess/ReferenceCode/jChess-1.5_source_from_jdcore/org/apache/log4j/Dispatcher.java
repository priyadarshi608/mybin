package org.apache.log4j;

import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.BoundedFIFO;
import org.apache.log4j.spi.LoggingEvent;


















/**
 * @deprecated
 */
class Dispatcher
  extends Thread
{
  /**
   * @deprecated
   */
  private BoundedFIFO bf;
  private AppenderAttachableImpl aai;
  private boolean interrupted = false;
  
  AsyncAppender container;
  

  /**
   * @deprecated
   */
  Dispatcher(BoundedFIFO bf, AsyncAppender container)
  {
    this.bf = bf;
    this.container = container;
    aai = aai;
    


    setDaemon(true);
    

    setPriority(1);
    setName("Dispatcher-" + getName());
  }
  



  void close()
  {
    synchronized (bf) {
      interrupted = true;
      


      if (bf.length() == 0) {
        bf.notify();
      }
    }
  }
  





  public void run()
  {
    for (;;)
    {
      LoggingEvent event;
      




      synchronized (bf) {
        if (bf.length() == 0)
        {
          if (interrupted) {
            break;
          }
          

          try
          {
            bf.wait();
          }
          catch (InterruptedException e) {
            break;
          }
        }
        event = bf.get();
        
        if (bf.wasFull())
        {
          bf.notify();
        }
      }
      

      synchronized (container.aai) {
        if ((aai != null) && (event != null)) {
          aai.appendLoopOnAppenders(event);
        }
      }
    }
    


    aai.removeAllAppenders();
  }
}
