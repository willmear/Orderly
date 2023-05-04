package uk.ac.bham.teamproject.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uk.ac.bham.teamproject.domain.GoogleCalendarAccessToken;
import uk.ac.bham.teamproject.repository.GoogleCalendarAccessTokenRepository;
import uk.ac.bham.teamproject.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uk.ac.bham.teamproject.domain.GoogleCalendarAccessToken}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GoogleCalendarAccessTokenResource {

    private final Logger log = LoggerFactory.getLogger(GoogleCalendarAccessTokenResource.class);

    private static final String ENTITY_NAME = "googleCalendarAccessToken";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GoogleCalendarAccessTokenRepository googleCalendarAccessTokenRepository;

    public GoogleCalendarAccessTokenResource(GoogleCalendarAccessTokenRepository googleCalendarAccessTokenRepository) {
        this.googleCalendarAccessTokenRepository = googleCalendarAccessTokenRepository;
    }

    /**
     * {@code POST  /google-calendar-access-tokens} : Create a new googleCalendarAccessToken.
     *
     * @param googleCalendarAccessToken the googleCalendarAccessToken to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new googleCalendarAccessToken, or with status {@code 400 (Bad Request)} if the googleCalendarAccessToken has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/google-calendar-access-tokens")
    public ResponseEntity<GoogleCalendarAccessToken> createGoogleCalendarAccessToken(
        @Valid @RequestBody GoogleCalendarAccessToken googleCalendarAccessToken
    ) throws URISyntaxException {
        log.debug("REST request to save GoogleCalendarAccessToken : {}", googleCalendarAccessToken);
        if (googleCalendarAccessToken.getId() != null) {
            throw new BadRequestAlertException("A new googleCalendarAccessToken cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GoogleCalendarAccessToken result = googleCalendarAccessTokenRepository.save(googleCalendarAccessToken);
        return ResponseEntity
            .created(new URI("/api/google-calendar-access-tokens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /google-calendar-access-tokens/:id} : Updates an existing googleCalendarAccessToken.
     *
     * @param id the id of the googleCalendarAccessToken to save.
     * @param googleCalendarAccessToken the googleCalendarAccessToken to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated googleCalendarAccessToken,
     * or with status {@code 400 (Bad Request)} if the googleCalendarAccessToken is not valid,
     * or with status {@code 500 (Internal Server Error)} if the googleCalendarAccessToken couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/google-calendar-access-tokens/{id}")
    public ResponseEntity<GoogleCalendarAccessToken> updateGoogleCalendarAccessToken(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GoogleCalendarAccessToken googleCalendarAccessToken
    ) throws URISyntaxException {
        log.debug("REST request to update GoogleCalendarAccessToken : {}, {}", id, googleCalendarAccessToken);
        if (googleCalendarAccessToken.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, googleCalendarAccessToken.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!googleCalendarAccessTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GoogleCalendarAccessToken result = googleCalendarAccessTokenRepository.save(googleCalendarAccessToken);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, googleCalendarAccessToken.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /google-calendar-access-tokens/:id} : Partial updates given fields of an existing googleCalendarAccessToken, field will ignore if it is null
     *
     * @param id the id of the googleCalendarAccessToken to save.
     * @param googleCalendarAccessToken the googleCalendarAccessToken to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated googleCalendarAccessToken,
     * or with status {@code 400 (Bad Request)} if the googleCalendarAccessToken is not valid,
     * or with status {@code 404 (Not Found)} if the googleCalendarAccessToken is not found,
     * or with status {@code 500 (Internal Server Error)} if the googleCalendarAccessToken couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/google-calendar-access-tokens/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GoogleCalendarAccessToken> partialUpdateGoogleCalendarAccessToken(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GoogleCalendarAccessToken googleCalendarAccessToken
    ) throws URISyntaxException {
        log.debug("REST request to partial update GoogleCalendarAccessToken partially : {}, {}", id, googleCalendarAccessToken);
        if (googleCalendarAccessToken.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, googleCalendarAccessToken.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!googleCalendarAccessTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GoogleCalendarAccessToken> result = googleCalendarAccessTokenRepository
            .findById(googleCalendarAccessToken.getId())
            .map(existingGoogleCalendarAccessToken -> {
                if (googleCalendarAccessToken.getEncryptedToken() != null) {
                    existingGoogleCalendarAccessToken.setEncryptedToken(googleCalendarAccessToken.getEncryptedToken());
                }
                if (googleCalendarAccessToken.getExpires() != null) {
                    existingGoogleCalendarAccessToken.setExpires(googleCalendarAccessToken.getExpires());
                }

                return existingGoogleCalendarAccessToken;
            })
            .map(googleCalendarAccessTokenRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, googleCalendarAccessToken.getId().toString())
        );
    }

    /**
     * {@code GET  /google-calendar-access-tokens} : get all the googleCalendarAccessTokens.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of googleCalendarAccessTokens in body.
     */
    @GetMapping("/google-calendar-access-tokens")
    public List<GoogleCalendarAccessToken> getAllGoogleCalendarAccessTokens(
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get all GoogleCalendarAccessTokens");
        if (eagerload) {
            return googleCalendarAccessTokenRepository.findAllWithEagerRelationships();
        } else {
            return googleCalendarAccessTokenRepository.findAll();
        }
    }

    /**
     * {@code GET  /google-calendar-access-tokens/:id} : get the "id" googleCalendarAccessToken.
     *
     * @param id the id of the googleCalendarAccessToken to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the googleCalendarAccessToken, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/google-calendar-access-tokens/{id}")
    public ResponseEntity<GoogleCalendarAccessToken> getGoogleCalendarAccessToken(@PathVariable Long id) {
        log.debug("REST request to get GoogleCalendarAccessToken : {}", id);
        Optional<GoogleCalendarAccessToken> googleCalendarAccessToken = googleCalendarAccessTokenRepository.findOneWithEagerRelationships(
            id
        );
        return ResponseUtil.wrapOrNotFound(googleCalendarAccessToken);
    }

    /**
     * {@code DELETE  /google-calendar-access-tokens/:id} : delete the "id" googleCalendarAccessToken.
     *
     * @param id the id of the googleCalendarAccessToken to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/google-calendar-access-tokens/{id}")
    public ResponseEntity<Void> deleteGoogleCalendarAccessToken(@PathVariable Long id) {
        log.debug("REST request to delete GoogleCalendarAccessToken : {}", id);
        googleCalendarAccessTokenRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
