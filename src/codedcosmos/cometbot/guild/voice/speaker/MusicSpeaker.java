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

import codedcosmos.cometbot.guild.chat.channel.TextChannelHandler;
import codedcosmos.cometbot.guild.chat.commands.Play;
import codedcosmos.cometbot.guild.voice.MusicPlayer;
import codedcosmos.cometbot.utils.log.Log;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class MusicSpeaker extends AudioEventAdapter {
	// ##### VARIABLES ##### \\
	private AudioPlayer player;
	private CopyOnWriteArrayList<LoadedTrack> queue;

	private AudioSendHandler sendHandler;

	private boolean isPlaying = false;

	private TextChannel lastTextChannel;

	// Number of songs played in a session
	private int numberOfSongs = 0;

	// The currently playing song
	private LoadedTrack currentTrack;

	// Shuffling related
	private boolean isShuffling = false;
	private Random shufflingRandom = new Random();

	// ##### VARIABLES ##### \\

	// ##### CONSTRUCTOR ##### \\
	public MusicSpeaker() {
		player = MusicPlayer.generatePlayer();
		player.setVolume(100);
		player.setFrameBufferDuration(120);
		player.addListener(this);

		queue = new CopyOnWriteArrayList<LoadedTrack>();

		sendHandler = new AudioPlayerSendHandler(player);
	}
	// ##### CONSTRUCTOR ##### \\

	// ##### PLAY ##### \\
	public void play(TextChannel channel, String dj, String link) {
		addToQueue(channel, dj, link);
		play();
	}

	public void play() {
		if (isPlaying) return;

		if (player.isPaused()) {
			player.setPaused(false);
			isPlaying = true;
			return;
		}

		if (queue.size() == 0) {
			Log.printErr("Error nothing in queue!");
			return;
		}

		LoadedTrack loadedTrack = getTrackFromQueue();

		// Set Current Track
		currentTrack = loadedTrack;

		AudioTrack track = loadedTrack.getTrack();

		player.startTrack(track, true);
		numberOfSongs++;
		isPlaying = true;

		// Send Message
		Log.print("Playing " + track);
		Play.sendPlayMessage(this, lastTextChannel);
	}
	// ##### PLAY ##### \\

	// ##### CONTINUE/SKIP PLAYBACK ##### \\
	public void skip() {
		stop();
		play();
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
		isPlaying = false;
		if (endReason.mayStartNext) {
			if (queue.size() > 0) {
				play();
			} else {
				// No Songs in Queue
				TextChannelHandler.sendThenWait(lastTextChannel, "No more songs in queue");
			}
		}
	}
	// ##### CONTINUE/SKIP PLAYBACK ##### \\

	// ##### QUEUE ##### \\
	public int addToQueue(TextChannel channel, String dj, String[] links) {
		for (String link : links) {
			addToQueue(channel, dj, link);
		}
		return queue.size();
	}

	public int addToQueue(TextChannel channel, String dj, String link) {
		MusicPlayer.getPlayerManager().loadItemOrdered(channel, link, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				LoadedTrack track = new LoadedTrack(audioTrack, dj, link);
				queue.add(track);
				Log.print("Added song " + track.summary());
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				for (AudioTrack audioTrack : audioPlaylist.getTracks()) {
					LoadedTrack track = new LoadedTrack(audioTrack, dj, link);
					queue.add(track);
					Log.print("Added song " + track.summary());
				}
			}

			@Override
			public void noMatches() {
				TextChannelHandler.sendThenWait(channel, "Failed to load song '" + link + "', no matches avaliable.");
			}

			@Override
			public void loadFailed(FriendlyException e) {
				Log.printErr(e);
				TextChannelHandler.sendThenWait(channel, "Failed to load song '" + link + "', load Failed!");
			}
		});

		return queue.size();
	}

	public void clearSongs() {
		queue.clear();
		numberOfSongs = 0;
	}

	private LoadedTrack getTrackFromQueue() {
		if (isShuffling) {

			int i = shufflingRandom.nextInt(queue.size());
			LoadedTrack loadedTrack = queue.get(i);
			queue.remove(i);
			return loadedTrack;

		} else {

			LoadedTrack loadedTrack = queue.get(0);
			queue.remove(0);
			return loadedTrack;

		}
	}
	// ##### QUEUE ##### \\

	// ##### STATE CHANGE ##### \\
	public void pause() {
		if (player.isPaused()) {
			player.setPaused(false);
			isPlaying = true;
		} else {
			player.setPaused(true);
			isPlaying = false;
		}
	}

	public void stop() {
		player.stopTrack();
		isPlaying = false;
	}

	public boolean toggleShuffle() {
		// Switch
		if (isShuffling) isShuffling = false;
		else isShuffling = true;

		// Return
		return isShuffling;
	}
	// ##### STATE CHANGE ##### \\

	// ##### GETTERS ##### \\
	public int getQueueSize() {
		return queue.size();
	}

	public boolean hasSongsInQueue() {
		return queue.size() > 0;
	}

	public AudioSendHandler getSendHandler() {
		return sendHandler;
	}

	public String getCurrentSong() {
		return "**"+currentTrack.getTrack().getInfo().title+"** by **"+currentTrack.getDJ()+"**";
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public int getSongsPlayed() {
		return numberOfSongs;
	}

	public void updateLastTextChannel(TextChannel channel) {
		this.lastTextChannel = channel;
	}
	// ##### GETTERS ##### \\
}
