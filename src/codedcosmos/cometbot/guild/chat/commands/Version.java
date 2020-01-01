package codedcosmos.cometbot.guild.chat.commands;

import codedcosmos.cometbot.core.CometBot;
import codedcosmos.cometbot.guild.chat.Command;
import codedcosmos.cometbot.guild.chat.channel.TextChannelHandler;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Version implements Command {

	@Override
	public String getHelp() {
		return "Gets the current version of the bot";
	}

	@Override
	public String[] getStynax() {
		return new String[] {""};
	}

	@Override
	public void run(MessageReceivedEvent event) throws Exception {
		TextChannelHandler.sendThenWait(event, "Current Version: " + CometBot.VERSION);
	}
}
