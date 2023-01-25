package dev.recipe.entities.bbs;

public class MainImageEntity {
    private int index;
    private int articleIndex;
    private byte[] mainPhoto;
    private String type;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getArticleIndex() {
        return articleIndex;
    }

    public void setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
    }

    public byte[] getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(byte[] mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
