package kaba4cow.traderclient.ta.bars;

import org.json.JSONObject;
import org.ta4j.core.BaseBar;

import kaba4cow.traderclient.utils.TimeUtils;

public class StreamBar extends BaseBar {

	private static final long serialVersionUID = 1L;

	public StreamBar(JSONObject json, BarInterval interval) {
		super(interval.getDuration(), TimeUtils.getDateTime(json.getJSONObject("k").getLong("T")),
				json.getJSONObject("k").getDouble("o"), json.getJSONObject("k").getDouble("h"),
				json.getJSONObject("k").getDouble("l"), json.getJSONObject("k").getDouble("c"),
				json.getJSONObject("k").getDouble("v"));
	}

}
