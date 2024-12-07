package com.learning.quizapp.service;

import com.learning.quizapp.dao.QuestionDao;
import com.learning.quizapp.dao.QuizDao;
import com.learning.quizapp.model.Question;
import com.learning.quizapp.model.QuestionWrapper;
import com.learning.quizapp.model.Quiz;
import com.learning.quizapp.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    @Autowired
    QuizDao quizDao;

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQ);
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(int id) {
        Optional<Quiz> quiz = quizDao.findById(id);
        List<Question> questionFromDB = quiz.get().getQuestions();
        List<QuestionWrapper> questionForUser = new ArrayList<>();
        for (Question q : questionFromDB) {
            QuestionWrapper qw = new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4());
            questionForUser.add(qw);
        }

        return new ResponseEntity<>(questionForUser, HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        Quiz quiz = quizDao.findById(id).get();
        List<Question> questions = quiz.getQuestions();
        int numberOfCorrectResponses = 0;
        int questionIterator = 0;
        for (Response response : responses) {
            if (response.getResponse().equals(questions.get(questionIterator).getRightAnswer())) {
                numberOfCorrectResponses++;
            }
            questionIterator++;
        }
        return new ResponseEntity<>(numberOfCorrectResponses, HttpStatus.OK);
    }
}
