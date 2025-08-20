package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.resource.ZipResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(ZipResourcePack.class)
public interface ZipResourcePackAccessor {
	@Accessor
	File getBackingZipFile();
}
