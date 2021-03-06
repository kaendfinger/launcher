/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.clank.launcher.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.clank.launcher.model.modpack.Feature;
import lombok.Data;

@Data
public class FeaturePattern {

    @JsonProperty("properties")
    private Feature feature;
    @JsonProperty("files")
    private FnPatternList filePatterns;

    public boolean matches(String path) {
        return filePatterns != null && filePatterns.matches(path);
    }
}
