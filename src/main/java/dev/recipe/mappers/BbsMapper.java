package dev.recipe.mappers;

import dev.recipe.entities.bbs.*;
import dev.recipe.utils.entities.bbs.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BbsMapper {

    int insertRecipe(RecipeEntity recipeArticleEntity);

    int insertMaterial(MaterialEntity material);

    int insertMaterialContent(MaterialContentEntity materialContent);

    int insertCompleteImages(CompleteImageEntity completeImage);

    int insertStep(StepEntity step);
}
