// RemoveRosterEntryToGroupAction.java

package jmri.jmrit.roster;

import jmri.jmrit.XmlFile;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import jmri.util.JmriJFrame;
import javax.swing.JLabel;
import java.awt.FlowLayout;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;

import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Remove a locomotive from a roster grouping.
 *
 *
 * <hr>
 * This file is part of JMRI.
 * <P>
 * JMRI is free software; you can redistribute it and/or modify it under 
 * the terms of version 2 of the GNU General Public License as published 
 * by the Free Software Foundation. See the "COPYING" file for a copy
 * of this license.
 * <P>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 * <P>
 * @author	Kevin Dickerson   Copyright (C) 2009
 * @version	$Revision: 1.1 $
 */
public class RemoveRosterEntryToGroupAction extends AbstractAction {

    /**
     * @param s Name of this action, e.g. in menus
     * @param who Component that action is associated with, used
     *              to ensure proper position in of dialog boxes
     */
    public RemoveRosterEntryToGroupAction(String s, Component who) {
        super(s);
        _who = who;
    }

    Component _who;
    String curRosterGroup;
    JmriJFrame frame = null;
    JComboBox typeBox;
    JLabel jLabel = new JLabel("Select the Group");
    JComboBox groupBox;
    JComboBox rosterBox;
    JButton okButton = new JButton("Remove");
    JButton cancelButton = new JButton("Exit");
    
    public void actionPerformed(ActionEvent event) {
        frame = new JmriJFrame("DisAssociate Loco from Group");
        Roster roster = Roster.instance();
        curRosterGroup = roster.getRosterGroup();
        roster.setRosterGroup(null);
        rosterBox = roster.fullRosterComboBox();
        groupBox = roster.rosterGroupBox();
        updateRosterEntry((String) groupBox.getSelectedItem());
        groupBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateRosterEntry((String) groupBox.getSelectedItem());
            }
        });
        
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        JPanel p;
        p = new JPanel(); p.setLayout(new FlowLayout());
        p.add(jLabel);
        p.add(groupBox);
        p.add(rosterBox);
        frame.getContentPane().add(p);
        
        p = new JPanel(); p.setLayout(new FlowLayout());
        p.add(okButton);
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    okPressed();
                }
            });
        p.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        frame.getContentPane().add(p);
        frame.pack();
        frame.setVisible(true);

        roster.setRosterGroup(curRosterGroup);
    }

    /**
     * Can provide some mechanism to prompt for user for one
     * last chance to change his/her mind
     * @return true if user says to continue
     */
    boolean userOK(String entry) {
        return ( JOptionPane.YES_OPTION ==
                 JOptionPane.showConfirmDialog(_who,
                                               "Delete roster group "+entry ,
                                               "Delete roster group "+entry+"?", JOptionPane.YES_NO_OPTION));
    }

    // initialize logging
    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RemoveRosterEntryToGroupAction.class.getName());

    public void okPressed(){
        String group = Roster.instance().getRosterGroupPrefix()+((String) groupBox.getSelectedItem());
        RosterEntry re = Roster.instance().entryFromTitle((String) rosterBox.getSelectedItem());
        re.deleteAttribute(group);
        re.updateFile();
        Roster.instance().writeRosterFile();
        Roster.instance().updateComboBox(rosterBox);
        frame.pack();
    
    }
    
    public void dispose(){
        Roster.instance().setRosterGroup(curRosterGroup);
        frame.dispose();
    
    }
    
    public void updateRosterEntry(String group) {
        Roster.instance().setRosterGroup(group);
        Roster.instance().updateComboBox(rosterBox);
        frame.pack();
    }
}
