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

package codedcosmos.cometbot.guild.voice.speaker;

import codedcosmos.cometbot.guild.context.CometGuildContext;
import codedcosmos.cometbot.guild.voice.speaker.components.AudioSendManager;
import codedcosmos.cometbot.guild.voice.speaker.components.TrackList;
import codedcosmos.cometbot.guild.voice.track.LoadedTrack;
import codedcosmos.cometbot.guild.chat.messages.built.lastplaying.NowPlayingMessage;
import codedcosmos.hyperdiscord.chat.TextSender;
import codedcosmos.hyperdiscord.utils.debug.Log;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.Arrays;

public class MusicSpeaker extends AudioEventAdapter {
	// Core
	private AudioSendManager player;
	private SpeakerStatus status;

	// The currently playing song
	private LoadedTrack currentTrack;
	
	// Components
	private TrackList trackList;
	
	// Guild Context
	private CometGuildContext context;

	public MusicSpeaker(CometGuildContext context) {
		player = new AudioSendManager(this);
		trackList = new TrackList();
		
		this.context = context;
		
		status = SpeakerStatus.Waiting;
	}

	public void play(boolean fromUser) {
		try {
			
			if (status == SpeakerStatus.Playing) {
				return;
			}
			
			if (player.isPaused()) {
				TextSender.sendThenWait(context.getBotTextChannel(), "Continuing Playback");
				player.unpause();
				status = SpeakerStatus.Playing;
				return;
			}
			
			if (!trackList.hasSongs()) {
				if (fromUser) {
					TextSender.send(context.getBotTextChannel(), "You must add item's to the queue first!");
					return;
				} else {
					TextSender.send(context.getBotTextChannel(), "Queue is empty, no more songs to play");
					return;
				}
			}
			
			LoadedTrack loadedTrack = trackList.getTrackFromQueue();
			
			// Set Current Track
			currentTrack = loadedTrack;
			
			AudioTrack track = loadedTrack.getTrack();
			
			player.startTrack(track);
			status = SpeakerStatus.Playing;
			
			// Send Message
			Log.print("Playing " + track);
			sendNowPlaying();
			
		} catch (Exception e) {
			stop();
			Log.printErr(e);
			TextSender.send(context.getBotTextChannel(), "Failed to play song:\nReason: "+e.getMessage());
		}
	}
	
	public void playSongAgain(TextChannel channel) {
		String[] links = new String[] {currentTrack.getLink()};
		
		addPlay(links, channel, currentTrack.getDJ());
	}
	
	public void toggleShuffle() {
		trackList.toggleShuffle(context.getBotTextChannel());
	}
	
	public void skip() {
		player.stopTrack();
		play(true);
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
		context.getDynamicMessages().completeNowPlayingMessagesSong();
		
		status = SpeakerStatus.Waiting;
		
		if (endReason.mayStartNext) {
			play(false);
			return;
		}
	}
	
	public void clearSongs() {
		trackList.clear();
	}
	
	public void pause() {
		if (status == SpeakerStatus.Waiting) {
			TextSender.send(context.getBotTextChannel(), "No track currently playing, unable to pause");
		} else if (player.isPaused()) {
			TextSender.send(context.getBotTextChannel(), "Resuming playback");
			player.unpause();
			status = SpeakerStatus.Playing;
		} else {
			TextSender.send(context.getBotTextChannel(), "Pausing");
			player.pause();
			status = SpeakerStatus.Paused;
		}
	}
	
	public void stop() {
		if (status == SpeakerStatus.Waiting) {
			TextSender.sendThenWait(context.getBotTextChannel(), "Unable to stop track, Not currently playing a song");
			return;
		}
		
		player.stopTrack();
		clearSongs();
		status = SpeakerStatus.Waiting;
		TextSender.sendThenWait(context.getBotTextChannel(), "Stopping track");
	}
	
	public void sendNowPlaying() {
		if (status == SpeakerStatus.Playing) {
			context.getDynamicMessages().sendNowPlayingMessage();
		} else {
			TextSender.sendThenWait(context.getBotTextChannel(), "Not currently playing a song");
		}
	}

	public void connect(Guild guild, VoiceChannel voiceChannel, TextChannel textChannel) {
		guild.getAudioManager().openAudioConnection(voiceChannel);
		guild.getAudioManager().setSendingHandler(player.getSendHandler());
		
		TextSender.send(textChannel, "On my way!");
	}
	
	public String getNowPlayingMessageText() {
		int current = trackList.songsPlayed();
		int total = trackList.size()+current;
		String currentSong = "**"+currentTrack.getTrack().getInfo().title+"**";
		String messageText = "> Now Playing ("+current+"/"+total+") " + currentSong;
		return messageText;
	}

	public void tick() {
		if (status != SpeakerStatus.Waiting) {
			context.getDynamicMessages().updateNowPlayingState();
		}
	}
	
	public void addPlay(MessageReceivedEvent event) {
		// Get Links
		String[] args = event.getMessage().getContentRaw().split(" ");
		String[] links = Arrays.copyOfRange(args, 1, args.length);
		
		addPlay(links, event.getTextChannel(), event.getAuthor().getName());
	}
	
	public void addPlay(String[] links, TextChannel channel, String dj) {
		// Add First one (This will block for that one link)
		trackList.addToQueue(channel, dj, links, true);
		
		// Check if no links where given, when song is already playing
		if (status == SpeakerStatus.Playing && links.length == 0) {
			TextSender.sendThenWait(context.getBotTextChannel(),"Already Playing a track");
			return;
		}
		
		// Play
		play(true);
		
		// Let user know number of songs in queue, if added
		if (trackList.size() == 1 && links.length != 0) {
			TextSender.sendThenWait(channel,"There is now " + trackList.size() + " song in the queue!");
		} else if (trackList.size() > 1 && links.length != 0) {
			TextSender.sendThenWait(channel,"There are now " + trackList.size() + " songs in the queue!");
		}
	}

	public void leave() {
		TextSender.sendThenWait(context.getBotTextChannel(), "Left channel, guess the party is over.");
		clearSongs();
		stop();
	}

	public LoadedTrack getCurrentTrack() {
		return currentTrack;
	}

	public String getCurrentTimestamp() {
		return player.getCurrentTimestamp();
	}

	public long getQueueTimeLength() {
		return trackList.getQueueTimeLength();
	}
	
	public SpeakerStatus getStatus() {
		return status;
	}
	
	public TrackList getTrackList() {
		return trackList;
	}
}
