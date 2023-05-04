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
import uk.ac.bham.teamproject.domain.Businesses;
import uk.ac.bham.teamproject.repository.BusinessesRepository;
import uk.ac.bham.teamproject.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uk.ac.bham.teamproject.domain.Businesses}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BusinessesResource {

    private final Logger log = LoggerFactory.getLogger(BusinessesResource.class);

    private static final String ENTITY_NAME = "businesses";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessesRepository businessesRepository;

    public BusinessesResource(BusinessesRepository businessesRepository) {
        this.businessesRepository = businessesRepository;
    }

    /**
     * {@code POST  /businesses} : Create a new businesses.
     *
     * @param businesses the businesses to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new businesses, or with status {@code 400 (Bad Request)} if the businesses has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/businesses")
    public ResponseEntity<Businesses> createBusinesses(@Valid @RequestBody Businesses businesses) throws URISyntaxException {
        log.debug("REST request to save Businesses : {}", businesses);
        if (businesses.getId() != null) {
            throw new BadRequestAlertException("A new businesses cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Businesses result = businessesRepository.save(businesses);
        return ResponseEntity
            .created(new URI("/api/businesses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /businesses/:id} : Updates an existing businesses.
     *
     * @param id the id of the businesses to save.
     * @param businesses the businesses to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businesses,
     * or with status {@code 400 (Bad Request)} if the businesses is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businesses couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/businesses/{id}")
    public ResponseEntity<Businesses> updateBusinesses(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Businesses businesses
    ) throws URISyntaxException {
        log.debug("REST request to update Businesses : {}, {}", id, businesses);
        if (businesses.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businesses.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!businessesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Businesses result = businessesRepository.save(businesses);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businesses.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /businesses/:id} : Partial updates given fields of an existing businesses, field will ignore if it is null
     *
     * @param id the id of the businesses to save.
     * @param businesses the businesses to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businesses,
     * or with status {@code 400 (Bad Request)} if the businesses is not valid,
     * or with status {@code 404 (Not Found)} if the businesses is not found,
     * or with status {@code 500 (Internal Server Error)} if the businesses couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/businesses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Businesses> partialUpdateBusinesses(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Businesses businesses
    ) throws URISyntaxException {
        log.debug("REST request to partial update Businesses partially : {}, {}", id, businesses);
        if (businesses.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businesses.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!businessesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Businesses> result = businessesRepository
            .findById(businesses.getId())
            .map(existingBusinesses -> {
                if (businesses.getName() != null) {
                    existingBusinesses.setName(businesses.getName());
                }
                if (businesses.getSummary() != null) {
                    existingBusinesses.setSummary(businesses.getSummary());
                }
                if (businesses.getRating() != null) {
                    existingBusinesses.setRating(businesses.getRating());
                }
                if (businesses.getLocation() != null) {
                    existingBusinesses.setLocation(businesses.getLocation());
                }
                if (businesses.getType() != null) {
                    existingBusinesses.setType(businesses.getType());
                }
                if (businesses.getImage() != null) {
                    existingBusinesses.setImage(businesses.getImage());
                }
                if (businesses.getImageContentType() != null) {
                    existingBusinesses.setImageContentType(businesses.getImageContentType());
                }

                return existingBusinesses;
            })
            .map(businessesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businesses.getId().toString())
        );
    }

    /**
     * {@code GET  /businesses} : get all the businesses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businesses in body.
     */
    @GetMapping("/businesses")
    public List<Businesses> getAllBusinesses() {
        log.debug("REST request to get all Businesses");
        return businessesRepository.findAll();
    }

    /**
     * {@code GET  /businesses/:id} : get the "id" businesses.
     *
     * @param id the id of the businesses to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businesses, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/businesses/{id}")
    public ResponseEntity<Businesses> getBusinesses(@PathVariable Long id) {
        log.debug("REST request to get Businesses : {}", id);
        Optional<Businesses> businesses = businessesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(businesses);
    }

    /**
     * {@code DELETE  /businesses/:id} : delete the "id" businesses.
     *
     * @param id the id of the businesses to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/businesses/{id}")
    public ResponseEntity<Void> deleteBusinesses(@PathVariable Long id) {
        log.debug("REST request to delete Businesses : {}", id);
        businessesRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
