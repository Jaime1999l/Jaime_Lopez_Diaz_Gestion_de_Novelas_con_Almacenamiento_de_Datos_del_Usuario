package com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.domain;

public class Novel {
    private String id;
    private String title;
    private String author;
    private int year;
    private String synopsis;
    private String imageUri;
    private boolean favorite;

    public Novel() {
    }

    public Novel(String title, String author, int year, String synopsis, String imageUri) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.synopsis = synopsis;
        this.imageUri = imageUri;
        this.favorite = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getSynopsis() { return synopsis; }
    public String getImageUri() { return imageUri; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}

