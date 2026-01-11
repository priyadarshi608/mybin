package org.jdesktop.swingworker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public abstract class SwingWorker<T, V>
  implements Future<T>, Runnable
{
  private static final int MAX_WORKER_THREADS = 10;
  private volatile int progress;
  private volatile StateValue state;
  private final FutureTask<T> future;
  private final SwingPropertyChangeSupport propertyChangeSupport;
  private AccumulativeRunnable<V> doProcess;
  private AccumulativeRunnable<Integer> doNotifyProgressChange;
  private static final AccumulativeRunnable<Runnable> doSubmit = new DoSubmitAccumulativeRunnable(null);
  private static ExecutorService executorService = null;
  
  public SwingWorker()
  {
    Callable local1 = new Callable()
    {
      public T call()
        throws Exception
      {
        SwingWorker.this.setState(SwingWorker.StateValue.STARTED);
        return doInBackground();
      }
    };
    future = new FutureTask(local1)
    {
      protected void done()
      {
        SwingWorker.this.doneEDT();
        SwingWorker.this.setState(SwingWorker.StateValue.DONE);
      }
    };
    state = StateValue.PENDING;
    propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
    doProcess = null;
    doNotifyProgressChange = null;
  }
  
  protected abstract T doInBackground()
    throws Exception;
  
  public final void run()
  {
    future.run();
  }
  
  protected final void publish(V... paramVarArgs)
  {
    synchronized (this)
    {
      if (doProcess == null) {
        doProcess = new AccumulativeRunnable()
        {
          public void run(List<V> paramAnonymousList)
          {
            process(paramAnonymousList);
          }
          
          protected void submit()
          {
            SwingWorker.doSubmit.add(new Runnable[] { this });
          }
        };
      }
    }
    doProcess.add(paramVarArgs);
  }
  
  protected void process(List<V> paramList) {}
  
  protected void done() {}
  
  protected final void setProgress(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 100)) {
      throw new IllegalArgumentException("the value should be from 0 to 100");
    }
    if (progress == paramInt) {
      return;
    }
    int i = progress;
    progress = paramInt;
    if (!getPropertyChangeSupport().hasListeners("progress")) {
      return;
    }
    synchronized (this)
    {
      if (doNotifyProgressChange == null) {
        doNotifyProgressChange = new AccumulativeRunnable()
        {
          public void run(List<Integer> paramAnonymousList)
          {
            firePropertyChange("progress", paramAnonymousList.get(0), paramAnonymousList.get(paramAnonymousList.size() - 1));
          }
          
          protected void submit()
          {
            SwingWorker.doSubmit.add(new Runnable[] { this });
          }
        };
      }
    }
    doNotifyProgressChange.add(new Integer[] { Integer.valueOf(i), Integer.valueOf(paramInt) });
  }
  
  public final int getProgress()
  {
    return progress;
  }
  
  public final void execute()
  {
    getWorkersExecutorService().execute(this);
  }
  
  public final boolean cancel(boolean paramBoolean)
  {
    return future.cancel(paramBoolean);
  }
  
  public final boolean isCancelled()
  {
    return future.isCancelled();
  }
  
  public final boolean isDone()
  {
    return future.isDone();
  }
  
  public final T get()
    throws InterruptedException, ExecutionException
  {
    return future.get();
  }
  
  public final T get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return future.get(paramLong, paramTimeUnit);
  }
  
  public final void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    getPropertyChangeSupport().addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public final void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    getPropertyChangeSupport().removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public final void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    getPropertyChangeSupport().firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  public final PropertyChangeSupport getPropertyChangeSupport()
  {
    return propertyChangeSupport;
  }
  
  public final StateValue getState()
  {
    if (isDone()) {
      return StateValue.DONE;
    }
    return state;
  }
  
  private void setState(StateValue paramStateValue)
  {
    StateValue localStateValue = state;
    state = paramStateValue;
    firePropertyChange("state", localStateValue, paramStateValue);
  }
  
  private void doneEDT()
  {
    Runnable local5 = new Runnable()
    {
      public void run()
      {
        done();
      }
    };
    if (SwingUtilities.isEventDispatchThread()) {
      local5.run();
    } else {
      SwingUtilities.invokeLater(local5);
    }
  }
  
  private static synchronized ExecutorService getWorkersExecutorService()
  {
    if (executorService == null)
    {
      ThreadFactory local6 = new ThreadFactory()
      {
        final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        
        public Thread newThread(Runnable paramAnonymousRunnable)
        {
          Thread localThread = defaultFactory.newThread(paramAnonymousRunnable);
          localThread.setName("SwingWorker-" + localThread.getName());
          return localThread;
        }
      };
      executorService = new ThreadPoolExecutor(0, 10, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue(), local6)
      {
        private final ReentrantLock pauseLock = new ReentrantLock();
        private final Condition unpaused = pauseLock.newCondition();
        private boolean isPaused = false;
        private final ReentrantLock executeLock = new ReentrantLock();
        
        public void execute(Runnable paramAnonymousRunnable)
        {
          executeLock.lock();
          try
          {
            pauseLock.lock();
            try
            {
              isPaused = true;
            }
            finally
            {
              pauseLock.unlock();
            }
            setCorePoolSize(10);
            super.execute(paramAnonymousRunnable);
            setCorePoolSize(0);
            pauseLock.lock();
            try
            {
              isPaused = false;
              unpaused.signalAll();
            }
            finally
            {
              pauseLock.unlock();
            }
          }
          finally
          {
            executeLock.unlock();
          }
        }
        
        protected void afterExecute(Runnable paramAnonymousRunnable, Throwable paramAnonymousThrowable)
        {
          super.afterExecute(paramAnonymousRunnable, paramAnonymousThrowable);
          pauseLock.lock();
          try
          {
            while (isPaused) {
              unpaused.await();
            }
          }
          catch (InterruptedException localInterruptedException) {}finally
          {
            pauseLock.unlock();
          }
        }
      };
    }
    return executorService;
  }
  
  private static class DoSubmitAccumulativeRunnable
    extends AccumulativeRunnable<Runnable>
    implements ActionListener
  {
    private static final int DELAY = 33;
    
    private DoSubmitAccumulativeRunnable() {}
    
    protected void run(List<Runnable> paramList)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        Runnable localRunnable = (Runnable)localIterator.next();
        localRunnable.run();
      }
    }
    
    protected void submit()
    {
      Timer localTimer = new Timer(33, this);
      localTimer.setRepeats(false);
      localTimer.start();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      run();
    }
  }
  
  public static enum StateValue
  {
    PENDING,  STARTED,  DONE;
    
    private StateValue() {}
  }
}
