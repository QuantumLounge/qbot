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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class LoadedTrack {
	private AudioTrack track;
	private String link;

	// User who posted track
	private String dj;

	public LoadedTrack(AudioTrack track, String dj, String link) {
		this.track = track;
		this.dj = dj;
		this.link = link;
	}

	public AudioTrack getTrack() {
		return track;
	}

	public String getLink() {
		return link;
	}

	public String getDJ() {
		return dj;
	}

	public String summary() {
		return "LoadedTrack: (" + link + " "  + dj + ")";
	}
}
