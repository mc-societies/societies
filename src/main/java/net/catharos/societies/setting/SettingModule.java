package net.catharos.societies.setting;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import net.catharos.groups.setting.CollectiveSettingProvider;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingProvider;
import net.catharos.lib.shank.AbstractModule;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * Represents a SettingModule
 */
public class SettingModule extends AbstractModule {

    private final World world;

    public SettingModule(@Named("default-world") World world) {this.world = world;}

    @Override
    protected void configure() {

        Setting<Location> instance = new Setting<Location>(0) {
            @Override
            public Location convert(byte[] value) {
                DataInputStream is = new DataInputStream(new ByteArrayInputStream(value));

                try {
                    return new Location(world, is.readInt(), is.readInt(), is.readInt());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return world.getSpawnLocation();
            }

            @Override
            public byte[] convert(@Nullable Location value) {
                if (value == null) {
                    return null;
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DataOutputStream os = new DataOutputStream(out);

                try {
                    os.writeInt(value.getBlockX());
                    os.writeInt(value.getBlockY());
                    os.writeInt(value.getBlockZ());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return out.toByteArray();
            }
        };
        bindNamedInstance("home", new TypeLiteral<Setting<Location>>() {}, instance);
        bind(SettingProvider.class).to(CollectiveSettingProvider.class);

        MapBinder.newMapBinder(binder(), Integer.class, Setting.class, Names.named("settings")).addBinding(0).toInstance(instance);
    }
}
