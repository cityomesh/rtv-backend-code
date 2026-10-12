package com.rtvnewsnetwork.quiz.repository;


import com.rtvnewsnetwork.quiz.model.QuizFeed;
import com.rtvnewsnetwork.quiz.model.QuizModel;
import com.rtvnewsnetwork.quiz.model.QuizStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends MongoRepository<QuizModel, String> {

//    @Aggregation(pipeline = {
//            "{ $match: { status: 'ACTIVE' } }",
//            "{ $project: { _quizId: { $toString: '$_id' }, questions: 1, status: 1, rewardCoinsPerQuestion: 1, share: 1, shareUrl: 1, createdAt: 1, updatedAt: 1 } }",
//            "{ $lookup: { from: 'quizAnswer', localField: '_quizId', foreignField: 'quizId', as: 'userAnswers' } }",
//            "{ $addFields: { userAnswers: { $filter: { input: '$userAnswers', as: 'a', cond: { $eq: ['$$a.userId', ?0] } } } } }",
//            "{ $addFields: { answer: { $cond: { if: { $gt: [ { $size: '$userAnswers' }, 0 ] }, then: { $arrayElemAt: [ '$userAnswers', 0 ] }, else: null } } } }",
//            //  Add a "played" flag: 0 = not played, 1 = played
//            "{ $addFields: { played: { $cond: { if: { $gt: [ { $size: '$userAnswers' }, 0 ] }, then: 1, else: 0 } } } }",
//            // 👇 Sort first by "played" (unplayed first), then by createdAt desc
//            "{ $sort: { played: 1, createdAt: -1 } }",
//
//            "{ $project: { userAnswers: 0 } }"
//    })

    @Aggregation(pipeline = {
            "{ $match: { status: 'ACTIVE' } }",
            "{ $project: { _quizId: { $toString: '$_id' }, questions: 1, status: 1, rewardCoinsPerQuestion: 1, share: 1, shareUrl: 1, createdAt: 1, updatedAt: 1 } }",
            "{ $lookup: { from: 'quizAnswer', localField: '_quizId', foreignField: 'quizId', as: 'userAnswers' } }",
            "{ $addFields: { userAnswers: { $filter: { input: '$userAnswers', as: 'a', cond: { $eq: ['$$a.userId', ?0] } } } } }",
            "{ $addFields: { "
                    + "answer: { $cond: { if: { $gt: [ { $size: '$userAnswers' }, 0 ] }, then: { $arrayElemAt: [ '$userAnswers', 0 ] }, else: null } }, "
                    + "played: { $cond: { if: { $gt: [ { $size: '$userAnswers' }, 0 ] }, then: 1, else: 0 } } "
                    + "} }",
            "{ $sort: { played: 1, createdAt: -1 } }",   // unplayed first, then by newest
            "{ $project: { userAnswers: 0 } }"
    })
    List<QuizFeed> findQuiz(String userId, Pageable pageable);

    List<QuizModel> findAllByStatus(QuizStatus status, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { _id: ObjectId(?1) } }",
            "{ $project: { _quizId: { $toString: '$_id' }, questions: 1, status: 1, rewardCoinsPerQuestion: 1, share: 1, shareUrl: 1, createdAt: 1, updatedAt: 1 } }",
            "{ $lookup: { from: 'quizAnswer', localField: '_quizId', foreignField: 'quizId', as: 'answer' } }",
            "{ $unwind: { path: '$answer', preserveNullAndEmptyArrays: true } }",
            "{ $match: { 'answer.userId': ?0 } }"
    })
    QuizFeed findQuizById(String userId, String quizId);

    @Query("""
{
  "$or": [
    { "questions.question": { "$regex": ?0, "$options": "i" } },
    { "questions.options.label": { "$regex": ?0, "$options": "i" } }
  ]
}
""")
    Page<QuizModel> searchByKeyword(String keyword, Pageable pageable);

}

