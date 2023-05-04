package uk.ac.bham.teamproject.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import uk.ac.bham.teamproject.domain.ChatMessage;
import uk.ac.bham.teamproject.domain.User;
import uk.ac.bham.teamproject.repository.UserRepository;
import uk.ac.bham.teamproject.security.SecurityUtils;
import uk.ac.bham.teamproject.service.ChatMessageQueryService;
import uk.ac.bham.teamproject.service.ChatMessageService;
import uk.ac.bham.teamproject.service.criteria.ChatMessageCriteria;
import uk.ac.bham.teamproject.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uk.ac.bham.teamproject.domain.ChatMessage}.
 */
@RestController
@RequestMapping("/api")
public class ChatMessageResource {

    private final Logger log = LoggerFactory.getLogger(ChatMessageResource.class);

    private static final String ENTITY_NAME = "chatMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChatMessageService chatMessageService;

    private final UserRepository userRepository;

    private final ChatMessageQueryService chatMessageQueryService;

    public ChatMessageResource(
        ChatMessageService chatMessageService,
        UserRepository userRepository,
        ChatMessageQueryService chatMessageQueryService
    ) {
        this.chatMessageService = chatMessageService;
        this.userRepository = userRepository;
        this.chatMessageQueryService = chatMessageQueryService;
    }

    /**
     * {@code GET  /chat-messages} : get all the chatMessages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chatMessages in body.
     */
    @GetMapping("/chat-messages")
    public ResponseEntity<List<ChatMessage>> getAllChatMessages(
        ChatMessageCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get ChatMessages by criteria: {}", criteria);
        Page<ChatMessage> page = chatMessageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/chat-messages/{receiverId}")
    public ResponseEntity<List<ChatMessage>> getChatMessageForSenderAndReceiver(@PathVariable Long receiverId) {
        log.debug("REST request to get getChatMessageForSenderAndReceiver receiverId : {}", receiverId);

        Long senderId = userRepository
            .findOneByLogin(SecurityUtils.getCurrentUserLogin().map(Object::toString).orElse(""))
            .orElse(null)
            .getId();

        log.debug("REST request to get getChatMessageForSenderAndReceiver senderId : {}", senderId);

        List<ChatMessage> chatMessage = chatMessageService.findBySenderAndReceiver(senderId, receiverId);
        return ResponseUtil.wrapOrNotFound(Optional.of(chatMessage));
    }

    @GetMapping("/chat-messages/current-user")
    public ResponseEntity<User> getCurrentUser() {
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().map(Object::toString).orElse("")).orElse(null);
        return ResponseUtil.wrapOrNotFound(Optional.of(user));
    }

    @GetMapping("/chat-messages-by-ids")
    public ResponseEntity<List<ChatMessage>> getBySenderIdAndReceiverId(
        @RequestParam(value = "senderId") String senderId,
        @RequestParam(value = "receiverId") String receiverId
    ) {
        List<ChatMessage> chatMessages = chatMessageService.findBySenderAndReceiver(Long.valueOf(senderId), Long.valueOf(receiverId));
        return ResponseUtil.wrapOrNotFound(Optional.of(chatMessages));
    }

    @GetMapping("/chat-message/receivers")
    public ResponseEntity<long[]> getAllBySorted() {
        long[] chatMessages = chatMessageService.getReceiverIdByTimestamp().stream().mapToLong(l -> l).toArray();
        return ResponseEntity.ok().body(chatMessages);
    }

    //    /**
    //     * {@code GET  /chat-messages/:id} : get the "id" chatMessage.
    //     *
    //     * @param id the id of the chatMessage to retrieve.
    //     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chatMessage, or with status {@code 404 (Not Found)}.
    //     */
    //    @GetMapping("/chat-messages/{id}")
    //    public ResponseEntity<ChatMessage> getChatMessage(@PathVariable Long id) {
    //        log.debug("REST request to get ChatMessage : {}", id);
    //        Optional<ChatMessage> chatMessage = chatMessageService.findOne(id);
    //        return ResponseUtil.wrapOrNotFound(chatMessage);
    //    }

    @PostMapping("/chat-messages")
    public ResponseEntity<ChatMessage> createChatMessage(@RequestBody ChatMessage chatMessage) throws URISyntaxException {
        log.debug("REST request to save message : {}", chatMessage);
        if (chatMessage.getId() != null) {
            throw new BadRequestAlertException("A new chat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ChatMessage result = chatMessageService.save(chatMessage);
        return ResponseEntity
            .created(new URI("/api/chat-message/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
}
