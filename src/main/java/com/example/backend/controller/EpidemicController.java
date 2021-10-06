package com.example.backend.controller;

import com.example.backend.entity.EpidemicQuestion;
import com.example.backend.entity.EpidemicQuestionnaire;
import com.example.backend.entity.User;
import com.example.backend.service.EpidemicService;
import com.example.backend.service.UserService;
import com.example.backend.utils.FormatConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class EpidemicController {
    @Autowired
    UserService userService;
    @Autowired
    EpidemicService epidemicService;

    @PostMapping("/epidemic/issue")
    public Map<String, Object> release(@RequestBody Map<String, Object> params) {
        int id = Integer.parseInt(params.get("id").toString());
        int status = Integer.parseInt(params.get("status").toString());
        List<Map<String, Object>> content = (List<Map<String, Object>>) params.get("content");
        String ThisTitle = params.get("title").toString();
        int userId = Integer.parseInt(params.get("user_id").toString());
        String endTime = params.get("endTime").toString();
        Map<String, Object> ret = new HashMap<>();
        try {
            EpidemicQuestionnaire epidemicQuestionnaire = epidemicService.getOneEpidemicQuestionnaire(id);
            int vQId;
            if (epidemicQuestionnaire == null) {
                epidemicQuestionnaire = new EpidemicQuestionnaire();
                epidemicService.save(epidemicQuestionnaire);
                vQId = epidemicQuestionnaire.getId();
                epidemicQuestionnaire.setStartTime(new Date());//创建时间
            } else {
                epidemicService.save(epidemicQuestionnaire);
                vQId = id;
            }
            epidemicQuestionnaire.setFatherId(epidemicQuestionnaire.getId());//非复制问卷fatherId设置为自身id
            if (status == 1) epidemicQuestionnaire.setStatus(1);
            else epidemicQuestionnaire.setStatus(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            epidemicQuestionnaire.setEndTime(sdf.parse(endTime));
            epidemicQuestionnaire.setTitle(ThisTitle);
            epidemicQuestionnaire.setPlace(0);
            User user = userService.selectUserByUserId(userId);
            epidemicQuestionnaire.setUser(user);
            epidemicQuestionnaire.setPaperType("打卡");
            List<EpidemicQuestion> epidemicQuestions = new ArrayList<>();
            for (Map<String, Object> question : content) {
                int vid = Integer.parseInt(params.get("id").toString());
                EpidemicQuestion epidemicQuestion = epidemicService.getEpidemicById(vid);
                if (epidemicQuestion == null) {
                    epidemicQuestion = new EpidemicQuestion();
                }
                List<String> choiceArray = (List<String>) (question.get("choices"));
                String choices = "";
                for (String choice : choiceArray) {
                    choices += choice + ",";
                }
                choices = choices.substring(0, choices.length() - 1);
                epidemicQuestion.setChoices(choices);
                int ptype = (int) question.get("ptype");
                epidemicQuestion.setVisible(ptype != -1);
                epidemicQuestion.setLimitation((Integer) question.get("limit"));
                epidemicQuestion.setType((String) question.get("type"));
                epidemicQuestion.setQuestion((String) question.get("title"));
                epidemicQuestion.setNecessary((boolean) question.get("necessary"));
                epidemicQuestion.setDescription(question.get("description").toString());
                epidemicService.save(epidemicQuestion);
                epidemicQuestions.add(epidemicQuestion);
            }
            epidemicQuestionnaire.setEpidemicQuestions(epidemicQuestions);
            epidemicQuestionnaire.setDescription(params.get("paperDescription").toString());
            epidemicQuestionnaire.setSingle((Boolean) params.get("isSingle"));
            epidemicQuestionnaire.setShowSeq((Boolean) params.get("showSeq"));
            epidemicService.save(epidemicQuestionnaire);
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

    @RequestMapping("/epidemic/content")
    public Map<String, Object> getContent(@RequestParam("id") int id) {
        Map<String, Object> ret = new HashMap<>();
        EpidemicQuestionnaire epidemicQuestionnaire = epidemicService.getOneEpidemicQuestionnaire(id);
        if (epidemicQuestionnaire == null) {
            ret.put("status", false);
            ret.put("exec", "未找到相应问卷");
            ret.put("content", null);
            return ret;
        }
        List<EpidemicQuestion> epidemicQuestionList = epidemicQuestionnaire.getEpidemicQuestions();
        List<Map<String, Object>> content = new ArrayList<>();
        for (EpidemicQuestion epidemicQuestion : epidemicQuestionList) {
            Map<String, Object> question = new HashMap<>();
            String choiceStr = epidemicQuestion.getChoices();
            String[] choices = choiceStr.split(",");
            if (epidemicQuestion.isVisible())
                question.put("ptype", 1);
            else
                question.put("ptype", -1);
            question.put("choices", choices);
            question.put("id", epidemicQuestion.getId());
            question.put("limit", epidemicQuestion.getLimitation());
            question.put("type", epidemicQuestion.getType());
            question.put("title", epidemicQuestion.getQuestion());
            question.put("necessary", epidemicQuestion.isNecessary());
            question.put("numLimit", new ArrayList<>());
            question.put("description", epidemicQuestion.getDescription());
            content.add(question);
        }
        ret.put("title", epidemicQuestionnaire.getTitle());
        ret.put("status", true);
        ret.put("exec", "成功!");
        ret.put("endTime", FormatConversion.DateToString(epidemicQuestionnaire.getEndTime()));
        ret.put("content", content);
        ret.put("paperDescription", epidemicQuestionnaire.getDescription());
        ret.put("isSingle", epidemicQuestionnaire.isSingle());
        ret.put("showSeq", epidemicQuestionnaire.isShowSeq());
        return ret;
    }
}
