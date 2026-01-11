import sss.dpstate_1.context.Context;
import sss.dpstate_1.state.StartState;
import sss.dpstate_1.state.StopState;

public class StatePatternDemo {
   public static void main(String[] args) {
      Context context = new Context();

      StartState startState = new StartState();
      startState.doAction(context);

      System.out.println(context.getState().toString());

      StopState stopState = new StopState();
      stopState.doAction(context);

      System.out.println(context.getState().toString());
   }
}
