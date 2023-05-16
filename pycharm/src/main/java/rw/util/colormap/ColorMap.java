package rw.util.colormap;


import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ColorMap {
    List<Color> colors;
    ImageIcon image;
    String name;

    ColorMap(ImageIcon image, List<Color> colors, String name) {
        Image scaled = image.getImage().getScaledInstance(200, 20, java.awt.Image.SCALE_SMOOTH);

        this.image = new ImageIcon(scaled);
        this.colors = colors;
        this.name = name;
    }

    public ImageIcon getImage() {
        return image;
    }

    public Color getColor(double min, double max, double value) {
        double normalised = ((value - min) / (max - min)) * 255.0;
        int index = (int) normalised;

        if (index < 0) {
            index = 0;
        }

        if (index > 255) {
            index = 255;
        }

        Color ret = this.colors.get(index);
        return ret;
    }

    public Color getColor(float min, float max, float value) {
        return this.getColor(min, max, (double) value);
    }

    public Color getColor(long min, long max, long value) {
        return this.getColor(min, max, (double) value);
    }

    public String getName() {
        return this.name;
    }

    public Color getMinColor() {
        return this.colors.get(0);
    }
}
