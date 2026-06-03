package me.jitish.mCT.commands;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.OldEnum;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

final class SummonnVariants {

    record EntityVariant(EntityType baseType, Consumer<Entity> modifier, String displayName) {}

    private static final Map<String, EntityVariant> VARIANTS = new LinkedHashMap<>();

    static {
        registerBaseVariants();
        registerZombieVariants();
        registerVillagerVariants();
        registerSheepVariants();
        registerRabbitVariants();
        registerCatVariants();
        registerFoxVariants();
        registerMooshroomVariants();
        registerPandaVariants();
        registerAxolotlVariants();
        registerFrogVariants();
        registerParrotVariants();
        registerWolfVariants();
        registerChickenVariants();
        registerCowVariants();
        registerPigVariants();
        registerCombinationVariants();
    }

    static Map<String, EntityVariant> getVariants() {
        return Collections.unmodifiableMap(VARIANTS);
    }

    private static void registerBaseVariants() {
        register("charged_creeper", EntityType.CREEPER,
                e -> ((Creeper) e).setPowered(true), "Charged Creeper");
        register("baby_villager", EntityType.VILLAGER,
                e -> ((Villager) e).setBaby(), "Baby Villager");
        register("baby_pig", EntityType.PIG,
                e -> ((Pig) e).setBaby(), "Baby Pig");
        register("baby_cow", EntityType.COW,
                e -> ((Cow) e).setBaby(), "Baby Cow");
        register("baby_sheep", EntityType.SHEEP,
                e -> ((Sheep) e).setBaby(), "Baby Sheep");
        register("baby_chicken", EntityType.CHICKEN,
                e -> ((Chicken) e).setBaby(), "Baby Chicken");
        register("baby_wolf", EntityType.WOLF,
                e -> ((Wolf) e).setBaby(), "Baby Wolf");
        register("baby_cat", EntityType.CAT,
                e -> ((Cat) e).setBaby(), "Baby Cat");
    }

    private static void registerZombieVariants() {
        register("baby_zombie", EntityType.ZOMBIE,
                e -> ((Zombie) e).setBaby(true), "Baby Zombie");
        register("baby_drowned", EntityType.DROWNED,
                e -> ((Drowned) e).setBaby(true), "Baby Drowned");
        register("baby_husk", EntityType.HUSK,
                e -> ((Husk) e).setBaby(true), "Baby Husk");
        register("baby_zombie_villager", EntityType.ZOMBIE_VILLAGER,
                e -> ((ZombieVillager) e).setBaby(true), "Baby Zombie Villager");
    }

    private static void registerVillagerVariants() {
        register("desert_villager", EntityType.VILLAGER,
                e -> ((Villager) e).setVillagerType(Villager.Type.DESERT), "Desert Villager");
        register("jungle_villager", EntityType.VILLAGER,
                e -> ((Villager) e).setVillagerType(Villager.Type.JUNGLE), "Jungle Villager");
        register("plains_villager", EntityType.VILLAGER,
                e -> ((Villager) e).setVillagerType(Villager.Type.PLAINS), "Plains Villager");
        register("savanna_villager", EntityType.VILLAGER,
                e -> ((Villager) e).setVillagerType(Villager.Type.SAVANNA), "Savanna Villager");
        register("snow_villager", EntityType.VILLAGER,
                e -> ((Villager) e).setVillagerType(Villager.Type.SNOW), "Snow Villager");
        register("swamp_villager", EntityType.VILLAGER,
                e -> ((Villager) e).setVillagerType(Villager.Type.SWAMP), "Swamp Villager");
        register("taiga_villager", EntityType.VILLAGER,
                e -> ((Villager) e).setVillagerType(Villager.Type.TAIGA), "Taiga Villager");
    }

