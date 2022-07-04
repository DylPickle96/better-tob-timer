/*
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.betterToBTimer;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Point;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.lang.System.*;

@Slf4j
@PluginDescriptor(
        name = "Better Tob Timer",
        description = "Instance Timers for Theatre of Blood rooms",
        tags = {"combat", "raid", "pve", "pvm", "bosses", "timer"}
)
public class BetterToBTimerPlugin extends Plugin {
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

    private int maidenStartTick = -1;
    private boolean maiden70;
    private int maiden70time;
    private boolean maiden50;
    private int maiden50time;
    private boolean maiden30;
    private int maiden30time;
    private int maidenProcTime;


    @Getter
	private boolean insideTob;
    @Getter
    private final Map<String, Integer> room = new HashMap<>();
    @Getter
    private final Map<String, Integer> time = new HashMap<>();
    @Getter
    private final LinkedList <String> phase = new LinkedList<>();
    @Getter
    private final Map<String, Integer> phaseTime = new HashMap<>();
    @Getter
    private final Map<String, Integer> phaseSplit = new HashMap<>();

    @Inject
    private Client client;

    @Inject
    private BetterTobTimerConfig config;

    @Inject
    private ConfigManager configManager;

    @Override
    protected void startUp() throws Exception {
    }

    @Override
    protected void shutDown() throws Exception {
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
		if (client.getLocalPlayer() == null)
		{
			return;
		}
		int tobVar = client.getVarbitValue(Varbits.THEATRE_OF_BLOOD);
		insideTob = tobVar == 2 || tobVar == 3;

		if (!insideTob)
		{

		}
    }

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (!insideTob || event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}
        String strippedMessage = Text.removeTags(event.getMessage());

        if (MAIDEN_WAVE.matcher(strippedMessage).find())
        {
        }
	}

	@Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (!insideTob)
		{
            return;
        }
        NPC npc = event.getNpc();
		int npcID = npc.getId();
        System.out.println(npcID);
		switch(npcID)
		{
            case NpcID.THE_MAIDEN_OF_SUGADINTI:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10814:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10822:
            {
                log.info("hello world");
                maidenStartTick = client.getTickCount();
                room.put(MAIDEN, maidenStartTick);
                System.out.println(room);
                break;
			}
		}
    }
    private void phase(String name, int ticks, boolean splitPhase, String boss, ChatMessage event)
    {
        if (splitPhase && !phase.isEmpty())
        {
            phaseSplit.put(name, ticks - phaseTime.get(phase.getLast()));
        }

        if (!name.equals(boss))
        {
            phaseTime.put(name, ticks);
            phase.add(name);

            printTime(ticks, boss + " - " + name, phaseSplit.getOrDefault(name, 0));
        }
        else
        {
            time.put(name, ticks);

            if (!phase.isEmpty() && event != null)
            {
                String string = event.getMessage();
                String[] message = string.split("(?=</col>)", 2);
                String startMessage = message[0];
                String endMessage = message[1];
                event.getMessageNode().setValue(startMessage + " (" + formatTime(ticks - phaseTime.get(phase.getLast())) + ")" + endMessage);
            }
        }
    }

    public String formatTime(int ticks)
    {
        int millis = ticks * TICK_LENGTH;
        String hundredths = String.valueOf(millis % 1000).substring(0, 1);
        return String.format("%d:%02d.%s",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1),
                hundredths);
    }

    private void printTime(int ticks, String subject, int splitTicks)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Wave '").append(subject).append("' complete! Duration: <col=EF1020>").append(formatTime(ticks));
        if (splitTicks > 0)
        {
            stringBuilder.append(" (").append(formatTime(splitTicks)).append(")");
        }
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", stringBuilder.toString(), "", false);
    }


    @Provides
    BetterTobTimerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BetterTobTimerConfig.class);
    }
}
