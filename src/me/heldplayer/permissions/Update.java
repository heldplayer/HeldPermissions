
package me.heldplayer.permissions;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Update {

    private static Permissions plugin;

    public Update(Permissions instance) {
        plugin = instance;
    }

    public Boolean updateCheck() {
        try {
            URL url = new URL(plugin.address);
            url.openConnection();
            new File(Permissions.updatepath);
            double lastmodifiedurl = Double.parseDouble(getLatestVersion(plugin.versionaddress));
            double lastmodifiedfile = Double.parseDouble(Permissions.version);
            if (lastmodifiedurl > lastmodifiedfile) {
                return Boolean.valueOf(true);
            }
            return Boolean.valueOf(false);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getServer().notify();
        return Boolean.valueOf(false);
    }

    public double version() {
        try {
            URL url = new URL(plugin.address);
            url.openConnection();
            new File(Permissions.updatepath);
            double lastmodifiedurl = Double.parseDouble(getLatestVersion(plugin.versionaddress));
            return lastmodifiedurl;
        }
        catch (MalformedURLException e) {
            plugin.getServer().notify();
            return 0.0D;
        }
        catch (IOException e) {
            plugin.getServer().notify();
            return 0.0D;
        }
    }

    public String[] reason() {
        String[] lastmodifiedurl = getUpdateReason(plugin.updatereasonaddress);
        return lastmodifiedurl;
    }

    public void download(String adress, String updatepath) {
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
        try {
            new File(updatepath);
            URL url = new URL(adress);
            out = new BufferedOutputStream(new FileOutputStream(updatepath));
            conn = url.openConnection();
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];

            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static String getLatestVersion(String site) {
        try {
            URL url = new URL(site);

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            if ((str = in.readLine()) != null) {
                return str.substring(0, 3);
            }

            in.close();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String[] getUpdateReason(String site) {
        try {
            URL url = new URL(site);

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            if ((str = in.readLine()) != null) {
                String[] st = str.split("%");
                return st;
            }

            in.close();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
