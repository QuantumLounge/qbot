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

package codedcosmos.cometbot.guild.chat;

import codedcosmos.cometbot.core.CometBot;
import codedcosmos.cometbot.guild.context.Guilds;
import codedcosmos.cometbot.utils.log.Log;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		// Just make sure guild is cached
		Guilds.addGuild(event.getGuild());

		// Ignore messages that don't start with .
		if (!event.getMessage().getContentDisplay().toString().startsWith(".")) return;

		String[] raw_message = event.getMessage().getContentDisplay().substring(1).split(" ");
		String commandID = raw_message[0];

		// Find and run command
		for (Command command : CometBot.commands) {
			if (command.getClass().getSimpleName().toLowerCase().equals(commandID)) {
				try {
					command.run(event);
				} catch (Exception e) {
					Log.printErr("Caught Exception when trying to execute command!");
					Log.printErr("Command Message: '" + event.getMessage().getContentRaw() + "'");
					Log.printErr("");
					Log.printErr(e);
				}
				break;
			}
		}
	}
}
