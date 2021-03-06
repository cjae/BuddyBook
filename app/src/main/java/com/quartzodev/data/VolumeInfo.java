package com.quartzodev.data;

/**
 * Created by victoraldir on 14/06/2017.
 */

import android.support.annotation.Keep;
import java.util.List;

/**
 * Created by victoraldir on 12/04/2017.
 */

@Keep
public class VolumeInfo {

    public String title;

    public List<String> authors;

    public String publisher;

    public String publishedDate;

    public String description;

    public ImageLink imageLink;

    public String searchField;

    public String getSearchField() {

        searchField = title;

        if (authors != null && !authors.isEmpty()) {
            for (String author : authors) {
                searchField = searchField.concat("_" + author);
            }
        }

        searchField = searchField.concat("_" + publisher);

        return searchField.toLowerCase();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageLink getImageLink() {
        return imageLink;
    }

    public void setImageLink(ImageLink imageLink) {
        this.imageLink = imageLink;
    }
}
