/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.clank.launcher.auth;

import com.clank.launcher.LauncherException;

/**
 * Thrown on authentication error.
 */
public class AuthenticationException extends LauncherException {

    public AuthenticationException(String message, String localizedMessage) {
        super(message, localizedMessage);
    }

    public AuthenticationException(Throwable cause, String localizedMessage) {
        super(cause, localizedMessage);
    }
}
