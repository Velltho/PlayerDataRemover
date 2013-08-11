/*
* Copyright (c) 2013, LankyLord
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* * Redistributions of source code must retain the above copyright notice, this
* list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package net.lankylord.playerdataremover;

import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.HashSet;
import java.util.List;

class FileManager {

    private final PlayerDataRemover instance;
    private final HashSet<File> playerDataFolders;

    public FileManager(PlayerDataRemover plugin) {
        this.instance = plugin;
        playerDataFolders = new HashSet<>();
        addDataFolders();
    }

    private void addDataFolders() {
        List<String> enabledWorlds = instance.getConfig().getStringList("enabled-worlds");
        for (String world : enabledWorlds) {
            File dataFolder = getDataFolder(world);
            if (dataFolder.exists())
                playerDataFolders.add(dataFolder);
        }
    }

    private File getDataFolder(String worldName) {
        File worldFolder = new File(instance.getServer().getWorldContainer().getPath() + File.separatorChar + worldName);
        return new File(worldFolder.getPath() + File.separatorChar + "players");
    }

    private File getPlayerFile(File dataFolder, String playerName) {
        return new File(dataFolder.getPath() + File.separatorChar + playerName + ".dat");
    }

    void removePlayerFile(String playerName) {
        for (File file : playerDataFolders) {
            File playerFile = getPlayerFile(file, playerName);
            playerFile.delete();
        }
    }

    public void removePlayerAsyncDelayed(final String playerName) {
        BukkitTask bukkitTask = instance.getServer().getScheduler().runTaskLaterAsynchronously(instance, new Runnable() {
            @Override
            public void run() {
                removePlayerFile(playerName);
            }
        }, 60L);
    }
}
