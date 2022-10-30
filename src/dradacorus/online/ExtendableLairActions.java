/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online;

import dradacorus.online.ExtendableKoboldSocket.Invite;
import dradacorus.online.sound.SoundData;
import dradacorus.online.sound.SoundTrack;
import dradacorus.online.utils.LairUtils;
import dradacorus.online.utils.SocketHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ExtendableLairActions implements ILairActions {

    private final IDragonServer dragon;

    public ExtendableLairActions(IDragonServer dragon) {
        this.dragon = dragon;
    }

    public List<String> getArguments(String str) {
        List<String> arguments = new ArrayList<>();

        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(str);
        while (m.find()) {
            arguments.add(m.group(1).replace("\"", ""));
        }

        return arguments;
    }

    public String getArgument(List<String> arguments, int index) {
        if (index < 0 || arguments.isEmpty() || index >= arguments.size()) {
            return "";
        }

        return arguments.get(index);
    }

    @Override
    public void executeAction(IKoboldSocket kobold, String input) {
        List<String> arguments = getArguments(input);
        String execute = getArgument(arguments, 0);

        switch (execute) {
            case "/help":
                help(kobold);
                break;
            case "/setname":
                setKoboldName(kobold, getArgument(arguments, 1));
                break;
            case "/createlair":
                createLair(kobold, getArgument(arguments, 1), getArgument(arguments, 2));
                break;
            case "/joinlair":
                joinLair(kobold, getArgument(arguments, 1), getArgument(arguments, 2));
                break;
            case "/leavelair":
                leaveLair(kobold);
                break;
            case "/invite":
                invite(kobold, getArgument(arguments, 1), getArgument(arguments, 2));
                break;
            case "/accept":
                accept(kobold, getArgument(arguments, 1));
                break;
            case "/decline":
                decline(kobold, getArgument(arguments, 1));
                break;
            case "/disconnect":
                disconnect(kobold);
                break;
            case "/setlairname":
                setLairName(kobold, getArgument(arguments, 1));
                break;
            case "/setlairpassword":
                setLairPassword(kobold, getArgument(arguments, 1));
                break;
            case "/kick":
                kick(kobold, getArgument(arguments, 1));
                break;
            case "/ban":
                ban(kobold, getArgument(arguments, 1));
                break;
            case "/op":
                op(kobold, getArgument(arguments, 1));
                break;
            case "/deop":
                deop(kobold, getArgument(arguments, 1));
                break;
            case "/listkobolds":
                listKobolds(kobold);
                break;
            case "/listlairs":
                listLairs(kobold);
                break;
            case "/playsound":
                playSound(kobold, getArgument(arguments, 1), getArgument(arguments, 2), getArgument(arguments, 3));
                break;
            //case "/playmusic":
            //playMusic(kobold, getArgument(arguments, 1), getArgument(arguments, 2));
            //break;
            //case "/stopmusic":
            //stopMusic(kobold);
            //break;

            // Unknown
            default:
                unknown(kobold, getArgument(arguments, 0));
                break;
        }
    }

    @Override
    public String listActions() {
        StringBuilder actions = new StringBuilder();
        actions.append("List of actions:\n");

        String[] actionsList = {
            "/help",
            "/setname",
            "/createlair",
            "/joinlair",
            "/leavelair",
            "/invite", "/accept", "/decline",
            "/disconnect",
            "/setlairname",
            "/setlairpassword",
            "/kick", "/ban",
            "/op", "/deop",
            "/listkobolds", "/listlairs",
            "/playsound", //"/playmusic", "/stopmusic"
        };

        for (String action : actionsList) {
            actions.append(action).append(", ");
        }

        return actions.substring(0, actions.length() - 2);
    }

    @Override
    public void help(IKoboldSocket kobold) {
        SocketHelper.Output.send(kobold, listActions());
    }

    @Override
    public void setKoboldName(IKoboldSocket kobold, String name) {
        String koboldName = name;

        int i = 1;
        while (LairUtils.findKoboldByName(dragon.getKobolds(), koboldName) != -1) {
            koboldName = name + "(" + i + ")";
            i++;
        }

        kobold.setKoboldName(koboldName);
        SocketHelper.Output.send(kobold, "Name set to " + kobold.getKoboldName());
    }

    @Override
    public void createLair(IKoboldSocket kobold, String name, String password) {
        if (kobold.getLair() != null) {
            SocketHelper.Output.send(kobold, "You are already in a lair");
            return;
        }

        if (name.isEmpty()) {
            SocketHelper.Output.send(kobold, "A name is required for the lair");
            return;
        }

        if (LairUtils.findLairByName(dragon, name) != -1) {
            SocketHelper.Output.send(kobold, "A lair with that name already exists");
            return;
        }

        if (!password.isEmpty()) {
            dragon.createLair(kobold, name, password);
        } else {
            dragon.createLair(kobold, name);
        }

        SocketHelper.Output.send(kobold, "Created lair " + kobold.getLair().getName());
    }

    @Override
    public void joinLair(IKoboldSocket kobold, String name, String password) {
        if (kobold.getLair() != null) {
            SocketHelper.Output.send(kobold, "You are already in a lair");
            return;
        }

        if (name.isEmpty()) {
            SocketHelper.Output.send(kobold, "You didn't select a valid lair");
            return;
        }

        if (dragon.getLairs().isEmpty()) {
            SocketHelper.Output.send(kobold, "No lairs are available");
            return;
        }

        int lairIdx = LairUtils.findLairByName(dragon, name);

        if (lairIdx == -1) {
            SocketHelper.Output.send(kobold, "Layer not found");
            return;
        }

        ILair lair = dragon.getLairs().get(lairIdx);

        if (LairUtils.isBanned(lair.getBlacklist(), kobold)) {
            SocketHelper.Output.send(kobold, "You are banned from the lair");
            return;
        }

        if (lair.hasPassword()) {
            if (password.isEmpty()) {
                SocketHelper.Output.send(kobold, "A password is required to join the lair");
                return;
            }

            if (!password.equals(lair.getPassword())) {
                SocketHelper.Output.send(kobold, "Wrong password");
                return;
            }
        }

        dragon.addKoboldToLair(lair, kobold);

        SocketHelper.Output.send(kobold, "Joined lair " + kobold.getLair().getName());
        SocketHelper.Output.sendTo(lair, kobold.getKoboldName() + " joined the lair");
    }

    @Override
    public void leaveLair(IKoboldSocket kobold) {
        if (kobold.getLair() == null) {
            SocketHelper.Output.send(kobold, "You are not currently in a lair");
            return;
        }

        ILair lair = kobold.getLair();

        lair.kick(kobold);

        SocketHelper.Output.send(kobold, "Left lair " + lair.getName());

        SocketHelper.Output.sendTo(lair, kobold.getKoboldName() + " left the lair");
    }

    @Override
    public void invite(IKoboldSocket kobold, String name, String message) {
        if (kobold.getLair() == null) {
            SocketHelper.Output.send(kobold, "You are not currently in a lair");
            return;
        }

        if (name.isEmpty()) {
            SocketHelper.Output.send(kobold, "You need to specify the kobold you want to invite");
            return;
        }

        int koboldIdx = LairUtils.findKoboldByName(dragon.getKobolds(), name);

        if (koboldIdx == -1) {
            SocketHelper.Output.send(kobold, "Kobold not found");
            return;
        }

        IKoboldSocket kobold1 = dragon.getKobolds().get(koboldIdx);

        if (kobold1.equals(kobold)) {
            SocketHelper.Output.send(kobold, "You cannot send an invite to yourself");
            return;
        }

        Invite invite = kobold.createInvite();

        kobold1.addInvite(invite);

        SocketHelper.Output.send(kobold, "Sent invite to " + kobold1.getKoboldName());
        SocketHelper.Output.send(kobold1, kobold.getKoboldName() + " invited you to lair " + kobold.getLair().getName() + " (/accept or /decline " + kobold.getKoboldName() + ")" + (!message.isEmpty() ? ": " + message : ""));
    }

    @Override
    public void accept(IKoboldSocket kobold, String name) {
        if (name.isEmpty()) {
            SocketHelper.Output.send(kobold, "You need to specify the name of the inviter");
            return;
        }

        int inviteIdx = LairUtils.findInviteByKoboldName(kobold.getInvites(), name);

        if (inviteIdx == -1) {
            SocketHelper.Output.send(kobold, "Invite not found");
            return;
        }

        Invite invite = kobold.getInvites().get(inviteIdx);

        if (kobold.getLair() != null) { //if kobold is in a lair
            if (!kobold.getLair().equals(invite.getLair())) { //if it's not the same lair as the invite's
                leaveLair(kobold);
                joinLair(kobold, invite.getLair().getName(), invite.getLair().getPassword());
            }
        } else {
            joinLair(kobold, invite.getLair().getName(), invite.getLair().getPassword());
        }

        IKoboldSocket inviteSender = invite.getSender();
        SocketHelper.Output.send(kobold, "Accepted invite of kobold " + inviteSender.getKoboldName());

        kobold.removeInvite(invite);
    }

    @Override
    public void decline(IKoboldSocket kobold, String name) {
        if (name.isEmpty()) {
            SocketHelper.Output.send(kobold, "You need to specify the name of the inviter");
            return;
        }

        int inviteIdx = LairUtils.findInviteByKoboldName(kobold.getInvites(), name);

        if (inviteIdx == -1) {
            SocketHelper.Output.send(kobold, "Invite not found");
            return;
        }

        Invite invite = kobold.getInvites().get(inviteIdx);

        IKoboldSocket kobold1 = invite.getSender();
        SocketHelper.Output.send(kobold1, "Declined invite of kobold " + kobold1.getKoboldName());

        kobold1.removeInvite(invite);
    }

    @Override
    public void disconnect(IKoboldSocket kobold) {
        kobold.disconnect();
    }

    @Override
    public void setLairName(IKoboldSocket kobold, String name) {
        ILair lair = dragon;

        if (kobold.getLair() != null) {
            lair = kobold.getLair();
        }

        if (!LairUtils.isOperator(lair.getOperators(), kobold)) {
            SocketHelper.Output.send(kobold, "You are not an operator of this lair");
            return;
        }

        if (name.isEmpty()) {
            SocketHelper.Output.send(kobold, "A name is required for the lair");
            return;
        }

        lair.setName(name);
    }

    @Override
    public void setLairPassword(IKoboldSocket kobold, String password) {
        ILair lair = dragon;

        if (kobold.getLair() != null) {
            lair = kobold.getLair();
        }

        if (!LairUtils.isOperator(lair.getOperators(), kobold)) {
            SocketHelper.Output.send(kobold, "You are not an operator of this lair");
            return;
        }

        lair.setName(password);
    }

    @Override
    public void kick(IKoboldSocket kobold, String name) {
        if (name.isEmpty()) {
            SocketHelper.Output.send(kobold, "You need to specify the name of a kobold");
            return;
        }

        ILair lair = dragon;

        if (kobold.getLair() != null) {
            lair = kobold.getLair();
        }

        List<IKoboldSocket> kobolds = lair.getKobolds();

        if (!LairUtils.isOperator(lair.getOperators(), kobold)) {
            SocketHelper.Output.send(kobold, "You are not an operator of this lair");
            return;
        }

        int koboldIdx = LairUtils.findKoboldByName(kobolds, name);

        if (koboldIdx == -1) {
            SocketHelper.Output.send(kobold, "Kobold not found");
            return;
        }

        IKoboldSocket kobold1 = kobolds.get(koboldIdx);

        if (kobold1.equals(kobold)) {
            SocketHelper.Output.send(kobold, "You cannot kick yourself");
            return;
        }

        lair.kick(kobold1);

        SocketHelper.Output.send(kobold1, "You have been kicked from lair " + lair.getName());

        SocketHelper.Output.sendTo(lair, kobold1.getKoboldName() + " was kicked from the lair");
    }

    @Override
    public void ban(IKoboldSocket kobold, String name) {
        if (name.isEmpty()) {
            SocketHelper.Output.send(kobold, "You need to specify the name of a kobold");
            return;
        }

        ILair lair = dragon;

        if (kobold.getLair() != null) {
            lair = kobold.getLair();
        }

        List<IKoboldSocket> kobolds = lair.getKobolds();

        if (!LairUtils.isOperator(lair.getOperators(), kobold)) {
            SocketHelper.Output.send(kobold, "You are not an operator of this lair");
            return;
        }

        int koboldIdx = LairUtils.findKoboldByName(kobolds, name);

        if (koboldIdx == -1) {
            SocketHelper.Output.send(kobold, "Kobold not found");
            return;
        }

        IKoboldSocket kobold1 = kobolds.get(koboldIdx);

        if (kobold1.equals(kobold)) {
            SocketHelper.Output.send(kobold, "You cannot ban yourself");
            return;
        }

        lair.ban(kobold1);

        SocketHelper.Output.send(kobold1, "You have been banned from lair " + lair.getName());

        SocketHelper.Output.sendTo(lair, kobold1.getKoboldName() + " was banned from the lair");
    }

    @Override
    public void op(IKoboldSocket kobold, String name) {
        if (name.isEmpty()) {
            SocketHelper.Output.send(kobold, "You need to specify the name of a kobold");
            return;
        }

        ILair lair = dragon;

        if (kobold.getLair() != null) {
            lair = kobold.getLair();
        }

        List<IKoboldSocket> kobolds = lair.getKobolds();

        if (!LairUtils.isOperator(lair.getOperators(), kobold)) {
            SocketHelper.Output.send(kobold, "You are not an operator of this lair");
            return;
        }

        int koboldIdx = LairUtils.findKoboldByName(kobolds, name);

        if (koboldIdx == -1) {
            SocketHelper.Output.send(kobold, "Kobold not found");
            return;
        }

        IKoboldSocket kobold1 = kobolds.get(koboldIdx);

        if (kobold1.equals(kobold)) {
            SocketHelper.Output.send(kobold, "You cannot make yourself an operator");
            return;
        }

        if (LairUtils.isOperator(lair.getOperators(), kobold)) {
            SocketHelper.Output.send(kobold, kobold1.getKoboldName() + " is already an operator");
            return;
        }

        SocketHelper.Output.send(kobold1, kobold1.getKoboldName() + " is now an operator");

        kobold1.getLair().op(kobold1);

        SocketHelper.Output.send(kobold1, kobold1.getKoboldName() + " is now an operator");

        SocketHelper.Output.send(kobold1, "You are now an operator");
    }

    @Override
    public void deop(IKoboldSocket kobold, String name) {
        if (name.isEmpty()) {
            SocketHelper.Output.send(kobold, "You need to specify the name of a kobold");
            return;
        }

        ILair lair = dragon;

        if (kobold.getLair() != null) {
            lair = kobold.getLair();
        }

        List<IKoboldSocket> kobolds = lair.getKobolds();

        if (!LairUtils.isOperator(lair.getOperators(), kobold)) {
            SocketHelper.Output.send(kobold, "You are not an operator of this lair");
            return;
        }

        int koboldIdx = LairUtils.findKoboldByName(kobolds, name);

        if (koboldIdx == -1) {
            SocketHelper.Output.send(kobold, "Kobold not found");
            return;
        }

        IKoboldSocket kobold1 = kobolds.get(koboldIdx);

        if (kobold1.equals(kobold)) {
            SocketHelper.Output.send(kobold, "You cannot remove your own operator privileges");
            return;
        }

        if (!LairUtils.isOperator(lair.getOperators(), kobold)) {
            SocketHelper.Output.send(kobold, kobold1.getKoboldName() + " is not an operator");
            return;
        }

        lair.deop(kobold1);

        SocketHelper.Output.send(kobold1, kobold1.getKoboldName() + " is no longer an operator");

        SocketHelper.Output.send(kobold1, "You are no longer an operator");
    }

    @Override
    public void listKobolds(IKoboldSocket kobold) {
        StringBuilder koboldsList = new StringBuilder();

        List<IKoboldSocket> kobolds = dragon.getKobolds();

        if (kobold.getLair() != null) {
            kobolds = kobold.getLair().getKobolds();
        }

        for (IKoboldSocket kobold1 : kobolds) {
            koboldsList.append(kobold1.getKoboldName()).append(", ");
        }

        koboldsList.delete(koboldsList.length() - 2, koboldsList.length());

        SocketHelper.Output.send(kobold, koboldsList.toString());
    }

    @Override
    public void listLairs(IKoboldSocket kobold) {
        if (kobold.getLair() != null) {
            SocketHelper.Output.send(kobold, "You cannot get a list of lairs inside a lair");
            return;
        }

        StringBuilder lairsList = new StringBuilder();

        if (dragon.getLairs().isEmpty()) {
            return;
        }

        for (ILair lair : dragon.getLairs()) {
            lairsList.append(lair.getName()).append(", ");
        }

        lairsList.delete(lairsList.length() - 2, lairsList.length());

        SocketHelper.Output.send(kobold, lairsList.toString());
    }

    @Override
    public void unknown(IKoboldSocket kobold, String action) {
        SocketHelper.Output.send(kobold, "Unknown action: " + action);
    }

    @Override
    public void playSound(IKoboldSocket kobold, String soundfile, String volume, String cycleCount) {
        if (soundfile.isEmpty()) {
            SocketHelper.Output.send(kobold, "You need to specify the name of a sound file");
            return;
        }

        double vol = SoundTrack.getMaxVolume();

        if (!volume.isEmpty()) {
            vol = Double.parseDouble(volume);
        }

        int cycles = 1;

        if (!cycleCount.isEmpty()) {
            cycles = Integer.parseInt(cycleCount);
        }

        ILair lair = dragon;

        if (kobold.getLair() != null) {
            lair = kobold.getLair();
        }

        List<IKoboldSocket> kobolds = lair.getKobolds();

        if (!(lair instanceof IDragonServer) && !LairUtils.isOperator(lair.getOperators(), kobold)) {
            SocketHelper.Output.send(kobold, "You are not an operator of this lair");
            return;
        }

        File file = new File("Audio/" + soundfile);

        if (!file.exists()) {
            SocketHelper.Output.send(kobold, "Sound file not found");
            return;
        }

        SoundData sndData = new SoundData(file, vol, cycles);

        SocketHelper.Output.sendTo(lair, "Playing sound " + soundfile);

        for (IKoboldSocket kobold1 : kobolds) {
            SocketHelper.Output.sendSoundPlayRequest(kobold1, sndData);
        }
    }

}
