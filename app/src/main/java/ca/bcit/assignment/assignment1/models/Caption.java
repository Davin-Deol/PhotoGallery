package ca.bcit.assignment.assignment1.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "Caption")
public class Caption {

    @PrimaryKey
    @ColumnInfo(name = "image")
    @NonNull
    private String image;

    @ColumnInfo(name = "caption")
    private String caption;

    @ColumnInfo(name = "whenCreated")
    @NonNull
    private Date whenCreated;

    @ColumnInfo(name = "location")
    private String location;

    public Caption(String image) {
        setImage(image);
        setCaption(null);
        setWhenCreated(new Date());
        setLocation("");
    }

    public void setImage(String image) {
        if (image != null) {
            this.image = image;
        }
    }

    public String getImage() {
        return image;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setWhenCreated(Date whenCreated) {
        if (whenCreated != null) {
            this.whenCreated = whenCreated;
        }
    }

    public Date getWhenCreated() {
        return whenCreated;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
