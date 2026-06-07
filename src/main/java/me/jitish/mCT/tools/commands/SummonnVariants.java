package me.jitish.mCT.tools.commands;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

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

    static final class EntityVariant {
        private final EntityType baseType;
        private final Consumer<Entity> modifier;
        private final String displayName;

        EntityVariant(EntityType baseType, Consumer<Entity> modifier, String displayName) {
            this.baseType = baseType;
            this.modifier = modifier;
            this.displayName = displayName;
        }

        public EntityType baseType() { return baseType; }
        public Consumer<Entity> modifier() { return modifier; }
        public String displayName() { return displayName; }
    }

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
        try { register("charged_creeper", "CREEPER", e -> ((Creeper) e).setPowered(true), "Charged Creeper"); } catch(Throwable t) {}
        try { register("baby_villager", "VILLAGER", e -> ((Villager) e).setBaby(), "Baby Villager"); } catch(Throwable t) {}
        try { register("baby_pig", "PIG", e -> ((Pig) e).setBaby(), "Baby Pig"); } catch(Throwable t) {}
        try { register("baby_cow", "COW", e -> ((Cow) e).setBaby(), "Baby Cow"); } catch(Throwable t) {}
        try { register("baby_sheep", "SHEEP", e -> ((Sheep) e).setBaby(), "Baby Sheep"); } catch(Throwable t) {}
        try { register("baby_chicken", "CHICKEN", e -> ((Chicken) e).setBaby(), "Baby Chicken"); } catch(Throwable t) {}
        try { register("baby_wolf", "WOLF", e -> ((Wolf) e).setBaby(), "Baby Wolf"); } catch(Throwable t) {}
        try { register("baby_cat", "CAT", e -> ((Cat) e).setBaby(), "Baby Cat"); } catch(Throwable t) {}
    }

    private static void registerZombieVariants() {
        try { register("baby_zombie", "ZOMBIE", e -> ((Zombie) e).setBaby(true), "Baby Zombie"); } catch(Throwable t) {}
        try { register("baby_drowned", "DROWNED", e -> ((Drowned) e).setBaby(true), "Baby Drowned"); } catch(Throwable t) {}
        try { register("baby_husk", "HUSK", e -> ((Husk) e).setBaby(true), "Baby Husk"); } catch(Throwable t) {}
        try { register("baby_zombie_villager", "ZOMBIE_VILLAGER", e -> ((ZombieVillager) e).setBaby(true), "Baby Zombie Villager"); } catch(Throwable t) {}
    }

    private static void registerVillagerVariants() {
        try { register("desert_villager", "VILLAGER", null, "Desert Villager"); } catch(Throwable t) {}
        try { register("jungle_villager", "VILLAGER", null, "Jungle Villager"); } catch(Throwable t) {}
        try { register("plains_villager", "VILLAGER", null, "Plains Villager"); } catch(Throwable t) {}
        try { register("savanna_villager", "VILLAGER", null, "Savanna Villager"); } catch(Throwable t) {}
        try { register("snow_villager", "VILLAGER", null, "Snow Villager"); } catch(Throwable t) {}
        try { register("swamp_villager", "VILLAGER", null, "Swamp Villager"); } catch(Throwable t) {}
        try { register("taiga_villager", "VILLAGER", null, "Taiga Villager"); } catch(Throwable t) {}
    }

    private static void registerSheepVariants() {
        try { register("white_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.WHITE), "White Sheep"); } catch(Throwable t) {}
        try { register("orange_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.ORANGE), "Orange Sheep"); } catch(Throwable t) {}
        try { register("magenta_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.MAGENTA), "Magenta Sheep"); } catch(Throwable t) {}
        try { register("light_blue_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.LIGHT_BLUE), "Light Blue Sheep"); } catch(Throwable t) {}
        try { register("yellow_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.YELLOW), "Yellow Sheep"); } catch(Throwable t) {}
        try { register("lime_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.LIME), "Lime Sheep"); } catch(Throwable t) {}
        try { register("pink_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.PINK), "Pink Sheep"); } catch(Throwable t) {}
        try { register("gray_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.GRAY), "Gray Sheep"); } catch(Throwable t) {}
        try { register("light_gray_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.LIGHT_GRAY), "Light Gray Sheep"); } catch(Throwable t) {}
        try { register("cyan_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.CYAN), "Cyan Sheep"); } catch(Throwable t) {}
        try { register("purple_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.PURPLE), "Purple Sheep"); } catch(Throwable t) {}
        try { register("blue_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.BLUE), "Blue Sheep"); } catch(Throwable t) {}
        try { register("brown_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.BROWN), "Brown Sheep"); } catch(Throwable t) {}
        try { register("green_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.GREEN), "Green Sheep"); } catch(Throwable t) {}
        try { register("red_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.RED), "Red Sheep"); } catch(Throwable t) {}
        try { register("black_sheep", "SHEEP", e -> ((Sheep) e).setColor(DyeColor.BLACK), "Black Sheep"); } catch(Throwable t) {}
        try { register("sheared_sheep", "SHEEP", e -> ((Sheep) e).setSheared(true), "Sheared Sheep"); } catch(Throwable t) {}
    }

    private static void registerRabbitVariants() {
        try { register("brown_rabbit", "RABBIT", e -> ((Rabbit) e).setRabbitType(Rabbit.Type.BROWN), "Brown Rabbit"); } catch(Throwable t) {}
        try { register("white_rabbit", "RABBIT", e -> ((Rabbit) e).setRabbitType(Rabbit.Type.WHITE), "White Rabbit"); } catch(Throwable t) {}
        try { register("black_rabbit", "RABBIT", e -> ((Rabbit) e).setRabbitType(Rabbit.Type.BLACK), "Black Rabbit"); } catch(Throwable t) {}
        try { register("black_and_white_rabbit", "RABBIT", e -> ((Rabbit) e).setRabbitType(Rabbit.Type.BLACK_AND_WHITE), "Black And White Rabbit"); } catch(Throwable t) {}
        try { register("gold_rabbit", "RABBIT", e -> ((Rabbit) e).setRabbitType(Rabbit.Type.GOLD), "Gold Rabbit"); } catch(Throwable t) {}
        try { register("salt_and_pepper_rabbit", "RABBIT", e -> ((Rabbit) e).setRabbitType(Rabbit.Type.SALT_AND_PEPPER), "Salt And Pepper Rabbit"); } catch(Throwable t) {}
        try { register("killer_bunny", "RABBIT", e -> ((Rabbit) e).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY), "Killer Bunny"); } catch(Throwable t) {}
    }

    private static void registerCatVariants() {
        try {
            for (Cat.Type type : getRegistryValues(Cat.Type.class)) {
                register(enumKey(type) + "_cat", "CAT", e -> ((Cat) e).setCatType(type), formatEnum(type) + " Cat");
            }
        } catch(Throwable t) {}
    }

    private static void registerFoxVariants() {
        try {
            for (Fox.Type type : Fox.Type.values()) {
                register(enumKey(type) + "_fox", "FOX", e -> ((Fox) e).setFoxType(type), formatEnum(type) + " Fox");
            }
        } catch(Throwable t) {}
    }

    private static void registerMooshroomVariants() {
        try {
            for (MushroomCow.Variant variant : MushroomCow.Variant.values()) {
                register(enumKey(variant) + "_mooshroom", "MUSHROOM_COW", e -> ((MushroomCow) e).setVariant(variant), formatEnum(variant) + " Mooshroom");
            }
        } catch(Throwable t) {}
    }

    private static void registerPandaVariants() {
        try {
            for (Panda.Gene gene : Panda.Gene.values()) {
                register(enumKey(gene) + "_panda", "PANDA", e -> {
                    if (e instanceof Panda) { Panda panda = (Panda) e; panda.setMainGene(gene); }
                }, formatEnum(gene) + " Panda");
            }
        } catch(Throwable t) {}
    }

    private static void registerAxolotlVariants() {
        try {
            for (Axolotl.Variant variant : Axolotl.Variant.values()) {
                register(enumKey(variant) + "_axolotl", "AXOLOTL", e -> ((Axolotl) e).setVariant(variant), formatEnum(variant) + " Axolotl");
            }
        } catch(Throwable t) {}
    }

    private static void registerFrogVariants() {
        try {
            for (Frog.Variant variant : getRegistryValues(Frog.Variant.class)) {
                register(enumKey(variant) + "_frog", "FROG", e -> ((Frog) e).setVariant(variant), formatEnum(variant) + " Frog");
            }
        } catch(Throwable t) {}
    }

    private static void registerParrotVariants() {
        try {
            for (Parrot.Variant variant : Parrot.Variant.values()) {
                register(enumKey(variant) + "_parrot", "PARROT", e -> ((Parrot) e).setVariant(variant), formatEnum(variant) + " Parrot");
            }
        } catch(Throwable t) {}
    }

    private static void registerWolfVariants() {
        try {
            for (Wolf.Variant variant : getRegistryValues(Wolf.Variant.class)) {
                register(enumKey(variant) + "_wolf", "WOLF", e -> ((Wolf) e).setVariant(variant), formatEnum(variant) + " Wolf");
            }
        } catch(Throwable t) {}
    }

    private static void registerChickenVariants() {
        try {
            for (Chicken.Variant variant : getRegistryValues(Chicken.Variant.class)) {
                register(enumKey(variant) + "_chicken", "CHICKEN", e -> ((Chicken) e).setVariant(variant), formatEnum(variant) + " Chicken");
            }
        } catch(Throwable t) {}
    }

    private static void registerCowVariants() {
        try {
            for (Cow.Variant variant : getRegistryValues(Cow.Variant.class)) {
                register(enumKey(variant) + "_cow", "COW", e -> ((Cow) e).setVariant(variant), formatEnum(variant) + " Cow");
            }
        } catch(Throwable t) {}
    }

    private static void registerPigVariants() {
        try {
            for (Pig.Variant variant : getRegistryValues(Pig.Variant.class)) {
                register(enumKey(variant) + "_pig", "PIG", e -> ((Pig) e).setVariant(variant), formatEnum(variant) + " Pig");
            }
        } catch(Throwable t) {}
    }

    private static void registerCombinationVariants() {
        try { register("chicken_jockey", "CHICKEN", e -> spawnPassenger(e, "ZOMBIE", SummonnVariants::makeBaby), "Chicken Jockey"); } catch(Throwable t) {}
        try { register("husk_jockey", "CHICKEN", e -> spawnPassenger(e, "HUSK", SummonnVariants::makeBaby), "Husk Jockey"); } catch(Throwable t) {}
        try { register("drowned_jockey", "CHICKEN", e -> spawnPassenger(e, "DROWNED", SummonnVariants::makeBaby), "Drowned Jockey"); } catch(Throwable t) {}
        try { register("zombie_villager_jockey", "CHICKEN", e -> spawnPassenger(e, "ZOMBIE_VILLAGER", SummonnVariants::makeBaby), "Zombie Villager Jockey"); } catch(Throwable t) {}
        try { register("spider_jockey", "SPIDER", e -> spawnPassenger(e, "SKELETON", null), "Spider Jockey"); } catch(Throwable t) {}
        try { register("cave_spider_jockey", "CAVE_SPIDER", e -> spawnPassenger(e, "SKELETON", null), "Cave Spider Jockey"); } catch(Throwable t) {}
        try { register("skeleton_horse_trap", "SKELETON_HORSE", SummonnVariants::spawnSkeletonHorseTrap, "Skeleton Horse Trap"); } catch(Throwable t) {}
        try { register("pillager_ravager", "RAVAGER", e -> spawnPassenger(e, "PILLAGER", null), "Pillager Ravager"); } catch(Throwable t) {}
        try { register("evoker_ravager", "RAVAGER", e -> spawnPassenger(e, "EVOKER", null), "Evoker Ravager"); } catch(Throwable t) {}
        try { register("vindicator_ravager", "RAVAGER", e -> spawnPassenger(e, "VINDICATOR", null), "Vindicator Ravager"); } catch(Throwable t) {}
        try { register("strider_jockey", "STRIDER", e -> {
            if (e instanceof Steerable) { Steerable steerable = (Steerable) e; steerable.setSaddle(true); }
            spawnPassenger(e, "ZOMBIFIED_PIGLIN", SummonnVariants::maybeEquipFungusOnAStick);
        }, "Strider Jockey"); } catch(Throwable t) {}
        try { register("baby_piglin_hoglin", "HOGLIN", e -> {
            makeBaby(e);
            spawnPassenger(e, "PIGLIN", SummonnVariants::makeBaby);
        }, "Baby Piglin Hoglin"); } catch(Throwable t) {}
    }

    private static void spawnPassenger(Entity mount, String riderTypeName, Consumer<Entity> riderCustomizer) {
        try {
            EntityType riderType = EntityType.valueOf(riderTypeName);
            World world = mount.getWorld();
            if (world == null) return;
            Entity rider = world.spawnEntity(mount.getLocation(), riderType);
            if (riderCustomizer != null) riderCustomizer.accept(rider);
            try {
                mount.addPassenger(rider);
            } catch (NoSuchMethodError ex) {
                @SuppressWarnings("deprecation")
                boolean ignored = mount.setPassenger(rider);
            }
        } catch (Throwable t) {}
    }

    private static void makeBaby(Entity entity) {
        if (entity instanceof Zombie) { ((Zombie) entity).setBaby(true); return; }
        if (entity instanceof Ageable) { ((Ageable) entity).setBaby(); }
    }

    private static void maybeEquipFungusOnAStick(Entity entity) {
        if (!(entity instanceof PigZombie)) return;
        PigZombie pigZombie = (PigZombie) entity;
        try {
            if (ThreadLocalRandom.current().nextInt(100) < 35) {
                org.bukkit.inventory.EntityEquipment equipment = pigZombie.getEquipment();
                if (equipment != null) {
                    Material mat = Material.valueOf("WARPED_FUNGUS_ON_A_STICK");
                    equipment.setItemInMainHand(new ItemStack(mat));
                    equipment.setItemInMainHandDropChance(0.0f);
                }
            }
        } catch (Throwable t) {}
    }

    private static void spawnSkeletonHorseTrap(Entity entity) {
        if (!(entity instanceof SkeletonHorse)) return;
        SkeletonHorse baseHorse = (SkeletonHorse) entity;
        setupSkeletonHorseRider(baseHorse);

        World world = baseHorse.getWorld();
        Location base = baseHorse.getLocation();
        for (int i = 0; i < 3; i++) {
            Location location = base.clone().add(randomOffset(), 0.0, randomOffset());
            Entity extra = world.spawnEntity(location, EntityType.valueOf("SKELETON_HORSE"));
            if (extra instanceof SkeletonHorse) {
                setupSkeletonHorseRider((SkeletonHorse) extra);
            }
        }
    }

    private static void setupSkeletonHorseRider(SkeletonHorse horse) {
        Entity rider = horse.getWorld().spawnEntity(horse.getLocation(), EntityType.valueOf("SKELETON"));
        try {
            horse.addPassenger(rider);
        } catch (NoSuchMethodError ex) {
            @SuppressWarnings("deprecation")
            boolean ignored = horse.setPassenger(rider);
        }
    }

    private static double randomOffset() {
        return ThreadLocalRandom.current().nextDouble(-2.0, 2.0);
    }

    private static void register(String key, String typeName, Consumer<Entity> modifier, String displayName) {
        try {
            EntityType baseType = EntityType.valueOf(typeName);
            VARIANTS.put(key, new EntityVariant(baseType, modifier, displayName));
        } catch (Throwable t) {}
    }

    private static String enumKey(Object value) {
        return rawVariantKey(value).toLowerCase(Locale.ROOT);
    }

    private static String formatEnum(Object value) {
        return formatKey(enumKey(value));
    }

    private static String rawVariantKey(Object value) {
        if (value instanceof Enum<?>) { return ((Enum<?>) value).name(); }
        try {
            java.lang.reflect.Method getKey = value.getClass().getMethod("getKey");
            Object key = getKey.invoke(value);
            java.lang.reflect.Method getNamespace = key.getClass().getMethod("getKey");
            return (String) getNamespace.invoke(key);
        } catch (Exception e) {}
        try {
            java.lang.reflect.Method name = value.getClass().getMethod("name");
            return (String) name.invoke(value);
        } catch (Exception e) {}
        return value.toString();
    }

    private static String formatKey(String key) {
        String raw = key.replace('_', ' ').replace('-', ' ');
        String[] parts = raw.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(part.substring(0, 1).toUpperCase(Locale.ROOT)).append(part.substring(1));
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T> Iterable<T> getRegistryValues(Class<T> type) {
        try {
            java.lang.reflect.Method getRegistry = Bukkit.class.getMethod("getRegistry", Class.class);
            Object registry = getRegistry.invoke(null, type);
            if (registry != null && registry instanceof Iterable) {
                return (Iterable<T>) registry;
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
            }
        }
        return values;
    }
}