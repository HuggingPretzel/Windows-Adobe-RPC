  
/**
 *      Copyright 2020 Daniel Sanchez
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 package com.districtmeps.winadoberpc;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;

public class DiscordRPCClient {

    private static boolean ready = false;
    private static JTextArea outText;
    private static AEProcess ae = null;
    private static boolean showUpdates = true;

    public static void main(String[] args) throws Exception {
        
        

        JFrame frame = new JFrame("After Effects Discord RPC");
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());

        outText = new JTextArea();
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(outText);

        JButton start = new JButton("Start RPC");
        start.setEnabled(true);
        start.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // AEProcess ae = new AEProcess("AfterFX.exe", 8592L, "Running", "0:00:26", "Adobe After Effects 2020 - C:\\Users\\danie\\Desktop\\Editing\\IRL Test\\RL test.aep");
                // outText.setText(outText.getText() + "\n" + ae.seperateWindowTitle()[1]);
                initDiscord();
                ae = null;
                start.setEnabled(false);
                
            }
        });

        JButton stop = new JButton("Stop");
        stop.setEnabled(true);
        stop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                start.setEnabled(true);
                DiscordRPCClient.ready = false;
                DiscordRPC.discordShutdown();
                ae = null;
                
                // System.exit(0);
            }
        });

        JButton hideUps = new JButton("Hide Updates");
        hideUps.setEnabled(true);
        JButton showUps = new JButton("Show Updates");
        showUps.setEnabled(true);
        showUps.setVisible(false);


        showUps.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                hideUps.setVisible(true);
                showUps.setVisible(false);
                showUpdates = true;
                outText.setText(outText.getText() + "\nWill now display future RPC Updates");
            }
        });

        hideUps.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showUps.setVisible(true);
                hideUps.setVisible(false);
                showUpdates = false;
                outText.setText(outText.getText() + "\nWill not display future RPC Updates");
            }
        });
        

        panel2.add(start);
        panel2.add(stop);
        panel2.add(hideUps);
        panel2.add(showUps);

        panel1.add(sp, BorderLayout.CENTER);
        panel1.add(panel2, BorderLayout.SOUTH);

        frame.add(panel1);
        frame.pack();
        frame.setSize(300, 500);
        frame.setMinimumSize(new Dimension(200,200));
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);




        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Closing Discord hook.");
            outText.setText(outText.getText() + "\nClosing Discord hook");
            DiscordRPC.discordShutdown();
        }));

        outText.setLineWrap(true);
        outText.setText(outText.getText() + "\nThank you for using my After Effects Discord RPC Client.\nTo use the client just press StartRPC and it will automatically check if AE is open and update on its own.\n\nGithub Source: \nIf you any questions or issues please contact me on discord *Danboi#1962*");

        System.out.println("Running callbacks...");
        outText.setText(outText.getText() + "\nRunning callbacks..");

        
        while (true) {
            
            DiscordRPC.discordRunCallbacks();

            if (!ready)
                continue;

            //check for AE and update

            AEProcess newAe = ProcessFinder.getAeProcess();
            // System.out.println(ae + " " + newAe);
            if(!(ae == null && newAe == null)){
                if(ae == null){
                    ae = newAe;
    
                    if(ae != null){
                        updateRPC(ae);
                        if(showUpdates) outText.setText(outText.getText() + "\nUpdating RPC 1");
                    } else {
                        outText.setText(outText.getText() + "\nAE is not running");
                    }
                } else if(newAe == null){
                    ae = null;
                    DiscordRPC.discordClearPresence();
                } else if(!(ae.isEqual(newAe))){
                    String title = newAe.seperateWindowTitle()[0];
                    // System.out.println("-" + title + "-");
                    if(title.equals("Updating...") || title.equals("DroverLord")){

                        // System.out.println(title);
                    } else {
                        updateRPC(newAe);
                        if(showUpdates) outText.setText(outText.getText() + "\nUpdating RPC 2");
                        ae = newAe;
                        // System.out.println(ae.returnAll() + "\n" + newAe.returnAll() + "\n\n");
                    }
                      
                }
            } else {
                DiscordRPC.discordClearPresence();
            }

            
        }
    }

    private static void initDiscord() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Welcome " + user.username + "#" + user.discriminator + ".");
            outText.setText(outText.getText() + "\nWelcome " + user.username + "#" + user.discriminator + ".");
            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Editing: ");
            presence.setDetails("AE");
            presence.setStartTimestamps(Instant.now().getEpochSecond());
            presence.setBigImage("aftereffects_large", "AE");
            presence.setSmallImage("aftereffects_small", "CC");
            DiscordRPC.discordUpdatePresence(presence.build());
            DiscordRPCClient.ready = true;
        }).build();
        DiscordRPC.discordInitialize("757390784575045743", handlers, false);
        DiscordRPC.discordRegister("757390784575045743", "");
    }

    // public static void updateRPC(){
    //     ae = ProcessFinder.getAeProcess();

    //     if(ae != null){

    //         DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Editing: " + ae.seperateWindowTitle()[1]);
    //         String details = ae.seperateWindowTitle()[0].substring(ae.seperateWindowTitle()[0].indexOf(" ") + 1);
    //         presence.setDetails(details);
    //         presence.setStartTimestamps(Instant.now().getEpochSecond());
    //         presence.setBigImage("aftereffects_large", ae.seperateWindowTitle()[0]);
    //         presence.setSmallImage("aftereffects_small", "CC");
    //         DiscordRPC.discordUpdatePresence(presence.build());
    //     } else {
    //         outText.setText(outText.getText() + "\nAE is not running");
    //     }
    // }

    public static void updateRPC(AEProcess ae){
        
        DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Editing: " + ae.seperateWindowTitle()[1]);
        String details = ae.seperateWindowTitle()[0].substring(ae.seperateWindowTitle()[0].indexOf(" ") + 1);
        presence.setDetails(details);
        presence.setStartTimestamps(Instant.now().getEpochSecond());
        presence.setBigImage("aftereffects_large", ae.seperateWindowTitle()[0]);
        presence.setSmallImage("aftereffects_small", "CC");
        DiscordRPC.discordUpdatePresence(presence.build());
        
    }

}