package dev.recipe.controllers;

import dev.recipe.entities.bbs.RecipeEntity;
import dev.recipe.services.BbsService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;



@Controller(value = "dev.recipe.recipeArticle.Controllers.BbsController")
@RequestMapping(value = "/bbs")
public class BbsController {
    private final BbsService bbsService;

    @Autowired
    public BbsController (BbsService bbsService){
        this.bbsService = bbsService;
    }

    //게시판 리스트 화면
    @GetMapping(value = "list",
    produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList(){

        ModelAndView modelAndView = new ModelAndView("articles/list");
        return modelAndView;
    }



    //게시판 write 화면
    @GetMapping(value = "write",
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite(){
        return new ModelAndView("articles/write");
    }

    //게시판 read 화면
    @GetMapping(value = "read",
    produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRead(){
        return new ModelAndView("articles/read");
    }

    //글 업로드
    @PostMapping(value = "write",
    produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWrite(RecipeEntity recipe,
                            @RequestParam(value = "stepContent", required = false) String[] step,
                            @RequestParam(value = "completePhoto", required = false) MultipartFile[] completePhoto,
                            @RequestParam(value = "mainPhoto") MultipartFile mainPhoto,
                            @RequestParam(value = "materialJson") String materialString,
                            @RequestPart(value = "stepPhoto", required = false) MultipartFile[] stepPhotos,
                            @RequestParam(value = "isOpen") String isOpen) throws IOException {
        JSONArray materialArray = new JSONArray(materialString);

        Enum<?> result = this.bbsService.writeRecipe(recipe,materialArray,stepPhotos,step,
                isOpen,mainPhoto,completePhoto);


        JSONObject responseObject = new JSONObject();

        return responseObject.toString();
    }





}
