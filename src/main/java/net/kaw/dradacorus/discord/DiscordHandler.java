/*
 * MIT License
 * 
 * Copyright (c) 2022 Kawtious
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.kaw.dradacorus.discord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.kaw.dradacorus.online.IKoboldSocket;

/**
 * Version: 0.9.0
 */
public class DiscordHandler {

    public static final String VERSION = "0.9.1";

    private static boolean enabled = false;

    /**
     * Initializes Discord Rich Presence
     *
     * @param echo Show updates in console
     * @param appId
     */
    public static boolean init(String appId) {
        if (enabled) {
            return false;
        }

        if (!isDiscordRunning()) {
            return false;
        }

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().build();

        DiscordRPC.discordInitialize(appId, handlers, true);
        enabled = true;

        return true;
    }

    public static void pause() {
        if (isEnabled()) {
            enabled = false;
        }
    }

    public static void resume() {
        if (!isEnabled()) {
            enabled = true;
        }
    }

    /**
     * Self-explanatory. Shuts down Rich Presence
     */
    public static void shutdown() {
        if (!enabled) {
            return;
        }

        DiscordRPC.discordShutdown();
    }

    /**
     * Updates the Rich Presence shown on Discord
     *
     * @param title Assigns a title to the Rich Presence
     * @param description Assigns a description to the Rich Presence
     * @param bigImage
     * @param name
     * @param partyTitle
     * @param partySize Determines how many are currently in the lobby
     * @param partyMax Determines the lobby's size
     */
    public static void update(DiscordContainer container) {
        if (!enabled) {
            return;
        }

        DiscordRichPresence richPresence;

        if (container.hasBigImage() && container.hasParty()) {
            richPresence = new DiscordRichPresence.Builder(container.getTitle())
                    .setDetails(container.getDetails())
                    .setBigImage(container.getBigImage(), container.getBigImageText())
                    .setParty(container.getPartyTitle(), container.getPartySize(),
                            container.getPartyMax())
                    .build();
        } else if (container.hasBigImage()) {
            richPresence = new DiscordRichPresence.Builder(container.getTitle())
                    .setDetails(container.getDetails())
                    .setBigImage(container.getBigImage(), container.getBigImageText()).build();
        } else if (container.hasParty()) {
            richPresence = new DiscordRichPresence.Builder(container.getTitle())
                    .setDetails(container.getDetails()).setParty(container.getPartyTitle(),
                            container.getPartySize(), container.getPartyMax())
                    .build();
        } else {
            richPresence = new DiscordRichPresence.Builder(container.getTitle())
                    .setDetails(container.getDetails()).build();
        }

        DiscordRPC.discordUpdatePresence(richPresence);
    }

    public static DiscordContainer buildDiscordContainer(IKoboldSocket client) {
        DiscordContainer container = new DiscordContainer();

        StringBuilder details = new StringBuilder();

        if (client.getLair() != null) {
            details.append("In lair ").append(client.getLair().getName());
            details.append(client.getLair().getKobolds().size());
            details.append(" kobold")
                    .append((client.getLair().getKobolds().size() != 1) ? "s" : "");
        } else {
            details.append("Following ").append(client.getDragon().getName());
            details.append(client.getDragon().getKobolds().size());
            details.append(" kobold")
                    .append((client.getDragon().getKobolds().size() != 1) ? "s" : "");
        }

        container.setTitle(details.toString());
        return container;
    }

    public static boolean isDiscordRunning() {
        String line;
        boolean found = false;

        try {
            String findProcess = "Discord.exe";
            String filenameFilter = "/nh /fi \"Imagename eq " + findProcess + "\"";
            String tasklist = System.getenv("windir") + "/system32/tasklist.exe " + filenameFilter;

            Process p = Runtime.getRuntime().exec(tasklist);

            try (final BufferedReader input =
                    new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                List<String> procs = new ArrayList<>();
                line = input.readLine();
                while (line != null) {
                    procs.add(line);
                    line = input.readLine();
                }

                input.close();

                found = procs.stream().filter(row -> row.contains(findProcess)).count() > 0;
                // Head-up! If no processes were found - we still get:
                // "INFO: No tasks are running which match the specified criteria."
                p.destroy();
            }
        } catch (IOException ex) {
            Logger.getLogger(IKoboldSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

        return found;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    private DiscordHandler() {}
}
