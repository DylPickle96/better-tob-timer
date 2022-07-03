package com.betterToBTimer;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Better Tob Timer",
	description = "Instance Timers for Theatre of Blood rooms",
	tags = {"combat", "raid", "pve", "pvm", "bosses", "timer"}
)
public class BetterToBTimerPlugin extends Plugin
{
	private static final DecimalFormat DMG_FORMAT = new DecimalFormat("#,##0");
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.0");
	private static final int THEATRE_OF_BLOOD_ROOM_STATUS = 6447;
	private static final int THEATRE_OF_BLOOD_BOSS_HP = 6448;
	private static final int TOB_LOBBY = 14642;
	private static final int MAIDEN_REGION = 12613;
	private static final int BLOAT_REGION = 13125;
	private static final int NYLOCAS_REGION = 13122;
	private static final int SOTETSEG_REGION = 13123;
	private static final int SOTETSEG_MAZE_REGION = 13379;
	private static final int NYLOCAS_WAVES_TOTAL = 31;
	private static final int TICK_LENGTH = 600;
	private static final int MAIDEN_ID = 25748;
	private static final int BLOAT_ID = 25749;
	private static final int NYLOCAS_ID = 25750;
	private static final int SOTETSEG_ID = 25751;
	private static final int XARPUS_ID = 25752;
	private static final int VERZIK_ID = 22473;
	private static final Pattern MAIDEN_WAVE = Pattern.compile("Wave 'The Maiden of Sugadinti' \\(.*\\) complete!");
	private static final Pattern BLOAT_WAVE = Pattern.compile("Wave 'The Pestilent Bloat' \\(.*\\) complete!Duration: (\\d+):(\\d+)\\.?(\\d+)");
	private static final Pattern NYLOCAS_WAVE = Pattern.compile("Wave 'The Nylocas' \\(.*\\) complete!");
	private static final Pattern SOTETSEG_WAVE = Pattern.compile("Wave 'Sotetseg' \\(.*\\) complete!");
	private static final Pattern XARPUS_WAVE = Pattern.compile("Wave 'Xarpus' \\(.*\\) complete!");
	private static final Pattern VERZIK_WAVE = Pattern.compile("Wave 'The Final Challenge' \\(.*\\) complete!");
	private static final Pattern COMPLETION = Pattern.compile("Theatre of Blood total completion time:");
	private static final Set<Integer> NYLOCAS_IDS = ImmutableSet.of(
			NpcID.NYLOCAS_HAGIOS, NpcID.NYLOCAS_HAGIOS_8347, NpcID.NYLOCAS_HAGIOS_8350, NpcID.NYLOCAS_HAGIOS_8353,
			NpcID.NYLOCAS_HAGIOS_10776, NpcID.NYLOCAS_HAGIOS_10779, NpcID.NYLOCAS_HAGIOS_10782, NpcID.NYLOCAS_HAGIOS_10785,
			NpcID.NYLOCAS_HAGIOS_10793, NpcID.NYLOCAS_HAGIOS_10796, NpcID.NYLOCAS_HAGIOS_10799, NpcID.NYLOCAS_HAGIOS_10802,
			NpcID.NYLOCAS_TOXOBOLOS_8343, NpcID.NYLOCAS_TOXOBOLOS_8346, NpcID.NYLOCAS_TOXOBOLOS_8349, NpcID.NYLOCAS_TOXOBOLOS_8352,
			NpcID.NYLOCAS_TOXOBOLOS_10775, NpcID.NYLOCAS_TOXOBOLOS_10778, NpcID.NYLOCAS_TOXOBOLOS_10781, NpcID.NYLOCAS_TOXOBOLOS_10784,
			NpcID.NYLOCAS_TOXOBOLOS_10792, NpcID.NYLOCAS_TOXOBOLOS_10795, NpcID.NYLOCAS_TOXOBOLOS_10798, NpcID.NYLOCAS_TOXOBOLOS_10801,
			NpcID.NYLOCAS_ISCHYROS_8342, NpcID.NYLOCAS_ISCHYROS_8345, NpcID.NYLOCAS_ISCHYROS_8348, NpcID.NYLOCAS_ISCHYROS_8351,
			NpcID.NYLOCAS_ISCHYROS_10774, NpcID.NYLOCAS_ISCHYROS_10777, NpcID.NYLOCAS_ISCHYROS_10780, NpcID.NYLOCAS_ISCHYROS_10783,
			NpcID.NYLOCAS_ISCHYROS_10791, NpcID.NYLOCAS_ISCHYROS_10794, NpcID.NYLOCAS_ISCHYROS_10797, NpcID.NYLOCAS_ISCHYROS_10800
	);
	private static final Set<Point> NYLOCAS_VALID_SPAWNS = ImmutableSet.of(
			new Point(17, 24), new Point(17, 25), new Point(18, 24), new Point(18, 25),
			new Point(31, 9), new Point(31, 10), new Point(32, 9), new Point(32, 10),
			new Point(46, 24), new Point(46, 25), new Point(47, 24), new Point(47, 25)
	);
	private static final Set<String> BOSS_NAMES = ImmutableSet.of(
			"The Maiden of Sugadinti", "Pestilent Bloat", "Nylocas Vasilias", "Sotetseg", "Xarpus", "Verzik Vitur"
	);

	private static final String MAIDEN = "Maiden";
	private static final String BLOAT = "Bloat";
	private static final String NYLO = "Nylo";
	private static final String SOTETSEG = "Sotetseg";
	private static final String XARPUS = "Xarpus";
	private static final String VERZIK = "Verzik";

	@Inject
	private Client client;

	@Inject
	private BetterTobTimerConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{

	}

	@Provides
	BetterTobTimerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BetterTobTimerConfig.class);
	}
}
