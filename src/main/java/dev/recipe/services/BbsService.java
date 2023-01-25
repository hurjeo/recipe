package dev.recipe.services;

import dev.recipe.entities.bbs.*;
import dev.recipe.enums.CommonResult;
import dev.recipe.enums.bbs.writeResult;
import dev.recipe.interfaces.IResult;
import dev.recipe.mappers.BbsMapper;
import dev.recipe.utils.JsonArrayConvert;
import dev.recipe.utils.entities.bbs.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Service (value = "dev.recipe.recipeArticle.Services.BbsService")
public class BbsService {

    private final dev.recipe.mappers.BbsMapper BbsMapper;

    @Autowired
    public BbsService (BbsMapper bbsMapper){
        this.BbsMapper = bbsMapper;
    }

    @Transactional
    public Enum<? extends IResult> writeRecipe(RecipeEntity recipe, JSONArray materialArray,
                                               MultipartFile[] stepPhotos, String[] step,
                                               String isOpen,
                                               MultipartFile mainPhoto,
                                               MultipartFile[] completePhoto)
            throws IOException {
        // 저장 || 저장후 공개
        if (isOpen.equals("save")||isOpen.equals("saveOpen")){
            recipe.setOpen(isOpen.equals("saveOpen"));
        } else {
            return writeResult.INCORRECT_APPROACH;
        }
        recipe.setMainImage(mainPhoto.getBytes());
        recipe.setMainImageType(mainPhoto.getContentType());
        recipe.setUserEmail("ju6599@naver.com");
        if (this.BbsMapper.insertRecipe(recipe) > 0){
            // material insert
            for (Object materialObject : materialArray){
                JSONObject materialJson = new JSONObject(materialObject.toString());
                MaterialEntity material = new MaterialEntity();
                material.setArticleIndex(recipe.getIndex());
                material.setMaterialTitle(materialJson.optString("materialTitle"));
                if (this.BbsMapper.insertMaterial(material) == 0){
                    return CommonResult.FAILURE;
                }
                MaterialContentEntity materialContent = new MaterialContentEntity();
                materialContent.setMaterialIndex(material.getIndex());
                String[] materialName = JsonArrayConvert.arrayConvert(materialJson.getJSONArray("materialName"));
                String[] materialCount = JsonArrayConvert.arrayConvert(materialJson.getJSONArray("materialCount"));
                for (int i = 0; i < materialName.length; i++) {
                    if (!Objects.equals(materialName[i], "") || !Objects.equals(materialCount[i], "")){
                        materialContent.setMaterialName(materialName[i]);
                        materialContent.setMaterialCount(materialCount[i]);
                        if (this.BbsMapper.insertMaterialContent(materialContent) == 0){
                            return CommonResult.FAILURE;
                        }
                    }
                }
            }
        }
        //완료 이미지
        for (MultipartFile completeImg : completePhoto){
            CompleteImageEntity completeImage = new CompleteImageEntity();
            completeImage.setArticleIndex(recipe.getIndex());
            completeImage.setCompleteImageType(completeImg.getContentType());
            completeImage.setCompleteImage(completeImg.getBytes());
            if (this.BbsMapper.insertCompleteImages(completeImage) == 0){
                return CommonResult.FAILURE;
            }
        }

        // 스탭 insert
        StepEntity stepEntity = new StepEntity();
        int arrayMax = Math.max(step.length, stepPhotos.length);
        MultipartFile[] stepPhotoArray = Arrays.copyOf(stepPhotos,arrayMax);
        for (int i = 0; i < arrayMax; i++) {
            if (!step[i].equals("") || !(stepPhotoArray[i] == null)){
            stepEntity.setArticleIndex(recipe.getIndex());
            stepEntity.setContent(step[i]);
            if (!(stepPhotoArray[i]==null)){
                stepEntity.setStepImage(stepPhotoArray[i].getBytes());
                stepEntity.setType(stepPhotoArray[i].getContentType());
            }
            if (this.BbsMapper.insertStep(stepEntity) == 0) {
                return CommonResult.FAILURE;
            }
            }
        }
        return CommonResult.SUCCESS;
    }
}







