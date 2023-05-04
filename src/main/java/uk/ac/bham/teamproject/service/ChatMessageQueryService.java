package uk.ac.bham.teamproject.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import uk.ac.bham.teamproject.domain.*; // for static metamodels
import uk.ac.bham.teamproject.domain.ChatMessage;
import uk.ac.bham.teamproject.repository.ChatMessageRepository;
import uk.ac.bham.teamproject.service.criteria.ChatMessageCriteria;

/**
 * Service for executing complex queries for {@link ChatMessage} entities in the database.
 * The main input is a {@link ChatMessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ChatMessage} or a {@link Page} of {@link ChatMessage} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ChatMessageQueryService extends QueryService<ChatMessage> {

    private final Logger log = LoggerFactory.getLogger(ChatMessageQueryService.class);

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageQueryService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * Return a {@link List} of {@link ChatMessage} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> findByCriteria(ChatMessageCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ChatMessage> specification = createSpecification(criteria);
        return chatMessageRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link ChatMessage} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ChatMessage> findByCriteria(ChatMessageCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ChatMessage> specification = createSpecification(criteria);
        return chatMessageRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ChatMessageCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ChatMessage> specification = createSpecification(criteria);
        return chatMessageRepository.count(specification);
    }

    /**
     * Function to convert {@link ChatMessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ChatMessage> createSpecification(ChatMessageCriteria criteria) {
        Specification<ChatMessage> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ChatMessage_.id));
            }
        }
        return specification;
    }
}
