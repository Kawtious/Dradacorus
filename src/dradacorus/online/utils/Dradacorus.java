/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.utils;

import dradacorus.utils.DragonConsole;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Dradacorus {

    private static final String VERSION = "Kobold-0.8.0.0";

    public static void verifyVersion() {
        String versionTitle = VERSION.split("-")[0];
        String versionNumber = VERSION.split("-")[1];

        int releaseVersion = Integer.parseInt(versionNumber.split("\\.")[0]);
        int majorVersion = Integer.parseInt(versionNumber.split("\\.")[1]);
        int minorVersion = Integer.parseInt(versionNumber.split("\\.")[2]);
        int patchVersion = Integer.parseInt(versionNumber.split("\\.")[3]);

        DragonConsole.WriteLine(Dradacorus.class, "Running Dradacorus " + versionTitle + " on version " + versionNumber);

        try {
            URL getVersion = new URL("https://raw.githubusercontent.com/Kawtious/Dradacorus/main/VERSION");
            Scanner getVersionText = new Scanner(getVersion.openStream());
            String currentGithubVersion = getVersionText.nextLine();

            String currentVersionTitle = currentGithubVersion.split("-")[0];
            String currentVersionNumber = currentGithubVersion.split("-")[1];

            int currentReleaseVersion = Integer.parseInt(currentVersionNumber.split("\\.")[0]);
            int currentMajorVersion = Integer.parseInt(currentVersionNumber.split("\\.")[1]);
            int currentMinorVersion = Integer.parseInt(currentVersionNumber.split("\\.")[2]);
            int currentPatchVersion = Integer.parseInt(currentVersionNumber.split("\\.")[3]);

            if (!versionTitle.equals(currentVersionTitle)) {
                DragonConsole.Error.WriteLine(Dradacorus.class, "This release of Dradacorus is outdated! Please update to latest release " + currentGithubVersion + " at https://github.com/Kawtious/Dradacorus");
                return;
            }

            if (releaseVersion < currentReleaseVersion) {
                DragonConsole.Error.WriteLine(Dradacorus.class, "This instance of Dradacorus is " + (currentReleaseVersion - releaseVersion) + " release versions behind, please update to latest version " + currentGithubVersion + " at https://github.com/Kawtious/Dradacorus");
                return;
            } else if (releaseVersion > currentReleaseVersion) {
                DragonConsole.WriteLine(Dradacorus.class, "This instance of Dradacorus is " + (releaseVersion - currentReleaseVersion) + " release versions ahead");
                return;
            }

            if (majorVersion < currentMajorVersion) {
                DragonConsole.Error.WriteLine(Dradacorus.class, "This instance of Dradacorus is " + (currentMajorVersion - majorVersion) + " major versions behind, please update to latest version " + currentGithubVersion + " at https://github.com/Kawtious/Dradacorus");
                return;
            } else if (majorVersion > currentMajorVersion) {
                DragonConsole.WriteLine(Dradacorus.class, "This instance of Dradacorus is " + (majorVersion - currentMajorVersion) + " major versions ahead");
                return;
            }

            if (minorVersion < currentMinorVersion) {
                DragonConsole.WriteLine(Dradacorus.class, "This instance of Dradacorus is " + (currentMinorVersion - minorVersion) + " minor versions behind, latest version is " + currentGithubVersion + " at https://github.com/Kawtious/Dradacorus");
                return;
            } else if (minorVersion > currentMinorVersion) {
                DragonConsole.WriteLine(Dradacorus.class, "This instance of Dradacorus is " + (minorVersion - currentMinorVersion) + " minor versions ahead");
                return;
            }

            if (patchVersion < currentPatchVersion) {
                DragonConsole.WriteLine(Dradacorus.class, "New patch for Dradacorus " + currentPatchVersion + " is available at https://github.com/Kawtious/Dradacorus");
                return;
            }

            DragonConsole.WriteLine(Dradacorus.class, "Dradacorus is up to date!");
        } catch (IOException ex) {
            // there was some connection problem, or the file did not exist on the server,
            // or your URL was not in the right format.
            // think about what to do now, and put it here.
            // for now, close the program.
            DragonConsole.Error.WriteLine(Dradacorus.class, "Error: Could not verify Dradacorus version");
        }
    }

    public static String getVersion() {
        return VERSION;
    }

    private Dradacorus() {
    }

}
