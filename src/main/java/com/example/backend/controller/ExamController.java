package com.example.backend.controller;

import com.example.backend.entity.ExamQuestion;
import com.example.backend.entity.ExamQuestionnaire;
import com.example.backend.entity.Response;
import com.example.backend.entity.User;
import com.example.backend.service.ExamService;
import com.example.backend.service.ResponseService;
import com.example.backend.service.UserService;
import com.example.backend.utils.Encrypt;
import com.example.backend.utils.FormatConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/exam")
public class ExamController {
    @Autowired
    UserService userService;
    @Autowired
    ExamService examService;
    @Autowired
    ResponseService responseService;

    @PostMapping("/issue")
    public Map<String, Object> release(@RequestBody Map<String, Object> params) {
        int id = Integer.parseInt(params.get("id").toString());
        int status = Integer.parseInt(params.get("status").toString());
        List<Map<String, Object>> content = (List<Map<String, Object>>) params.get("question");
        String ThisTitle = params.get("title").toString();
        int userId = Integer.parseInt(params.get("user_id").toString());
        String endTime = params.get("endTime").toString();
        int duration = Integer.parseInt(params.get("duration").toString());
        Map<String, Object> ret = new HashMap<>();
        try {
            ExamQuestionnaire examQuestionnaire = examService.getOneExamQuestionnaire(id);
            int vQId;
            if (examQuestionnaire == null) {
                examQuestionnaire = new ExamQuestionnaire();
                examService.save(examQuestionnaire);
                vQId = examQuestionnaire.getId();
                examQuestionnaire.setStartTime(new Date());//创建时间
            } else {
                examService.save(examQuestionnaire);
                vQId = id;
            }
            examQuestionnaire.setFatherId(examQuestionnaire.getId());//非复制问卷fatherId设置为自身id
            if (status == 1) examQuestionnaire.setStatus(1);
            else examQuestionnaire.setStatus(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            examQuestionnaire.setEndTime(sdf.parse(endTime));
            examQuestionnaire.setTitle(ThisTitle);
            examQuestionnaire.setPlace(0);
            User user = userService.selectUserByUserId(userId);
            examQuestionnaire.setUser(user);
            examQuestionnaire.setPaperType("考试");
            examQuestionnaire.setDuration(duration);
            List<ExamQuestion> examQuestionList = new ArrayList<>();
            for (Map<String, Object> question : content) {
                int vid = Integer.parseInt(params.get("id").toString());
                ExamQuestion examQuestion = examService.getExamById(vid);
                if (examQuestion == null) {
                    examQuestion = new ExamQuestion();
                }
                List<String> choiceArray = (List<String>) (question.get("choices"));
                String choices = "";
                for (String choice : choiceArray) {
                    choices += choice + ",";
                }
                choices = choices.substring(0, choices.length() - 1);
                examQuestion.setChoices(choices);
                examQuestion.setType(question.get("type").toString());
                examQuestion.setScore(Integer.parseInt(question.get("score").toString()));
                examQuestion.setQuestion(question.get("title").toString());
                examQuestion.setCorrectAnswer(question.get("correctAnswer").toString());
                examQuestion.setNecessary((boolean) question.get("necessary"));
                examQuestion.setDescription(question.get("description").toString());
                examService.save(examQuestion);
                examQuestionList.add(examQuestion);
            }
            examQuestionnaire.setExamQuestions(examQuestionList);
            examQuestionnaire.setDescription(params.get("paperDescription").toString());
            examQuestionnaire.setSingle((Boolean) params.get("isSingle"));
            examQuestionnaire.setShowSeq((Boolean) params.get("showSeq"));
            examService.save(examQuestionnaire);
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
        ExamQuestionnaire examQuestionnaire = examService.getOneExamQuestionnaire(id);
        if (examQuestionnaire == null) {
            ret.put("status", false);
            ret.put("exec", "未找到相应问卷");
            ret.put("content", null);
            return ret;
        }
        List<ExamQuestion> examQuestionList = examQuestionnaire.getExamQuestions();
        List<Map<String, Object>> content = new ArrayList<>();
        for (ExamQuestion examQuestion : examQuestionList) {
            Map<String, Object> question = new HashMap<>();
            String choiceStr = examQuestion.getChoices();
            String[] choices = choiceStr.split(",");
            question.put("choices", choices);
            question.put("id", examQuestion.getId());
            question.put("type", examQuestion.getType());
            question.put("title", examQuestion.getQuestion());
            question.put("necessary", examQuestion.isNecessary());
            question.put("score", examQuestion.getScore());
            question.put("correctAnswer", examQuestion.getCorrectAnswer());
            question.put("description", examQuestion.getDescription());
            content.add(question);
        }
        ret.put("title", examQuestionnaire.getTitle());
        ret.put("status", true);
        ret.put("exec", "成功!");
        ret.put("endTime", FormatConversion.DateToString(examQuestionnaire.getEndTime()));
        ret.put("content", content);
        ret.put("paperDescription", examQuestionnaire.getDescription());
        ret.put("isSingle", examQuestionnaire.isSingle());
        ret.put("showSeq", examQuestionnaire.isShowSeq());
        return ret;
    }

    @GetMapping("/result")
    public Map<String, Object> getExamResult(@RequestParam("id") String stringId, @RequestParam("user_id") int userId) {
        Map<String, Object> ret = new HashMap<>();
        try {
            int id = Integer.parseInt(Encrypt.DecryptByAES(stringId));
            ExamQuestionnaire examQuestionnaire = examService.getOneExamQuestionnaire(id);
            List<ExamQuestion> examQuestions = examQuestionnaire.getExamQuestions();
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (ExamQuestion examQuestion : examQuestions) {
                Map<String, Object> map = new HashMap<>();
                List<String> correctAns = Arrays.asList(examQuestion.getCorrectAnswer().split(","));
                List<String> myAns = new ArrayList<>();
                List<Response> responses = examQuestion.getResponseList();
                long latest = 0L;
                System.out.println(responses.size());
                for (Response response : responses) {
                    if (userId == response.getUserId()) {
                        long time = examService.findWittenById(response.getIsWrittenId()).getSubmitTime().getTime();
                        System.out.println(time);
                        if (latest < time) {
                            myAns = new ArrayList<>();
                            myAns.add(response.getAnswer());
                            latest = time;
                        } else if (latest == time) {
                            myAns.add(response.getAnswer());
                        }
                    }
                }
                int correctNum = 0;
                if (correctAns.size() == myAns.size()) {
                    for (String ans : correctAns) {
                        boolean correct = false;
                        for (String myAnswer : myAns) {
                            if (myAnswer.equals(ans)) {
                                correct = true;
                                break;
                            }
                        }
                        if (correct) correctNum++;
                    }

                }
                if (correctNum == correctAns.size()) {
                    map.put("score", examQuestion.getScore());
                } else {
                    map.put("score", 0);
                }
                StringBuilder myAnsString = new StringBuilder();
                StringBuilder correctAnsString = new StringBuilder();
                for (String ans : myAns) {
                    myAnsString.append(ans);
                    if (!ans.equals(myAns.get(myAns.size() - 1))) myAnsString.append(",");
                }
                for (String ans : correctAns) {
                    correctAnsString.append(ans);
                    if (!ans.equals(correctAns.get(correctAns.size() - 1))) correctAnsString.append(",");
                }
                map.put("correctAnswer", correctAnsString.toString());
                map.put("myAnswer", myAnsString.toString());
                mapList.add(map);
            }
            ret.put("status", true);
            ret.put("exc", "");
            ret.put("answer", mapList);
        } catch (Exception e) {
            ret.put("status", false);
            ret.put("exc", e.toString());
        }
        return ret;
    }

}
