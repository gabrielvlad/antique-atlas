package folk.sisby.antique_atlas.client;

import folk.sisby.antique_atlas.AntiqueAtlas;
import folk.sisby.antique_atlas.client.assets.BiomeTextures;
import folk.sisby.antique_atlas.client.assets.MarkerTypes;
import folk.sisby.antique_atlas.client.assets.TextureSets;
import folk.sisby.antique_atlas.client.gui.AtlasScreen;
import folk.sisby.antique_atlas.core.PlayerEventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.Map;

public class AntiqueAtlasClient implements ClientModInitializer {
    private static AtlasScreen atlasScreen;

    public static AtlasScreen getAtlasScreen() {
        if (atlasScreen == null) {
            atlasScreen = new AtlasScreen();
            atlasScreen.setMapScale(AntiqueAtlas.CONFIG.Interface.defaultScale);
        }
        return atlasScreen;
    }

    public static void openAtlasScreen() {
        openAtlasScreen(getAtlasScreen().prepareToOpen());
    }

    private static void openAtlasScreen(AtlasScreen screen) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen == null) { // In-game screen
            atlasScreen.updateL18n();
            mc.setScreen(screen);
        }
    }

    /**
     * Register fallback texture sets for any biomes present in the client world that don't have explicit sets.
     * Doing this on world join catches data-biomes that might not be registered in other worlds.
     */
    public static void registerFallbackTextures(ClientWorld world) {
        for (Map.Entry<RegistryKey<Biome>, Biome> biome : BuiltinRegistries.BIOME.getEntrySet()) {
            Identifier id = BuiltinRegistries.BIOME.getId(biome.getValue());
            if (!BiomeTextures.getInstance().contains(id)) {
                BiomeTextures.getInstance().registerFallback(id, BuiltinRegistries.BIOME.entryOf(biome.getKey()));
            }
        }
    }


    @Override
    public void onInitializeClient() {
        AntiqueAtlasKeybindings.init();
        AntiqueAtlasClientNetworking.init();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(TextureSets.getInstance());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(BiomeTextures.getInstance());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(MarkerTypes.getInstance());

        ClientPlayConnectionEvents.INIT.register((h, c) -> AntiqueAtlas.tileData.onClientConnectedToServer());
        ClientPlayConnectionEvents.INIT.register((h, c) -> AntiqueAtlas.markersData.onClientConnectedToServer());
        ClientPlayConnectionEvents.INIT.register((h, c) -> {if (!c.isIntegratedServerRunning()) AntiqueAtlas.globalMarkersData.onClientConnectedToRemoteServer();});

        ClientTickEvents.END_WORLD_TICK.register(world -> world.getPlayers().forEach(PlayerEventHandler::onPlayerTick));
    }
}
