package com.qw.event.article;

public class ScanEvent {
    private int articleId;

    public ScanEvent(int articleId) {
        this.articleId = articleId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
}
