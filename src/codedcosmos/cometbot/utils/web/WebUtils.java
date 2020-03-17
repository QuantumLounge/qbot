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

package codedcosmos.cometbot.utils.web;

import codedcosmos.hyperdiscord.utils.debug.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class WebUtils {
	public static void main(String[] args) {
		try {
			searchForSongs("stonebank");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<SearchTrack> searchForSongs(String search) throws IOException {
		ArrayList<SearchTrack> searches = new ArrayList<SearchTrack>();
		
		searches.addAll(searchSoundcloud(search));
		
		return searches;
	}
	
	public static ArrayList<SearchTrack> searchSoundcloud(String search) throws IOException {
		ArrayList<SearchTrack> searches = new ArrayList<SearchTrack>();
		
		search = search.replaceAll(" ", "%20");
		String link = "https://soundcloud.com/search?q="+search;
		
		Document doc = Jsoup.connect(link).get();
		Elements songs = doc.getElementsByTag("h2");
		
		for (Element element : songs) {
			Elements atags = element.getElementsByTag("a");
			if (atags.size() != 1) continue;
			
			Element tag = atags.get(0);
			Log.print(tag);
			String song_link = "https://soundcloud.com" + tag.attr("href");
			String song_content = tag.text();
			
			// Parse webpage
			Document songdoc = Jsoup.connect(song_link).get();

			if (songdoc.getElementsByTag("contents").size() > 0) {
				Log.print("True " + song_link);
			} else {
				Log.print("False " + song_link);
			}
			//searches.add(new SearchTrack(song_link, song_content));
		}
		
		return searches;
	}
}
