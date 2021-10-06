package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.entity.VoteQuestion;
import com.example.backend.entity.VoteQuestionnaire;
import com.example.backend.service.UserService;
import com.example.backend.service.VoteService;
import com.example.backend.utils.FormatConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class VoteController {
    @Autowired
    UserService userService;
    @Autowired
    VoteService voteService;

    @PostMapping("/vote/issue")
    public Map<String, Object> release(@RequestBody Map<String, Object> params) {
        int id = Integer.parseInt(params.get("id").toString());
        int status = Integer.parseInt(params.get("status").toString());
        List<Map<String, Object>> content = (List<Map<String, Object>>) params.get("content");
        String ThisTitle = params.get("title").toString();
        int userId = Integer.parseInt(params.get("user_id").toString());
        String endTime = params.get("endTime").toString();
        Map<String, Object> ret = new HashMap<>();
        try {
            VoteQuestionnaire voteQuestionnaire = voteService.getOneVoteQuestionnaire(id);
            int vQId;
            if (voteQuestionnaire == null) {
                voteQuestionnaire = new VoteQuestionnaire();
                voteService.save(voteQuestionnaire);
                vQId = voteQuestionnaire.getId();
                voteQuestionnaire.setStartTime(new Date());//创建时间
            } else {
                voteService.save(voteQuestionnaire);
                vQId = id;
            }
            voteQuestionnaire.setFatherId(voteQuestionnaire.getId());//非复制问卷fatherId设置为自身id
            if (status == 1) voteQuestionnaire.setStatus(1);
            else voteQuestionnaire.setStatus(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            voteQuestionnaire.setEndTime(sdf.parse(endTime));
            voteQuestionnaire.setTitle(ThisTitle);
            voteQuestionnaire.setPlace(0);
            User user = userService.selectUserByUserId(userId);
            voteQuestionnaire.setUser(user);
            voteQuestionnaire.setPaperType("投票");
//            List<VoteQuestion> voteQuestionList = voteQuestionnaire.getVoteQuestions();
            List<VoteQuestion> voteQuestionList = new ArrayList<>();
            for (Map<String, Object> question : content) {
                int vid = (int) question.get("id");
                VoteQuestion voteQuestion = voteService.getVoteById(vid);
                if (voteQuestion == null) {
                    voteQuestion = new VoteQuestion();
//                    voteQuestionnaire.getVoteQuestions().add(voteQuestion);
                }
                List<String> choiceArray = (List<String>) (question.get("choices"));
                String choices = "";
                for (String choice : choiceArray) {
                    choices += choice + ",";
                }
                choices = choices.substring(0, choices.length() - 1);
                voteQuestion.setChoices(choices);
                if (question.get("ptype") != null) {
                    int ptype = (int) question.get("ptype");
                    voteQuestion.setVisible(ptype != -1);
                } else {
                    voteQuestion.setVisible(false);
                }
                voteQuestion.setLimitation((Integer) question.get("limit"));
                voteQuestion.setType((String) question.get("type"));
                voteQuestion.setQuestion((String) question.get("title"));
                voteQuestion.setNecessary((boolean) question.get("necessary"));
                voteQuestion.setDescription(question.get("description").toString());
                voteService.save(voteQuestion);
                voteQuestionList.add(voteQuestion);
            }
            voteQuestionnaire.setVoteQuestions(voteQuestionList);
            voteQuestionnaire.setDescription(params.get("paperDescription").toString());
            voteQuestionnaire.setSingle((Boolean) params.get("isSingle"));
            voteQuestionnaire.setShowSeq((Boolean) params.get("showSeq"));
            voteService.save(voteQuestionnaire);
//
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

    @RequestMapping("/vote/content")
    public Map<String, Object> getContent(@RequestParam("id") int id) {
        Map<String, Object> ret = new HashMap<>();
        VoteQuestionnaire voteQuestionnaire = voteService.getOneVoteQuestionnaire(id);
        if (voteQuestionnaire == null) {
            ret.put("status", false);
            ret.put("exec", "未找到相应问卷");
            ret.put("content", null);
            return ret;
        }
        List<VoteQuestion> voteQuestionList = voteQuestionnaire.getVoteQuestions();
        List<Map<String, Object>> content = new ArrayList<>();
        for (VoteQuestion voteQuestion : voteQuestionList) {
            Map<String, Object> question = new HashMap<>();
            String choiceStr = voteQuestion.getChoices();
            String[] choices = choiceStr.split(",");
            if (voteQuestion.isVisible())
                question.put("ptype", 1);
            else
                question.put("ptype", -1);
            question.put("choices", choices);
            question.put("id", voteQuestion.getId());
            question.put("limit", voteQuestion.getLimitation());
            question.put("type", voteQuestion.getType());
            question.put("title", voteQuestion.getQuestion());
            question.put("necessary", voteQuestion.isNecessary());
            question.put("numLimit", new ArrayList<>());
            question.put("description", voteQuestion.getDescription());
            content.add(question);
        }
        ret.put("title", voteQuestionnaire.getTitle());
        ret.put("status", true);
        ret.put("exec", "成功!");
        ret.put("content", content);
        ret.put("endTime", FormatConversion.DateToString(voteQuestionnaire.getEndTime()));
        ret.put("paperDescription", voteQuestionnaire.getDescription());
        ret.put("isSingle", voteQuestionnaire.isSingle());
        ret.put("showSeq", voteQuestionnaire.isShowSeq());
        return ret;
    }
}
