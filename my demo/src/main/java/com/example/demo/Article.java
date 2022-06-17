package com.example.demo;

public class Article {
    public String type;
    public int score;
    public float indexscore;
    public String id;
    public String title;
    public String text;
    public String subreddit;

    public Article(){}

    public Article(String type, int score, float indexscore, String id, String title, String text,
                   String subreddit){
        this.type = type;
        this.indexscore = indexscore;
        this.score = score;
        this.id = id;
        this.title = title;
        this.text = text;
        this.subreddit = subreddit;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
    public void setScore(int score){
        this.score = score;
    }
    public int getScore(){return score;}
    public void setIndexscore(float indexscore){
        this.indexscore = indexscore;
    }
    public float getIndexscore(){return indexscore;}
    public void setid(String id){
        this.id = id;
    }
    public String getid(){
        return id;
    }
    public void setTitle(String title){this.title = title;}
    public String getTitle(){
        return title;
    }
    public void setText(String text){this.text = text;}
    public String getText(){return text;}
    public void setSubreddit(String subreddit){
        this.subreddit = subreddit;
    }
    public String getSubreddit(){
        return subreddit;
    }

    @Override
    public String toString() {
        if (type == "post") {
            return String.format("Type: %s \nID: %s \t|\t Score: %d \t|\t Subrddit: " +
                    "%s \t|\t Timestamp: %s \nTitle: %s\nText: %s\n\n", type, id, score, subreddit, title, text);
        } else if (type == "comment") {
            return String.format("Type: %s \nID: %s \t|\t Score: %d \t|\t Subrddit: " +
                    "%s \t|\t Timestamp: %s \nText: %s\n\n", type, id, score, subreddit, text);
        } else
            return null;
    }

}
