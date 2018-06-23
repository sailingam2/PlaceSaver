package info.androidhive.recyclerview;


public class Place {
    private String name;
    private double latitude, longitude;
    private String imagePath;
    private int id;

    public Place() {

    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Place(int id, String name, double latitude, double longitude, String imagePath) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagePath = imagePath;
        this.id = id;

    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
