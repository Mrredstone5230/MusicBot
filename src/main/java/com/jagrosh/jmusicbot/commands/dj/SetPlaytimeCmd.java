/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;

/**
 *
 * @author PolishKrowa
 */
public class SetPlaytimeCmd extends DJCommand
{
    public SetPlaytimeCmd(Bot bot)
    {
        super(bot);
        this.name = "setplaytime";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.help = "sets currently play time (Play cursor)";
        this.arguments = "[timestamp. ex: 1h30m3s]";
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(handler.getPlayer().getPlayingTrack()==null)
        {
            event.reply("No music playing.");
            return;
        }

        long position = handler.getPlayer().getPlayingTrack().getPosition();
        if(event.getArgs().isEmpty())
        {
            event.reply("Current position is `"+toReadableText(position)+"`");
        }
        else
        {
            long positionStamp = 0;
            if (!event.getArgs().equalsIgnoreCase("reset"))
                positionStamp = convertTimeStamp(event.getArgs());

            if(positionStamp == -1)
                event.reply(event.getClient().getError()+" Invalid timestamp. Should be 1h4m3s (For example)");
            else
            {
                handler.getPlayer().getPlayingTrack().setPosition(positionStamp == 0 ? 1 : positionStamp);
                event.reply(" Position changed from `"+toReadableText(position)+"` to `"+toReadableText(positionStamp)+"`");
            }
        }
    }

    private String toReadableText(long timestamp) {
        int hours = (int) timestamp / 3600000;

        long minutesStamp = timestamp % 3600000;
        int minutes = (int) minutesStamp / 60000;

        long secondsStamp = minutesStamp % 60000;
        int seconds = (int) secondsStamp / 1000;

        return (hours > 0 ? hours + " hours, " : "") + minutes + " minutes and " + seconds + " seconds.";
    }

    private long convertTimeStamp(String input) {
        // 1h3m1s
        long output = -1;

        try {
            return Long.parseLong(input);
        } catch(NumberFormatException e) {}

        input = input.replaceAll(" ", "");
        if (input.contains("h")) {
            String inputHours = input.split("h")[0];
            if (input.split("h").length > 1)
                input = input.split("h")[1];
            try {
                output += (long) Integer.parseInt(inputHours) * 3600000;
            } catch(NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        }
        if (input.contains("m")) {
            String inputMinutes = input.split("m")[0];
            if (input.split("m").length > 1)
                input = input.split("m")[1];
            try {
                output += (long) Integer.parseInt(inputMinutes) * 60000;
            } catch(NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        }
        if (input.contains("s")) {
            String inputSeconds = input.split("s")[0];
            try {
                output += (long) Integer.parseInt(inputSeconds) * 1000;
            } catch(NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        }

        return output;
    }
    
}
