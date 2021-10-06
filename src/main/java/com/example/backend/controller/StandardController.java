package com.example.backend.controller;

import com.example.backend.entity.StandardQuestion;
import com.example.backend.entity.StandardQuestionnaire;
import com.example.backend.entity.User;
import com.example.backend.service.StandardService;
import com.example.backend.service.UserService;
import com.example.backend.utils.FormatConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class StandardController {
    @Autowired
    UserService userService;
    @Autowired
    StandardService standardService;

    @PostMapping("/standard/issue")
    public Map<String, Object> release(@RequestBody Map<String, Object> params) {
        int id = Integer.parseInt(params.get("id").toString());
        int status = Integer.parseInt(params.get("status").toString());
        List<Map<String, Object>> content = (List<Map<String, Object>>) params.get("content");
        String ThisTitle = params.get("title").toString();
        int userId = Integer.parseInt(params.get("user_id").toString());
        String endTime = params.get("endTime").toString();
        Map<String, Object> ret = new HashMap<>();
        try {
            StandardQuestionnaire standardQuestionnaire = standardService.getOneStandardQuestionnaire(id);
            int vQId;
            if (standardQuestionnaire == null) {
                standardQuestionnaire = new StandardQuestionnaire();
                standardService.save(standardQuestionnaire);
                vQId = standardQuestionnaire.getId();
                standardQuestionnaire.setStartTime(new Date());//创建时间
            } else {
                standardService.save(standardQuestionnaire);
                vQId = id;
            }
            standardQuestionnaire.setFatherId(standardQuestionnaire.getId());//非复制问卷fatherId设置为自身id
            if (status == 1) standardQuestionnaire.setStatus(1);
            else standardQuestionnaire.setStatus(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            standardQuestionnaire.setEndTime(sdf.parse(endTime));
            standardQuestionnaire.setTitle(ThisTitle);
            standardQuestionnaire.setPlace(0);
            User user = userService.selectUserByUserId(userId);
            standardQuestionnaire.setUser(user);
            standardQuestionnaire.setPaperType(params.get("paperType").toString());
//            List<StandardQuestion> standardQuestions = standardQuestionnaire.getStandardQuestions();
            List<StandardQuestion> standardQuestions = new ArrayList<>();
            for (Map<String, Object> question : content) {
                int vid = (int) question.get("id");
                StandardQuestion standardQuestion = standardService.getStandardById(vid);
                if (standardQuestion == null) {
                    standardQuestion = new StandardQuestion();
//                    standardQuestionnaire.getStandardQuestions().add(standardQuestion);
                }
                List<String> choiceArray = (List<String>) (question.get("choices"));
                String choices = "";
                for (String choice : choiceArray) {
                    choices += choice + ",";
                }
                choices = choices.substring(0, choices.length() - 1);
                standardQuestion.setChoices(choices);
                if (question.get("ptype") != null) {
                    int ptype = (int) question.get("ptype");
                    standardQuestion.setVisible(ptype != -1);
                } else {
                    standardQuestion.setVisible(false);
                }
                standardQuestion.setLimitation((Integer) question.get("limit"));
                standardQuestion.setType((String) question.get("type"));
                standardQuestion.setQuestion((String) question.get("title"));
                standardQuestion.setNecessary((boolean) question.get("necessary"));
                standardQuestion.setDescription(question.get("description").toString());
                standardService.save(standardQuestion);
                standardQuestions.add(standardQuestion);
            }
            standardQuestionnaire.setStandardQuestions(standardQuestions);
            standardQuestionnaire.setDescription(params.get("paperDescription").toString());
            standardQuestionnaire.setSingle((Boolean) params.get("isSingle"));
            standardQuestionnaire.setShowSeq((Boolean) params.get("showSeq"));
            standardService.save(standardQuestionnaire);
            ret.put("status", true);
            ret.put("exec", "无异常");
            ret.put("id", vQId);
        } catch (Exception e) {
            ret.put("status", false);
            ret.put("exec", e.getMessage());
            ret.put("id", -1);
        }
        return ret;
    }

    @RequestMapping("/standard/content")
    public Map<String, Object> getContent(@RequestParam("id") int id) {
        Map<String, Object> ret = new HashMap<>();
        StandardQuestionnaire standardQuestionnaire = standardService.getOneStandardQuestionnaire(id);
        if (standardQuestionnaire == null) {
            ret.put("status", false);
            ret.put("exec", "未找到相应问卷");
            ret.put("content", null);
            return ret;
        }
        List<StandardQuestion> standardQuestionList = standardQuestionnaire.getStandardQuestions();
        List<Map<String, Object>> content = new ArrayList<>();
        for (StandardQuestion standardQuestion : standardQuestionList) {
            Map<String, Object> question = new HashMap<>();
            String choiceStr = standardQuestion.getChoices();
            String[] choices = choiceStr.split(",");
            if (standardQuestion.isVisible())
                question.put("ptype", 1);
            else
                question.put("ptype", -1);
            question.put("choices", choices);
            question.put("id", standardQuestion.getId());
            question.put("limit", standardQuestion.getLimitation());
            question.put("type", standardQuestion.getType());
            question.put("title", standardQuestion.getQuestion());
            question.put("necessary", standardQuestion.isNecessary());
            question.put("numLimit", new ArrayList<>());
            question.put("description", standardQuestion.getDescription());
            content.add(question);
        }
        ret.put("title", standardQuestionnaire.getTitle());
        ret.put("status", true);
        ret.put("exec", "成功!");
        ret.put("content", content);
        ret.put("endTime", FormatConversion.DateToString(standardQuestionnaire.getEndTime()));
        ret.put("paperDescription", standardQuestionnaire.getDescription());
        ret.put("isSingle", standardQuestionnaire.isSingle());
        ret.put("showSeq", standardQuestionnaire.isShowSeq());
        return ret;
    }
}
