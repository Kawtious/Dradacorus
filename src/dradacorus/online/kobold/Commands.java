/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.kobold;

import dradacorus.online.dragon.IDragonServer;
import dradacorus.online.server.lairs.ILair;
import dradacorus.online.server.lairs.LairUtils;
import dradacorus.online.utils.SocketHelper;
import java.util.List;

public class Commands {

    private final IDragonServer dragon;

    private final IKoboldSocket kobold;

    public Commands(IDragonServer dragon, IKoboldSocket kobold) {
        this.dragon = dragon;
        this.kobold = kobold;
    }

    public void help() {
        SocketHelper.send(kobold, listCommands());
    }

    public String listCommands() {
        StringBuilder sb = new StringBuilder();

        sb.append("List of commands:\n");

        String[] commandList = {
            "/help",
            "/setname",
            "/createlair", "/createroom",
            "/joinlair", "/joinroom",
            "/leavelair", "/leaveroom",
            "/invite",
            "/accept",
            "/decline",
            "/disconnect",
            "/setlairname", "/setroomname",
            "/setlairpassword", "/setroompassword",
            "/kick", "/ban",
            "/op", "/deop",
            "/listkobolds", "/listlairs", "/listlobbies", "/listlairs", "/listrooms"
        };

        for (String command : commandList) {
            sb.append(command).append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        sb.append("\n");
        return sb.toString();
    }

    public void setKoboldName(String name) {
        kobold.setKoboldName(name);
        SocketHelper.send(kobold, "Name set to " + kobold.getKoboldName());
    }

    public void createLair(String name, String password) {
        if (name.isEmpty()) {
            SocketHelper.send(kobold, "A name is required for the lair");
            return;
        }

        if (LairUtils.findLairByName(dragon, name) != -1) {
            SocketHelper.send(kobold, "A lair with that name already exists");
            return;
        }

        if (!password.isEmpty()) {
            dragon.createLair(kobold, name, password);
        } else {
            dragon.createLair(kobold, name);
        }

        SocketHelper.send(kobold, "Created lair " + kobold.getLair().getName());
    }

    public void joinLair(String name, String password) {
        if (kobold.getLair() != null) {
            SocketHelper.send(kobold, "You are already in a lair");
            return;
        }

        if (name.isEmpty()) {
            SocketHelper.send(kobold, "You didn't select a valid lair");
            return;
        }

        if (dragon.getLairs().isEmpty()) {
            SocketHelper.send(kobold, "No lairs are available");
            return;
        }

        int lairIdx = LairUtils.findLairByName(dragon, name);

        if (lairIdx == -1) {
            SocketHelper.send(kobold, "Layer not found");
            return;
        }

        ILair lair = dragon.getLairs().get(lairIdx);

        if (LairUtils.isBanned(lair.getBlacklist(), kobold)) {
            SocketHelper.send(kobold, "You are banned from the lair");
            return;
        }

        if (lair.hasPassword()) {
            if (password.isEmpty()) {
                SocketHelper.send(kobold, "A password is required to join the lair");
                return;
            }

            if (!password.equals(lair.getPassword())) {
                SocketHelper.send(kobold, "Wrong password");
                return;
            }
        }

        dragon.addKoboldToLair(lair, kobold);

        SocketHelper.send(kobold, "Joined lair " + kobold.getLair().getName());
        SocketHelper.sendTo(lair, kobold.getKoboldName() + " joined the lair");
    }

    public void leaveLair() {
        if (kobold.getLair() == null) {
            SocketHelper.send(kobold, "You are not currently in a lair");
            return;
        }

        ILair lair = kobold.getLair();

        lair.kick(kobold);

        SocketHelper.send(kobold, "Left lair " + lair.getName());

        SocketHelper.sendTo(lair, kobold.getKoboldName() + " left the lair");
    }

    public void invite(String name, String message) {
        if (kobold.getLair() == null) {
            SocketHelper.send(kobold, "You are not currently in a lair");
            return;
        }

        if (name.isEmpty()) {
            SocketHelper.send(kobold, "You need to specify the kobold you want to invite");
            return;
        }

        int koboldIdx = LairUtils.findKoboldByName(dragon.getKobolds(), name);

        if (koboldIdx == -1) {
            SocketHelper.send(kobold, "Kobold not found");
            return;
        }

        IKoboldSocket kobold1 = dragon.getKobolds().get(koboldIdx);

        if (kobold1.equals(kobold)) {
            SocketHelper.send(kobold, "You cannot send an invite to yourself");
            return;
        }

        Invite invite = new Invite(kobold, kobold.getLair());

        kobold1.addInvite(invite);

        SocketHelper.send(kobold, "Sent invite to kobold " + kobold1.getKoboldName());
        SocketHelper.send(kobold1, "Kobold " + kobold.getKoboldName() + " invited you to lair " + kobold.getLair().getName() + " (/accept or /decline <name of the inviter>)" + (!message.isEmpty() ? ": " + message : ""));
    }

    public void accept(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(kobold, "Argument not valid");
            return;
        }

        int inviteIdx = LairUtils.findInviteByKoboldName(kobold.getInvites(), name);

        if (inviteIdx == -1) {
            SocketHelper.send(kobold, "Invite not found");
            return;
        }

        Invite invite = kobold.getInvites().get(inviteIdx);

        if (kobold.getLair() != null) { //if kobold is in a lair
            if (!kobold.getLair().equals(invite.getLair())) { //if it's not the same lair as the invite's
                leaveLair();
                joinLair(invite.getLair().getName(), invite.getLair().getPassword());
            }
        } else {
            joinLair(invite.getLair().getName(), invite.getLair().getPassword());
        }

        IKoboldSocket inviteSender = invite.getSender();
        SocketHelper.send(kobold, "Accepted invite of kobold " + inviteSender.getKoboldName());

        kobold.removeInvite(invite);
    }

