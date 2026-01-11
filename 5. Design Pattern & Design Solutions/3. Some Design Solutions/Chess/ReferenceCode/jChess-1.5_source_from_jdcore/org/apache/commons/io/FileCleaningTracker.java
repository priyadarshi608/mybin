package org.apache.commons.io;

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Collection;
import java.util.Vector;






























public class FileCleaningTracker
{
  ReferenceQueue q;
  final Collection trackers;
  volatile boolean exitWhenFinished;
  Thread reaper;
  
  public FileCleaningTracker()
  {
    q = new ReferenceQueue();
    


    trackers = new Vector();
    


    exitWhenFinished = false;
  }
  












  public void track(File file, Object marker)
  {
    track(file, marker, (FileDeleteStrategy)null);
  }
  









  public void track(File file, Object marker, FileDeleteStrategy deleteStrategy)
  {
    if (file == null) {
      throw new NullPointerException("The file must not be null");
    }
    addTracker(file.getPath(), marker, deleteStrategy);
  }
  








  public void track(String path, Object marker)
  {
    track(path, marker, (FileDeleteStrategy)null);
  }
  









  public void track(String path, Object marker, FileDeleteStrategy deleteStrategy)
  {
    if (path == null) {
      throw new NullPointerException("The path must not be null");
    }
    addTracker(path, marker, deleteStrategy);
  }
  







  private synchronized void addTracker(String path, Object marker, FileDeleteStrategy deleteStrategy)
  {
    if (exitWhenFinished) {
      throw new IllegalStateException("No new trackers can be added once exitWhenFinished() is called");
    }
    if (reaper == null) {
      reaper = new Reaper();
      reaper.start();
    }
    trackers.add(new Tracker(path, deleteStrategy, marker, q));
  }
  






  public int getTrackCount()
  {
    return trackers.size();
  }
  





















  public synchronized void exitWhenFinished()
  {
    exitWhenFinished = true;
    if (reaper != null) {
      synchronized (reaper) {
        reaper.interrupt();
      }
    }
  }
  


  private final class Reaper
    extends Thread
  {
    Reaper()
    {
      super();
      setPriority(10);
      setDaemon(true);
    }
    




    public void run()
    {
      while ((!exitWhenFinished) || (trackers.size() > 0)) {
        FileCleaningTracker.Tracker tracker = null;
        try
        {
          tracker = (FileCleaningTracker.Tracker)q.remove();
        } catch (Exception e) {}
        continue;
        
        if (tracker != null) {
          tracker.delete();
          tracker.clear();
          trackers.remove(tracker);
        }
      }
    }
  }
  






  private static final class Tracker
    extends PhantomReference
  {
    private final String path;
    




    private final FileDeleteStrategy deleteStrategy;
    





    Tracker(String path, FileDeleteStrategy deleteStrategy, Object marker, ReferenceQueue queue)
    {
      super(queue);
      this.path = path;
      this.deleteStrategy = (deleteStrategy == null ? FileDeleteStrategy.NORMAL : deleteStrategy);
    }
    





    public boolean delete()
    {
      return deleteStrategy.deleteQuietly(new File(path));
    }
  }
}
