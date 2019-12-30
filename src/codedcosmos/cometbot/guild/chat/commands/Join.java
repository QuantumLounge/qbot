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
import codedcosmos.cometbot.utils.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Iterator;
import java.util.List;

public class Join implements Command {
	@Override
	public String getHelp() {
		return "Asks the bot to join your current voice channel";
	}

	@Override
	public String[] getStynax() {
		return new String[] {""};
	}

	@Override
	public void run(MessageReceivedEvent event) throws Exception{
		runCommand(event);
	}

	public static boolean runCommand(MessageReceivedEvent event) throws Exception {
		User author = event.getAuthor();
		Guild guild = event.getGuild();

		Member member = guild.getMember(author);

		List<VoiceChannel> channels = guild.getVoiceChannels();
		for (Iterator<VoiceChannel> iter = channels.iterator(); iter.hasNext(); ) {
			VoiceChannel channel = iter.next();

			boolean userInChannel = false;
			for (Member memberCheck : channel.getMembers()) {
				if (member.getIdLong() == memberCheck.getIdLong()) {
					userInChannel = true;
					break;
				}
			}

			if (userInChannel) {
				// Check size
				if (channel.getMembers().size() == channel.getUserLimit()) {
					TextChannelHandler.send(event, "Sorry, there doesn't seem to be any room there.");
					return false;
				}

				// Get Context
				GuildContext context = Guilds.getContextBy(guild);

				// Update Last Text Channel
				context.getSpeaker().updateLastTextChannel(event.getTextChannel());

				// Ensure it's not already connected
				if (context.isConnectedToVoice()) {
					Log.print("Bot already Connected");
					return true;
				}

				// Everything is correct, add to channel
				guild.getAudioManager().openAudioConnection(channel);
				guild.getAudioManager().setSendingHandler(context.getSpeaker().getSendHandler());

				TextChannelHandler.send(event, "On my way!");

				context.setVoiceChannel(channel);

				return true;
			}
		}

		// User isn't in any channels
		TextChannelHandler.send(event, "You must join a voice channel to use this command!");
		return false;
	}
}
