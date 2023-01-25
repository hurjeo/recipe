package dev.recipe.entities.bbs;

import java.util.Date;
import java.util.Objects;

public class RecipeArticleEntity {
    private int index;
    private String userEmail;
    private String title;
    private String recipeIntroduce;
    private String categoryType;
    private String categorySituation;
    private String categoryMatter;
    private String categoryHow;
    private String content;
    private String infoPersonnel;
    private String infoTime;
    private String infoDifficult;
    private String cookTip;
    private int view;
    private Date writtenOn;
    private Date modifiedOn;

    private byte[] main_image;
    private String main_imageType;
    private boolean isOpen;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeArticleEntity that = (RecipeArticleEntity) o;
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


    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategorySituation() {
        return categorySituation;
    }

    public void setCategorySituation(String categorySituation) {
        this.categorySituation = categorySituation;
    }

    public String getCategoryMatter() {
        return categoryMatter;
    }

    public void setCategoryMatter(String categoryMatter) {
        this.categoryMatter = categoryMatter;
    }

    public String getCategoryHow() {
        return categoryHow;
    }

    public void setCategoryHow(String categoryHow) {
        this.categoryHow = categoryHow;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInfoPersonnel() {
        return infoPersonnel;
    }

    public void setInfoPersonnel(String infoPersonnel) {
        this.infoPersonnel = infoPersonnel;
    }

    public String getInfoTime() {
        return infoTime;
    }

    public void setInfoTime(String infoTime) {
        this.infoTime = infoTime;
    }

    public String getInfoDifficult() {
        return infoDifficult;
    }

    public void setInfoDifficult(String infoDifficult) {
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

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public byte[] getMain_image() {
        return main_image;
    }

    public void setMain_image(byte[] main_image) {
        this.main_image = main_image;
    }

    public String getMain_imageType() {
        return main_imageType;
    }

    public void setMain_imageType(String main_imageType) {
        this.main_imageType = main_imageType;
    }
}