    private static void registerSheepVariants() {
        register("white_sheep", EntityType.SHEEP,
                e -> ((Sheep) e).setColor(DyeColor.WHITE), "White Sheep");
        register("black_sheep", EntityType.SHEEP,
                e -> ((Sheep) e).setColor(DyeColor.BLACK), "Black Sheep");
        register("brown_sheep", EntityType.SHEEP,
                e -> ((Sheep) e).setColor(DyeColor.BROWN), "Brown Sheep");
        register("gray_sheep", EntityType.SHEEP,
                e -> ((Sheep) e).setColor(DyeColor.GRAY), "Gray Sheep");
        register("light_gray_sheep", EntityType.SHEEP,
                e -> ((Sheep) e).setColor(DyeColor.LIGHT_GRAY), "Light Gray Sheep");
        register("pink_sheep", EntityType.SHEEP,
                e -> ((Sheep) e).setColor(DyeColor.PINK), "Pink Sheep");
        register("sheared_sheep", EntityType.SHEEP,
                e -> ((Sheep) e).setSheared(true), "Sheared Sheep");
    }

    private static void registerRabbitVariants() {
        register("brown_rabbit", EntityType.RABBIT,
                e -> ((Rabbit) e).setRabbitType(Rabbit.Type.BROWN), "Brown Rabbit");
        register("white_rabbit", EntityType.RABBIT,
                e -> ((Rabbit) e).setRabbitType(Rabbit.Type.WHITE), "White Rabbit");
        register("black_rabbit", EntityType.RABBIT,
                e -> ((Rabbit) e).setRabbitType(Rabbit.Type.BLACK), "Black Rabbit");
        register("gold_rabbit", EntityType.RABBIT,
                e -> ((Rabbit) e).setRabbitType(Rabbit.Type.GOLD), "Gold Rabbit");
        register("salt_rabbit", EntityType.RABBIT,
                e -> ((Rabbit) e).setRabbitType(Rabbit.Type.SALT_AND_PEPPER), "Salt Rabbit");
        register("killer_bunny", EntityType.RABBIT,
                e -> ((Rabbit) e).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY), "Killer Bunny");
    }

    private static void registerCatVariants() {
        for (Cat.Type type : getRegistryValues(Cat.Type.class)) {
            register(enumKey(type) + "_cat", EntityType.CAT,
                    e -> ((Cat) e).setCatType(type), formatEnum(type) + " Cat");
        }
    }

    private static void registerFoxVariants() {
        for (Fox.Type type : Fox.Type.values()) {
            register(enumKey(type) + "_fox", EntityType.FOX,
                    e -> ((Fox) e).setFoxType(type), formatEnum(type) + " Fox");
        }
    }

    private static void registerMooshroomVariants() {
        for (MushroomCow.Variant variant : MushroomCow.Variant.values()) {
            register(enumKey(variant) + "_mooshroom", EntityType.MOOSHROOM,
                    e -> ((MushroomCow) e).setVariant(variant), formatEnum(variant) + " Mooshroom");
        }
    }

    private static void registerPandaVariants() {
        for (Panda.Gene gene : Panda.Gene.values()) {
            register(enumKey(gene) + "_panda", EntityType.PANDA, e -> {
                Panda panda = (Panda) e;
                panda.setMainGene(gene);
                panda.setHiddenGene(gene);
            }, formatEnum(gene) + " Panda");
        }
    }

    private static void registerAxolotlVariants() {
        for (Axolotl.Variant variant : Axolotl.Variant.values()) {
            register(enumKey(variant) + "_axolotl", EntityType.AXOLOTL,
                    e -> ((Axolotl) e).setVariant(variant), formatEnum(variant) + " Axolotl");
        }
    }

    private static void registerFrogVariants() {
        for (Frog.Variant variant : getRegistryValues(Frog.Variant.class)) {
            register(enumKey(variant) + "_frog", EntityType.FROG,
                    e -> ((Frog) e).setVariant(variant), formatEnum(variant) + " Frog");
        }
    }

    private static void registerParrotVariants() {
        for (Parrot.Variant variant : Parrot.Variant.values()) {
            register(enumKey(variant) + "_parrot", EntityType.PARROT,
                    e -> ((Parrot) e).setVariant(variant), formatEnum(variant) + " Parrot");
        }
    }

    private static void registerWolfVariants() {
        for (Wolf.Variant variant : getRegistryValues(Wolf.Variant.class)) {
            register(enumKey(variant) + "_wolf", EntityType.WOLF,
                    e -> ((Wolf) e).setVariant(variant), formatEnum(variant) + " Wolf");
        }
    }

    private static void registerChickenVariants() {
        for (Chicken.Variant variant : getRegistryValues(Chicken.Variant.class)) {
            register(enumKey(variant) + "_chicken", EntityType.CHICKEN,
                    e -> ((Chicken) e).setVariant(variant), formatEnum(variant) + " Chicken");
        }
    }

    private static void registerCowVariants() {
        for (Cow.Variant variant : getRegistryValues(Cow.Variant.class)) {
            register(enumKey(variant) + "_cow", EntityType.COW,
                    e -> ((Cow) e).setVariant(variant), formatEnum(variant) + " Cow");
        }
    }

    private static void registerPigVariants() {
        for (Pig.Variant variant : getRegistryValues(Pig.Variant.class)) {
            register(enumKey(variant) + "_pig", EntityType.PIG,
                    e -> ((Pig) e).setVariant(variant), formatEnum(variant) + " Pig");
        }
    }

    private static void registerCombinationVariants() {
        register("chicken_jockey", EntityType.CHICKEN,
                e -> spawnPassenger(e, EntityType.ZOMBIE, SummonnVariants::makeBaby), "Chicken Jockey");
        register("husk_jockey", EntityType.CHICKEN,
                e -> spawnPassenger(e, EntityType.HUSK, SummonnVariants::makeBaby), "Husk Jockey");
        register("drowned_jockey", EntityType.CHICKEN,
                e -> spawnPassenger(e, EntityType.DROWNED, SummonnVariants::makeBaby), "Drowned Jockey");
        register("zombie_villager_jockey", EntityType.CHICKEN,
                e -> spawnPassenger(e, EntityType.ZOMBIE_VILLAGER, SummonnVariants::makeBaby),
                "Zombie Villager Jockey");

        register("spider_jockey", EntityType.SPIDER,
                e -> spawnPassenger(e, EntityType.SKELETON, null), "Spider Jockey");
        register("cave_spider_jockey", EntityType.CAVE_SPIDER,
                e -> spawnPassenger(e, EntityType.SKELETON, null), "Cave Spider Jockey");

        register("skeleton_horse_trap", EntityType.SKELETON_HORSE,
                SummonnVariants::spawnSkeletonHorseTrap, "Skeleton Horse Trap");

        register("pillager_ravager", EntityType.RAVAGER,
                e -> spawnPassenger(e, EntityType.PILLAGER, null), "Pillager Ravager");
        register("evoker_ravager", EntityType.RAVAGER,
                e -> spawnPassenger(e, EntityType.EVOKER, null), "Evoker Ravager");
        register("vindicator_ravager", EntityType.RAVAGER,
                e -> spawnPassenger(e, EntityType.VINDICATOR, null), "Vindicator Ravager");

        register("strider_jockey", EntityType.STRIDER, e -> {
            if (e instanceof Steerable steerable) {
                steerable.setSaddle(true);
            }
            spawnPassenger(e, EntityType.ZOMBIFIED_PIGLIN, SummonnVariants::maybeEquipFungusOnAStick);
        }, "Strider Jockey");

        register("baby_piglin_hoglin", EntityType.HOGLIN, e -> {
            makeBaby(e);
            spawnPassenger(e, EntityType.PIGLIN, SummonnVariants::makeBaby);
        }, "Baby Piglin Hoglin");
    }

    private static void spawnPassenger(Entity mount, EntityType riderType, Consumer<Entity> riderCustomizer) {
        World world = mount.getWorld();
        if (world == null) {
            return;
        }
        Entity rider = world.spawnEntity(mount.getLocation(), riderType);
        if (riderCustomizer != null) {
            riderCustomizer.accept(rider);
        }
        mount.addPassenger(rider);
    }

    private static void makeBaby(Entity entity) {
        if (entity instanceof Zombie zombie) {
            zombie.setBaby(true);
            return;
        }
        if (entity instanceof Ageable ageable) {
            ageable.setBaby();
        }
    }

    private static void maybeEquipFungusOnAStick(Entity entity) {
        if (!(entity instanceof PigZombie pigZombie)) {
            return;
        }
        if (ThreadLocalRandom.current().nextInt(100) < 35) {
            var equipment = pigZombie.getEquipment();
            if (equipment != null) {
                equipment.setItemInMainHand(new ItemStack(Material.WARPED_FUNGUS_ON_A_STICK));
                equipment.setItemInMainHandDropChance(0.0f);
            }
        }
    }

    private static void spawnSkeletonHorseTrap(Entity entity) {
        if (!(entity instanceof SkeletonHorse baseHorse)) {
            return;
        }
        setupSkeletonHorseRider(baseHorse);

        World world = baseHorse.getWorld();
        Location base = baseHorse.getLocation();
        for (int i = 0; i < 3; i++) {
            Location location = base.clone().add(randomOffset(), 0.0, randomOffset());
            Entity extra = world.spawnEntity(location, EntityType.SKELETON_HORSE);
            if (extra instanceof SkeletonHorse horse) {
                setupSkeletonHorseRider(horse);
            }
        }
    }

    private static void setupSkeletonHorseRider(SkeletonHorse horse) {
        Entity rider = horse.getWorld().spawnEntity(horse.getLocation(), EntityType.SKELETON);
        horse.addPassenger(rider);
    }

    private static double randomOffset() {
        return ThreadLocalRandom.current().nextDouble(-2.0, 2.0);
    }

    private static void register(String key, EntityType baseType, Consumer<Entity> modifier, String displayName) {
        VARIANTS.put(key, new EntityVariant(baseType, modifier, displayName));
    }

    private static String enumKey(Object value) {
        return rawVariantKey(value).toLowerCase(Locale.ROOT);
    }

    private static String formatEnum(Object value) {
        return formatKey(enumKey(value));
    }

    private static String rawVariantKey(Object value) {
        if (value instanceof Keyed keyed) {
            return keyed.getKey().getKey();
        }
        if (value instanceof OldEnum<?> oldEnum) {
            return oldEnum.name();
        }
        if (value instanceof Enum<?> enumValue) {
            return enumValue.name();
        }
        return value.toString();
    }

    private static String formatKey(String key) {
        String raw = key.replace('_', ' ').replace('-', ' ');
        String[] parts = raw.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append(part.substring(0, 1).toUpperCase(Locale.ROOT)).append(part.substring(1));
        }
        return sb.toString();
    }

    private static <T extends Keyed> Iterable<T> getRegistryValues(Class<T> type) {
        try {
            Registry<T> registry = Bukkit.getRegistry(type);
            if (registry != null) {
                return registry;
            }
        } catch (Exception | LinkageError ignored) {
            // Fall back to static constants if the registry is missing in the runtime API.
        }
        return reflectStaticValues(type);
    }

    private static <T> Iterable<T> reflectStaticValues(Class<T> type) {
        List<T> values = new ArrayList<>();
        for (Field field : type.getFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) || !type.isAssignableFrom(field.getType())) {
                continue;
            }
            try {
                values.add(type.cast(field.get(null)));
            } catch (IllegalAccessException ignored) {
                // Ignore inaccessible constants.
            }
        }
        return values;
    }
}
