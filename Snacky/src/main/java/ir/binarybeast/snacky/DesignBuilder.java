package ir.binarybeast.snacky;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ir.binarybeast.snacky.utils.DimensionUtils;
import ir.binarybeast.snacky.utils.Theme;


public class DesignBuilder {
    final Map<String, String> keysAndValues;


    DesignBuilder(@NonNull DesignBuilder.Builder builder) {
        this.keysAndValues = builder.keysAndValues;
    }

    public int getBackgroundRadius() {
        if (!keysAndValues.containsKey(SharedConfig.KEY_BACKGROUND_RADIUS)) {
            return Theme.getDefaultBackgroundRadius();
        }
        return Integer.parseInt(Objects.requireNonNull(keysAndValues.get(SharedConfig.KEY_BACKGROUND_RADIUS)));
    }

    public int getBackgroundColor() {
        if (!keysAndValues.containsKey(SharedConfig.KEY_BACKGROUND_COLOR)) {
            return Theme.getDefaultBackgroundColor();
        }
        return Integer.parseInt(Objects.requireNonNull(keysAndValues.get(SharedConfig.KEY_BACKGROUND_COLOR)));
    }

    public int getTitleTextColor() {
        if (!keysAndValues.containsKey(SharedConfig.KEY_TITLE_TEXT_COLOR)) {
            return Theme.getDefaultTextColor();
        }
        return Integer.parseInt(Objects.requireNonNull(keysAndValues.get(SharedConfig.KEY_TITLE_TEXT_COLOR)));
    }

    public int getTitleTextSize() {
        if (!keysAndValues.containsKey(SharedConfig.KEY_TITLE_TEXT_SIZE)) {
            return Theme.getDefaultTextSize();
        }
        return Integer.parseInt(Objects.requireNonNull(keysAndValues.get(SharedConfig.KEY_TITLE_TEXT_SIZE)));
    }

    public int getSubtitleTextColor() {
        if (!keysAndValues.containsKey(SharedConfig.KEY_SUBTITLE_TEXT_COLOR)) {
            return Theme.getDefaultTextColor();
        }
        return Integer.parseInt(Objects.requireNonNull(keysAndValues.get(SharedConfig.KEY_SUBTITLE_TEXT_COLOR)));
    }

    public int getSubtitleTextSize() {
        if (!keysAndValues.containsKey(SharedConfig.KEY_SUBTITLE_TEXT_SIZE)) {
            return Theme.getDefaultTextSize();
        }
        return Integer.parseInt(Objects.requireNonNull(keysAndValues.get(SharedConfig.KEY_SUBTITLE_TEXT_SIZE)));
    }

    public int getUndoTextColor() {
        if (!keysAndValues.containsKey(SharedConfig.KEY_UNDO_TEXT_COLOR)) {
            return Theme.getDefaultTextColor();
        }
        return Integer.parseInt(Objects.requireNonNull(keysAndValues.get(SharedConfig.KEY_UNDO_TEXT_COLOR)));
    }

    public int getUndoTextSize() {
        if (!keysAndValues.containsKey(SharedConfig.KEY_UNDO_TEXT_SIZE)) {
            return Theme.getDefaultTextSize();
        }
        return Integer.parseInt(Objects.requireNonNull(keysAndValues.get(SharedConfig.KEY_UNDO_TEXT_SIZE)));
    }

    public boolean isRtl() {
        if (!keysAndValues.containsKey(SharedConfig.KEY_IS_RTL)) {
            return Theme.getDefaultDirection();
        }
        return Boolean.parseBoolean(Objects.requireNonNull(keysAndValues.get(SharedConfig.KEY_IS_RTL)));
    }

    public static class Builder {
        private Map<String, String> keysAndValues = new HashMap();

        public Builder() {
        }

        public Builder setBackgroundRadius(int radius) {
            this.putInt(SharedConfig.KEY_BACKGROUND_RADIUS, DimensionUtils.dpToPx(radius));
            return this;
        }

        public Builder setBackgroundColor(int color) {
            this.putInt(SharedConfig.KEY_BACKGROUND_COLOR, color);
            return this;
        }

        public Builder setTitleTextColor(int color) {
            this.putInt(SharedConfig.KEY_TITLE_TEXT_COLOR, color);
            return this;
        }

        public Builder setTitleTextSize(int size) {
            this.putInt(SharedConfig.KEY_TITLE_TEXT_SIZE, size);
            return this;
        }

        public Builder setTitleTypeface(int font) {
            this.putInt(SharedConfig.KEY_TITLE_TEXT_FONT, font);
            return this;
        }

        public Builder setTitleTypeface(String path) {
            this.putString(SharedConfig.KEY_TITLE_TEXT_TYPEFACE, path);
            return this;
        }

        public Builder setSubtitleTextColor(int color) {
            this.putInt(SharedConfig.KEY_SUBTITLE_TEXT_COLOR, color);
            return this;
        }

        public Builder setSubtitleTextSize(int size) {
            this.putInt(SharedConfig.KEY_SUBTITLE_TEXT_SIZE, size);
            return this;
        }
        public Builder setSubtitleTypeface(int font) {
            this.putInt(SharedConfig.KEY_SUBTITLE_TEXT_FONT, font);
            return this;
        }

        public Builder setSubtitleTypeface(String path) {
            this.putString(SharedConfig.KEY_SUBTITLE_TEXT_TYPEFACE, path);
            return this;
        }

        public Builder setUndoTitleTextColor(int color) {
            this.putInt(SharedConfig.KEY_UNDO_TEXT_COLOR, color);
            return this;
        }

        public Builder setUndoTextSize(int size) {
            this.putInt(SharedConfig.KEY_UNDO_TEXT_SIZE, size);
            return this;
        }
        public Builder setUndoTypeface(int font) {
            this.putInt(SharedConfig.KEY_UNDO_TEXT_FONT, font);
            return this;
        }

        public Builder setUndoTypeface(String path) {
            this.putString(SharedConfig.KEY_UNDO_TEXT_TYPEFACE, path);
            return this;
        }
        public Builder isRtl(boolean isRtl) {
            this.putBoolean(SharedConfig.KEY_IS_RTL, isRtl);
            return this;
        }

        private void putString(@NonNull String key, @NonNull String value) {
            this.keysAndValues.put(key, value);
        }

        private void putBoolean(@NonNull String key, boolean value) {
            this.keysAndValues.put(key, Boolean.toString(value));
        }

        private void putDouble(@NonNull String key, double value) {
            this.keysAndValues.put(key, Double.toString(value));
        }

        private void putFloat(@NonNull String key, float value) {
            this.keysAndValues.put(key, Float.toString(value));
        }

        private void putLong(@NonNull String key, long value) {
            this.keysAndValues.put(key, Long.toString(value));
        }

        private void putInt(@NonNull String key, int value) {
            this.keysAndValues.put(key, Integer.toString(value));
        }

        @NonNull
        public DesignBuilder build() {
            return new DesignBuilder(this);
        }
    }


}
