package com.example.nimish.udacitytracker.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nimishsanghi on 29/07/16.
 */
public class Course implements Parcelable {

    private String courseCode;
    private String title;
    private String homepage;
    private String subtitle;
    private String level;
    private String image;
    private String bannerImage;
    private String teaserVideo;
    private String summary;
    private String shortSummary;
    private String requiredKnowledge;
    private String expectedLearning;
    private String expectedDuration;
    private String expectedDurationUnit;
    private String newRelease;
    private int favorite;

    public Course(String courseCode, String title, String homepage, String subtitle, String
            level, String image, String bannerImage, String teaserVideo, String summary, String
                          shortSummary, String requiredKnowledge, String expectedLearning, String
                          expectedDuration, String expectedDurationUnit, String newRelease, int favorite) {
        this.courseCode = courseCode;
        this.title = title;
        this.homepage = homepage;
        this.subtitle = subtitle;
        this.level = level;
        this.image = image;
        this.bannerImage = bannerImage;
        this.teaserVideo = teaserVideo;
        this.summary = summary;
        this.shortSummary = shortSummary;
        this.requiredKnowledge = requiredKnowledge;
        this.expectedLearning = expectedLearning;
        this.expectedDuration = expectedDuration;
        this.expectedDurationUnit = expectedDurationUnit;
        this.newRelease = newRelease;
        this.favorite = favorite;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getTeaserVideo() {
        return teaserVideo;
    }

    public void setTeaserVideo(String teaserVideo) {
        this.teaserVideo = teaserVideo;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getShortSummary() {
        return shortSummary;
    }

    public void setShortSummary(String shortSummary) {
        this.shortSummary = shortSummary;
    }

    public String getRequiredKnowledge() {
        return requiredKnowledge;
    }

    public void setRequiredKnowledge(String requiredKnowledge) {
        this.requiredKnowledge = requiredKnowledge;
    }

    public String getExpectedLearning() {
        return expectedLearning;
    }

    public void setExpectedLearning(String expectedLearning) {
        this.expectedLearning = expectedLearning;
    }

    public String getExpectedDuration() {
        return expectedDuration;
    }

    public void setExpectedDuration(String expectedDuration) {
        this.expectedDuration = expectedDuration;
    }

    public String getExpectedDurationUnit() {
        return expectedDurationUnit;
    }

    public void setExpectedDurationUnit(String expectedDurationUnit) {
        this.expectedDurationUnit = expectedDurationUnit;
    }

    public String getNewRelease() {
        return newRelease;
    }

    public void setNewRelease(String newRelease) {
        this.newRelease = newRelease;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseCode='" + courseCode + '\'' +
                ", title='" + title + '\'' +
                ", homepage='" + homepage + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", level='" + level + '\'' +
                ", image='" + image + '\'' +
                ", bannerImage='" + bannerImage + '\'' +
                ", teaserVideo='" + teaserVideo + '\'' +
                ", summary='" + summary + '\'' +
                ", shortSummary='" + shortSummary + '\'' +
                ", requiredKnowledge='" + requiredKnowledge + '\'' +
                ", expectedLearning='" + expectedLearning + '\'' +
                ", expectedDuration='" + expectedDuration + '\'' +
                ", expectedDurationUnit='" + expectedDurationUnit + '\'' +
                ", newRelease='" + newRelease + '\'' +
                ", favorite='" + favorite + '\'' +
                '}';
    }

    private  Course(Parcel in) {
        this.courseCode = in.readString();
        this.title = in.readString();
        this.homepage = in.readString();
        this.subtitle = in.readString();
        this.level = in.readString();
        this.image = in.readString();
        this.bannerImage = in.readString();
        this.teaserVideo = in.readString();
        this.summary = in.readString();
        this.shortSummary = in.readString();
        this.requiredKnowledge = in.readString();
        this.expectedLearning = in.readString();
        this.expectedDuration = in.readString();
        this.expectedDurationUnit = in.readString();
        this.newRelease = in.readString();
        this.favorite = in.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        dest.writeString(courseCode);
        dest.writeString(title);
        dest.writeString(homepage);
        dest.writeString(subtitle);
        dest.writeString(level);
        dest.writeString(image);
        dest.writeString(bannerImage);
        dest.writeString(teaserVideo);
        dest.writeString(summary);
        dest.writeString(shortSummary);
        dest.writeString(requiredKnowledge);
        dest.writeString(expectedLearning);
        dest.writeString(expectedDuration);
        dest.writeString(expectedDurationUnit);
        dest.writeString(newRelease);
        dest.writeInt(favorite);

    }

    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
}
