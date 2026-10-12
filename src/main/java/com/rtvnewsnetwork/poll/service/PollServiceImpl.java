package com.rtvnewsnetwork.poll.service;

import com.rtvnewsnetwork.common.exception.ResourceNotFoundException;

import com.rtvnewsnetwork.event.service.EventPublisher;
import com.rtvnewsnetwork.event.service.EventService;
import com.rtvnewsnetwork.poll.model.*;
import com.rtvnewsnetwork.poll.repository.PollRepository;
import com.rtvnewsnetwork.poll.repository.PollResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class PollServiceImpl implements PollService ,EventPublisher {

    private final PollRepository pollRepository;
    private final PollResponseRepository pollResponseRepository;
    private final EventService eventService;

    public PollServiceImpl(PollRepository pollRepository,
                           PollResponseRepository pollResponseRepository,
                           EventService eventService) {
        this.pollRepository = pollRepository;
        this.pollResponseRepository = pollResponseRepository;
        this.eventService = eventService;
    }
    @Override
    public Map<String, Integer> calculatePercentage(String pollId) {
        List<PollResponse> pollResponses = pollResponseRepository.findAllByPollId(pollId);
        double totalResponses = pollResponses.size();
        Map<String, Integer> optionPercentages = new HashMap<>();

        Map<String, Long> optionResponseCounts = pollResponses.stream()
                .collect(Collectors.groupingBy(PollResponse::getSelectedChoiceId, Collectors.counting()));

        for (Map.Entry<String, Long> entry : optionResponseCounts.entrySet()) {
            String choiceId = entry.getKey();
            long responseCount = entry.getValue();
            int percentage = (int) Math.round((responseCount / totalResponses) * 100);
            optionPercentages.put(choiceId, percentage);
        }

        return optionPercentages;
    }

    private PollModel updateChoices(PollModel poll, String choiceId, Map<String, Integer> optionPercentages) {
        for (Choice choice : poll.getQuestion().getOptions()) {
            choice.setMarked(choice.getId().equals(choiceId));
            choice.setOptionPercentage(optionPercentages.getOrDefault(choice.getId(), 0));
        }
        return poll;
    }

    @Override
    public PollModel addChoices(String userId, String pollId, String choiceId) {
        PollModel poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not Found"));

        ResponseStats responseStats = poll.getResponseStats();
        if (responseStats == null) {
            responseStats = new ResponseStats();
        }
        responseStats.setResponseCount(responseStats.getResponseCount() + 1);
        poll.setResponseStats(responseStats);

        PollResponse pollResponse = new PollResponse(null, userId, pollId, choiceId, null, null);
        pollResponseRepository.save(pollResponse);

        Map<String, Integer> optionPercentages = calculatePercentage(pollId);
        PollModel updatedPoll = updateChoices(poll, choiceId, optionPercentages);

        return pollRepository.save(updatedPoll);
    }

    private List<PollModel> updateMarkedResponse(List<AggregatedPollModel> pollList) {
        List<PollModel> updatedPollList = new ArrayList<>();

        for (AggregatedPollModel model : pollList) {
            for (Choice option : model.getQuestion().getOptions()) {
                boolean marked = Arrays.stream(model.getAnswer())
                        .anyMatch(response -> response.getSelectedChoiceId().equals(option.getId()));
                option.setMarked(marked);
            }

            PollModel pollModel = new PollModel(
                    model.getId(),
                    model.getQuestion(),
                    model.getResponseStats(),
                    model.getExpiryDate(),
                    model.getCreatedAt(),
                    model.getUpdatedAt()
            );
            updatedPollList.add(pollModel);
        }

        return updatedPollList;
    }

    @Override
    public List<PollModel> getPollFeed(String userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Order.desc("updatedAt")));
        List<AggregatedPollModel> pollList = pollRepository.getAggregatedPoll(userId, pageRequest);
        return updateMarkedResponse(pollList);
    }

    @Override
    public PollModel addPoll(PollModel pollModel) {
        PollModel response = pollRepository.save(pollModel);

        Map<String, String> eventData = new HashMap<>();
        eventData.put("title", "A New Poll Is Live!🗳️");
        eventData.put("description", "Share your opinion and vote now!");
        eventData.put("path", "/polls/"+response.getId());

//        produceEventToKafka(EventType.NEW_POST, null, KafkaTopicConfig.NOTIFICATIONS_CHANNEL, eventData);

     //   String deepLink = branchLinkService.createBranchLink(response.getId(), "poll");

//        if (deepLink != null) {
//            pollModel.setShareUrl(deepLink);
//        } else {
//            System.out.println("Failed to create deep link for post ID: " + response.getId());
//        }

//        pollModel.setId(response.getId());
//        pollRepository.save(pollModel);
        return pollModel;
    }

    @Override
    public PollModel updatePoll(String id, PollModel pollModel) {
        return pollRepository.findById(id).map(existing -> {
            // update fields
            existing.setQuestion(pollModel.getQuestion());
            existing.setResponseStats(pollModel.getResponseStats());
            existing.setExpiryDate(pollModel.getExpiryDate());
            return pollRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Poll not found with id: " + id));
    }

    @Override
    public void deletePoll(String id) {
        if (!pollRepository.existsById(id)) {
            throw new RuntimeException("Poll not found with id: " + id);
        }
        pollRepository.deleteById(id);
    }

    @Override
    public Optional<PollModel> getPollById(String id) {
        return pollRepository.findById(id);
    }

    @Override
    public List<PollModel> getPolls(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("updatedAt")));
        return pollRepository.findAll(pageRequest).toList();
    }

    @Override
    public EventService getEventService() {
        return eventService;
    }

    @Override
    public List<PollModel> getPollFeedSearch(String userId, int page, int size, String query) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        if (query != null && !query.isBlank()) {
            return pollRepository.searchByTitleOrLabel(query, pageRequest).toList();
        }
        List<AggregatedPollModel> pollList = pollRepository.getAggregatedPoll(userId, pageRequest);
        return updateMarkedResponse(pollList);
    }

}

