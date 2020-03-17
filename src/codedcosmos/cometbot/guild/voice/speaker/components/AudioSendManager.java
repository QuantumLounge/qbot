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

package codedcosmos.cometbot.guild.voice.speaker.components;

import codedcosmos.cometbot.guild.voice.lava.AudioPlayerSendHandler;
import codedcosmos.cometbot.guild.voice.lava.MusicPlayer;
import codedcosmos.hyperdiscord.utils.text.TextUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioSendManager {
	// Player
	private AudioPlayer player;
	private AudioSendHandler sendHandler;
	
	public AudioSendManager(AudioEventListener listener) {
		player = MusicPlayer.generatePlayer();
		player.setVolume(100);
		player.setFrameBufferDuration(120);
		player.addListener(listener);
		
		sendHandler = new AudioPlayerSendHandler(player);
	}
	
	// Playing
	public void startTrack(AudioTrack track) {
		player.startTrack(track, true);
	}
	
	// Stopping
	public void stopTrack() {
		player.stopTrack();
	}
	
	// Pausing
	public boolean isPaused() {
		return player.isPaused();
	}
	
	public void pause() {
		player.setPaused(true);
	}
	
	public void unpause() {
		player.setPaused(false);
	}

	// Getters
	public String getCurrentTimestamp() {
		return TextUtils.convertToMMSS(player.getPlayingTrack().getPosition()) + " / " + TextUtils.convertToMMSS(player.getPlayingTrack().getDuration());
	}
	
	public AudioSendHandler getSendHandler() {
		return sendHandler;
	}
}