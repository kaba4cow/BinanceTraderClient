package kaba4cow.traderclient.ui.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ta4j.core.BarSeries;
import org.ta4j.core.backtest.BarSeriesManager;

import kaba4cow.traderclient.ApplicationSettings;
import kaba4cow.traderclient.BinanceSpot;
import kaba4cow.traderclient.ta.bars.BarInterval;
import kaba4cow.traderclient.ta.strategies.ADXStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.BollingerRSIStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.CCICorrectionStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.DeltaCrossoverStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.LevelBreakerStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.LoHiBounceStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.LoHiCrossoverStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.MACDStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.MACDTrendStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.MACrossoverStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.MAMultiCrossoverStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.MovingMomentumStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.RSI2StrategyBuilder;
import kaba4cow.traderclient.ta.strategies.RSIThresholdStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.RandomStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.StrategyBuilder;
import kaba4cow.traderclient.ta.strategies.TrendFollowerStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.WilliamsFractalsStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;
import kaba4cow.traderclient.tradingbot.TradingBotBuilder;
import kaba4cow.traderclient.utils.FormatUtils;
import kaba4cow.traderclient.utils.TradingReport;
import kaba4cow.traderclient.utils.UIUtils;

public class CreateTradingBotDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final List<StrategyBuilder> strategies;

	private final JButton symbolButton;
	private final JComboBox<String> intervalComboBox;
	private final JComboBox<String> strategyComboBox;
	private final JPanel strategyPanel;

	private final JComboBox<Integer> timeFrameComboBox;
	private final JLabel profitLabel;
	private final JSlider profitSlider;
	private final JLabel winrateLabel;
	private final JSlider winrateSlider;
	private final JLabel densityLabel;
	private final JSlider densitySlider;
	private final JLabel ratingLabel;

	private final JSpinner tradeQuantitySpinner;
	private final SpinnerNumberModel tradeQuantitySpinnerModel;
	private final JLabel tradeQuantityBaseLabel;
	private final JLabel tradeQuantityQuoteAmountLabel;
	private final JLabel tradeQuantityQuoteLabel;

	private final JButton startButton;
	private final JButton cancelButton;

	private String baseAsset;
	private String quoteAsset;
	private double price;

	private BarSeries series;

	private ProgressDialog progressDialog;

	public CreateTradingBotDialog() {
		super();
		setTitle("Create Trading Bot");
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1d;
		gbc.weighty = 1d;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;

		baseAsset = ApplicationSettings.getBaseAsset();
		quoteAsset = ApplicationSettings.getQuoteAsset();

		{
			JPanel settingsPanel = UIUtils.createTitledGridPanel("Bot Settings", 0, 2);
			settingsPanel.add(new JLabel("Pair:"));
			settingsPanel.add(symbolButton = new JButton(baseAsset + "/" + quoteAsset));
			symbolButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SymbolDialog dialog = new SymbolDialog(baseAsset, quoteAsset);
					if (dialog.getBaseAsset() != null) {
						baseAsset = dialog.getBaseAsset();
						quoteAsset = dialog.getQuoteAsset();
						symbolButton.setText(baseAsset + "/" + quoteAsset);
						updateSeries();
						testStrategy();
					}
				}
			});
			settingsPanel.add(new JLabel("Interval:"));
			settingsPanel.add(intervalComboBox = new JComboBox<>());
			for (BarInterval interval : BarInterval.values())
				intervalComboBox.addItem(interval.toString());
			intervalComboBox.setSelectedItem(ApplicationSettings.getInterval().toString());
			intervalComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateSeries();
					testStrategy();
				}
			});
			settingsPanel.add(new JLabel("Strategy:"));
			settingsPanel.add(strategyComboBox = new JComboBox<>());
			for (StrategyBuilder strategy : strategies)
				strategyComboBox.addItem(strategy.getName());
			strategyComboBox.setSelectedIndex(0);
			strategyComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateStrategyPanel();
					testStrategy();
				}
			});
			{
				JPanel tradeQuantityBasePanel = new JPanel();
				tradeQuantityBasePanel.setLayout(new GridLayout(1, 0));
				tradeQuantitySpinnerModel = new SpinnerNumberModel(1d, 1d, 10d, 0.1d);
				tradeQuantityBasePanel.add(tradeQuantitySpinner = new JSpinner(tradeQuantitySpinnerModel));
				tradeQuantityBasePanel.add(tradeQuantityBaseLabel = new JLabel(quoteAsset, SwingConstants.CENTER));
				settingsPanel.add(new JLabel("Trade Quantity (Base):"));
				settingsPanel.add(tradeQuantityBasePanel);

				JPanel tradeQuantityQuotePanel = new JPanel();
				tradeQuantityQuotePanel.setLayout(new GridLayout(1, 0));
				tradeQuantityQuotePanel.add(tradeQuantityQuoteAmountLabel = new JLabel("", SwingConstants.RIGHT));
				tradeQuantityQuotePanel.add(tradeQuantityQuoteLabel = new JLabel("", SwingConstants.CENTER));
				settingsPanel.add(new JLabel("Trade Quantity (Quote):"));
				settingsPanel.add(tradeQuantityQuotePanel);

				tradeQuantitySpinner.addChangeListener(e -> {
					double quantity = (double) tradeQuantitySpinner.getValue() * price;
					tradeQuantityQuoteAmountLabel.setText(FormatUtils.number(quantity, 8));
				});
			}
			add(settingsPanel, gbc);
		}

		{
			strategyPanel = UIUtils.createTitledGridPanel("Strategy Parameters", 0, 3);
			add(strategyPanel, gbc);
		}

		{
			ChangeListener listener = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					JSlider slider = (JSlider) e.getSource();
					if (!slider.getValueIsAdjusting())
						testStrategy();
				}
			};

			JPanel reportPanel = UIUtils.createTitledGridPanel("Backtesting Report", 0, 2);
			reportPanel.add(new JLabel("Time Frame:"));
			timeFrameComboBox = new JComboBox<>();
			for (int i = 1; i <= 10; i++)
				timeFrameComboBox.addItem(100 * i);
			timeFrameComboBox.setSelectedItem(1000);
			timeFrameComboBox.addItemListener(e -> updateSeries());
			reportPanel.add(timeFrameComboBox);
			reportPanel.add(profitLabel = new JLabel());
			reportPanel.add(profitSlider = new JSlider(0, 100, 100));
			profitSlider.addChangeListener(listener);
			reportPanel.add(winrateLabel = new JLabel());
			reportPanel.add(winrateSlider = new JSlider(0, 100, 100));
			winrateSlider.addChangeListener(listener);
			reportPanel.add(densityLabel = new JLabel());
			reportPanel.add(densitySlider = new JSlider(0, 100, 100));
			densitySlider.addChangeListener(listener);
			reportPanel.add(ratingLabel = new JLabel());
			add(reportPanel, gbc);
		}

		{
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1, 2));
			startButton = new JButton("Start");
			startButton.addActionListener(e -> {
				new TradingBotBuilder()//
						.setBaseAsset(baseAsset)//
						.setQuoteAsset(quoteAsset)//
						.setInterval(getInterval())//
						.setStrategy(getStrategy())//
						.setTradeQuantity((double) tradeQuantitySpinner.getValue())//
						.build().start();
				dispose();
			});
			buttonPanel.add(startButton);
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(e -> {
				dispose();
			});
			buttonPanel.add(cancelButton);
			add(buttonPanel, gbc);
		}

		updateSeries();
		updateStrategyPanel();

		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	static {
		strategies = new ArrayList<>();
		strategies.add(new RandomStrategyBuilder());
		strategies.add(new DeltaCrossoverStrategyBuilder());
		strategies.add(new WilliamsFractalsStrategyBuilder());
		strategies.add(new LoHiCrossoverStrategyBuilder());
		strategies.add(new LoHiBounceStrategyBuilder());
		strategies.add(new TrendFollowerStrategyBuilder());
		strategies.add(new LevelBreakerStrategyBuilder());
		strategies.add(new BollingerRSIStrategyBuilder());
		strategies.add(new MACrossoverStrategyBuilder());
		strategies.add(new MAMultiCrossoverStrategyBuilder());
		strategies.add(new MACDTrendStrategyBuilder());
		strategies.add(new MACDStrategyBuilder());
		strategies.add(new ADXStrategyBuilder());
		strategies.add(new CCICorrectionStrategyBuilder());
		strategies.add(new MovingMomentumStrategyBuilder());
		strategies.add(new RSIThresholdStrategyBuilder());
		strategies.add(new RSI2StrategyBuilder());
	}

	private void updateStrategyPanel() {
		strategyPanel.removeAll();
		StrategyBuilder strategy = getStrategy();
		Field[] fields = strategy.getClass().getDeclaredFields();
		for (Field field : fields)
			try {
				JComponent[] components = new JComponent[3];
				field.setAccessible(true);
				if (field.isAnnotationPresent(StrategyParameterInt.class))
					addStrategyParameterInt(strategy, field, field.getAnnotation(StrategyParameterInt.class),
							components);
				else if (field.isAnnotationPresent(StrategyParameterEnum.class))
					addStrategyParameterEnum(strategy, field, field.getAnnotation(StrategyParameterEnum.class),
							components);
				for (JComponent component : components)
					strategyPanel.add(component);
			} catch (Exception e) {
			}
		pack();
	}

	private void addStrategyParameterInt(StrategyBuilder strategy, Field field, StrategyParameterInt parameter,
			JComponent[] components) throws Exception {
		components[0] = new JLabel(parameter.name());
		JSpinner parameterSpinner = new JSpinner(
				new SpinnerNumberModel(field.getInt(strategy), parameter.min(), parameter.max(), 1));
		parameterSpinner.setValue(field.get(strategy));
		parameterSpinner.addChangeListener(e -> {
			try {
				field.setInt(strategy, (int) parameterSpinner.getValue());
				testStrategy();
			} catch (Exception e1) {
			}
		});
		components[1] = parameterSpinner;
		JButton bestButton = new JButton("Best");
		bestButton.addActionListener(e -> createProgressDialog(parameter.min(), parameter.max(), () -> {
			int bestValue = (int) parameterSpinner.getValue();
			double bestRating = Double.NEGATIVE_INFINITY;
			try {
				bestRating = calculateRating(createTradingReport(strategy));
			} catch (Exception e1) {
			}
			for (int value = parameter.min(); value <= parameter.max(); value++)
				try {
					field.setInt(strategy, value);
					double rating = calculateRating(createTradingReport(strategy));
					if (rating > bestRating) {
						bestRating = rating;
						bestValue = value;
					}
					progressDialog.updateProgress(value);
					parameterSpinner.setValue(bestValue);
				} catch (Exception e1) {
				}
			try {
				field.setInt(strategy, bestValue);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			progressDialog.dispose();
		}));
		components[2] = bestButton;
	}

	private void addStrategyParameterEnum(StrategyBuilder strategy, Field field, StrategyParameterEnum parameter,
			JComponent[] components) throws Exception {
		components[0] = new JLabel(parameter.name());
		JComboBox<String> parameterComboBox = new JComboBox<>();
		Class<? extends Enum<?>> enumType = parameter.type();
		Enum<?>[] enumConstants = enumType.getEnumConstants();
		for (Enum<?> enumConstant : enumConstants)
			parameterComboBox.addItem(enumConstant.toString());
		parameterComboBox.setSelectedIndex(((Enum<?>) field.get(strategy)).ordinal());
		parameterComboBox.addItemListener(e -> {
			try {
				field.set(strategy, enumConstants[parameterComboBox.getSelectedIndex()]);
				testStrategy();
			} catch (Exception e1) {
			}
		});
		components[1] = parameterComboBox;
		JButton bestButton = new JButton("Best");
		bestButton.addActionListener(e -> createProgressDialog(0, enumConstants.length - 1, () -> {
			int bestValue = parameterComboBox.getSelectedIndex();
			double bestRating = Double.NEGATIVE_INFINITY;
			try {
				bestRating = calculateRating(createTradingReport(strategy));
			} catch (Exception e1) {
			}
			for (int value = 0; value < enumConstants.length; value++)
				try {
					field.set(strategy, enumConstants[value]);
					double rating = calculateRating(createTradingReport(strategy));
					if (rating > bestRating) {
						bestRating = rating;
						bestValue = value;
					}
					progressDialog.updateProgress(value);
					parameterComboBox.setSelectedIndex(bestValue);
				} catch (Exception e1) {
				}
			try {
				field.set(strategy, enumConstants[bestValue]);
			} catch (Exception e1) {
			}
			progressDialog.dispose();
		}));
		components[2] = bestButton;
	}

	private void createProgressDialog(int min, int max, Runnable calculation) {
		Thread thread = new Thread(calculation, "Calculation Thread");
		progressDialog = new ProgressDialog(min, max);
		thread.start();
		progressDialog.setVisible(true);
	}

	private void testStrategy() {
		try {
			TradingReport report = createTradingReport(getStrategy());
			profitLabel.setText("Profit: " + FormatUtils.percent(report.profit));
			winrateLabel.setText("Winrate: " + FormatUtils.percent(report.winrate));
			densityLabel.setText("Density: " + FormatUtils.percent(report.density));
			ratingLabel.setText("Rating: " + FormatUtils.number(calculateRating(report), 3));
		} catch (Exception e) {
			profitLabel.setText("Profit: N/A");
			winrateLabel.setText("Winrate: N/A");
			densityLabel.setText("Density: N/A");
			ratingLabel.setText("Rating: N/A");
		}
	}

	private TradingReport createTradingReport(StrategyBuilder strategy) {
		return new TradingReport(new BarSeriesManager(series).run(strategy.build(series)), series);
	}

	private double calculateRating(TradingReport report) {
		return (report.profit * profitSlider.getValue() + report.winrate * winrateSlider.getValue()
				+ report.density * densitySlider.getValue())
				/ (profitSlider.getValue() + winrateSlider.getValue() + densitySlider.getValue());
	}

	private void updateSeries() {
		series = BinanceSpot.getBarSeries(baseAsset + quoteAsset, getInterval(),
				(int) timeFrameComboBox.getSelectedItem());
		price = BinanceSpot.getPrice(baseAsset + quoteAsset);
		tradeQuantityBaseLabel.setText(baseAsset);
		tradeQuantityQuoteLabel.setText(quoteAsset);
		double minTradeQuantity = BinanceSpot.getSymbol(baseAsset + quoteAsset).minTradeQuantity / price;
		double maxTradeQuantity = BinanceSpot.getSymbol(baseAsset + quoteAsset).maxTradeQuantity / price;
		tradeQuantitySpinnerModel.setMinimum(minTradeQuantity);
		tradeQuantitySpinnerModel.setMaximum(maxTradeQuantity);
		tradeQuantitySpinnerModel.setStepSize(0.001d);
		tradeQuantitySpinnerModel.setValue(minTradeQuantity);
		try {
			tradeQuantitySpinner.commitEdit();
		} catch (ParseException e) {
		}
		testStrategy();
	}

	private StrategyBuilder getStrategy() {
		return strategies.get(strategyComboBox.getSelectedIndex());
	}

	private BarInterval getInterval() {
		return BarInterval.get((String) intervalComboBox.getSelectedItem());
	}

}