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

import codedcosmos.cometbot.guild.chat.ChatListener;
import codedcosmos.cometbot.guild.chat.Command;
import codedcosmos.cometbot.guild.chat.commands.*;
import codedcosmos.cometbot.event.EventHandler;
import codedcosmos.cometbot.guild.context.Guilds;
import codedcosmos.cometbot.guild.voice.MusicPlayer;
import codedcosmos.cometbot.utils.log.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;

public class CometBot {

	// Version
	private static final String VERSION = "0.1 - alpha";

	// Commands
	public static ArrayList<Command> commands;

	// Configurable by arguments

	// -token
	// Sets the token key for the bot
	private static String TOKEN;

	// -cachesize
	// Sets cache size in megabytes
	private static int CACHE_SIZE = 5000;

	public static void main(String[] args) {
		Log.print("Starting Comet Bot");

		HashMap<String, String> argMap = new HashMap<String, String>();

		Log.print("Extracting arguments");
		for (int i = 0; i < args.length; i++) {
			if (i + 1 >= args.length) break;

			String arg1 = args[i];
			String arg2 = args[i + 1];

			if (arg1.startsWith("-") && !arg2.startsWith("-")) {
				// Add them
				argMap.put(arg1.substring(1), arg2);
			}
		}

		Log.print("Applying Launch Arguments");

		String parse = "null";

		try {
			if (argMap.containsKey("token")) {
				// Set token
				parse = "token";
				TOKEN = argMap.get("token");
			} else {
				// Default
				TOKEN = "null";
			}
			Log.print(argMap.containsKey("cache_size"));
			if (argMap.containsKey("cache_size")) {
				// Set
				parse = "cache_size";
				CACHE_SIZE = Integer.parseInt(argMap.get("cache_size"));
			} else {
				// Default
				CACHE_SIZE = 5000;
			}
		} catch (NumberFormatException e) {
			Log.printErr("Argument '" + parse + "' failed to parse correctly");
			Log.printErr(e);
		}

		if (TOKEN == null || TOKEN.equals("null")) {
			Log.printErr("Token is null, start program with -token INSERT_TOKEN_HERE");
			Log.printErr("Program will now stop");
			return;
		}

		// Load Commands
		Log.print("Loading commands");
		commands = new ArrayList<Command>();

		// Get Commands
		commands.add(new ClearQueue());
		commands.add(new Help());
		commands.add(new Join());
		commands.add(new Leave());
		commands.add(new Pause());
		commands.add(new Ping());
		commands.add(new Play());
		commands.add(new Shuffle());
		commands.add(new Skip());
		commands.add(new Stop());

		Log.print("Found " + commands.size() + " commands!");

		// Prepare guilds
		Guilds.init();
		Log.print("Prepared Guilds");

		// Prepare music player
		MusicPlayer.init();
		Log.print("Prepared Music Player");

		try {
			JDABuilder builder = new JDABuilder(TOKEN);

			builder.setActivity(Activity.listening(".help"));
			builder.addEventListeners(new ChatListener());
			builder.addEventListeners(new EventHandler());

			JDA jda = builder.build();
		} catch (LoginException e) {
			Log.printErr(e);
		}
	}
}
