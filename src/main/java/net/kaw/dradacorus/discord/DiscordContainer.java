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

import java.io.Serializable;

public class DiscordContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String details;

    private String bigImage;

    private String bigImageText;

    private String partyTitle;

    private int partySize;

    private int partyMax;

    public DiscordContainer() {
        this.title = "";
        this.details = "";
        this.bigImage = "";
        this.bigImageText = "";
        this.partyTitle = "";
        this.partySize = -1;
        this.partyMax = -1;
    }

    public DiscordContainer(String title, String details) {
        this.title = title;
        this.details = details;
        this.bigImage = "";
        this.bigImageText = "";
        this.partyTitle = "";
        this.partySize = -1;
        this.partyMax = -1;
    }

    public DiscordContainer(String title, String details, String bigImage, String bigImageText) {
        this.title = title;
        this.details = details;
        this.bigImage = bigImage;
        this.bigImageText = bigImageText;
        this.partyTitle = "";
        this.partySize = -1;
        this.partyMax = -1;
    }

    public DiscordContainer(String title, String details, String partyTitle, int partySize,
            int partyMax) {
        this.title = title;
        this.details = details;
        this.bigImage = "";
        this.bigImageText = "";
        this.partyTitle = partyTitle;
        this.partySize = partySize;
        this.partyMax = partyMax;
    }

    public DiscordContainer(String title, String details, String bigImage, String bigImageText,
            String partyTitle, int partySize, int partyMax) {
        this.title = title;
        this.details = details;
        this.bigImage = bigImage;
        this.bigImageText = bigImageText;
        this.partyTitle = partyTitle;
        this.partySize = partySize;
        this.partyMax = partyMax;
    }

    public boolean hasBigImage() {
        return !bigImage.isEmpty() && !bigImageText.isEmpty();
    }

    public boolean hasParty() {
        return !partyTitle.isEmpty() && (partySize != -1) && (partyMax != -1);
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public String getBigImage() {
        return bigImage;
    }

    public String getBigImageText() {
        return bigImageText;
    }

    public String getPartyTitle() {
        return partyTitle;
    }

    public int getPartySize() {
        return partySize;
    }

    public int getPartyMax() {
        return partyMax;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setBigImage(String bigImage) {
        this.bigImage = bigImage;
    }

    public void setBigImageText(String bigImageText) {
        this.bigImageText = bigImageText;
    }

    public void setPartyTitle(String partyTitle) {
        this.partyTitle = partyTitle;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public void setPartyMax(int partyMax) {
        this.partyMax = partyMax;
    }

}
