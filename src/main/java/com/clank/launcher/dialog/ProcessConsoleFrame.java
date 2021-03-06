/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.clank.launcher.dialog;

import com.clank.launcher.swing.LinedBoxPanel;
import com.clank.launcher.swing.SwingHelper;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

import static com.clank.launcher.util.SharedLocale._;

/**
 * A version of the console window that can manage a process.
 */
public class ProcessConsoleFrame extends ConsoleFrame {
    
    private JButton killButton;
    private JButton minimizeButton;
    private TrayIcon trayIcon;

    @Getter private Process process;
    @Getter @Setter private boolean killOnClose;

    private PrintWriter processOut;

    /**
     * Create a new instance of the frame.
     *
     * @param numLines the number of log lines
     * @param colorEnabled whether color is enabled in the log
     */
    public ProcessConsoleFrame(int numLines, boolean colorEnabled) {
        super(_("console.title"), numLines, colorEnabled);
        processOut = new PrintWriter(
                getMessageLog().getOutputStream(new Color(0, 0, 255)), true);
        initComponents();
        updateComponents();
    }

    /**
     * Track the given process.
     *
     * @param process the process
     */
    public synchronized void setProcess(Process process) {
        try {
            Process lastProcess = this.process;
            if (lastProcess != null) {
                processOut.println(_("console.processEndCode", lastProcess.exitValue()));
            }
        } catch (IllegalThreadStateException e) {
        }

        if (process != null) {
            processOut.println(_("console.attachedToProcess"));
        }

        this.process = process;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateComponents();
            }
        });
    }

    private synchronized boolean hasProcess() {
        return process != null;
    }

    @Override
    protected void performClose() {
        if (hasProcess()) {
            if (killOnClose) {
                performKill();
            }
        }

        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
        }

        super.performClose();
    }

    private void performKill() {
        if (!confirmKill()) {
            return;
        }

        synchronized (this) {
            if (hasProcess()) {
                process.destroy();
                setProcess(null);
            }
        }

        updateComponents();
    }

    protected void initComponents() {
        killButton = new JButton(_("console.forceClose"));
        minimizeButton = new JButton(); // Text set later

        LinedBoxPanel buttonsPanel = getButtonsPanel();
        buttonsPanel.addGlue();
        buttonsPanel.addElement(killButton);
        buttonsPanel.addElement(minimizeButton);
        
        killButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performKill();
            }
        });

        minimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contextualClose();
            }
        });
        
        if (!setupTrayIcon()) {
            minimizeButton.setEnabled(true);
        }
    }

    private boolean setupTrayIcon() {
        if (!SystemTray.isSupported()) {
            return false;
        }

        trayIcon = new TrayIcon(getTrayRunningIcon());
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(_("console.trayTooltip"));

        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reshow();
            }
        });
       
        PopupMenu popup = new PopupMenu();
        MenuItem item;

        popup.add(item = new MenuItem(_("console.trayTitle")));
        item.setEnabled(false);

        popup.add(item = new MenuItem(_("console.tray.showWindow")));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reshow();
            }
        });

        popup.add(item = new MenuItem(_("console.tray.forceClose")));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performKill();
            }
        });
       
        trayIcon.setPopupMenu(popup);
       
        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
            return true;
        } catch (AWTException e) {
        }
        
        return false;
    }

    private synchronized void updateComponents() {
        Image icon = hasProcess() ? getTrayRunningIcon() : getTrayClosedIcon();

        killButton.setEnabled(hasProcess());

        if (!hasProcess() || trayIcon == null) {
            minimizeButton.setText(_("console.closeWindow"));
        } else {
            minimizeButton.setText(_("console.hideWindow"));
        }

        if (trayIcon != null) {
            trayIcon.setImage(icon);
        }

        setIconImage(icon);
    }

    private synchronized void contextualClose() {
        if (!hasProcess() || trayIcon == null) {
            performClose();
        } else {
            minimize();
        }

        updateComponents();
    }

    private boolean confirmKill() {
        return SwingHelper.confirmDialog(this,  _("console.confirmKill"), _("console.confirmKillTitle"));
    }

    private void minimize() {
        setVisible(false);
    }

    private void reshow() {
        setVisible(true);
        requestFocus();
    }

}
