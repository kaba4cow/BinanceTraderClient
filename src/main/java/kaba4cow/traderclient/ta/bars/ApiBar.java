package kaba4cow.traderclient.ta.bars;

import org.json.JSONArray;
import org.ta4j.core.BaseBar;

import kaba4cow.traderclient.utils.TimeUtils;

public class ApiBar extends BaseBar {

	private static final long serialVersionUID = 1L;

	public ApiBar(JSONArray json, BarInterval interval) {
		super(interval.getDuration(), TimeUtils.getDateTime(json.getLong(6)), json.getDouble(1), json.getDouble(2),
				json.getDouble(3), json.getDouble(4), json.getDouble(5));
	}

}
