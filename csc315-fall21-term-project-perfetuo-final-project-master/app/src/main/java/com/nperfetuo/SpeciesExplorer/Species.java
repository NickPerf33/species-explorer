package com.nperfetuo.SpeciesExplorer;

public class Species {

    private String commonName;
    private String scientificName;
    private String order;
    private String description;
    private String imageFile;
    private boolean seen;

    public Species() {
    }

    public Species(String commonName, String scientificName, String order, String description, String imageFile, boolean seen) {
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.order = order;
        this.description = description;
        this.imageFile = imageFile;
        this.seen = seen;
    }

    public String getCommonName() { return commonName; }

    public String getScientificName() {
        return scientificName;
    }

    public String getOrder(){ return order; }

    public String getDescription() { return description; }

    public String getImageFile() {
        return imageFile;
    }

    public boolean getSeen() {
        return seen;
    }
}


