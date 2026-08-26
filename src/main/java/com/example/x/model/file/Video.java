package com.example.x.model.file;

import java.io.Serializable;

public class Video extends File implements Serializable {
    private int quality;
    private String playFormat;
    private String totalTime;

    public Video(String filePath, int quality, String playFormat, int totalTime) {
        super(filePath);
        this.quality = validateQuality(quality);
        this.playFormat = validateFormat(playFormat);
        this.totalTime = getFormattedTotalTime(Math.max(totalTime, 0));
    }

    private int validateQuality(int quality) {
        switch (quality) {
            case 360:
            case 720:
            case 1080:
                return quality;
            default:
                return 720;
        }
    }

    public static boolean isValidQuality(int quality) {
        return quality == 360 || quality == 720 || quality == 1080;
    }


    private String validateFormat(String format) {
        String upperFormat = format.toUpperCase();
        switch (upperFormat) {
            case "WMV":
            case "MOV":
            case "MKV":
            case "MP4":
                return upperFormat;
            default:
                return "MP4";
        }
    }

    public static boolean isValidFormat(String format) {
        String upper = format.toUpperCase();
        return upper.equals("WMV") || upper.equals("MOV") || upper.equals("MKV") || upper.equals("MP4");
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getPlayFormat() {
        return playFormat;
    }

    public void setPlayFormat(String playFormat) {
        this.playFormat = playFormat;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = getFormattedTotalTime(Math.max(totalTime, 0));
    }

    @Override
    public String getFileType() {
        return "Video (" + playFormat + "," + quality + "p)";
    }

    public String getFormattedTotalTime(int totalTime) {
        int min = totalTime / 60;
        int sec = totalTime % 60;
        return String.format("%d:%02d", min, sec);
    }
}
