package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.service.*;
import com.example.backend.utils.Encrypt;
import com.example.backend.utils.FormatConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class QuestionnaireController {
    @Autowired
    QuestionnaireService questionnaireService;
    @Autowired
    ApplyService applyService;
    @Autowired
    VoteService voteService;
    @Autowired
    StandardService standardService;
    @Autowired
    ExamService examService;
    @Autowired
    EpidemicService epidemicService;
    @Autowired
    UserService userService;
    @Autowired
    WrittenService writtenService;
    @Autowired
    ResponseService responseService;

    @GetMapping("/manage/get_all")
    public Map<String, Object> GetAllQuestionnaire(@RequestParam("user_id") int userId) {
        Map<String, Object> ret = new HashMap<>();
        try {
            List<Questionnaire> questionnaires = questionnaireService.getAllQuestionnaire(userId);
            List<Map<String, Object>> userQuestionnaires = new ArrayList<>();
            for (Questionnaire questionnaire : questionnaires) {
                Map<String, Object> map = new HashMap<>();
                map.put("randomId", Encrypt.EncryptByAES(String.valueOf(questionnaire.getId())));
                map.put("id", questionnaire.getId());
                map.put("begin_time", FormatConversion.DateToString(questionnaire.getStartTime()));
                map.put("end_time", FormatConversion.DateToString(questionnaire.getEndTime()));
                map.put("head", questionnaire.getTitle());
                map.put("status", questionnaire.getStatus());
                map.put("answer_number", questionnaire.getCount());
                map.put("place", questionnaire.getPlace());
                map.put("paperType", questionnaire.getPaperType());
                userQuestionnaires.add(map);
            }
            ret.put("user_questionnaire", userQuestionnaires);
            ret.put("success", true);
        } catch (Exception e) {
            ret.put("exc", e.toString());
            ret.put("success", false);
        }
        return ret;
    }

    @PostMapping("/manage/copy")
    public Map<String, Object> CopyQuestionnaire(@RequestBody Map<String, Object> params) {

        Map<String, Object> ret = new HashMap<>();
        try {
            int QuestionnaireId = Integer.parseInt(params.get("id").toString());
            int user_id = Integer.parseInt(params.get("user_id").toString());
            ApplyQuestionnaire theApplyQuestionnaire = applyService.getOneApplyQuestionnaire(QuestionnaireId);
            EpidemicQuestionnaire theEpidemicQuestionnaire = epidemicService.getOneEpidemicQuestionnaire(QuestionnaireId);
            ExamQuestionnaire theExamQuestionnaire = examService.getOneExamQuestionnaire(QuestionnaireId);
            StandardQuestionnaire theStandardQuestionnaire = standardService.getOneStandardQuestionnaire(QuestionnaireId);
            VoteQuestionnaire theVoteQuestionnaire = voteService.getOneVoteQuestionnaire(QuestionnaireId);
            User user = userService.selectUserByUserId(user_id);
            if (theApplyQuestionnaire != null) {
                ApplyQuestionnaire applyQuestionnaire = new ApplyQuestionnaire();
                applyQuestionnaire.setUser(user);
                applyQuestionnaire.setStatus(0);
                applyQuestionnaire.setStartTime(new Date());//创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                applyQuestionnaire.setEndTime(theApplyQuestionnaire.getEndTime());
                applyQuestionnaire.setDescription(theApplyQuestionnaire.getDescription());
                applyQuestionnaire.setPlace(theApplyQuestionnaire.getPlace());
                if (theApplyQuestionnaire.getFatherId() == 0) throw new Exception("fatherId获取失败");
                applyQuestionnaire.setFatherId(theApplyQuestionnaire.getFatherId());
                String copyname = "副本" + (questionnaireService.getCopyCount(theApplyQuestionnaire.getFatherId()));
                String newTitle = theApplyQuestionnaire.getTitle() + copyname;
                applyQuestionnaire.setTitle(newTitle);
                applyQuestionnaire.setCount(0);
                applyQuestionnaire.setPaperType(theApplyQuestionnaire.getPaperType());
//                applyQuestionnaire.setApplyLimit(theApplyQuestionnaire.getApplyLimit());
                List<ApplyQuestion> questions = theApplyQuestionnaire.getApplyQuestions();
                List<ApplyQuestion> newQuestions = new ArrayList<>();
                for (ApplyQuestion question : questions) {
                    ApplyQuestion newApplyQuestion = new ApplyQuestion();
                    newApplyQuestion.setQuestion(question.getQuestion());
                    newApplyQuestion.setNecessary(question.isNecessary());
                    newApplyQuestion.setChoices(question.getChoices());
                    newApplyQuestion.setLimitation(question.getLimitation());
                    newApplyQuestion.setType(question.getType());
                    newApplyQuestion.setDescription(question.getDescription());
                    newApplyQuestion.setLimitNum(question.getLimitNum());
//                    newApplyQuestion.setAllowance(question.getLimitNum());//选项余量人数恢复为初始值
                    newApplyQuestion.setVisible(question.isVisible());
                    applyService.save(newApplyQuestion);
                    newQuestions.add(newApplyQuestion);
                }
                applyQuestionnaire.setApplyQuestions(newQuestions);
                applyService.save(applyQuestionnaire);
            } else if (theEpidemicQuestionnaire != null) {
                EpidemicQuestionnaire epidemicQuestionnaire = new EpidemicQuestionnaire();
                epidemicQuestionnaire.setUser(user);
                epidemicQuestionnaire.setStatus(0);
                epidemicQuestionnaire.setStartTime(new Date());//创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                epidemicQuestionnaire.setEndTime(theEpidemicQuestionnaire.getEndTime());
                epidemicQuestionnaire.setPlace(theEpidemicQuestionnaire.getPlace());
                epidemicQuestionnaire.setDescription(theEpidemicQuestionnaire.getDescription());
                if (theEpidemicQuestionnaire.getFatherId() == 0) throw new Exception("fatherId获取失败");
                epidemicQuestionnaire.setFatherId(theEpidemicQuestionnaire.getFatherId());
                String copyname = "副本" + (questionnaireService.getCopyCount(theEpidemicQuestionnaire.getFatherId()));
                String newName = copyname + theEpidemicQuestionnaire.getTitle();
                epidemicQuestionnaire.setTitle(newName);
                epidemicQuestionnaire.setCount(0);
                epidemicQuestionnaire.setPaperType(theEpidemicQuestionnaire.getPaperType());
                List<EpidemicQuestion> questions = theEpidemicQuestionnaire.getEpidemicQuestions();
                List<EpidemicQuestion> newQuestions = new ArrayList<>();
                for (EpidemicQuestion question : questions) {
                    EpidemicQuestion newEpidemicQuestion = new EpidemicQuestion();
                    newEpidemicQuestion.setQuestion(question.getQuestion());
                    newEpidemicQuestion.setNecessary(question.isNecessary());
                    newEpidemicQuestion.setChoices(question.getChoices());
                    newEpidemicQuestion.setLimitation(question.getLimitation());
                    newEpidemicQuestion.setType(question.getType());
                    newEpidemicQuestion.setDescription(question.getDescription());
                    newEpidemicQuestion.setVisible(question.isVisible());
                    epidemicService.save(newEpidemicQuestion);
                    newQuestions.add(newEpidemicQuestion);
                }
                epidemicQuestionnaire.setEpidemicQuestions(newQuestions);
                epidemicService.save(epidemicQuestionnaire);
            } else if (theExamQuestionnaire != null) {
                ExamQuestionnaire examQuestionnaire = new ExamQuestionnaire();
                examQuestionnaire.setUser(user);
                examQuestionnaire.setStatus(0);
                examQuestionnaire.setStartTime(new Date());//创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                examQuestionnaire.setEndTime(theExamQuestionnaire.getEndTime());
                examQuestionnaire.setPlace(theExamQuestionnaire.getPlace());
                examQuestionnaire.setDescription(theExamQuestionnaire.getDescription());
                if (theExamQuestionnaire.getFatherId() == 0) throw new Exception("fatherId获取失败");
                examQuestionnaire.setFatherId(theExamQuestionnaire.getFatherId());
                String copyname = "副本" + (questionnaireService.getCopyCount(theExamQuestionnaire.getFatherId()));
                String newName = theExamQuestionnaire.getTitle() + copyname;
                examQuestionnaire.setTitle(newName);
                examQuestionnaire.setCount(0);
                examQuestionnaire.setPaperType(theExamQuestionnaire.getPaperType());
                examQuestionnaire.setCanSeeScore(theExamQuestionnaire.isCanSeeScore());
                examQuestionnaire.setTotalScore(theExamQuestionnaire.getTotalScore());
                examQuestionnaire.setDuration(theExamQuestionnaire.getDuration());
                List<ExamQuestion> questions = theExamQuestionnaire.getExamQuestions();
                List<ExamQuestion> newQuestions = new ArrayList<>();
                for (ExamQuestion question : questions) {
                    ExamQuestion newExamQuestion = new ExamQuestion();
                    newExamQuestion.setQuestion(question.getQuestion());
                    newExamQuestion.setNecessary(question.isNecessary());
                    newExamQuestion.setChoices(question.getChoices());
                    newExamQuestion.setLimitation(question.getLimitation());
                    newExamQuestion.setType(question.getType());
                    newExamQuestion.setDescription(question.getDescription());
                    newExamQuestion.setType(question.getType());
                    newExamQuestion.setScore(question.getScore());
                    newExamQuestion.setCorrectAnswer(question.getCorrectAnswer());
                    examService.save(newExamQuestion);
                    newQuestions.add(newExamQuestion);
                }
                examQuestionnaire.setExamQuestions(newQuestions);
                examService.save(examQuestionnaire);
            } else if (theStandardQuestionnaire != null) {
                StandardQuestionnaire standardQuestionnaire = new StandardQuestionnaire();
                standardQuestionnaire.setUser(user);
                standardQuestionnaire.setStatus(0);
                standardQuestionnaire.setStartTime(new Date());//创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                standardQuestionnaire.setEndTime(theStandardQuestionnaire.getEndTime());
                standardQuestionnaire.setPlace(theStandardQuestionnaire.getPlace());
                standardQuestionnaire.setDescription(theStandardQuestionnaire.getDescription());
                if (theStandardQuestionnaire.getFatherId() == 0) throw new Exception("fatherId获取失败");
                standardQuestionnaire.setFatherId(theStandardQuestionnaire.getFatherId());
                String copyname = "副本" + (questionnaireService.getCopyCount(theStandardQuestionnaire.getFatherId()));
                String newName = theStandardQuestionnaire.getTitle() + copyname;
                standardQuestionnaire.setTitle(newName);
                standardQuestionnaire.setCount(0);
                standardQuestionnaire.setPaperType(theStandardQuestionnaire.getPaperType());
                List<StandardQuestion> questions = theStandardQuestionnaire.getStandardQuestions();
                List<StandardQuestion> newQuestions = new ArrayList<>();
                for (StandardQuestion question : questions) {
                    StandardQuestion newStandardQuestion = new StandardQuestion();
                    newStandardQuestion.setQuestion(question.getQuestion());
                    newStandardQuestion.setNecessary(question.isNecessary());
                    newStandardQuestion.setChoices(question.getChoices());
                    newStandardQuestion.setLimitation(question.getLimitation());
                    newStandardQuestion.setType(question.getType());
                    newStandardQuestion.setDescription(question.getDescription());
                    newStandardQuestion.setVisible(question.isVisible());
                    standardService.save(newStandardQuestion);
                    newQuestions.add(newStandardQuestion);
                }
                standardQuestionnaire.setStandardQuestions(newQuestions);
                standardService.save(standardQuestionnaire);
            } else {
                VoteQuestionnaire voteQuestionnaire = new VoteQuestionnaire();
                voteQuestionnaire.setUser(user);
                voteQuestionnaire.setStatus(0);
                voteQuestionnaire.setStartTime(new Date());//创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                voteQuestionnaire.setEndTime(theVoteQuestionnaire.getEndTime());
                voteQuestionnaire.setPlace(theVoteQuestionnaire.getPlace());
                voteQuestionnaire.setDescription(theVoteQuestionnaire.getDescription());
                if (theVoteQuestionnaire.getFatherId() == 0) throw new Exception("fatherId获取失败");
                voteQuestionnaire.setFatherId(theVoteQuestionnaire.getFatherId());
                String copyname = "副本" + (questionnaireService.getCopyCount(theVoteQuestionnaire.getFatherId()));
                String newName = theVoteQuestionnaire.getTitle() + copyname;
                voteQuestionnaire.setTitle(newName);
                voteQuestionnaire.setCount(0);
                voteQuestionnaire.setPaperType(theVoteQuestionnaire.getPaperType());
                List<VoteQuestion> questions = theVoteQuestionnaire.getVoteQuestions();
                List<VoteQuestion> newQuestions = new ArrayList<>();
                for (VoteQuestion question : questions) {
                    VoteQuestion newVoteQuestion = new VoteQuestion();
                    newVoteQuestion.setQuestion(question.getQuestion());
                    newVoteQuestion.setNecessary(question.isNecessary());
                    newVoteQuestion.setChoices(question.getChoices());
                    newVoteQuestion.setLimitation(question.getLimitation());
                    newVoteQuestion.setType(question.getType());
                    newVoteQuestion.setDescription(question.getDescription());
                    newVoteQuestion.setVisible(question.isVisible());
                    voteService.save(newVoteQuestion);
                    newQuestions.add(newVoteQuestion);
                }
                voteQuestionnaire.setVoteQuestions(newQuestions);
                voteService.save(voteQuestionnaire);
            }
            ret.put("exe", "无异常");
            ret.put("success", true);
        } catch (Exception e) {
            ret.put("exe", e.toString());
            ret.put("success", false);
        }
        return ret;
    }

    @PostMapping("/manage/change_place")
    public Map<String, Object> ReplaceQuestionnaire(@RequestParam("id") int questionnaireId, @RequestParam("place") int place) {
        Map<String, Object> ret = new HashMap<>();
        try {
            Questionnaire questionnaire = questionnaireService.findQuestionnaireById(questionnaireId);
            questionnaire.setPlace(place);
            questionnaireService.save(questionnaire);
            ret.put("success", true);
        } catch (Exception e) {
            ret.put("exc", e.toString());
            ret.put("success", false);
        }
        return ret;
    }

    @PostMapping("/manage/stop")
    public Map<String, Object> StopAQuestionnaire(@RequestParam("id") int questionnaireId) {
        Map<String, Object> ret = new HashMap<>();
        try {
            Questionnaire questionnaire = questionnaireService.findQuestionnaireById(questionnaireId);
            questionnaire.setStatus(2);
            questionnaireService.save(questionnaire);
            ret.put("success", true);
        } catch (Exception e) {
            ret.put("exc", e.toString());
            ret.put("success", false);
        }
        return ret;
    }

    @PostMapping("/manage/begin")
    public Map<String, Object> BeginAQuestionnaire(@RequestParam("id") int questionnaireId) {
        Map<String, Object> ret = new HashMap<>();
        try {
            Questionnaire questionnaire = questionnaireService.findQuestionnaireById(questionnaireId);
            questionnaire.setStatus(1);
            questionnaire.setIssueTime(new Date());
            questionnaireService.save(questionnaire);
            ret.put("success", true);
        } catch (Exception e) {
            ret.put("exc", e.toString());
            ret.put("success", false);
        }
        return ret;
    }

    @PostMapping("/manage/delete")
    public Map<String, Object> DeleteAQuestionnaire(@RequestParam("id") int questionnaireId) {
        Map<String, Object> ret = new HashMap<>();
        try {
            Questionnaire questionnaire = questionnaireService.findQuestionnaireById(questionnaireId);
            questionnaireService.delete(questionnaire);
            ret.put("success", true);
        } catch (Exception e) {
            ret.put("exc", e.toString());
            ret.put("success", false);
        }
        return ret;
    }

    @GetMapping("/datapreview/getdata")
    public Map<String, Object> PreviewQuestionnaireData(@RequestParam("id") int questionnaireId) {
        Map<String, Object> ret = new HashMap<>();
        try {
            StandardQuestionnaire standardQuestionnaire = standardService.getOneStandardQuestionnaire(questionnaireId);
            VoteQuestionnaire voteQuestionnaire = voteService.getOneVoteQuestionnaire(questionnaireId);
            ApplyQuestionnaire applyQuestionnaire = applyService.getOneApplyQuestionnaire(questionnaireId);
            ExamQuestionnaire examQuestionnaire = examService.getOneExamQuestionnaire(questionnaireId);
            EpidemicQuestionnaire epidemicQuestionnaire = epidemicService.getOneEpidemicQuestionnaire(questionnaireId);
            if (standardQuestionnaire != null) {
                ret.put("title", standardQuestionnaire.getTitle());
                ret.put("answerCount", standardQuestionnaire.getCount());
                List<StandardQuestion> standardQuestions = standardQuestionnaire.getStandardQuestions();
                List<Map<String, Object>> questions = new ArrayList<>();
                for (StandardQuestion standardQuestion : standardQuestions) {//遍历该问卷所有题目
                    Map<String, Object> map = new HashMap<>();
                    String choice = standardQuestion.getChoices();
                    if (choice.equals("无")) map.put("choices", choice);
                    else {
                        String[] choiceArray = choice.split(",");
                        map.put("choices", choiceArray);
                    }
                    int limit = standardQuestion.getLimitation();
                    int id = standardQuestion.getId();
                    if (limit == -1) map.put("limit", "");
                    else map.put("limit", limit);
                    map.put("type", standardQuestion.getType());
                    map.put("title", standardQuestion.getQuestion());
                    map.put("necessary", standardQuestion.isNecessary());
                    List<Response> responses = standardQuestion.getResponseList();
                    List<String> gapList = new ArrayList<>();
                    int[] num = new int[100];
                    String type = standardQuestion.getType();
                    boolean flag = type.equals("填空题") || type.equals("打分题") || type.equals("文本题") || type.equals("时间选择器") || type.equals("定位题");
                    for (Response oneResponse : responses) {
                        String answers = oneResponse.getAnswer();
                        if (flag) gapList.add(answers);  //处理填空
                        else {
                            String[] choiceArray = choice.split(",");
                            String[] answerList = answers.split(",");
                            for (int i = 0; i < answerList.length; i++) {  //处理选择
                                for (int j = 0; j < choiceArray.length; j++) {
                                    if (answerList[i].equals(choiceArray[j])) {
                                        num[j]++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (flag) {
                        map.put("answers", gapList);
                    } else {
                        List<String> choiceAnswerSet = new ArrayList<>();
                        String[] choiceArray = choice.split(",");
                        for (int i = 0; i < choiceArray.length; i++) choiceAnswerSet.add(String.valueOf(num[i]));
                        map.put("answers", choiceAnswerSet);
                    }
                    questions.add(map);
                }
                ret.put("questions", questions);
            } else if (voteQuestionnaire != null) {
                ret.put("title", voteQuestionnaire.getTitle());
                ret.put("answerCount", voteQuestionnaire.getCount());
                List<VoteQuestion> voteQuestions = voteQuestionnaire.getVoteQuestions();
                List<Map<String, Object>> questions = new ArrayList<>();
                for (VoteQuestion voteQuestion : voteQuestions) {//遍历该问卷所有题目
                    Map<String, Object> map = new HashMap<>();
                    String choice = voteQuestion.getChoices();
                    if (choice.equals("无")) map.put("choices", choice);
                    else {
                        String[] choiceArray = choice.split(",");
                        map.put("choices", choiceArray);
                    }
                    int limit = voteQuestion.getLimitation();
                    int id = voteQuestion.getId();
                    if (limit == -1) map.put("limit", "");
                    else map.put("limit", limit);
                    map.put("type", voteQuestion.getType());
                    map.put("title", voteQuestion.getQuestion());
                    map.put("necessary", voteQuestion.isNecessary());
                    List<Response> responses = voteQuestion.getResponseList();
                    List<String> gapList = new ArrayList<>();
                    int[] num = new int[100];
                    String type = voteQuestion.getType();
                    boolean flag = type.equals("填空题") || type.equals("打分题") || type.equals("文本题") || type.equals("时间选择器") || type.equals("定位题");
                    for (Response oneResponse : responses) {
                        String answers = oneResponse.getAnswer();
                        if (flag) gapList.add(answers);  //处理填空
                        else {
                            String[] choiceArray = choice.split(",");
                            String[] answerList = answers.split(",");
                            for (int i = 0; i < answerList.length; i++) {  //处理选择
                                for (int j = 0; j < choiceArray.length; j++) {
                                    if (answerList[i].equals(choiceArray[j])) {
                                        num[j]++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (flag) {
                        map.put("answers", gapList);
                    } else {
                        List<String> choiceAnswerSet = new ArrayList<>();
                        String[] choiceArray = choice.split(",");
                        for (int i = 0; i < choiceArray.length; i++) choiceAnswerSet.add(String.valueOf(num[i]));
                        map.put("answers", choiceAnswerSet);
                    }
                    questions.add(map);
                }
                ret.put("questions", questions);
            } else if (applyQuestionnaire != null) {
                ret.put("title", applyQuestionnaire.getTitle());
                ret.put("answerCount", applyQuestionnaire.getCount());
                List<ApplyQuestion> applyQuestions = applyQuestionnaire.getApplyQuestions();
                List<Map<String, Object>> questions = new ArrayList<>();
                for (ApplyQuestion applyQuestion : applyQuestions) {//遍历该问卷所有题目
                    Map<String, Object> map = new HashMap<>();
                    String choice = applyQuestion.getChoices();
                    if (choice.equals("无")) map.put("choices", choice);
                    else {
                        String[] choiceArray = choice.split(",");
                        map.put("choices", choiceArray);
                    }
                    int limit = applyQuestion.getLimitation();
                    int id = applyQuestion.getId();
                    if (limit == -1) map.put("limit", "");
                    else map.put("limit", limit);
                    map.put("type", applyQuestion.getType());
                    map.put("title", applyQuestion.getQuestion());
                    map.put("necessary", applyQuestion.isNecessary());
                    List<Response> responses = applyQuestion.getResponseList();
                    List<String> gapList = new ArrayList<>();
                    int[] num = new int[100];
                    String type = applyQuestion.getType();
                    boolean flag = type.equals("填空题") || type.equals("打分题") || type.equals("文本题") || type.equals("时间选择器") || type.equals("定位题");
                    for (Response oneResponse : responses) {
                        String answers = oneResponse.getAnswer();
                        if (flag) gapList.add(answers);  //处理填空
                        else {
                            String[] choiceArray = choice.split(",");
                            String[] answerList = answers.split(",");
                            for (int i = 0; i < answerList.length; i++) {  //处理选择
                                for (int j = 0; j < choiceArray.length; j++) {
                                    if (answerList[i].equals(choiceArray[j])) {
                                        num[j]++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (flag) {
                        map.put("answers", gapList);
                    } else {
                        List<String> choiceAnswerSet = new ArrayList<>();
                        String[] choiceArray = choice.split(",");
                        for (int i = 0; i < choiceArray.length; i++) choiceAnswerSet.add(String.valueOf(num[i]));
                        map.put("answers", choiceAnswerSet);
                    }
                    questions.add(map);
                }
                ret.put("questions", questions);
            } else if (examQuestionnaire != null) {
                ret.put("title", examQuestionnaire.getTitle());
                ret.put("answerCount", examQuestionnaire.getCount());
                List<ExamQuestion> examQuestions = examQuestionnaire.getExamQuestions();
                List<Map<String, Object>> questions = new ArrayList<>();
                for (ExamQuestion examQuestion : examQuestions) {//遍历该问卷所有题目
                    Map<String, Object> map = new HashMap<>();
                    String choice = examQuestion.getChoices();
                    if (choice.equals("无")) map.put("choices", choice);
                    else {
                        String[] choiceArray = choice.split(",");
                        map.put("choices", choiceArray);
                    }
                    int limit = examQuestion.getLimitation();
                    int id = examQuestion.getId();
                    if (limit == -1) map.put("limit", "");
                    else map.put("limit", limit);
                    map.put("type", examQuestion.getType());
                    map.put("title", examQuestion.getQuestion());
                    map.put("necessary", examQuestion.isNecessary());
                    List<Response> responses = examQuestion.getResponseList();
                    List<String> gapList = new ArrayList<>();
                    int[] num = new int[100];
                    String type = examQuestion.getType();
                    boolean flag = type.equals("填空题") || type.equals("打分题") || type.equals("文本题") || type.equals("时间选择器") || type.equals("定位题");
                    for (Response oneResponse : responses) {
                        String answers = oneResponse.getAnswer();
                        if (flag) gapList.add(answers);  //处理填空
                        else {
                            String[] choiceArray = choice.split(",");
                            String[] answerList = answers.split(",");
                            for (int i = 0; i < answerList.length; i++) {  //处理选择
                                for (int j = 0; j < choiceArray.length; j++) {
                                    if (answerList[i].equals(choiceArray[j])) {
                                        num[j]++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (flag) {
                        map.put("answers", gapList);
                    } else {
                        List<String> choiceAnswerSet = new ArrayList<>();
                        String[] choiceArray = choice.split(",");
                        for (int i = 0; i < choiceArray.length; i++) choiceAnswerSet.add(String.valueOf(num[i]));
                        map.put("answers", choiceAnswerSet);
                    }
                    questions.add(map);
                }
                ret.put("questions", questions);
            } else if (epidemicQuestionnaire != null) {
                ret.put("title", epidemicQuestionnaire.getTitle());
                ret.put("answerCount", epidemicQuestionnaire.getCount());
                List<EpidemicQuestion> epidemicQuestions = epidemicQuestionnaire.getEpidemicQuestions();
                List<Map<String, Object>> questions = new ArrayList<>();
                for (EpidemicQuestion epidemicQuestion : epidemicQuestions) {//遍历该问卷所有题目
                    Map<String, Object> map = new HashMap<>();
                    String choice = epidemicQuestion.getChoices();
                    if (choice.equals("无")) map.put("choices", choice);
                    else {
                        String[] choiceArray = choice.split(",");
                        map.put("choices", choiceArray);
                    }
                    int limit = epidemicQuestion.getLimitation();
                    int id = epidemicQuestion.getId();
                    if (limit == -1) map.put("limit", "");
                    else map.put("limit", limit);
                    map.put("type", epidemicQuestion.getType());
                    map.put("title", epidemicQuestion.getQuestion());
                    map.put("necessary", epidemicQuestion.isNecessary());
                    List<Response> responses = epidemicQuestion.getResponseList();
                    List<String> gapList = new ArrayList<>();
                    int[] num = new int[100];
                    String type = epidemicQuestion.getType();
                    boolean flag = type.equals("填空题") || type.equals("打分题") || type.equals("文本题") || type.equals("时间选择器") || type.equals("定位题");
                    for (Response oneResponse : responses) {
                        String answers = oneResponse.getAnswer();
                        if (flag) gapList.add(answers);  //处理填空
                        else {
                            String[] choiceArray = choice.split(",");
                            String[] answerList = answers.split(",");
                            for (int i = 0; i < answerList.length; i++) {  //处理选择
                                for (int j = 0; j < choiceArray.length; j++) {
                                    if (answerList[i].equals(choiceArray[j])) {
                                        num[j]++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (flag) {
                        map.put("answers", gapList);
                    } else {
                        List<String> choiceAnswerSet = new ArrayList<>();
                        String[] choiceArray = choice.split(",");
                        for (int i = 0; i < choiceArray.length; i++) choiceAnswerSet.add(String.valueOf(num[i]));
                        map.put("answers", choiceAnswerSet);
                    }
                    questions.add(map);
                }
                ret.put("questions", questions);
            } else throw new Exception("questionnaire not found");
            ret.put("success", true);
        } catch (Exception e) {
            ret.put("exc", e.toString());
            ret.put("success", false);
        }
        return ret;
    }

    @PostMapping("/manage/move")
    public Map<String, Object> MoveAQuestionnaire(@RequestParam("id") int questionnaireId) {
        Map<String, Object> ret = new HashMap<>();
        try {
            ApplyQuestionnaire theApplyQuestionnaire = applyService.getOneApplyQuestionnaire(questionnaireId);
            EpidemicQuestionnaire theEpidemicQuestionnaire = epidemicService.getOneEpidemicQuestionnaire(questionnaireId);
            ExamQuestionnaire theExamQuestionnaire = examService.getOneExamQuestionnaire(questionnaireId);
            StandardQuestionnaire theStandardQuestionnaire = standardService.getOneStandardQuestionnaire(questionnaireId);
            VoteQuestionnaire theVoteQuestionnaire = voteService.getOneVoteQuestionnaire(questionnaireId);
            Questionnaire questionnaire = questionnaireService.findQuestionnaireById(questionnaireId);
            User user = questionnaire.getUser();
            if (theApplyQuestionnaire != null) {
                ApplyQuestionnaire applyQuestionnaire = new ApplyQuestionnaire();
                applyQuestionnaire.setUser(user);
                applyQuestionnaire.setStatus(theApplyQuestionnaire.getStatus());
                applyQuestionnaire.setStartTime(new Date());//创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                applyQuestionnaire.setEndTime(theApplyQuestionnaire.getEndTime());
                applyQuestionnaire.setDescription(theApplyQuestionnaire.getDescription());
                applyQuestionnaire.setPlace(theApplyQuestionnaire.getPlace());
                applyQuestionnaire.setFatherId(theApplyQuestionnaire.getFatherId());
                applyQuestionnaire.setTitle(theApplyQuestionnaire.getTitle());
                applyQuestionnaire.setCount(theApplyQuestionnaire.getCount());
                applyQuestionnaire.setPaperType(theApplyQuestionnaire.getPaperType());
//                applyQuestionnaire.setApplyLimit(theApplyQuestionnaire.getApplyLimit());
                applyQuestionnaire.setApplyQuestions(theApplyQuestionnaire.getApplyQuestions());
                applyService.save(applyQuestionnaire);
            } else if (theEpidemicQuestionnaire != null) {
                EpidemicQuestionnaire epidemicQuestionnaire = new EpidemicQuestionnaire();
                epidemicQuestionnaire.setUser(user);
                epidemicQuestionnaire.setStatus(theEpidemicQuestionnaire.getStatus());
                epidemicQuestionnaire.setStartTime(new Date());//创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                epidemicQuestionnaire.setEndTime(theEpidemicQuestionnaire.getEndTime());
                epidemicQuestionnaire.setPlace(theEpidemicQuestionnaire.getPlace());
                epidemicQuestionnaire.setDescription(theEpidemicQuestionnaire.getDescription());
                epidemicQuestionnaire.setFatherId(theEpidemicQuestionnaire.getFatherId());
                epidemicQuestionnaire.setTitle(theEpidemicQuestionnaire.getTitle());
                epidemicQuestionnaire.setCount(theEpidemicQuestionnaire.getCount());
                epidemicQuestionnaire.setPaperType(theEpidemicQuestionnaire.getPaperType());
                epidemicQuestionnaire.setEpidemicQuestions(theEpidemicQuestionnaire.getEpidemicQuestions());
                epidemicService.save(epidemicQuestionnaire);
            } else if (theExamQuestionnaire != null) {
                ExamQuestionnaire examQuestionnaire = new ExamQuestionnaire();
                examQuestionnaire.setUser(user);
                examQuestionnaire.setStatus(theExamQuestionnaire.getStatus());
                examQuestionnaire.setStartTime(new Date());//创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                examQuestionnaire.setEndTime(theExamQuestionnaire.getEndTime());
                examQuestionnaire.setPlace(theExamQuestionnaire.getPlace());
                examQuestionnaire.setDescription(theExamQuestionnaire.getDescription());
                examQuestionnaire.setFatherId(theExamQuestionnaire.getFatherId());
                examQuestionnaire.setTitle(theExamQuestionnaire.getTitle());
                examQuestionnaire.setCount(theExamQuestionnaire.getCount());
                examQuestionnaire.setPaperType(theExamQuestionnaire.getPaperType());
                examQuestionnaire.setCanSeeScore(theExamQuestionnaire.isCanSeeScore());
                examQuestionnaire.setTotalScore(theExamQuestionnaire.getTotalScore());
                examQuestionnaire.setDuration(theExamQuestionnaire.getDuration());
                examQuestionnaire.setExamQuestions(theExamQuestionnaire.getExamQuestions());
                examService.save(examQuestionnaire);
            } else if (theStandardQuestionnaire != null) {
                StandardQuestionnaire standardQuestionnaire = new StandardQuestionnaire();
                standardQuestionnaire.setUser(user);
                standardQuestionnaire.setStatus(theStandardQuestionnaire.getStatus());
                standardQuestionnaire.setStartTime(new Date());//创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                standardQuestionnaire.setEndTime(theStandardQuestionnaire.getEndTime());
                standardQuestionnaire.setPlace(theStandardQuestionnaire.getPlace());
                standardQuestionnaire.setDescription(theStandardQuestionnaire.getDescription());
                standardQuestionnaire.setFatherId(theStandardQuestionnaire.getFatherId());
                standardQuestionnaire.setTitle(theStandardQuestionnaire.getTitle());
                standardQuestionnaire.setCount(theStandardQuestionnaire.getCount());
                standardQuestionnaire.setPaperType(theStandardQuestionnaire.getPaperType());
                standardQuestionnaire.setStandardQuestions(theStandardQuestionnaire.getStandardQuestions());
                standardService.save(standardQuestionnaire);
            } else {
                VoteQuestionnaire voteQuestionnaire = new VoteQuestionnaire();
                voteQuestionnaire.setUser(user);
                voteQuestionnaire.setStatus(theVoteQuestionnaire.getStatus());
                voteQuestionnaire.setStartTime(new Date());//创建时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                voteQuestionnaire.setEndTime(theVoteQuestionnaire.getEndTime());
                voteQuestionnaire.setPlace(theVoteQuestionnaire.getPlace());
                voteQuestionnaire.setDescription(theVoteQuestionnaire.getDescription());
                voteQuestionnaire.setFatherId(theVoteQuestionnaire.getFatherId());
                voteQuestionnaire.setTitle(theVoteQuestionnaire.getTitle());
                voteQuestionnaire.setCount(theVoteQuestionnaire.getCount());
                voteQuestionnaire.setPaperType(theVoteQuestionnaire.getPaperType());
                voteQuestionnaire.setVoteQuestions(theVoteQuestionnaire.getVoteQuestions());
                voteService.save(voteQuestionnaire);
            }
            questionnaireService.delete(questionnaire);
            ret.put("success", true);
        } catch (Exception e) {
            ret.put("exc", e.toString());
            ret.put("success", false);
        }
        return ret;
    }

    @GetMapping("/result/export")
    public Map<String, Object> exportResult(@RequestParam("id") int questionnaireId) {
        Map<String, Object> ret = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        Questionnaire questionnaire = questionnaireService.findQuestionnaireById(questionnaireId);
        List<IsWritten> isWrittenList = writtenService.getAllByQuestionnaire(questionnaireId);
        List<Question> questions = new ArrayList<>();
        if (questionnaire.getPaperType().equals("调查")) {
            questions.addAll(questionnaire.getStandardQuestions());
        } else if (questionnaire.getPaperType().equals("报名")) {
            questions.addAll(questionnaire.getApplyQuestions());
        } else if (questionnaire.getPaperType().equals("投票")) {
            questions.addAll(questionnaire.getVoteQuestions());
        } else if (questionnaire.getPaperType().equals("考试")) {
            questions.addAll(questionnaire.getExamQuestions());
        } else if (questionnaire.getPaperType().equals("打卡")) {
            questions.addAll(questionnaire.getEpidemicQuestions());
        } else {
            ret.put("status", false);
            ret.put("exec", "无效问卷类型");
            ret.put("data", "");
            return ret;
        }
        for (IsWritten isWritten : isWrittenList) {
            int userId = isWritten.getUserId();
            User user = userService.selectUserByUserId(userId);
            Map<String, Object> oneData = new HashMap<>();
            List<Map<String, Object>> questionAnswers = new ArrayList<>();
            oneData.put("phone", user.getPhone());
            oneData.put("title", questionnaire.getTitle());
            oneData.put("submitTime", FormatConversion.DateToString(isWritten.getSubmitTime()));
            for (Question question : questions) {
                Map<String, Object> questionAnswer = new HashMap<>();
                String answerStr = "";
                List<Response> responses = responseService.getByQuestionAndIsWritten(question.getId(), isWritten.getId());
                for (Response response : responses) {
                    answerStr += response.getAnswer() + ",";
                }
                answerStr = answerStr.substring(0, answerStr.length() - 1);
                questionAnswer.put("title", question.getQuestion());
                questionAnswer.put("answer", answerStr);
                questionAnswers.add(questionAnswer);
            }
            oneData.put("question", questionAnswers);
            data.add(oneData);
        }
        ret.put("data", data);
        ret.put("status", true);
        ret.put("exec", "导出结果成功");
        ret.put("data", data);
        return ret;
    }
}
