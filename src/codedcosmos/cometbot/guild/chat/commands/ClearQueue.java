/*
 * Discord CometBot by codedcosmos
 *
 * CometBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License 3 as published by
 * the Free Software Foundation.
 * CometBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License 3 for more details.
 * You should have received a copy of the GNU General Public License 3
 * along with CometBot.  If not, see <https://www.gnu.org/licenses/>.
 */

package codedcosmos.cometbot.guild.chat.commands;

import codedcosmos.cometbot.guild.chat.Command;
import codedcosmos.cometbot.guild.chat.channel.TextChannelHandler;
import codedcosmos.cometbot.guild.context.GuildContext;
import codedcosmos.cometbot.guild.context.Guilds;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearQueue implements Command {

	@Override
	public String getHelp() {
		return "Clears the current queue of songs";
	}

	@Override
	public String[] getStynax() {
		return new String[] {""};
	}

	@Override
	public void run(MessageReceivedEvent event) throws Exception {
		GuildContext context = Guilds.getContextBy(event.getGuild());
		context.getSpeaker().clearSongs();

		TextChannelHandler.send(event,"Cleared Queue");
	}
}
