package rw.tests.fixtures;

import rw.config.ConfigManager;

import static org.mockito.Mockito.*;

public class ConfigManagerFixture {
    NativeFileSystemFixture nativeFileSystemFixture;

    public ConfigManagerFixture(NativeFileSystemFixture nativeFileSystemFixture) {
        this.nativeFileSystemFixture = nativeFileSystemFixture;
    }

    public void setUp() {
        ConfigManager.singleton = spy(ConfigManager.get());
        ConfigManager.singleton.fs = spy(ConfigManager.get().fs);
        lenient().when(ConfigManager.singleton.fs.getHome()).thenReturn(nativeFileSystemFixture.home);
    }

    public void tearDown() {

    }
}
