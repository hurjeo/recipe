package dev.recipe.entities.bbs;

public class StepEntity {
    private int index;
    private String stepContent;
    private byte[] stepImage;
    private int articleIndex;
    private String type;

    public String getStepContent() {
        return stepContent;
    }

    public void setStepContent(String stepContent) {
        this.stepContent = stepContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getContent() {
        return stepContent;
    }

    public void setContent(String content) {
        this.stepContent = content;
    }

    public byte[] getStepImage() {
        return stepImage;
    }

    public void setStepImage(byte[] stepImage) {
        this.stepImage = stepImage;
    }

    public int getArticleIndex() {
        return articleIndex;
    }

    public void setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
    }
}
