package com.rtvnewsnetwork.poll.service;

import com.rtvnewsnetwork.poll.model.PollModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PollService {

    Map<String, Integer> calculatePercentage(String pollId);

    PollModel addChoices(String userId, String pollId, String choiceId);

    List<PollModel> getPollFeed(String userId, int page, int size);

    PollModel addPoll(PollModel pollModel);

    PollModel updatePoll(String id, PollModel pollModel);
    void deletePoll(String id);
    Optional<PollModel> getPollById(String id);

    List<PollModel> getPolls(int page, int size);
    List<PollModel> getPollFeedSearch(String userId, int page, int size, String query);


}
