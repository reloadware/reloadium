package rw.util.colormap;


import rw.media.Media;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColorMaps {
    public ColorMap inferno = new ColorMap(Media.inferno, Inferno.colors, "Inferno");
    public ColorMap plasma = new ColorMap(Media.plasma, Plasma.colors, "Plasma");
    public ColorMap viridis = new ColorMap(Media.viridis, Viridis.colors, "Viridis");

    static ColorMaps singleton;

    public static ColorMaps get() {
        if( singleton == null ) {
            singleton = new ColorMaps();
        }
        return singleton;
    }

    public static ColorMap getSelected() {
        PreferencesState state = Preferences.getInstance().getState();
        ColorMap ret = ColorMaps.get().getColorMapByName(state.timingColorMap);
        return ret;
    }

    private Map<ImageIcon, ColorMap> imageToColorMap = Map.of(
            this.inferno.getImage(), this.inferno,
            this.plasma.getImage(), this.plasma,
            this.viridis.getImage(), this.viridis
    );

    private Map<String, ColorMap> nameToColorMap = Map.of(
            this.inferno.getName(), this.inferno,
            this.plasma.getName(), this.plasma,
            this.viridis.getName(), this.viridis
    );

    public List<ColorMap> getAll() {
        return List.of(this.inferno, this.plasma, this.viridis);
    }
    public List<Icon> getAllImages() {
        List<Icon> ret = getAll().stream().map(e -> e.getImage()).collect(Collectors.toList());
        return ret;
    }

    public ColorMap getColorMapByImage(ImageIcon image) {
        return this.imageToColorMap.get(image);
    }

    public ColorMap getColorMapByName(String image) {
        return this.nameToColorMap.get(image);
    }
}
