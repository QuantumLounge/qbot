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

package codedcosmos.cometbot.guild.chat.messages.built.search;

import codedcosmos.cometbot.core.CometBot;
import codedcosmos.cometbot.guild.commands.Search;
import codedcosmos.cometbot.guild.context.CometGuildContext;
import codedcosmos.cometbot.utils.unicode.UnicodeReactions;
import codedcosmos.cometbot.utils.web.SearchTrack;
import codedcosmos.cometbot.utils.web.YoutubeSearcher;
import codedcosmos.hyperdiscord.chat.messages.DynamicMessage;
import codedcosmos.hyperdiscord.chat.reactions.ReactionBox;
import codedcosmos.hyperdiscord.guild.GuildContext;
import codedcosmos.hyperdiscord.utils.debug.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.ArrayList;

public class SearchMessage extends DynamicMessage {
	
	// Search
	private String search;
	private int index;
	
	private ReactionBox[] reactionBoxes;
	private ReactionBox cancel;
	private ArrayList<SearchTrack> songs;
	
	private boolean sent = false;
	private boolean completed = false;
	
	public SearchMessage(CometGuildContext context) {
		super(context);
		
		reactionBoxes = new ReactionBox[0];
		songs = new ArrayList<SearchTrack>(0);
	}
	
	public void sendSearch(String search) {
		this.search = search;
		this.index = index;
		
		send();
	}
	
	@Override
	public Message getNew() {
		// Retrieve Tracks
		songs = new ArrayList<SearchTrack>();
		try {
			songs.addAll(YoutubeSearcher.searchForSongs(search, 5));
		} catch (Exception e) {
			Log.printErr(e);
			return getError(e.getMessage());
		}
		if (songs.size() == 0) {
			return getEmptyMessage();
		}
		
		if (completed) {
			sent = true;
			return getSearchCompletedMessage();
		}
		
		// Create message
		Message message = getMessage(songs);
		sent = true;
		
		// Get message
		return message;
	}
	
	@Override
	public void postSend(Message message) {
		setupReactionBoxes(message);
	}
	
	@Override
	public void postUpdate(Message message) {
	
	}
	
	private void setupReactionBoxes(Message message) {
		if (!sent) {
			return;
		}
		
		// Numerical selection
		reactionBoxes = new ReactionBox[songs.size()];
		String[] characters = new String[]{UnicodeReactions.ONE, UnicodeReactions.TWO, UnicodeReactions.THREE, UnicodeReactions.FOUR, UnicodeReactions.FIVE};
		
		for (int i = 0; i < reactionBoxes.length; i++) {
			reactionBoxes[i] = new ReactionBox(message, characters[i]);
		}
		
		// Cancel
		cancel = new ReactionBox(message, UnicodeReactions.CANCEL);
	}
	
	private Message getEmptyMessage() {
		MessageBuilder builder = new MessageBuilder();
		
		EmbedBuilder embedBuilder = new EmbedBuilder();
		
		embedBuilder.setDescription("No results found");
		
		builder.setContent("");
		builder.setEmbed(embedBuilder.build());
		Message message = builder.build();
		return message;
	}
	
	private Message getSearchCompletedMessage() {
		MessageBuilder builder = new MessageBuilder();
		
		EmbedBuilder embedBuilder = new EmbedBuilder();
		
		embedBuilder.setDescription("Search completed");
		
		builder.setContent("");
		builder.setEmbed(embedBuilder.build());
		Message message = builder.build();
		return message;
	}
	
	private Message getMessage(ArrayList<SearchTrack> songs) {
		MessageBuilder builder = new MessageBuilder();
		
		EmbedBuilder embedBuilder = new EmbedBuilder();
		
		String content = "";
		int i = 1;
		
		StringBuilder sb = new StringBuilder();
		for (SearchTrack song : songs) {
			String choice = "**" + i + ")** " + song.getName();
			sb.append("\n").append(choice);
			
			i++;
		}
		
		embedBuilder.setDescription(sb.toString());
		
		builder.setContent(content);
		builder.setEmbed(embedBuilder.build());
		
		Message message = builder.build();
		return message;
	}
	
	private Message getError(String errorMessage) {
		MessageBuilder builder = new MessageBuilder();
		
		builder.setContent("Failed to search for songs\nError: " + errorMessage);
		
		Message message = builder.build();
		return message;
	}
	
	public void clearReactions() {
		if (!sent) return;
		
		message.clearReactions().complete();
	}
	
	@Override
	public void onReactionAdd(GuildMessageReactionAddEvent event) {
		if (!sent) return;
		if (completed) {
			clearReactions();
			return;
		}
		
		cancel.onReactionAdd(event);
		for (ReactionBox box : reactionBoxes) {
			box.onReactionAdd(event);
		}
		
		boolean update = false;
		
		if (cancel.isSelected()) {
			update = true;
			completed = true;
		}
		
		for (int i = 0; i < songs.size(); i++) {
			if (reactionBoxes[i].isSelected()) {
				update = true;
				completed = true;
				
				CometGuildContext context = CometBot.guilds.getContextBy(event.getGuild());
				Log.printErr(songs.get(i).getLink());
				context.getSpeaker().addPlay(songs.get(i).getLink(), message.getTextChannel(), event.getUser().getName());
				break;
			}
		}
		
		if (update) {
			updateState();
		}
	}
	
	public boolean shouldUpdate() {
		return (sent && !completed);
	}
}
