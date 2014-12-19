package me.heldplayer.permissions;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;

public class Updater {

    public static final String WEBSITE_ROOT = "http://dsiwars.specialattack.net/jars/HeldsPermissions/";
    public static final String UPDATE_ADDRESS = WEBSITE_ROOT + "HeldsPermissions.jar";
    public static final String BRIDGE_UPDATE_ADDRESS = WEBSITE_ROOT + "HeldsPermissionsBridge.jar";
    public static final String VERSION_ADDRESS = WEBSITE_ROOT + "version.txt";
    public static final String CHANGELOG_ADDRESS = "WEBSITE_ROOT + changelog.txt";
    public static final String UPDATE_PATH = "plugins" + File.separator + "HeldPermissions.jar";
    public static final String BRUDGE_UPDATE_PATH = "plugins" + File.separator + "HeldPermissionsBridge.jar";
    public static String version;

    public static boolean updateAvailable() {
        String[] versionString = Updater.version.split("\\.");
        int[] version = new int[versionString.length];

        for (int i = 0; i < version.length; i++) {
            version[i] = Integer.parseInt(versionString[i]);
        }

        versionString = readFile(VERSION_ADDRESS, Updater.version).split("\\.");
        int[] latestVersion = new int[versionString.length];

        for (int i = 0; i < latestVersion.length; i++) {
            latestVersion[i] = Integer.parseInt(versionString[i]);
        }

        for (int i = 0; i < version.length && i < latestVersion.length; i++) {
            if (latestVersion[i] > version[i]) {
                return true;
            }
        }

        return false;
    }

    public static String readFile(String location, String def) {
        try {
            URL url = new URL(location);

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String string = in.readLine();
            if (string != null) {
                return string.trim();
            }

            in.close();
        } catch (MalformedURLException e) {
            Permissions.log.log(Level.WARNING, "Failed reading file " + location, e);
        } catch (IOException e) {
            Permissions.log.log(Level.WARNING, "Failed reading file " + location, e);
        }

        return def;
    }

    public static String[] getChangelog() {
        try {
            URL url = new URL(CHANGELOG_ADDRESS);

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String string;

            ArrayList<String> lines = new ArrayList<String>();

            while ((string = in.readLine()) != null) {
                lines.add(string);
            }

            in.close();

            return lines.toArray(new String[] { });
        } catch (MalformedURLException e) {
            Permissions.log.log(Level.WARNING, "Failed loading changelog", e);
        } catch (IOException e) {
            Permissions.log.log(Level.WARNING, "Failed loading changelog", e);
        }

        return null;
    }

    public static boolean update() {
        if (updateAvailable()) {
            downloadAndReplaceFile(UPDATE_ADDRESS, UPDATE_PATH);
            downloadAndReplaceFile(BRIDGE_UPDATE_ADDRESS, BRUDGE_UPDATE_PATH);
        }

        return false;
    }

    public static void downloadAndReplaceFile(String adress, String path) {
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
        try {
            URL url = new URL(adress);
            out = new BufferedOutputStream(new FileOutputStream(path));
            conn = url.openConnection();
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];

            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
            }
        } catch (Exception e) {
            Permissions.log.log(Level.WARNING, "Failed downloading and replacing file (" + path + ")", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ioe) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
            }
        }
    }

}
