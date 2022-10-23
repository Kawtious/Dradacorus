/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dradacorus.discord;

import dradacorus.online.IKoboldSocket;
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

/**
 * Version: 0.9.0
 */
public class DiscordHandler {

    public static final String VERSION = "0.9.0";

    private static boolean enabled = false;

    /**
     * Initializes Discord Rich Presence
     *
     * @param echo  Show updates in console
     * @param appId
     */
    public static boolean init(String appId) {
        if (isEnabled()) {
            return false;
        }

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Welcome " + user.username + "#" + user.discriminator + "!");
        }).build();

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
        if (!isEnabled()) {
            return;
        }

        DiscordRPC.discordShutdown();
    }

    /**
     * Updates the Rich Presence shown on Discord
     *
     * @param title       Assigns a title to the Rich Presence
     * @param description Assigns a description to the Rich Presence
     * @param bigImage
     * @param name
     * @param partyTitle
     * @param partySize   Determines how many are currently in the lobby
     * @param partyMax    Determines the lobby's size
     */
    public static void update(DiscordContainer container) {
        if (!isEnabled()) {
            return;
        }

        DiscordRichPresence richPresence;

        if (container.hasBigImage() && container.hasParty()) {
            richPresence = new DiscordRichPresence.Builder(container.getTitle())
                    .setDetails(container.getDetails())
                    .setBigImage(container.getBigImage(), container.getBigImageText())
                    .setParty(container.getPartyTitle(), container.getPartySize(), container.getPartyMax())
                    .build();
        } else if (container.hasBigImage()) {
            richPresence = new DiscordRichPresence.Builder(container.getTitle())
                    .setDetails(container.getDetails())
                    .setBigImage(container.getBigImage(), container.getBigImageText())
                    .build();
        } else if (container.hasParty()) {
            richPresence = new DiscordRichPresence.Builder(container.getTitle())
                    .setDetails(container.getDetails())
                    .setParty(container.getPartyTitle(), container.getPartySize(), container.getPartyMax())
                    .build();
        } else {
            richPresence = new DiscordRichPresence.Builder(container.getTitle())
                    .setDetails(container.getDetails())
                    .build();
        }

        DiscordRPC.discordUpdatePresence(richPresence);
    }

    public static DiscordContainer buildDiscordContainer(IKoboldSocket client) {
        DiscordContainer container = new DiscordContainer();

        StringBuilder detailsBuilder = new StringBuilder();

        if (client.getLair() != null) {
            container.setDetails("In lair " + client.getLair().getName());
            detailsBuilder
                    .append(client.getLair().getKobolds().size())
                    .append(" kobold").append((client.getLair().getKobolds().size() != 1) ? "s" : "");
        } else {
            container.setDetails("Following dragon " + client.getDragon().getName());
            detailsBuilder
                    .append(client.getDragon().getKobolds().size())
                    .append(" kobold").append((client.getDragon().getKobolds().size() != 1) ? "s" : "");
        }

        container.setTitle(detailsBuilder.toString());
        return container;
    }

    public static boolean isDiscordRunning() {
        String line;
        boolean found = false;

        try {
            String findProcess = "Discord.exe";
            String filenameFilter = "/nh /fi \"Imagename eq " + findProcess + "\"";
            String tasklist = System.getenv("windir") + "/system32/tasklist.exe " + filenameFilter;
            Process p = Runtime.getRuntime().exec(tasklist);//new ProcessBuilder(tasklist, "").start();

            try (final BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
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

    private DiscordHandler() {
    }

}
