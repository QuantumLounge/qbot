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

package codedcosmos.cometbot.event.guild;

import codedcosmos.cometbot.guild.chat.commands.Leave;
import codedcosmos.cometbot.guild.context.GuildContext;
import codedcosmos.cometbot.guild.context.Guilds;
import codedcosmos.cometbot.utils.log.Log;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

public class GuildVoiceConnectStatus  {
	public static void onVoiceJoinEvent(GuildVoiceJoinEvent event) {
		Log.print("On Voice Join");
		// TODO
	}

	public static void onVoiceLeaveEvent(GuildVoiceLeaveEvent event) {
		// Check to see how many people are in guild voice channel
		if (event.getChannelLeft().getMembers().size() == 1) {
			Leave.disconnect(event.getGuild());
			Log.print("Left channel, because noone was in there.");
		}
	}
}
