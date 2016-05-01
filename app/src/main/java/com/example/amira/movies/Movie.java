package com.example.amira.movies;

/**
 * Created by amira on 4/29/2016.
 *
 */
public class Movie {

    private String title;

    private String releaseDate;

    private String poster;

    private String voteAverage;

    private String overview;

    private boolean isAdult;

    private int id;


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getReleaseDate() {
        return releaseDate;
    }


    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }


    public String getPoster() {
        return poster;
    }


    public void setPoster(String poster) {
        this.poster = poster;
    }


    public String getVoteAverage() {
        return voteAverage;
    }


    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }


    public String getOverview() {
        return overview;
    }


    public void setOverview(String overview) {
        this.overview = overview;
    }


    public boolean isAdult() {
        return isAdult;
    }


    public void setIsAdult(boolean isAdult) {
        this.isAdult = isAdult;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


}
