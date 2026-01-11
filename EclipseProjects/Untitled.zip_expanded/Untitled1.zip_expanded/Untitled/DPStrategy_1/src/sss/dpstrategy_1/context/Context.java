package sss.dpstrategy_1.context;

import sss.dpstrategy_1.strategy.Strategy;

public class Context {
	private Strategy strategy;

	public Context(Strategy strategy){
		this.strategy = strategy;
	}

	public int executeStrategy(int num1, int num2){
		return strategy.doOperation(num1, num2);
	}
}
