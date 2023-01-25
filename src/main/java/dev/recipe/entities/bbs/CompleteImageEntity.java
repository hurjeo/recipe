package dev.recipe.entities.bbs;

public class CompleteImageEntity {

    private int index;
    private byte[] completeImage;
    private String completeImageType;
    private int articleIndex;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public byte[] getCompleteImage() {
        return completeImage;
    }

    public void setCompleteImage(byte[] completeImage) {
        this.completeImage = completeImage;
    }

    public String getCompleteImageType() {
        return completeImageType;
    }

    public void setCompleteImageType(String completeImageType) {
        this.completeImageType = completeImageType;
    }

    public int getArticleIndex() {
        return articleIndex;
    }

    public void setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
    }
}
