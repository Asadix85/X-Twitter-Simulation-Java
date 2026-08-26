package com.example.x.model.file;

import java.io.Serializable;

public class Photo extends File implements Serializable {
    private String format;
    private int height;
    private int weight;

    public Photo(String filePath, String format) {
        super(filePath);
        this.format = format;
        this.height = 0;
        this.weight = 0;
    }

    private String validateFormat(String format) {
        String upperFormat = format.toUpperCase();
        switch (upperFormat) {
            case "JPEG":
            case "PNG":
            case "SVG":
            case "WEBP":
                return upperFormat;
            default:
                return "JPEG";
        }
    }

    public static boolean isValidFormat(String format) {
        String upper = format.toUpperCase();
        return upper.equals("JPEG") || upper.equals("PNG") || upper.equals("SVG") || upper.equals("WEBP");
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    @Override
    public String getFileType() {
        return "Photo (" + format + ")";
    }
}
