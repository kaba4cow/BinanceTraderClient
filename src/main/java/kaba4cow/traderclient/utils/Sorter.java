package kaba4cow.traderclient.utils;

import java.util.Comparator;
import java.util.function.Function;

@FunctionalInterface
public interface Sorter<T> extends Comparator<T> {

	static <T, U extends Comparable<? super U>> Sorter<T> comparing(Function<T, U> keyExtractor,
			boolean reverse) {
		if (reverse)
			return (o1, o2) -> keyExtractor.apply(o2).compareTo(keyExtractor.apply(o1));
		else
			return (o1, o2) -> keyExtractor.apply(o1).compareTo(keyExtractor.apply(o2));
	}

}
