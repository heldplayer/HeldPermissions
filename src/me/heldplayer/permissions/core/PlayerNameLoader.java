
package me.heldplayer.permissions.core;

import java.util.Map;
import java.util.UUID;

import me.heldplayer.permissions.Permissions;
import net.specialattack.bukkit.core.SpACore;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;

public class PlayerNameLoader extends UUIDLoader {

    @Override
    public void load(PermissionsManager manager, ConfigurationSection section) {
        ConfigurationSection newSection = new YamlConfiguration();

        ConfigurationSection groups = section.getConfigurationSection("groups");
        if (groups != null) {
            newSection.set("groups", groups);
        }

        ConfigurationSection users = section.getConfigurationSection("users");
        if (users != null) {
            HttpProfileRepository repository = SpACore.getProfileRepository();

            ConfigurationSection newUsers = newSection.createSection("users");
            Map<String, Object> userMap = users.getValues(false);

            Permissions.log.info(String.format("Converting %s usernames to use UUIDs", userMap.size()));

            Profile[] profiles = repository.findProfilesByNames(userMap.keySet().toArray(new String[userMap.size()]));

            for (Profile profile : profiles) {
                UUID uuid = profile.getUUID();

                Permissions.log.info(String.format("Found '%s' for '%s'", uuid, profile.getName()));

                ConfigurationSection newUser = (ConfigurationSection) userMap.get(profile.getName().toLowerCase());

                newUser.set("lastName", profile.getName());

                newUsers.set(uuid.toString(), newUser);
            }

            Permissions.log.info(String.format("Done converting! Went from %s to %s player entries", userMap.size(), profiles.length));
        }

        String defaultGroup = section.getString("default", "default");
        newSection.set("default", defaultGroup);

        super.load(manager, newSection);
    }

}
