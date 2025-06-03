/*
 * Excrele's System Booster (ESB) - A Spigot plugin for server performance monitoring and management.
 * Updated for Spigot 1.21.5 compatibility, with asynchronous scanning and improved metrics.
 * Fixed redstone scan to handle copper bulb variants correctly.
 * Fixed ground clutter management to use EntityType.ITEM instead of DROPPED_ITEM.
 * Features a GUI for server information, entity scanning, ground clutter management,
 * redstone detection, chunk corruption checking, and chunk regeneration.
 * All commands require specific permissions and use the /esb prefix.
 */
package com.excrele.esb;

import com.sun.management.OperatingSystemMXBean;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ESBPlugin extends JavaPlugin implements Listener {
    // Map to store ground items and their despawn timers
    private final Map<Item, Long> groundItems = new ConcurrentHashMap<>();
    private BukkitTask timerTask;

    @Override
    public void onEnable() {
        // Register the plugin's events
        getServer().getPluginManager().registerEvents(this, this);
        // Register the /esb command
        Objects.requireNonNull(getCommand("esb")).setExecutor(new ESBCommand());
        // Start the ground clutter timer
        startGroundClutterTimer();
        getLogger().info("Excrele's System Booster enabled for Spigot 1.21.5!");
    }

    @Override
    public void onDisable() {
        // Cancel the timer task to prevent memory leaks
        if (timerTask != null) {
            timerTask.cancel();
        }
        getLogger().info("Excrele's System Booster disabled!");
    }

    // Command handler for /esb
    private class ESBCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("esb.use")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use /esb!");
                return true;
            }
            // Open the main GUI
            openMainGUI(player);
            return true;
        }
    }

    // Create and open the main GUI
    private void openMainGUI(Player player) {
        Inventory gui = Bukkit.createInventory(new ESBInventoryHolder(), 27, ChatColor.DARK_BLUE + "ESB Control Panel");
        // Server Info
        ItemStack serverInfo = new ItemStack(Material.PAPER);
        ItemMeta serverInfoMeta = serverInfo.getItemMeta();
        serverInfoMeta.setDisplayName(ChatColor.GREEN + "Server Information");
        serverInfoMeta.setLore(Arrays.asList(ChatColor.YELLOW + "View server performance metrics"));
        serverInfo.setItemMeta(serverInfoMeta);
        gui.setItem(10, serverInfo);

        // Entity Scan
        ItemStack entityScan = new ItemStack(Material.COMPASS);
        ItemMeta entityScanMeta = entityScan.getItemMeta();
        entityScanMeta.setDisplayName(ChatColor.GREEN + "Scan Entities");
        entityScanMeta.setLore(Arrays.asList(ChatColor.YELLOW + "List top 5 chunks with most entities"));
        entityScan.setItemMeta(entityScanMeta);
        gui.setItem(11, entityScan);

        // Mob Scan
        ItemStack mobScan = new ItemStack(Material.ZOMBIE_HEAD);
        ItemMeta mobScanMeta = mobScan.getItemMeta();
        mobScanMeta.setDisplayName(ChatColor.GREEN + "Scan Mobs");
        mobScanMeta.setLore(Arrays.asList(ChatColor.YELLOW + "List top 5 chunks with most mobs"));
        mobScan.setItemMeta(mobScanMeta);
        gui.setItem(12, mobScan);

        // Ground Clutter
        ItemStack groundClutter = new ItemStack(Material.DROPPER);
        ItemMeta groundClutterMeta = groundClutter.getItemMeta();
        groundClutterMeta.setDisplayName(ChatColor.GREEN + "Manage Ground Clutter");
        groundClutterMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Scan and set timers for ground items"));
        groundClutter.setItemMeta(groundClutterMeta);
        gui.setItem(13, groundClutter);

        // Redstone Scan
        ItemStack redstoneScan = new ItemStack(Material.REDSTONE);
        ItemMeta redstoneScanMeta = redstoneScan.getItemMeta();
        redstoneScanMeta.setDisplayName(ChatColor.GREEN + "Scan Active Redstone");
        redstoneScanMeta.setLore(Arrays.asList(ChatColor.YELLOW + "List top 5 chunks with active redstone"));
        redstoneScan.setItemMeta(redstoneScanMeta);
        gui.setItem(14, redstoneScan);

        // Chunk Corruption
        ItemStack chunkCorruption = new ItemStack(Material.BARRIER);
        ItemMeta chunkCorruptionMeta = chunkCorruption.getItemMeta();
        chunkCorruptionMeta.setDisplayName(ChatColor.GREEN + "Scan Chunk Corruption");
        chunkCorruptionMeta.setLore(Arrays.asList(ChatColor.YELLOW + "List corrupted chunks"));
        chunkCorruption.setItemMeta(chunkCorruptionMeta);
        gui.setItem(15, chunkCorruption);

        // Regenerate Chunk
        ItemStack regenerateChunk = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta regenerateChunkMeta = regenerateChunk.getItemMeta();
        regenerateChunkMeta.setDisplayName(ChatColor.GREEN + "Regenerate Current Chunk");
        regenerateChunkMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Regenerate chunk if corrupted"));
        regenerateChunk.setItemMeta(regenerateChunkMeta);
        gui.setItem(16, regenerateChunk);

        player.openInventory(gui);
    }

    // InventoryHolder for GUI identification
    private static class ESBInventoryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    // Handle GUI click events
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ESBInventoryHolder)) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }
        String displayName = clickedItem.getItemMeta().getDisplayName();
        switch (displayName) {
            case "§aServer Information":
                if (player.hasPermission("esb.serverinfo")) {
                    displayServerInfo(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to view server info!");
                }
                break;
            case "§aScan Entities":
                if (player.hasPermission("esb.entityscan")) {
                    scanEntities(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to scan entities!");
                }
                break;
            case "§aScan Mobs":
                if (player.hasPermission("esb.mobscan")) {
                    scanMobs(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to scan mobs!");
                }
                break;
            case "§aManage Ground Clutter":
                if (player.hasPermission("esb.groundclutter")) {
                    manageGroundClutter(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to manage ground clutter!");
                }
                break;
            case "§aScan Active Redstone":
                if (player.hasPermission("esb.redstonescan")) {
                    scanRedstone(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to scan redstone!");
                }
                break;
            case "§aScan Chunk Corruption":
                if (player.hasPermission("esb.chunkcorruption")) {
                    scanChunkCorruption(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to scan chunk corruption!");
                }
                break;
            case "§aRegenerate Current Chunk":
                if (player.hasPermission("esb.regeneratechunk")) {
                    regenerateChunk(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to regenerate chunks!");
                }
                break;
        }
    }

    // Display server information
    private void displayServerInfo(Player player) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;
        double cpuUsage = 0.0;
        try {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            cpuUsage = osBean.getCpuLoad() * 100;
        } catch (Exception e) {
            cpuUsage = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        }
        int loadedChunks = 0;
        int entities = 0;
        int mobs = 0;
        for (World world : Bukkit.getWorlds()) {
            loadedChunks += world.getLoadedChunks().length;
            for (Entity entity : world.getEntities()) {
                entities++;
                if (entity.getType().isAlive()) {
                    mobs++;
                }
            }
        }
        int players = Bukkit.getOnlinePlayers().size();
        // commented out as  not currently working properly
        //double[] tps = Bukkit.getServer().getTPS();
        player.sendMessage(ChatColor.AQUA + "=== Server Information ===");
        player.sendMessage(ChatColor.YELLOW + "RAM: " + usedMemory + "/" + totalMemory + " MB");
        player.sendMessage(ChatColor.YELLOW + "CPU Usage: " + (cpuUsage >= 0 ? String.format("%.2f", cpuUsage) : "N/A") + "%");
        player.sendMessage(ChatColor.YELLOW + "Loaded Chunks: " + loadedChunks);
        player.sendMessage(ChatColor.YELLOW + "Entities: " + entities);
        player.sendMessage(ChatColor.YELLOW + "Mobs: " + mobs);
        player.sendMessage(ChatColor.YELLOW + "Players: " + players);
       // commented out as not currently working properly
        // player.sendMessage(ChatColor.YELLOW + "TPS: " + String.format("%.2f", tps[0]));
    }

    // Scan chunks for entities (asynchronous)
    private void scanEntities(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Map<Chunk, Integer> entityCounts = new HashMap<>();
                for (World world : Bukkit.getWorlds()) {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        int count = chunk.getEntities().length;
                        entityCounts.put(chunk, count);
                    }
                }
                List<Map.Entry<Chunk, Integer>> sorted = entityCounts.entrySet().stream()
                        .sorted(Map.Entry.<Chunk, Integer>comparingByValue().reversed())
                        .limit(5)
                        .collect(Collectors.toList());
                // Return to main thread to send messages
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.AQUA + "=== Top 5 Chunks with Most Entities ===");
                        for (Map.Entry<Chunk, Integer> entry : sorted) {
                            Chunk chunk = entry.getKey();
                            player.sendMessage(ChatColor.YELLOW + "World: " + chunk.getWorld().getName() +
                                    ", X: " + chunk.getX() + ", Z: " + chunk.getZ() +
                                    ", Entities: " + entry.getValue());
                        }
                    }
                }.runTask(ESBPlugin.this);
            }
        }.runTaskAsynchronously(this);
    }

    // Scan chunks for mobs (asynchronous)
    private void scanMobs(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Map<Chunk, Integer> mobCounts = new HashMap<>();
                for (World world : Bukkit.getWorlds()) {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        int count = 0;
                        for (Entity entity : chunk.getEntities()) {
                            if (entity.getType().isAlive()) {
                                count++;
                            }
                        }
                        mobCounts.put(chunk, count);
                    }
                }
                List<Map.Entry<Chunk, Integer>> sorted = mobCounts.entrySet().stream()
                        .sorted(Map.Entry.<Chunk, Integer>comparingByValue().reversed())
                        .limit(5)
                        .collect(Collectors.toList());
                // Return to main thread to send messages
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.AQUA + "=== Top 5 Chunks with Most Mobs ===");
                        for (Map.Entry<Chunk, Integer> entry : sorted) {
                            Chunk chunk = entry.getKey();
                            player.sendMessage(ChatColor.YELLOW + "World: " + chunk.getWorld().getName() +
                                    ", X: " + chunk.getX() + ", Z: " + chunk.getZ() +
                                    ", Mobs: " + entry.getValue());
                        }
                    }
                }.runTask(ESBPlugin.this);
            }
        }.runTaskAsynchronously(this);
    }

    // Scan and manage ground clutter
    private void manageGroundClutter(Player player) {
        groundItems.clear();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() == EntityType.ITEM) {
                    Item item = (Item) entity;
                    groundItems.put(item, System.currentTimeMillis() + 5 * 60 * 1000);
                }
            }
        }
        player.sendMessage(ChatColor.GREEN + "Ground clutter scan complete. Items will despawn in 5 minutes.");
    }

    // Timer for ground clutter despawn and hologram display
    private void startGroundClutterTimer() {
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                Iterator<Map.Entry<Item, Long>> iterator = groundItems.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Item, Long> entry = iterator.next();
                    Item item = entry.getKey();
                    long despawnTime = entry.getValue();
                    if (!item.isValid()) {
                        iterator.remove();
                        continue;
                    }
                    long timeLeft = (despawnTime - currentTime) / 1000;
                    if (timeLeft <= 0) {
                        item.remove();
                        iterator.remove();
                    } else {
                        // Update hologram (simplified as text above item)
                        item.setCustomName(ChatColor.YELLOW + "Despawns in " + timeLeft + "s");
                        item.setCustomNameVisible(true);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L); // Run every second
    }

    // Scan chunks for active redstone (asynchronous)
    private void scanRedstone(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Map<Chunk, Integer> redstoneCounts = new HashMap<>();
                // List of redstone-related material names to check
                List<String> redstoneMaterials = Arrays.asList(
                        "REDSTONE",
                        "PISTON",
                        "STICKY_PISTON",
                        "REPEATER",
                        "COMPARATOR",
                        "COPPER_BULB",
                        "EXPOSED_COPPER_BULB",
                        "WEATHERED_COPPER_BULB",
                        "OXIDIZED_COPPER_BULB",
                        "WAXED_COPPER_BULB",
                        "WAXED_EXPOSED_COPPER_BULB",
                        "WAXED_WEATHERED_COPPER_BULB",
                        "WAXED_OXIDIZED_COPPER_BULB"
                );
                for (World world : Bukkit.getWorlds()) {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        int count = 0;
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                                    String material = chunk.getBlock(x, y, z).getType().toString();
                                    if (redstoneMaterials.stream().anyMatch(material::contains)) {
                                        count++;
                                    }
                                }
                            }
                        }
                        redstoneCounts.put(chunk, count);
                    }
                }
                List<Map.Entry<Chunk, Integer>> sorted = redstoneCounts.entrySet().stream()
                        .sorted(Map.Entry.<Chunk, Integer>comparingByValue().reversed())
                        .limit(5)
                        .collect(Collectors.toList());
                // Return to main thread to send messages
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.AQUA + "=== Top 5 Chunks with Active Redstone ===");
                        for (Map.Entry<Chunk, Integer> entry : sorted) {
                            Chunk chunk = entry.getKey();
                            player.sendMessage(ChatColor.YELLOW + "World: " + chunk.getWorld().getName() +
                                    ", X: " + chunk.getX() + ", Z: " + chunk.getZ() +
                                    ", Redstone Components: " + entry.getValue());
                        }
                    }
                }.runTask(ESBPlugin.this);
            }
        }.runTaskAsynchronously(this);
    }

    // Scan for chunk corruption (asynchronous)
    private void scanChunkCorruption(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> corruptedChunks = new ArrayList<>();
                for (World world : Bukkit.getWorlds()) {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        try {
                            // Basic corruption check: attempt to access block data
                            chunk.getBlock(0, world.getMinHeight(), 0).getType();
                        } catch (Exception e) {
                            corruptedChunks.add("World: " + world.getName() +
                                    ", X: " + chunk.getX() + ", Z: " + chunk.getZ() +
                                    ", Reason: " + e.getMessage());
                        }
                    }
                }
                // Return to main thread to send messages
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.AQUA + "=== Corrupted Chunks ===");
                        if (corruptedChunks.isEmpty()) {
                            player.sendMessage(ChatColor.GREEN + "No corrupted chunks found.");
                        } else {
                            for (String chunkInfo : corruptedChunks) {
                                player.sendMessage(ChatColor.YELLOW + chunkInfo);
                            }
                        }
                    }
                }.runTask(ESBPlugin.this);
            }
        }.runTaskAsynchronously(this);
    }

    // Regenerate player's current chunk if corrupted
    private void regenerateChunk(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        World world = chunk.getWorld();
        try {
            // Check for corruption
            chunk.getBlock(0, world.getMinHeight(), 0).getType();
            player.sendMessage(ChatColor.GREEN + "Chunk is not corrupted.");
        } catch (Exception e) {
            player.sendMessage(ChatColor.YELLOW + "Chunk is corrupted. Regenerating...");
            new BukkitRunnable() {
                @Override
                public void run() {
                    world.regenerateChunk(chunk.getX(), chunk.getZ());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.sendMessage(ChatColor.GREEN + "Chunk regenerated successfully.");
                        }
                    }.runTask(ESBPlugin.this);
                }
            }.runTaskAsynchronously(this);
        }
    }
}