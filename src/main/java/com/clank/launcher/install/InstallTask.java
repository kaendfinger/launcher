/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.clank.launcher.install;

import com.clank.concurrency.ProgressObservable;

public interface InstallTask extends ProgressObservable {

    void execute() throws Exception;

}
