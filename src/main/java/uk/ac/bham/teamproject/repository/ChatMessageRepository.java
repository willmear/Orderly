package uk.ac.bham.teamproject.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uk.ac.bham.teamproject.domain.ChatMessage;

/**
 * Spring Data JPA repository for the ChatMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, JpaSpecificationExecutor<ChatMessage> {
    List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampDesc(Long senderId, Long receiverId);
}
