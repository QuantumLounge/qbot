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
import codedcosmos.cometbot.guild.voice.speaker.MusicSpeaker;
import codedcosmos.cometbot.utils.log.Log;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class Play implements Command {
	@Override
	public String getHelp() {
		return "Plays music. List of supported websites:\n" +
				"Youtube, SoundCloud, Bandcamp, Vimeo, Twitch Streams";
	}

	@Override
	public String[] getStynax() {
		return new String[] {"[links...]"};
	}

	@Override
	public void run(MessageReceivedEvent event) throws Exception {
		// Join the channel
		if (!Join.runCommand(event)) return;

		// Get Context
		GuildContext context = Guilds.getContextBy(event);

		// First update last channel
		context.getSpeaker().updateLastTextChannel(event.getTextChannel());

		// Get Links
		String[] args = event.getMessage().getContentRaw().split(" ");
		String[] links = Arrays.copyOfRange(args, 1, args.length);

		// Get size before adding
		int queueSizePrevious = context.getSpeaker().getQueueSize();

		// Add to queue
		for (int i = 0; i < links.length; i++) {
			context.getSpeaker().addToQueue(event.getTextChannel(), event.getAuthor().getName(), links[i]);
		}

		// Wait for queue to complete, or wait 1 second
		long timeout = System.currentTimeMillis();
		while (context.getSpeaker().getQueueSize() < queueSizePrevious+links.length) {
			if (links.length == 0) {
				break;
			}
			if (System.currentTimeMillis() > timeout+5000) {
				Log.print("Waited 5 seconds for queue to complete.");
				break;
			}
		}

		if (!context.getSpeaker().isPlaying() && !context.getSpeaker().isStopped()) {
			TextChannelHandler.sendThenWait(event,"Continuing Playback");
		} else if (context.getSpeaker().hasSongsInQueue()) {
			TextChannelHandler.sendThenWait(event,"There are now " + context.getSpeaker().getQueueSize() + " songs in the queue!");
		} else {
			// No songs in queue
			Log.print("Play say's there are no songs in the queue");

			if (links.length == 0) {
				TextChannelHandler.send(event, "You must add item's to the queue first!");
			}

			return;
		}

		// Play
		context.getSpeaker().play();
	}

	public static void sendPlayMessage(MusicSpeaker speaker, TextChannel channel) {
		int current = speaker.getSongsPlayed();
		int total = speaker.getQueueSize()+current;
		String message = "> Now Playing ("+current+"/"+total+") " + speaker.getCurrentSong();
		TextChannelHandler.sendThenWait(channel, message);
	}
}
