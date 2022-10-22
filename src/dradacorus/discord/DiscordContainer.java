/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.discord;

import java.io.Serializable;

public class DiscordContainer implements Serializable {

    private static final long serialVersionUID = -4156074L;

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

    public DiscordContainer(String title, String details, String partyTitle, int partySize, int partyMax) {
        this.title = title;
        this.details = details;
        this.bigImage = "";
        this.bigImageText = "";
        this.partyTitle = partyTitle;
        this.partySize = partySize;
        this.partyMax = partyMax;
    }

    public DiscordContainer(String title, String details, String bigImage, String bigImageText, String partyTitle, int partySize, int partyMax) {
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
