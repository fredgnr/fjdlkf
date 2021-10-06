package com.example.backend.controller;

import com.example.backend.entity.Question;
import com.example.backend.entity.Questionnaire;
import com.example.backend.entity.Response;
import com.example.backend.service.*;
import com.example.backend.utils.DateCompare;
import com.example.backend.utils.Encrypt;
import com.example.backend.entity.*;
import com.example.backend.service.*;
import com.example.backend.utils.FormatConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class AnswerController {
    @Autowired
    ResponseService responseService;
    @Autowired
    QuestionnaireService questionnaireService;
    @Autowired
    WrittenService writtenService;
    @Autowired
    StandardService standardService;
    @Autowired
    ApplyService applyService;
    @Autowired
    VoteService voteService;
    @Autowired
    QuestionService questionService;
    @Autowired
    ExamService examService;
    @Autowired
    EpidemicService epidemicService;

    @PostMapping("/answer/submit")
    public Map<String, Object> submitAnswer(@RequestBody Map<String, Object> params) {
        Map<String, Object> ret = new HashMap<>();
        String stringId = params.get("id").toString();
        int id = Integer.parseInt(Encrypt.DecryptByAES(stringId));
        int user_id = Integer.parseInt(params.get("user_id").toString());
        List<Map<String, Object>> answerList = (List<Map<String, Object>>) params.get("answerList");
        Questionnaire questionnaire = questionnaireService.findQuestionnaireById(id);
        Date endTime = questionnaire.getEndTime();
        Date nowTime = new Date();
        if (DateCompare.is1after2(nowTime, endTime)) {
            questionnaire.setStatus(2);
            questionnaireService.save(questionnaire);
            ret.put("status", false);
            ret.put("exec", "提交失败,考试时间已结束!");
            return ret;
        }
        if (questionnaire.isSingle() && writtenService.countIsWritten(id, user_id) > 0) {
            ret.put("status", false);
            ret.put("exec", "该问卷仅能填写一次!");
            return ret;
        }
        if (questionnaire.getPaperType().equals("报名")) {
            try {
                applyService.submitApplyAnswer(questionnaire, user_id, answerList);
            } catch (Exception e) {
                ret.put("status", false);
                ret.put("exec", "报名失败，选项余量不足");
                return ret;
            }
        } else {
            IsWritten isWritten = new IsWritten();
            isWritten.setQuestionnaireId(id);
            isWritten.setUserId(user_id);
            isWritten.setSubmitTime(new Date());
            writtenService.save(isWritten);
            for (Map<String, Object> answer : answerList) {
                int questionId = Integer.parseInt(answer.get("id").toString());
                Question question = questionService.getById(questionId);
                String options = answer.get("answer").toString();
                String type = answer.get("type").toString();
                List<Response> responses = question.getResponseList();
                if (type.equals("多选题") || type.equals("多选报名题") || type.equals("多选投票题")) {
                    String[] optionArr = options.split(",");
                    for (String option : optionArr) {
                        Response response = new Response();
                        response.setAnswer(option);
                        response.setQuestionId(questionId);
                        response.setType(type);
                        response.setUserId(user_id);
                        response.setIsWrittenId(isWritten.getId());
//                    response.setQuestion(question);
                        responses.add(response);
                        responseService.save(response);
                    }
                } else {
                    Response response = new Response();
                    response.setAnswer(options);
                    response.setQuestionId(questionId);
                    response.setType(type);
                    response.setUserId(user_id);
                    response.setIsWrittenId(isWritten.getId());
                    responses.add(response);
                    responseService.save(response);
                }
                question.setResponseList(responses);
                questionService.save(question);
            }
        }
        questionnaire.setCount(questionnaire.getCount() + 1);
        questionnaireService.save(questionnaire);
        ret.put("status", true);
        ret.put("exec", "提交成功!");
        return ret;
    }

    @GetMapping("/answer/get")
    public Map<String, Object> getContent(@RequestParam("id") String stringId,
                                          @RequestParam("user_id") int userId) {
        Map<String, Object> ret = new HashMap<>();
        List<Map<String, Object>> question = new ArrayList<>();
        int id = Integer.parseInt(Encrypt.DecryptByAES(stringId));
        StandardQuestionnaire standardQuestionnaire = standardService.getOneStandardQuestionnaire(id);
        VoteQuestionnaire voteQuestionnaire = voteService.getOneVoteQuestionnaire(id);
        ApplyQuestionnaire applyQuestionnaire = applyService.getOneApplyQuestionnaire(id);
        ExamQuestionnaire examQuestionnaire = examService.getOneExamQuestionnaire(id);
        EpidemicQuestionnaire epidemicQuestionnaire = epidemicService.getOneEpidemicQuestionnaire(id);
        if (standardQuestionnaire != null) {
            List<StandardQuestion> standardQuestionList = standardQuestionnaire.getStandardQuestions();
            for (StandardQuestion standardQuestion : standardQuestionList) {
                Map<String, Object> q = new HashMap<>();
                String choiceStr = standardQuestion.getChoices();
                String[] choices = choiceStr.split(",");
                q.put("choices", choices);
                q.put("id", standardQuestion.getId());
                q.put("limit", standardQuestion.getLimitation());
                q.put("type", standardQuestion.getType());
                q.put("title", standardQuestion.getQuestion());
                q.put("ptype", -1);
                q.put("necessary", standardQuestion.isNecessary());
                q.put("voteNum", "");
                q.put("selectNum", "");
                q.put("numLimit", "");
                q.put("description", standardQuestion.getDescription());
                question.add(q);
            }
            Date nowTime = new Date();
            Date endTime = standardQuestionnaire.getEndTime();
            if (DateCompare.is1after2(nowTime, endTime)) {
                standardQuestionnaire.setStatus(2);
                standardService.save(standardQuestionnaire);
            }
            ret.put("paperType", standardQuestionnaire.getPaperType());
            ret.put("now", FormatConversion.DateToString(nowTime));
            ret.put("title", standardQuestionnaire.getTitle());
            ret.put("pstatus", standardQuestionnaire.getStatus());
            ret.put("endTime", FormatConversion.DateToString(endTime));
            ret.put("paperDescription", standardQuestionnaire.getDescription());
            ret.put("isSingle", standardQuestionnaire.isSingle());
            ret.put("showSeq", standardQuestionnaire.isShowSeq());
        } else if (applyQuestionnaire != null) {
            List<ApplyQuestion> applyQuestionList = applyQuestionnaire.getApplyQuestions();
            for (ApplyQuestion applyQuestion : applyQuestionList) {
                Map<String, Object> q = new HashMap<>();
                String choiceStr = applyQuestion.getChoices();
                String[] choices = choiceStr.split(",");
                q.put("choices", choices);
                q.put("id", applyQuestion.getId());
                q.put("limit", applyQuestion.getLimitation());
                String type = applyQuestion.getType();
                q.put("type", type);
                if (type.equals("单选报名题") || type.equals("多选报名题")) {
                    String numLimitStr = applyQuestion.getLimitNum();
                    String[] numLimitArray = numLimitStr.split(",");
                    List<Integer> numLimit = new ArrayList<>();
                    for (String str : numLimitArray) {
                        numLimit.add(Integer.parseInt(str));
                    }
                    q.put("numLimit", numLimit);
                    List<Integer> selectNum = new ArrayList<>();
                    for (String choice : choices) {
                        selectNum.add(responseService.countByOption(choice, applyQuestion.getId()));
                    }
                    q.put("selectNum", selectNum);
                } else {
                    q.put("selectNum", "");
                    q.put("numLimit", "");
                }
                if (applyQuestion.isVisible())
                    q.put("ptype", 0);
                else
                    q.put("ptype", -1);
                q.put("necessary", applyQuestion.isNecessary());
                q.put("title", applyQuestion.getQuestion());
                q.put("voteNum", "");
                q.put("description", applyQuestion.getDescription());
                question.add(q);
            }
            Date nowTime = new Date();
            Date endTime = applyQuestionnaire.getEndTime();
            if (DateCompare.is1after2(nowTime, endTime)) {
                applyQuestionnaire.setStatus(2);
                applyService.save(applyQuestionnaire);
            }
            ret.put("paperType", applyQuestionnaire.getPaperType());
            ret.put("now", FormatConversion.DateToString(nowTime));
            ret.put("title", applyQuestionnaire.getTitle());
            ret.put("pstatus", applyQuestionnaire.getStatus());
            ret.put("endTime", FormatConversion.DateToString(applyQuestionnaire.getEndTime()));
            ret.put("paperDescription", applyQuestionnaire.getDescription());
            ret.put("isSingle", applyQuestionnaire.isSingle());
            ret.put("showSeq", applyQuestionnaire.isShowSeq());
        } else if (voteQuestionnaire != null) {
            List<VoteQuestion> voteQuestionList = voteQuestionnaire.getVoteQuestions();
            for (VoteQuestion voteQuestion : voteQuestionList) {
                Map<String, Object> q = new HashMap<>();
                String choiceStr = voteQuestion.getChoices();
                String[] choices = choiceStr.split(",");
                q.put("choices", choices);
                q.put("id", voteQuestion.getId());
                q.put("limit", voteQuestion.getLimitation());
                String type = voteQuestion.getType();
                q.put("type", type);
                if (type.equals("单选投票题") || type.equals("多选投票题")) {
                    List<Integer> voteNum = new ArrayList<>();
                    for (String choice : choices) {
                        voteNum.add(responseService.countByOption(choice, voteQuestion.getId()));
                    }
                    q.put("voteNum", voteNum);
                } else {
                    q.put("voteNum", "");
                }
                if (voteQuestion.isVisible())
                    q.put("ptype", 1);
                else
                    q.put("ptype", -1);
                q.put("necessary", voteQuestion.isNecessary());
                q.put("title", voteQuestion.getQuestion());
                q.put("numLimit", "");
                q.put("selectNum", "");
                q.put("description", voteQuestion.getDescription());
                question.add(q);
            }
            Date nowTime = new Date();
            Date endTime = voteQuestionnaire.getEndTime();
            if (DateCompare.is1after2(nowTime, endTime)) {
                voteQuestionnaire.setStatus(2);
                voteService.save(voteQuestionnaire);
            }
            ret.put("paperType", voteQuestionnaire.getPaperType());
            ret.put("now", FormatConversion.DateToString(nowTime));
            ret.put("title", voteQuestionnaire.getTitle());
            ret.put("pstatus", voteQuestionnaire.getStatus());
            ret.put("endTime", FormatConversion.DateToString(endTime));
            ret.put("paperDescription", voteQuestionnaire.getDescription());
            ret.put("isSingle", voteQuestionnaire.isSingle());
            ret.put("showSeq", voteQuestionnaire.isShowSeq());
        } else if (examQuestionnaire != null) {
            List<ExamQuestion> examQuestionList = examQuestionnaire.getExamQuestions();
            for (ExamQuestion examQuestion : examQuestionList) {
                Map<String, Object> q = new HashMap<>();
                String choiceStr = examQuestion.getChoices();
                String[] choices = choiceStr.split(",");
                q.put("choices", choices);
                q.put("id", examQuestion.getId());
                q.put("limit", examQuestion.getLimitation());
                q.put("type", examQuestion.getType());
                q.put("title", examQuestion.getQuestion());
                q.put("ptype", -1);
                q.put("necessary", examQuestion.isNecessary());
                q.put("voteNum", "");
                q.put("selectNum", "");
                q.put("numLimit", "");
                q.put("description", examQuestion.getDescription());
                question.add(q);
            }
            Date nowTime = new Date();
            Date endTime = examQuestionnaire.getEndTime();
            if (DateCompare.is1after2(nowTime, endTime)) {
                examQuestionnaire.setStatus(2);
                examService.save(examQuestionnaire);
            }
            ret.put("paperType", examQuestionnaire.getPaperType());
            ret.put("now", FormatConversion.DateToString(nowTime));
            ret.put("title", examQuestionnaire.getTitle());
            ret.put("pstatus", examQuestionnaire.getStatus());
            ret.put("endTime", FormatConversion.DateToString(endTime));
            ret.put("paperDescription", examQuestionnaire.getDescription());
            ret.put("isSingle", examQuestionnaire.isSingle());
            ret.put("showSeq", examQuestionnaire.isShowSeq());
        } else if (epidemicQuestionnaire != null) {
            List<EpidemicQuestion> epidemicQuestionList = epidemicQuestionnaire.getEpidemicQuestions();
            for (EpidemicQuestion epidemicQuestion : epidemicQuestionList) {
                Map<String, Object> q = new HashMap<>();
                String choiceStr = epidemicQuestion.getChoices();
                String[] choices = choiceStr.split(",");
                q.put("choices", choices);
                q.put("id", epidemicQuestion.getId());
                q.put("limit", epidemicQuestion.getLimitation());
                q.put("type", epidemicQuestion.getType());
                q.put("title", epidemicQuestion.getQuestion());
                q.put("ptype", -1);
                q.put("necessary", epidemicQuestion.isNecessary());
                q.put("voteNum", "");
                q.put("selectNum", "");
                q.put("numLimit", "");
                q.put("description", epidemicQuestion.getDescription());
                question.add(q);
            }
            Date nowTime = new Date();
            Date endTime = epidemicQuestionnaire.getEndTime();
            if (DateCompare.is1after2(nowTime, endTime)) {
                epidemicQuestionnaire.setStatus(2);
                epidemicService.save(epidemicQuestionnaire);
            }
            ret.put("paperType", epidemicQuestionnaire.getPaperType());
            ret.put("now", FormatConversion.DateToString(nowTime));
            ret.put("title", epidemicQuestionnaire.getTitle());
            ret.put("pstatus", epidemicQuestionnaire.getStatus());
            ret.put("endTime", FormatConversion.DateToString(endTime));
            ret.put("paperDescription", epidemicQuestionnaire.getDescription());
            ret.put("isSingle", epidemicQuestionnaire.isSingle());
            ret.put("showSeq", epidemicQuestionnaire.isShowSeq());
        } else {
            ret.put("status", false);
            ret.put("exec", "无效问卷类型");
            ret.put("question", "");
            ret.put("title", "");
        }
        int count = writtenService.countIsWritten(id, userId);
        ret.put("count", count);
        ret.put("status", true);
        ret.put("exec", "获取问卷内容成功!");
        ret.put("question", question);
        return ret;
    }
}
