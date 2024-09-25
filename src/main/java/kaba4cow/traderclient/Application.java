package kaba4cow.traderclient;

import kaba4cow.traderclient.tradingbot.TradingBot;
import kaba4cow.traderclient.ui.ApplicationWindow;
import kaba4cow.traderclient.utils.UIUtils;

public class Application {

	private Application() {
	}

	public static void main(String[] args) {
		UIUtils.initialize();
		ApplicationSettings.initialize();
		ApplicationWindow window = new ApplicationWindow();
		ApplicationSettings.setSeries(ApplicationSettings.getBaseAsset(), ApplicationSettings.getQuoteAsset(),
				ApplicationSettings.getInterval());
		TradingBot.loadTradingBots();
		new Thread("Application Refresh Thread") {
			@Override
			public void run() {
				while (true) {
					if (!ApplicationSettings.getSeries().isEmpty())
						ApplicationSettings.updateLastBar();
					try {
						Thread.sleep(500l);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
		window.setVisible(true);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				TradingBot.saveTradingBots();
			}
		});
	}

}
