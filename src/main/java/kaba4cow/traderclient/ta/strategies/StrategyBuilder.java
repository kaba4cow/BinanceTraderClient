package kaba4cow.traderclient.ta.strategies;

import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.AndRule;
import org.ta4j.core.rules.OrRule;

import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public abstract class StrategyBuilder {

	private final String name;

	public StrategyBuilder(String name) {
		this.name = name;
	}

	public abstract BuilderStrategy build(BarSeries series);

	public final String getName() {
		return name;
	}

	public JSONObject serialize() {
		JSONObject json = new JSONObject().put("class", getClass().getName());
		JSONArray jsonFields = new JSONArray();
		Field[] fields = getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				JSONObject jsonField = new JSONObject();
				jsonField.put("name", field.getName());
				if (field.isAnnotationPresent(StrategyParameterInt.class))
					jsonField.put("value", field.getInt(this));
				else if (field.isAnnotationPresent(StrategyParameterEnum.class))
					jsonField.put("value", ((Enum<?>) field.get(this)).ordinal());
				jsonFields.put(jsonField);
			} catch (Exception e) {
			}
		}
		json.put("fields", jsonFields);
		return json;
	}

	public static StrategyBuilder deserialize(JSONObject json) {
		try {
			StrategyBuilder instance = (StrategyBuilder) Class.forName(json.getString("class")).getDeclaredConstructor()
					.newInstance();
			JSONArray jsonFields = json.getJSONArray("fields");
			for (int i = 0; i < jsonFields.length(); i++) {
				JSONObject jsonField = jsonFields.getJSONObject(i);
				Field field = instance.getClass().getDeclaredField(jsonField.getString("name"));
				field.setAccessible(true);
				if (field.isAnnotationPresent(StrategyParameterInt.class))
					field.setInt(instance, jsonField.getInt("value"));
				else if (field.isAnnotationPresent(StrategyParameterEnum.class)) {
					StrategyParameterEnum parameter = field.getAnnotation(StrategyParameterEnum.class);
					field.set(instance, parameter.type().getEnumConstants()[jsonField.getInt("value")]);
				}
			}
			return instance;
		} catch (Exception e) {
			return null;
		}
	}

	protected Rule or(Rule rule1, Rule rule2, Rule... rules) {
		Rule rule = new OrRule(rule1, rule2);
		for (int i = 0; i < rules.length; i++)
			rule = new OrRule(rule, rules[i]);
		return rule;
	}

	protected Rule and(Rule rule1, Rule rule2, Rule... rules) {
		Rule rule = new AndRule(rule1, rule2);
		for (int i = 0; i < rules.length; i++)
			rule = new AndRule(rule, rules[i]);
		return rule;
	}

}
