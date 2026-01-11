import java.util.concurrent.RecursiveAction;

/*
 * MyRecursiveAction extends  RecursiveAction,
 * RecursiveAction extends ForkJoinTask.
 */
class MyRecursiveAction extends RecursiveAction{
    
	private static final long serialVersionUID = 3536318942148628978L;
	
	//Part of array on which computation will be performed.
    int start;
    int end;
    
    //Array on which computation will be done recursively.
    long[] numberAr;
    
    
    public MyRecursiveAction(int start, int end, long[] numberAr) {   
           this.start=start;
           this.end=end;
           this.numberAr=numberAr;
    }
    
 
    /* computation will be performed in this method
     * and method won't return any result.
     */
    @Override
    protected void compute() {
    
           /* We divide array into small arrays, as small as minimumProcessingSize.
            * So that each processor could efficiently process smaller array, using this
            * approach enables work-stealing approach to comes into picture.
            */
           int minimumProcessingSize=100;
           
           //Array is small enough to be processed, we need not to divide array.
           if(end-start < minimumProcessingSize){
                  for (int i = start; i < end; i++) {
                        numberAr[i]=numberAr[i]*numberAr[i];
                  }
           }
           //divide array in small arrays.
           else {
                  int mid= (start+end)/2;
                  invokeAll(new MyRecursiveAction(start, mid, numberAr),
                               new MyRecursiveAction(mid, end, numberAr));
           }
    }
}
