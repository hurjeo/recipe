package dev.recipe.entities.bbs;

import java.util.Date;
import java.util.Objects;

public class RecipeEntity {
    private int index;
    private String userEmail;
    private String title;
    private String recipeIntroduce;
    private int categoryType;
    private int categorySituation;
    private int categoryMatter;
    private int categoryHow;
    private int infoPersonnel;
    private int infoTime;
    private int infoDifficult;
    private String cookTip;
    private int view;
    private Date writtenOn;
    private Date modifiedOn;

    private byte[] mainImage;
    private String mainImageType;
    private boolean isOpen;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeEntity that = (RecipeEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecipeIntroduce() {
        return recipeIntroduce;
    }

    public void setRecipeIntroduce(String recipeIntroduce) {
        this.recipeIntroduce = recipeIntroduce;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public int getCategorySituation() {
        return categorySituation;
    }

    public void setCategorySituation(int categorySituation) {
        this.categorySituation = categorySituation;
    }

    public int getCategoryMatter() {
        return categoryMatter;
    }

    public void setCategoryMatter(int categoryMatter) {
        this.categoryMatter = categoryMatter;
    }

    public int getCategoryHow() {
        return categoryHow;
    }

    public void setCategoryHow(int categoryHow) {
        this.categoryHow = categoryHow;
    }

    public int getInfoPersonnel() {
        return infoPersonnel;
    }

    public void setInfoPersonnel(int infoPersonnel) {
        this.infoPersonnel = infoPersonnel;
    }

    public int getInfoTime() {
        return infoTime;
    }

    public void setInfoTime(int infoTime) {
        this.infoTime = infoTime;
    }

    public int getInfoDifficult() {
        return infoDifficult;
    }

    public void setInfoDifficult(int infoDifficult) {
        this.infoDifficult = infoDifficult;
    }

    public String getCookTip() {
        return cookTip;
    }

    public void setCookTip(String cookTip) {
        this.cookTip = cookTip;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public Date getWrittenOn() {
        return writtenOn;
    }

    public void setWrittenOn(Date writtenOn) {
        this.writtenOn = writtenOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public byte[] getMainImage() {
        return mainImage;
    }

    public void setMainImage(byte[] mainImage) {
        this.mainImage = mainImage;
    }

    public String getMainImageType() {
        return mainImageType;
    }

    public void setMainImageType(String mainImageType) {
        this.mainImageType = mainImageType;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
