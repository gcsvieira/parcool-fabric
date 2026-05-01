package com.alrex.parcool.common.entity;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityTypes {
    public static final EntityType<ZiplineRopeEntity> ZIPLINE_ROPE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "zipline_rope"),
            EntityType.Builder.<ZiplineRopeEntity>of(ZiplineRopeEntity::new, MobCategory.MISC)
                    .noSave()
                    .clientTrackingRange((int) (Zipline.MAXIMUM_HORIZONTAL_DISTANCE / 1.9))
                    .updateInterval(Integer.MAX_VALUE)
                    .noSummon()
                    .sized(0.1f, 0.1f)
                    .fireImmune()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "zipline_rope")))
    );

    public static void registerAll() {
        // Trigger static initialization
    }
}
