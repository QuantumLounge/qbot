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

package codedcosmos.cometbot.guild.context;

import codedcosmos.cometbot.guild.voice.MusicPlayer;
import codedcosmos.cometbot.guild.voice.speaker.MusicSpeaker;
import codedcosmos.cometbot.utils.log.Log;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.ArrayList;

public class GuildContext {

	private Guild guild;
	private MusicSpeaker speaker;
	private boolean isConnectedToVoice;
	private VoiceChannel voiceChannel;

	public GuildContext(Guild guild) {
		this.guild = guild;
		this.speaker = new MusicSpeaker();
		isConnectedToVoice = false;
	}

	public boolean matches(long guildid) {
		return (guild.getIdLong() == guildid);
	}

	public boolean matches(GuildContext context) {
		return (guild.getIdLong() == context.getIdLong());
	}

	public MusicSpeaker getSpeaker() {
		return speaker;
	}

	public String getName() {
		return guild.getName();
	}

	public void disconnectFromVoice() {
		isConnectedToVoice = false;
		guild.getAudioManager().closeAudioConnection();
		speaker.clearSongs();
	}

	public long getIdLong() {
		return guild.getIdLong();
	}

	public boolean isConnectedToVoice() {
		return isConnectedToVoice;
	}

	public void setVoiceChannel(VoiceChannel voicechannel) {
		this.voiceChannel = voicechannel;
		isConnectedToVoice = true;
	}

	public VoiceChannel getVoiceChannel() {
		return voiceChannel;
	}
}
