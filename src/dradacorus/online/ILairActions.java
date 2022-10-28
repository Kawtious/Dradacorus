/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online;

public interface ILairActions {

    public void executeAction(IKoboldSocket kobold, String input);

    public String listActions();

    public void help(IKoboldSocket kobold);

    public void setKoboldName(IKoboldSocket kobold, String name);

    public void createLair(IKoboldSocket kobold, String name, String password);

    public void joinLair(IKoboldSocket kobold, String name, String password);

    public void leaveLair(IKoboldSocket kobold);

    public void invite(IKoboldSocket kobold, String name, String message);

    public void accept(IKoboldSocket kobold, String name);

    public void decline(IKoboldSocket kobold, String name);

    public void disconnect(IKoboldSocket kobold);

    public void setLairName(IKoboldSocket kobold, String name);

    public void setLairPassword(IKoboldSocket kobold, String password);

    public void kick(IKoboldSocket kobold, String name);

    public void ban(IKoboldSocket kobold, String name);

    public void op(IKoboldSocket kobold, String name);

    public void deop(IKoboldSocket kobold, String name);

    public void listKobolds(IKoboldSocket kobold);

    public void listLairs(IKoboldSocket kobold);

    public void unknown(IKoboldSocket kobold, String action);

    public void playSound(IKoboldSocket kobold, String soundfile, String volume, String cycleCount);

}
