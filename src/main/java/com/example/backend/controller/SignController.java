package com.example.backend.controller;

import com.example.backend.entity.ApplyQuestion;
import com.example.backend.entity.ApplyQuestionnaire;
import com.example.backend.entity.User;
import com.example.backend.service.ApplyService;
import com.example.backend.service.UserService;
import com.example.backend.utils.FormatConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/sign")
public class SignController {
    @Autowired
    UserService userService;
    @Autowired
    ApplyService applyService;

    @PostMapping("/issue")
    public Map<String, Object> release(@RequestBody Map<String, Object> params) {
        int id = Integer.parseInt(params.get("id").toString());
        int status = Integer.parseInt(params.get("status").toString());
//        List<List<Object>> content=(List<List<Object>>)(params.get("content"));
        List<Map<String, Object>> content = (List<Map<String, Object>>) params.get("content");
        String ThisTitle = params.get("title").toString();
        int userId = Integer.parseInt(params.get("user_id").toString());
        String endTime = params.get("endTime").toString();
        Map<String, Object> ret = new HashMap<>();
        try {
            ApplyQuestionnaire applyQuestionnaire = applyService.getOneApplyQuestionnaire(id);
            int vQId;
            if (applyQuestionnaire == null) {
                applyQuestionnaire = new ApplyQuestionnaire();
                applyService.save(applyQuestionnaire);
                vQId = applyQuestionnaire.getId();
                applyQuestionnaire.setStartTime(new Date());//创建时间
            } else {
                applyService.save(applyQuestionnaire);
                vQId = id;
            }
            applyQuestionnaire.setFatherId(applyQuestionnaire.getId());//非复制问卷fatherId设置为自身id
            if (status == 1) applyQuestionnaire.setStatus(1);
            else applyQuestionnaire.setStatus(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            applyQuestionnaire.setEndTime(sdf.parse(endTime));
            applyQuestionnaire.setTitle(ThisTitle);
            applyQuestionnaire.setPlace(0);
            User user = userService.selectUserByUserId(userId);
            applyQuestionnaire.setUser(user);
            applyQuestionnaire.setPaperType("报名");
//            List<ApplyQuestion> applyQuestions = applyQuestionnaire.getApplyQuestions();
            List<ApplyQuestion> applyQuestions = new ArrayList<>();
            for (Map<String, Object> question : content) {
                int vid = (int) question.get("id");
                ApplyQuestion applyQuestion = applyService.getApplyById(vid);
                if (applyQuestion == null) {
                    applyQuestion = new ApplyQuestion();
                    //applyQuestionnaire.getApplyQuestions().add(applyQuestion);
                }
                List<String> choiceArray = (List<String>) (question.get("choices"));
                String choices = "";
                for (String choice : choiceArray) {
                    choices += choice + ",";
                }
                choices = choices.substring(0, choices.length() - 1);
                applyQuestion.setChoices(choices);
                String numLimits = "";
                if (question.get("type").toString().equals("单选报名题") ||
                        question.get("type").toString().equals("多选报名题")) {
                    List<Integer> numLimitArray = (List<Integer>) (question.get("numLimit"));
                    for (int numLimit : numLimitArray) {
                        numLimits += numLimit + ",";
                    }
                    numLimits = numLimits.substring(0, numLimits.length() - 1);
                }
                applyQuestion.setLimitNum(numLimits);
                if (question.get("ptype") != null) {
                    int ptype = (int) question.get("ptype");
                    applyQuestion.setVisible(ptype != -1);
                } else {
                    applyQuestion.setVisible(false);
                }
                applyQuestion.setLimitation((Integer) question.get("limit"));
                applyQuestion.setType((String) question.get("type"));
                applyQuestion.setQuestion((String) question.get("title"));
                applyQuestion.setNecessary((boolean) question.get("necessary"));
                applyQuestion.setDescription(question.get("description").toString());
                applyService.save(applyQuestion);
                applyQuestions.add(applyQuestion);
                System.out.println("1");//for test
            }
            System.out.println("2");//for test
            applyQuestionnaire.setApplyQuestions(applyQuestions);
            applyQuestionnaire.setDescription(params.get("paperDescription").toString());
            applyQuestionnaire.setSingle((Boolean) params.get("isSingle"));
            applyQuestionnaire.setShowSeq((Boolean) params.get("showSeq"));
            applyService.save(applyQuestionnaire);
            System.out.println("3");//for test
            vQId = applyQuestionnaire.getId();
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

    @RequestMapping("/content")
    public Map<String, Object> getContent(@RequestParam("id") int id) {
        Map<String, Object> ret = new HashMap<>();
        ApplyQuestionnaire applyQuestionnaire = applyService.getOneApplyQuestionnaire(id);
        if (applyQuestionnaire == null) {
            ret.put("status", false);
            ret.put("exec", "未找到相应问卷");
            ret.put("content", null);
            return ret;
        }
        List<ApplyQuestion> applyQuestionList = applyQuestionnaire.getApplyQuestions();
        List<Map<String, Object>> content = new ArrayList<>();
        for (ApplyQuestion applyQuestion : applyQuestionList) {
            Map<String, Object> question = new HashMap<>();
            String choiceStr = applyQuestion.getChoices();
            String[] choices = choiceStr.split(",");
            question.put("choices", choices);
            if (applyQuestion.isVisible())
                question.put("ptype", 0);
            else
                question.put("ptype", -1);
            if (applyQuestion.getType().equals("单选报名题") ||
                    applyQuestion.getType().equals("多选报名题")) {
                String numLimitStr = applyQuestion.getLimitNum();
                String[] numLimitArray = numLimitStr.split(",");
                List<Integer> numLimit = new ArrayList<>();
                for (String str : numLimitArray) {
                    numLimit.add(Integer.parseInt(str));
                }
                question.put("numLimit", numLimit);
            } else {
                question.put("numLimit", new ArrayList<>());
            }
            question.put("id", applyQuestion.getId());
            question.put("limit", applyQuestion.getLimitation());
            question.put("type", applyQuestion.getType());
            question.put("title", applyQuestion.getQuestion());
            question.put("necessary", applyQuestion.isNecessary());
            question.put("description", applyQuestion.getDescription());
            content.add(question);
        }
        ret.put("title", applyQuestionnaire.getTitle());
        ret.put("status", true);
        ret.put("endTime", FormatConversion.DateToString(applyQuestionnaire.getEndTime()));
        ret.put("exec", "成功!");
        ret.put("content", content);
        ret.put("paperDescription", applyQuestionnaire.getDescription());
        ret.put("isSingle", applyQuestionnaire.isSingle());
        ret.put("showSeq", applyQuestionnaire.isShowSeq());
        return ret;
    }
}
