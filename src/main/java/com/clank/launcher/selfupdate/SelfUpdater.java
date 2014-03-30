/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.clank.launcher.selfupdate;

import com.clank.concurrency.DefaultProgress;
import com.clank.concurrency.ProgressObservable;
import com.clank.launcher.Launcher;
import com.clank.launcher.install.FileMover;
import com.clank.launcher.install.Installer;
import lombok.NonNull;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.clank.launcher.util.SharedLocale._;

public class SelfUpdater implements Callable<File>, ProgressObservable {

    public static boolean updatedAlready = false;

    private final Launcher launcher;
    private final URL url;
    private final Installer installer;
    private ProgressObservable progress = new DefaultProgress(0, _("updater.updating"));

    public SelfUpdater(@NonNull Launcher launcher, @NonNull URL url) {
        this.launcher = launcher;
        this.url = url;
        this.installer = new Installer(launcher.getInstallerDir());
    }

    @Override
    public File call() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            File dir = launcher.getLauncherBinariesDir();
            File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jar.pack");
            File tempFile = installer.getDownloader().download(url, "", 10000, "launcher.jar.pack");

            progress = installer.getDownloader();
            installer.download();

            installer.queue(new FileMover(tempFile, file));

            progress = installer;
            installer.execute();

            updatedAlready = true;

            return file;
        } finally {
            executor.shutdownNow();
        }
    }

    @Override
    public double getProgress() {
        return progress.getProgress();
    }

    @Override
    public String getStatus() {
        return progress.getStatus();
    }

}
