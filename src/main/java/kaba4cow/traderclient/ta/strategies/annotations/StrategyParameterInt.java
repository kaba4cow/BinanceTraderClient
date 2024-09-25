package kaba4cow.traderclient.ta.strategies.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StrategyParameterInt {

	public String name();

	public int min();

	public int max();
	
}
