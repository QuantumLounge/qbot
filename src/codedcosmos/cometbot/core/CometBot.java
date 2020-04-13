/*
 *     Discord CometBot by codedcosmos
 *
 *     CometBot is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License 3 as published by
 *     the Free Software Foundation.
 *     CometBot is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License 3 for more details.
 *     You should have received a copy of the GNU General Public License 3
 *     along with CometBot.  If not, see <https://www.gnu.org/licenses/>.
 */

package codedcosmos.cometbot.core;

import codedcosmos.cometbot.event.EventHandler;
import codedcosmos.cometbot.guild.chat.messages.CometCommandListener;
import codedcosmos.cometbot.guild.context.CometGuildContext;
import codedcosmos.cometbot.guild.context.CometGuildHandler;
import codedcosmos.cometbot.guild.voice.lava.MusicPlayer;
import codedcosmos.hyperdiscord.bot.ArgsParser;
import codedcosmos.hyperdiscord.guild.GuildHandler;
import codedcosmos.hyperdiscord.utils.debug.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Random;

public class CometBot {

	// Version
	public static final String VERSION = "1.1";

	// Guilds
	public static GuildHandler<CometGuildContext> guilds;
	
	// Commands
	public static CometCommandListener commands;
	
	// Math
	public static Random random = new Random();

	public static void main(String[] args) {
		Log.print("Starting Comet Bot");

		HashMap<String, String> mappedArgs = ArgsParser.parseArgs(args, "token");

		String token = mappedArgs.get("token");
		
		// Setup guilds
		guilds = new CometGuildHandler();

		// Commands
		commands = new CometCommandListener();

		// Prepare music player
		MusicPlayer.init();
		Log.print("Prepared Music Player");

		try {
			JDABuilder builder = new JDABuilder(token);

			builder.setActivity(Activity.listening(".help"));
			builder.addEventListeners(commands);
			builder.addEventListeners(new EventHandler());

			JDA jda = builder.build();

			ExecutorThread thread = new ExecutorThread("TickingThread", 10) {
				@Override
				public void onStart() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Log.printErr(e);
					}
				}

				@Override
				public void onStop() {

				}

				@Override
				public void run() {
					for (CometGuildContext guild : guilds.getGuilds()) {
						guild.getSpeaker().tick();
					}
				}
			};
			thread.start();
		} catch (LoginException e) {
			Log.printErr(e);
		}
	}
}
