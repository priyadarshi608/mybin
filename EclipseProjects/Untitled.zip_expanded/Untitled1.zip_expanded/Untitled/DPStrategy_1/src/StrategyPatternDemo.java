import sss.dpstrategy_1.context.Context;
import sss.dpstrategy_1.strategy.Strategy;
import sss.dpstrategy_1.strategy.OperationAdd;
import sss.dpstrategy_1.strategy.OperationSubstract;

public class StrategyPatternDemo {
	public static void main(String[] args) {
		Context context = new Context(new OperationAdd());		
		System.out.println("10 + 5 = " + context.executeStrategy(10, 5));

		context = new Context(new OperationSubstract());		
		System.out.println("10 - 5 = " + context.executeStrategy(10, 5));
	}
}
