package uk.ac.bham.teamproject.service;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bham.teamproject.domain.ChatMessage;
import uk.ac.bham.teamproject.repository.ChatMessageRepository;

/**
 * Service Implementation for managing {@link ChatMessage}.
 */
@Service
@Transactional
public class ChatMessageService {

    private final Logger log = LoggerFactory.getLogger(ChatMessageService.class);

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * Save a chatMessage.
     *
     * @param chatMessage the entity to save.
     * @return the persisted entity.
     */
    public ChatMessage save(ChatMessage chatMessage) {
        log.debug("Request to save ChatMessage : {}", chatMessage);
        return chatMessageRepository.save(chatMessage);
    }

    /**
     * Update a chatMessage.
     *
     * @param chatMessage the entity to save.
     * @return the persisted entity.
     */
    public ChatMessage update(ChatMessage chatMessage) {
        log.debug("Request to update ChatMessage : {}", chatMessage);
        // no save call needed as we have no fields that can be updated
        return chatMessage;
    }

    /**
     * Partially update a chatMessage.
     *
     * @param chatMessage the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ChatMessage> partialUpdate(ChatMessage chatMessage) {
        log.debug("Request to partially update ChatMessage : {}", chatMessage);

        return chatMessageRepository
            .findById(chatMessage.getId())
            .map(existingChatMessage -> {
                return existingChatMessage;
            }); // .map(chatMessageRepository::save)
    }

    //    /**
    //     * Get all the chatMessages.
    //     *
    //     * @param pageable the pagination information.
    //     * @return the list of entities.
    //     */
    //    @Transactional(readOnly = true)
    //    public Page<ChatMessage> findAll(Pageable pageable) {
    //        log.debug("Request to get all ChatMessages");
    //        return chatMessageRepository.findAll(pageable);
    //    }

    @Transactional(readOnly = true)
    public List<ChatMessage> findBySenderAndReceiver(Long senderId, Long receiverId) {
        log.debug("Request to get all ChatMessages");
        return chatMessageRepository
            .findAll()
            .stream()
            .filter(chatMessage ->
                (chatMessage.getSenderId().equals(senderId) && chatMessage.getReceiverId().equals(receiverId)) ||
                (chatMessage.getSenderId().equals(receiverId) && chatMessage.getReceiverId().equals(senderId))
            )
            .sorted(Comparator.comparing(ChatMessage::getTimestamp).reversed())
            .collect(Collectors.toList());
        //        return chatMessageRepository.findBySenderIdAndReceiverIdOrderByTimestampDesc(senderId, receiverId);
    }

    @Transactional(readOnly = true)
    public List<Long> getReceiverIdByTimestamp() {
        log.debug("Request to get all ChatMessages");
        return sortMessages(chatMessageRepository.findAll());
    }

    private List<Long> sortMessages(List<ChatMessage> messages) {
        // Step 1: Create a map to store the latest message for each receiverId
        Map<Long, ChatMessage> latestMessages = new HashMap<>();
        for (ChatMessage message : messages) {
            long receiverId = message.getReceiverId();
            ChatMessage latestMessage = latestMessages.get(receiverId);
            if (latestMessage == null || message.getTimestamp().isAfter(latestMessage.getTimestamp())) {
                latestMessages.put(receiverId, message);
            }
        }
        System.out.println("latest message:" + latestMessages);

        var boo = latestMessages
            .entrySet()
            .stream()
            .sorted(Map.Entry.<Long, ChatMessage>comparingByValue(Comparator.comparing(ChatMessage::getTimestamp).reversed()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        System.out.println("Boo:" + boo);

        return boo;
    }

    /**
     * Get one chatMessage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ChatMessage> findOne(Long id) {
        log.debug("Request to get ChatMessage : {}", id);
        return chatMessageRepository.findById(id);
    }

    /**
     * Delete the chatMessage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ChatMessage : {}", id);
        chatMessageRepository.deleteById(id);
    }
}
