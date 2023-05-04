package uk.ac.bham.teamproject.web.rest;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uk.ac.bham.teamproject.domain.Targets;
import uk.ac.bham.teamproject.repository.TargetsRepository;
import uk.ac.bham.teamproject.repository.UserOrdersRepository;
import uk.ac.bham.teamproject.repository.UserRepository;
import uk.ac.bham.teamproject.security.SecurityUtils;
import uk.ac.bham.teamproject.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uk.ac.bham.teamproject.domain.Targets}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TargetsResource {

    private final Logger log = LoggerFactory.getLogger(TargetsResource.class);

    private static final String ENTITY_NAME = "targets";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TargetsRepository targetsRepository;
    private final UserRepository userRepository;
    private final UserOrdersRepository userOrdersRepository;

    public TargetsResource(TargetsRepository targetsRepository, UserRepository userRepository, UserOrdersRepository userOrdersRepository) {
        this.targetsRepository = targetsRepository;
        this.userRepository = userRepository;
        this.userOrdersRepository = userOrdersRepository;
    }

    /**
     * {@code POST  /targets} : Create a new targets.
     *
     * @param targets the targets to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new targets, or with status {@code 400 (Bad Request)} if the targets has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/targets")
    public ResponseEntity<Targets> createTargets(@RequestBody Targets targets) throws URISyntaxException {
        log.debug("REST request to save Targets : {}", targets);
        if (targets.getId() != null) {
            throw new BadRequestAlertException("A new targets cannot already have an ID", ENTITY_NAME, "idexists");
        }

        targets.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().map(Object::toString).orElse("")).orElse(null));

        Targets result = targetsRepository.save(targets);
        return ResponseEntity
            .created(new URI("/api/targets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /targets/:id} : Updates an existing targets.
     *
     * @param id the id of the targets to save.
     * @param targets the targets to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated targets,
     * or with status {@code 400 (Bad Request)} if the targets is not valid,
     * or with status {@code 500 (Internal Server Error)} if the targets couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/targets/{id}")
    public ResponseEntity<Targets> updateTargets(@PathVariable(value = "id", required = false) final Long id, @RequestBody Targets targets)
        throws URISyntaxException {
        log.debug("REST request to update Targets : {}, {}", id, targets);
        if (targets.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, targets.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!targetsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Targets result = targetsRepository.save(targets);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, targets.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /targets/:id} : Partial updates given fields of an existing targets, field will ignore if it is null
     *
     * @param id the id of the targets to save.
     * @param targets the targets to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated targets,
     * or with status {@code 400 (Bad Request)} if the targets is not valid,
     * or with status {@code 404 (Not Found)} if the targets is not found,
     * or with status {@code 500 (Internal Server Error)} if the targets couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/targets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Targets> partialUpdateTargets(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Targets targets
    ) throws URISyntaxException {
        log.debug("REST request to partial update Targets partially : {}, {}", id, targets);
        if (targets.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, targets.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!targetsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Targets> result = targetsRepository
            .findById(targets.getId())
            .map(existingTargets -> {
                if (targets.getText() != null) {
                    existingTargets.setText(targets.getText());
                }

                return existingTargets;
            })
            .map(targetsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, targets.getId().toString())
        );
    }

    /**
     * {@code GET  /targets} : get all the targets.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of targets in body.
     */
    @GetMapping("/targets")
    public List<Targets> getAllTargets(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Targets");
        if (SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            if (eagerload) {
                return targetsRepository.findAllWithEagerRelationships();
            } else {
                return targetsRepository.findAll();
            }
        } else {
            return targetsRepository.findByUserIsCurrentUser();
        }
    }

    /**
     * {@code GET  /targets/:id} : get the "id" targets.
     *
     * @param id the id of the targets to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the targets, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/targets/{id}")
    public ResponseEntity<Targets> getTargets(@PathVariable Long id) {
        log.debug("REST request to get Targets : {}", id);
        Optional<Targets> targets = targetsRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(targets);
    }

    /**
     * {@code DELETE  /targets/:id} : delete the "id" targets.
     *
     * @param id the id of the targets to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/targets/{id}")
    public ResponseEntity<Void> deleteTargets(@PathVariable Long id) {
        log.debug("REST request to delete Targets : {}", id);
        targetsRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
