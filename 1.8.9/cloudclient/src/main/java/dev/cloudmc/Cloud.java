/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc;

import dev.cloudmc.config.ConfigLoader;
import dev.cloudmc.config.ConfigSaver;
import dev.cloudmc.feature.mod.ModManager;
import dev.cloudmc.feature.option.OptionManager;
import dev.cloudmc.feature.setting.SettingManager;
import dev.cloudmc.gui.hudeditor.HudEditor;
import dev.cloudmc.helpers.CpsHelper;
import dev.cloudmc.helpers.MessageHelper;
import dev.cloudmc.helpers.font.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.lwjgl.opengl.Display;

import java.io.FileNotFoundException;
import java.io.IOException;

@Mod(
        modid = Cloud.modID,
        name = Cloud.modName,
        version = Cloud.modVersion,
        acceptedMinecraftVersions = "[1.8.9]"
)
public class Cloud {

    @Mod.Instance()
    public static Cloud INSTANCE;

    public static final String modID = "cloudmc";
    public static final String modName = "TEDS Client";
    public static final String modVersion = "1.0.0 [1.8.9]";

    public Minecraft mc = Minecraft.getMinecraft();

    public ModManager modManager;
    public SettingManager settingManager;
    public HudEditor hudEditor;
    public OptionManager optionManager;
    public FontHelper fontHelper;
    public CpsHelper cpsHelper;
    public MessageHelper messageHelper;
    public DiscordRP discordRP;

    /**
     * Initializes the client
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle(Cloud.modName + " Client " + Cloud.modVersion);
        registerEvents(
                cpsHelper = new CpsHelper(),
                settingManager = new SettingManager(),
                modManager = new ModManager(),
                optionManager = new OptionManager(),
                hudEditor = new HudEditor(),
                fontHelper = new FontHelper(),
                messageHelper = new MessageHelper(),
                discordRP = new DiscordRP()
        );

        try {
            if (!ConfigSaver.configExists()) {
                ConfigSaver.saveConfig();
            }
            ConfigLoader.loadConfig();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        fontHelper.init();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ConfigSaver.saveConfig();
                discordRP.shutdown();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }));
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        discordRP.start();
    };

    private void registerEvents(Object... events) {
        for (Object event : events) {
            MinecraftForge.EVENT_BUS.register(event);
        }
    }

    public DiscordRP getDiscordRP() {
        return discordRP;
    }
}