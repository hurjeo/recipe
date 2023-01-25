package dev.recipe.controllers;

import dev.recipe.entities.UserEntity;
import dev.recipe.entities.bbs.RecipeEntity;
import dev.recipe.enums.bbs.writeResult;
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



@Controller(value = "dev.recipe.controllers.BbsController")
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
        return new ModelAndView("bbs/list");
    }



    //게시판 write 화면
    @GetMapping(value = "write",
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite(@SessionAttribute(value = "user", required = false) UserEntity user) {
        return user == null ? new ModelAndView("redirect:/member/login") : new ModelAndView("bbs/write");
    }

    //게시판 read 화면
    @GetMapping(value = "read",
    produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRead(@SessionAttribute(value = "user", required = false) UserEntity user){
        ModelAndView modelAndView = new ModelAndView("bbs/read");
        modelAndView.addObject("user",user);
        return modelAndView;
    }

    //글 업로드
    @PostMapping(value = "write",
    produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWrite(RecipeEntity recipe,
                            @SessionAttribute(value = "user", required = false) UserEntity user,
                            @RequestParam(value = "stepContent", required = false) String[] step,
                            @RequestParam(value = "completePhoto", required = false) MultipartFile[] completePhoto,
                            @RequestParam(value = "mainPhoto") MultipartFile mainPhoto,
                            @RequestParam(value = "materialJson") String materialString,
                            @RequestPart(value = "stepPhoto", required = false) MultipartFile[] stepPhotos,
                            @RequestParam(value = "isOpen") String isOpen) throws IOException {
        if (user == null){
            return writeResult.NOT_LOGIN.toString().toLowerCase();
        }
        recipe.setUserEmail(user.getEmail());
        JSONObject responseObject = new JSONObject();
        JSONArray materialArray = new JSONArray(materialString);
        Enum<?> result = this.bbsService.writeRecipe(recipe,materialArray,stepPhotos,step,
                isOpen,mainPhoto,completePhoto);




        return responseObject.toString();
    }





}
