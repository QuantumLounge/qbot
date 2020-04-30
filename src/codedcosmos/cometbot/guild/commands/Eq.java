package codedcosmos.cometbot.guild.commands;

import codedcosmos.cometbot.core.CometBot;
import codedcosmos.cometbot.guild.context.CometGuildContext;
import codedcosmos.cometbot.guild.voice.speaker.SpeakerStatus;
import codedcosmos.cometbot.utils.web.SearchTrack;
import codedcosmos.cometbot.utils.web.YoutubeSearcher;
import codedcosmos.hyperdiscord.chat.TextSender;
import codedcosmos.hyperdiscord.command.Command;
import codedcosmos.hyperdiscord.utils.debug.Log;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class Eq implements Command {
	@Override
	public String getName() {
		return "eq";
	}
	
	@Override
	public String getHelp() {
		return "Adjust's Equaliser settings for the bot";
	}
	
	@Override
	public String[] getStynax() {
		return new String[] {"[normal | bass | treble]"};
	}
	
	@Override
	public void run(MessageReceivedEvent event) throws Exception {
		// Get Context
		CometGuildContext context = CometBot.guilds.getContextBy(event);
		
		// Args
		String[] args = event.getMessage().getContentRaw().split(" ");

		if (args.length > 2) {
			TextSender.send(event, "To many arguments");
		} else if (args[1].equals("normal")) {
			context.getSpeaker().getPlayer().disableEq();
			TextSender.send(event, "Now using a balanced eq");
		} else if (args[1].equals("bass")) {
			context.getSpeaker().getPlayer().bassBoost();
			TextSender.send(event, "Now playing bass-boosted audio");
		} else if (args[1].equals("treble")) {
			context.getSpeaker().getPlayer().trebleBoost();
			TextSender.send(event, "Now playing treble-boosted audio");
		} else {
			TextSender.send(event, "Invalid eq setting, must be one of these 'bass, normal and treble'");
		}
	}
	
	public String[] getAliases() {
		return new String[] {"equaliser", "bass"};
	}
}