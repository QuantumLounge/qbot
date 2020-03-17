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

import codedcosmos.cometbot.guild.context.CometGuildContext;
import codedcosmos.cometbot.utils.web.SearchTrack;
import codedcosmos.cometbot.utils.web.WebUtils;
import codedcosmos.hyperdiscord.chat.messages.DynamicMessage;
import codedcosmos.hyperdiscord.utils.debug.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

import java.util.ArrayList;

public class SearchMessage extends DynamicMessage {
	
	// Search
	private String search;
	private int index;
	
	public SearchMessage(CometGuildContext context) {
		super(context);
	}
	
	public void sendSearch(String search) {
		this.search = search;
		this.index = index;
		
		send();
	}
	
	@Override
	public Message getNew() {
		// Retrieve Tracks
		ArrayList<SearchTrack> songs = new ArrayList<SearchTrack>();
		try {
			songs.addAll(WebUtils.searchForSongs(search));
		} catch (Exception e) {
			Log.printErr(e);
			return getError(e.getMessage());
		}
		
		MessageBuilder builder = new MessageBuilder();
		
		EmbedBuilder embedBuilder = new EmbedBuilder();
		
		String content = "";
		int i = 1;
		
		StringBuilder sb = new StringBuilder();
		for (SearchTrack song : songs) {
			String choice = "[**"+song.getName()+"**]("+song.getLink()+")";
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
		
		builder.setContent("Failed to search for songs:\nError Message:\n" + errorMessage);
		
		Message message = builder.build();
		return message;
	}
	
	@Override
	public void postSend(Message message) {}
	
	@Override
	public void postUpdate(Message message) {}
}
