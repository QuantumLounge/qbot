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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Leave implements Command {
	@Override
	public String getHelp() {
		return "Leaves the current channel";
	}

	@Override
	public String[] getStynax() {
		return new String[] {""};
	}

	@Override
	public void run(MessageReceivedEvent event) throws Exception {
		disconnect(event.getGuild());

		TextChannelHandler.send(event,"Goodbye :D");

	}

	public static void disconnect(Guild guild) {
		guild.getAudioManager().closeAudioConnection();

		GuildContext context = Guilds.getContextBy(guild);
		context.disconnectFromVoice();
	}
}
