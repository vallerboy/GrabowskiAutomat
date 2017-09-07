package pl.oskarpolak.grabowski.models;

public class CsvInfo {
    private String driver;
    private String registration;
    private String course;
    private float longitude;
    private float latitude;
    private String date;

    public CsvInfo() {
    }

    public CsvInfo(String driver, String registration, String course, float longitude, float latitude, String date) {
        this.driver = driver;
        this.registration = registration;
        this.course = course;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsvInfo csvInfo = (CsvInfo) o;

        if (Float.compare(csvInfo.longitude, longitude) != 0) return false;
        if (Float.compare(csvInfo.latitude, latitude) != 0) return false;
        if (driver != null ? !driver.equals(csvInfo.driver) : csvInfo.driver != null) return false;
        if (registration != null ? !registration.equals(csvInfo.registration) : csvInfo.registration != null)
            return false;
        if (course != null ? !course.equals(csvInfo.course) : csvInfo.course != null) return false;
        return date != null ? date.equals(csvInfo.date) : csvInfo.date == null;
    }

    @Override
    public int hashCode() {
        int result = driver != null ? driver.hashCode() : 0;
        result = 31 * result + (registration != null ? registration.hashCode() : 0);
        result = 31 * result + (course != null ? course.hashCode() : 0);
        result = 31 * result + (longitude != +0.0f ? Float.floatToIntBits(longitude) : 0);
        result = 31 * result + (latitude != +0.0f ? Float.floatToIntBits(latitude) : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "CsvInfo{" +
                "driver='" + driver + '\'' +
                ", registration='" + registration + '\'' +
                ", course='" + course + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", date='" + date + '\'' +
                '}';
    }
}
