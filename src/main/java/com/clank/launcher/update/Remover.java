/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.clank.launcher.update;

import com.clank.concurrency.ProgressObservable;
import com.clank.launcher.Instance;
import com.clank.launcher.LauncherException;
import com.clank.launcher.LauncherUtils;
import com.clank.launcher.persistence.Persistence;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.clank.launcher.LauncherUtils.checkInterrupted;
import static com.clank.launcher.util.SharedLocale._;

public class Remover implements Callable<Instance>, ProgressObservable {

    private final Instance instance;

    public Remover(@NonNull Instance instance) {
        this.instance = instance;
    }

    @Override
    public double getProgress() {
        return -1;
    }

    @Override
    public String getStatus() {
        return _("instanceDeleter.deleting", instance.getDir());
    }

    @Override
    public Instance call() throws Exception {
        instance.setInstalled(false);
        instance.setUpdatePending(true);
        Persistence.commitAndForget(instance);

        checkInterrupted();

        Thread.sleep(2000);

        List<File> failures = new ArrayList<File>();

        try {
            LauncherUtils.interruptibleDelete(instance.getDir(), failures);
        } catch (IOException e) {
            Thread.sleep(1000);
            LauncherUtils.interruptibleDelete(instance.getDir(), failures);
        }

        if (failures.size() > 0) {
            throw new LauncherException(failures.size() + " failed to delete",
                     _("instanceDeleter.failures", failures.size()));
        }

        return instance;
    }

}
