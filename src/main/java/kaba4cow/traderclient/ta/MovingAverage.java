package kaba4cow.traderclient.ta;

public enum MovingAverage {

	SMA("Simple"), //
	EMA("Exponential"), //
	ZLEMA("Zero-Lag Exponential"), //
	MMA("Modified"), //
	WMA("Weighted"), //
	LWMA("Linearly Weighted"), //
	HMA("Hull");

	private final String name;

	private MovingAverage(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