    public void decline(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(kobold, "Argument not valid");
            return;
        }

        int inviteIdx = LairUtils.findInviteByKoboldName(kobold.getInvites(), name);

        if (inviteIdx == -1) {
            SocketHelper.send(kobold, "Invite not found");
            return;
        }

        Invite invite = kobold.getInvites().get(inviteIdx);

        IKoboldSocket kobold1 = invite.getSender();
        SocketHelper.send(kobold1, "Declined invite of kobold " + kobold1.getKoboldName());

        kobold1.removeInvite(invite);
    }

    public void disconnect() {
        kobold.disconnect();
    }

    public void setLairName(String name) {
        if (kobold.getLair() == null) {
            SocketHelper.send(kobold, "You need to be in a lair for that");
            return;
        }

        if (!LairUtils.isOperator(kobold.getLair().getOperators(), kobold)) {
            SocketHelper.send(kobold, "You are not an operator of this lair");
            return;
        }

        if (name.isEmpty()) {
            SocketHelper.send(kobold, "A name is required for the lair");
            return;
        }

        kobold.getLair().setName(name);
    }

    public void setLairPassword(String password) {
        if (kobold.getLair() == null) {
            SocketHelper.send(kobold, "You need to be in a lair for that");
            return;
        }

        if (!LairUtils.isOperator(kobold.getLair().getOperators(), kobold)) {
            SocketHelper.send(kobold, "You are not an operator of this lair");
            return;
        }

        kobold.getLair().setName(password);
    }

    public void kick(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(kobold, "You need to select a kobold");
            return;
        }

        if (!LairUtils.isOperator(kobold.getLair().getOperators(), kobold)) {
            SocketHelper.send(kobold, "You are not an operator of this lair");
            return;
        }

        int koboldIdx = LairUtils.findKoboldByName(kobold.getLair().getKobolds(), name);

        if (koboldIdx == -1) {
            SocketHelper.send(kobold, "Kobold not found");
            return;
        }

        IKoboldSocket kobold1 = kobold.getLair().getKobolds().get(koboldIdx);

        ILair lair = kobold1.getLair();

        lair.kick(kobold1);

        SocketHelper.send(kobold1, "You have been kicked from lair " + lair.getName());

        SocketHelper.sendTo(lair, kobold1.getKoboldName() + " was kicked from the lair");
    }

    public void ban(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(kobold, "You need to select a kobold");
            return;
        }

        if (!LairUtils.isOperator(kobold.getLair().getOperators(), kobold)) {
            SocketHelper.send(kobold, "You are not an operator of this lair");
            return;
        }

        int koboldIdx = LairUtils.findKoboldByName(kobold.getLair().getKobolds(), name);

        if (koboldIdx == -1) {
            SocketHelper.send(kobold, "Kobold not found");
            return;
        }

        IKoboldSocket kobold1 = kobold.getLair().getKobolds().get(koboldIdx);

        ILair lair = kobold1.getLair();

        lair.ban(kobold1);

        SocketHelper.send(kobold1, "You have been banned from from lair " + lair.getName());

        SocketHelper.sendTo(lair, kobold1.getKoboldName() + " was banned from the lair");
    }

    public void op(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(kobold, "Argument is not valid");
            return;
        }

        if (!LairUtils.isOperator(kobold.getLair().getOperators(), kobold)) {
            SocketHelper.send(kobold, "You are not an operator of this lair");
            return;
        }

        int koboldIdx = LairUtils.findKoboldByName(kobold.getLair().getKobolds(), name);

        if (koboldIdx == -1) {
            SocketHelper.send(kobold, "Kobold not found");
            return;
        }

        IKoboldSocket kobold1 = kobold.getLair().getKobolds().get(koboldIdx);

        SocketHelper.send(kobold1, kobold1.getKoboldName() + " is now an operator");

        kobold1.getLair().op(kobold1);

        SocketHelper.send(kobold1, kobold1.getKoboldName() + " is now an operator");

        SocketHelper.send(kobold1, "You are now an operator");
    }

    public void deop(String name) {
        if (name.isEmpty()) {
            SocketHelper.send(kobold, "Argument is not valid");
            return;
        }

        if (!LairUtils.isOperator(kobold.getLair().getOperators(), kobold)) {
            SocketHelper.send(kobold, "You are not an operator of this lair");
            return;
        }

        int koboldIdx = LairUtils.findKoboldByName(kobold.getLair().getKobolds(), name);

        if (koboldIdx == -1) {
            SocketHelper.send(kobold, "Kobold not found");
            return;
        }

        IKoboldSocket kobold1 = kobold.getLair().getKobolds().get(koboldIdx);

        kobold1.getLair().deop(kobold1);

        SocketHelper.send(kobold1, kobold1.getKoboldName() + " is no longer an operator");

        SocketHelper.send(kobold1, "You are no longer an operator");
    }

    public void listKobolds() {
        StringBuilder sb = new StringBuilder();

        List<IKoboldSocket> kobolds = dragon.getKobolds();

        if (kobold.getLair() != null) {
            kobolds = kobold.getLair().getKobolds();
        }

        for (IKoboldSocket kobold1 : kobolds) {
            sb.append(kobold1.getKoboldName()).append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        SocketHelper.send(kobold, sb.toString());
    }

    public void listLairs() {
        if (kobold.getLair() != null) {
            SocketHelper.send(kobold, "You cannot get a list of lairs inside a lair");
            return;
        }

        StringBuilder sb = new StringBuilder();

        if (dragon.getLairs().isEmpty()) {
            return;
        }

        for (ILair lair : dragon.getLairs()) {
            sb.append(lair.getName()).append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        SocketHelper.send(kobold, sb.toString());
    }

    public void unknown(String command) {
        SocketHelper.send(kobold, "Unknown command: " + command);
    }

    public IKoboldSocket getKobold() {
        return kobold;
    }

}
