package ru.javawebinar.basejava;

import ru.javawebinar.basejava.storage.SqlStorage;
import ru.javawebinar.basejava.storage.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    private final Properties props = new Properties();
    private final File storageDir;
    private final Storage storage;

    public static Config get() {
        return INSTANCE;
    }

    private Config() {
        try (InputStream is = getClass().getResourceAsStream("/config/resumes.properties")) {
            props.load(is);
            storageDir = new File(props.getProperty("storage.dir"));
            storage = new SqlStorage(getDbUrl(), getDbUser(), getDbPassword());
        } catch (IOException e) {
            throw new IllegalStateException("Invalid config file ", e);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Can't find config file ", e);
        }
    }

    public File getStorageDir() {
        return storageDir;
    }

    public String getDbUrl() {
        return props.getProperty("db.url");
    }

    public String getDbUser() {
        return props.getProperty("db.user");
    }

    public String getDbPassword() {
        return props.getProperty("db.password");
    }

    public Storage getStorage() {
        return storage;
    }
}
