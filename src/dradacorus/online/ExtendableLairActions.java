/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online;

import dradacorus.online.ExtendableKoboldSocket.Invite;
import dradacorus.online.utils.LairUtils;
import dradacorus.online.utils.SocketHelper;
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
            case "/help", "/?" -> {
                help(kobold);
            }
            case "/setname", "/nickname", "/name" -> {
                setKoboldName(kobold, getArgument(arguments, 1));
            }
            case "/createlair" -> {
                createLair(kobold, getArgument(arguments, 1), getArgument(arguments, 2));
            }
            case "/joinlair" -> {
                joinLair(kobold, getArgument(arguments, 1), getArgument(arguments, 2));
            }
            case "/leavelair" -> {
                leaveLair(kobold);
            }
            case "/invite" -> {
                invite(kobold, getArgument(arguments, 1), getArgument(arguments, 2));
            }
            case "/accept" -> {
                accept(kobold, getArgument(arguments, 1));
            }
            case "/decline" -> {
                decline(kobold, getArgument(arguments, 1));
            }
            case "/disconnect" -> {
                disconnect(kobold);
            }
            case "/setlairname" -> {
                setLairName(kobold, getArgument(arguments, 1));
            }
            case "/setlairpassword" -> {
                setLairPassword(kobold, getArgument(arguments, 1));
            }
            case "/kick" -> {
                kick(kobold, getArgument(arguments, 1));
            }
            case "/ban" -> {
                ban(kobold, getArgument(arguments, 1));
            }
            case "/op" -> {
                op(kobold, getArgument(arguments, 1));
            }
            case "/deop" -> {
                deop(kobold, getArgument(arguments, 1));
            }
            case "/listkobolds" -> {
                listKobolds(kobold);
            }
            case "/listlairs" -> {
                listLairs(kobold);
            }

            // Unknown
            default -> {
                unknown(kobold, getArgument(arguments, 0));
            }
        }
    }

    @Override
    public void help(IKoboldSocket kobold) {
        SocketHelper.Output.send(kobold, listActions());
    }

    @Override
    public String listActions() {
        StringBuilder sb = new StringBuilder();

        sb.append("List of actions:\n");

        String[] actionsList = {
            "/help",
            "/setname",
            "/createlair", "/createroom",
            "/joinlair", "/joinroom",
            "/leavelair", "/leaveroom",
            "/invite", "/accept", "/decline",
            "/disconnect",
            "/setlairname",
            "/setlairpassword",
            "/kick", "/ban",
            "/op", "/deop",
            "/listkobolds", "/listlairs"
        };

        for (String action : actionsList) {
            sb.append(action).append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        sb.append("\n");
        return sb.toString();
    }

    @Override
    public void setKoboldName(IKoboldSocket kobold, String name) {
        kobold.setKoboldName(name);
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

        SocketHelper.Output.send(kobold, "Sent invite to kobold " + kobold1.getKoboldName());
        SocketHelper.Output.send(kobold1, "Kobold " + kobold.getKoboldName() + " invited you to lair " + kobold.getLair().getName() + " (/accept or /decline " + kobold.getKoboldName() + ")" + (!message.isEmpty() ? ": " + message : ""));
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
        ILair lair = (ILair) dragon;

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
        ILair lair = (ILair) dragon;

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

        ILair lair = (ILair) dragon;

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

        ILair lair = (ILair) dragon;

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

        ILair lair = (ILair) dragon;

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

        ILair lair = (ILair) dragon;

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
        StringBuilder sb = new StringBuilder();

        List<IKoboldSocket> kobolds = dragon.getKobolds();

        if (kobold.getLair() != null) {
            kobolds = kobold.getLair().getKobolds();
        }

        for (IKoboldSocket kobold1 : kobolds) {
            sb.append(kobold1.getKoboldName()).append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        SocketHelper.Output.send(kobold, sb.toString());
    }

    @Override
    public void listLairs(IKoboldSocket kobold) {
        if (kobold.getLair() != null) {
            SocketHelper.Output.send(kobold, "You cannot get a list of lairs inside a lair");
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

        SocketHelper.Output.send(kobold, sb.toString());
    }

    @Override
    public void unknown(IKoboldSocket kobold, String action) {
        SocketHelper.Output.send(kobold, "Unknown action: " + action);
    }

}
