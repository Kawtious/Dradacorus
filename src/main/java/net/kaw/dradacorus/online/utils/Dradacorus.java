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
package net.kaw.dradacorus.online.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import net.kaw.dradacorus.utils.DragonConsole;

public class Dradacorus {

    private static final String VERSION_TITLE = "Kobold";

    private static final int RELEASE = 0;

    private static final int MAJOR = 9;

    private static final int MINOR = 0;

    private static final int PATCH = 0;

    private static final String REPO = "https://github.com/Kawtious/Dradacorus";

    private static final String REPO_VERSION_FILE =
            "https://raw.githubusercontent.com/Kawtious/Dradacorus/main/VERSION";

    public static void verifyVersion() {
        DragonConsole.writeLine("Running Dradacorus " + VERSION_TITLE + " on version " + RELEASE
                + "." + MAJOR + "." + MINOR + "." + PATCH);

        try {
            URL getVersion = new URL(REPO_VERSION_FILE);

            String currentGithubVersion;
            try (Scanner getVersionText = new Scanner(getVersion.openStream())) {
                currentGithubVersion = getVersionText.nextLine();
            }

            String currentVersionTitle = currentGithubVersion.split("-")[0];
            String currentVersionNumber = currentGithubVersion.split("-")[1];

            int currentReleaseVersion = Integer.parseInt(currentVersionNumber.split("\\.")[0]);
            int currentMajorVersion = Integer.parseInt(currentVersionNumber.split("\\.")[1]);
            int currentMinorVersion = Integer.parseInt(currentVersionNumber.split("\\.")[2]);
            int currentPatchVersion = Integer.parseInt(currentVersionNumber.split("\\.")[3]);

            if (!VERSION_TITLE.equals(currentVersionTitle)) {
                DragonConsole.Error.writeLine(
                        "This release of Dradacorus is outdated! Please update to latest release "
                                + currentGithubVersion + " at " + REPO);
                return;
            }

            if (RELEASE < currentReleaseVersion) {
                DragonConsole.Error.writeLine(
                        "This instance of Dradacorus is " + (currentReleaseVersion - RELEASE)
                                + " release versions behind, please update to latest version "
                                + currentGithubVersion + " at " + REPO);
                return;
            } else if (RELEASE > currentReleaseVersion) {
                DragonConsole.writeLine("This instance of Dradacorus is "
                        + (RELEASE - currentReleaseVersion) + " release versions ahead");
                return;
            }

            if (MAJOR < currentMajorVersion) {
                DragonConsole.Error
                        .writeLine("This instance of Dradacorus is " + (currentMajorVersion - MAJOR)
                                + " major versions behind, please update to latest version "
                                + currentGithubVersion + " at " + REPO);
                return;
            } else if (MAJOR > currentMajorVersion) {
                DragonConsole.writeLine("This instance of Dradacorus is "
                        + (MAJOR - currentMajorVersion) + " major versions ahead");
                return;
            }

            if (MINOR < currentMinorVersion) {
                DragonConsole
                        .writeLine("This instance of Dradacorus is " + (currentMinorVersion - MINOR)
                                + " minor versions behind, latest version is "
                                + currentGithubVersion + " at " + REPO);
                return;
            } else if (MINOR > currentMinorVersion) {
                DragonConsole.writeLine("This instance of Dradacorus is "
                        + (MINOR - currentMinorVersion) + " minor versions ahead");
                return;
            }

            if (PATCH < currentPatchVersion) {
                DragonConsole.writeLine("New patch for Dradacorus " + currentPatchVersion
                        + " is available at " + REPO);
                return;
            }

            DragonConsole.writeLine("Dradacorus is up to date!");
        } catch (IOException ex) {
            // there was some connection problem, or the file did not exist on the server,
            // or your URL was not in the right format.
            // think about what to do now, and put it here.
            // for now, close the program.
            DragonConsole.Error.writeLine("Error: Could not verify Dradacorus version");
        }
    }

    private Dradacorus() {}

}
