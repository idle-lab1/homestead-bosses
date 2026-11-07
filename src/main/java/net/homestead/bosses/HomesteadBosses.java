package net.homestead.bosses;

import net.homestead.bosses.entity.*;
import net.homestead.bosses.client.BossRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(HomesteadBosses.MODID)
public class HomesteadBosses {
    public static final String MODID = "homesteadbosses";
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Bosses are sized generously and tracked from far away so players can see them coming
    // across a field, the way a vanilla Wither or Ravager telegraphs itself.
    public static final RegistryObject<EntityType<PumpkinKingEntity>> PUMPKIN_KING = ENTITIES.register("pumpkin_king",
        () -> EntityType.Builder.of(PumpkinKingEntity::new, MobCategory.MONSTER).sized(1.2f, 3.0f).fireImmune().clientTrackingRange(10).build("pumpkin_king"));
    public static final RegistryObject<EntityType<BakersBaneEntity>> BAKERS_BANE = ENTITIES.register("bakers_bane",
        () -> EntityType.Builder.of(BakersBaneEntity::new, MobCategory.MONSTER).sized(1.1f, 2.8f).clientTrackingRange(10).build("bakers_bane"));
    public static final RegistryObject<EntityType<WheatHarvesterEntity>> WHEAT_HARVESTER = ENTITIES.register("wheat_harvester",
        () -> EntityType.Builder.of(WheatHarvesterEntity::new, MobCategory.MONSTER).sized(1.4f, 3.4f).clientTrackingRange(10).build("wheat_harvester"));
    public static final RegistryObject<EntityType<PumpkinMinionEntity>> PUMPKIN_MINION = ENTITIES.register("pumpkin_minion",
        () -> EntityType.Builder.of(PumpkinMinionEntity::new, MobCategory.MONSTER).sized(0.6f, 1.4f).fireImmune().clientTrackingRange(8).build("pumpkin_minion"));

    public static final RegistryObject<Item> PUMPKIN_KING_EGG = egg("pumpkin_king_spawn_egg", PUMPKIN_KING, 0xD87921, 0x35221B);
    public static final RegistryObject<Item> BAKERS_BANE_EGG = egg("bakers_bane_spawn_egg", BAKERS_BANE, 0xE7B8A0, 0x7C3C48);
    public static final RegistryObject<Item> WHEAT_HARVESTER_EGG = egg("wheat_harvester_spawn_egg", WHEAT_HARVESTER, 0xD7B64A, 0x4F6530);

    private static RegistryObject<Item> egg(String name, RegistryObject<? extends EntityType<? extends net.minecraft.world.entity.Mob>> type, int base, int spots) {
        return ITEMS.register(name, () -> new SpawnEggItem(type, base, spots, new Item.Properties()));
    }

    public HomesteadBosses() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITIES.register(bus);
        ITEMS.register(bus);
        bus.addListener(this::attributes);
        bus.addListener(this::buildCreativeTab);
        bus.addListener(BossRenderers::register);
    }

    private void attributes(EntityAttributeCreationEvent event) {
        event.put(PUMPKIN_KING.get(), PumpkinKingEntity.createAttributes().build());
        event.put(BAKERS_BANE.get(), BakersBaneEntity.createAttributes().build());
        event.put(WHEAT_HARVESTER.get(), WheatHarvesterEntity.createAttributes().build());
        event.put(PUMPKIN_MINION.get(), PumpkinMinionEntity.createAttributes().build());
    }

    // Without this the spawn eggs only exist via /give or JEI: they'd never show up in the
    // creative inventory like a real mob's spawn egg does. This slots them into vanilla's
    // own "Spawn Eggs" tab, right where a player would expect to find them.
    private void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(PUMPKIN_KING_EGG.get());
            event.accept(BAKERS_BANE_EGG.get());
            event.accept(WHEAT_HARVESTER_EGG.get());
        }
    }
}
