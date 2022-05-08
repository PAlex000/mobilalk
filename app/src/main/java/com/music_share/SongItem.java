package com.music_share;

public class SongItem {
    private String id;
    private String title;
    private String description;
    private String url;
    private int imageResource;


    public SongItem(String title, String description, String url, int imageResource) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageResource = imageResource;
    }

    public SongItem() {
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUrl() {
        return this.url;
    }

    public int getImageResource() {
        return this.imageResource;
    }

    public String _getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
