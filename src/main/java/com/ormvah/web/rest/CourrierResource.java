package com.ormvah.web.rest;

import com.ormvah.domain.Courrier;
import com.ormvah.domain.Employee;
import com.ormvah.domain.LeService;
import com.ormvah.domain.User;
import com.ormvah.domain.enumeration.Status;
import com.ormvah.domain.enumeration.TypeCourrier;
import com.ormvah.security.AuthoritiesConstants;
import com.ormvah.security.SecurityUtils;
import com.ormvah.service.CourrierService;
import com.ormvah.service.EmployeeQueryService;
import com.ormvah.service.UserService;
import com.ormvah.service.dto.EmployeeCriteria;
import com.ormvah.web.rest.errors.BadRequestAlertException;
import com.ormvah.service.dto.CourrierCriteria;
import com.ormvah.service.CourrierQueryService;

import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.*;

/**
 * REST controller for managing {@link com.ormvah.domain.Courrier}.
 */
@RestController
@RequestMapping("/api")
public class CourrierResource {

    private final Logger log = LoggerFactory.getLogger(CourrierResource.class);

    private static final String ENTITY_NAME = "courrier";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CourrierService courrierService;
    private final CourrierQueryService courrierQueryService;
    private UserService userService;
    private EmployeeQueryService employeeQueryService;

    public CourrierResource(CourrierService courrierService,
                            CourrierQueryService courrierQueryService,
                            UserService userService,
                            EmployeeQueryService employeeQueryService) {
        this.courrierService = courrierService;
        this.courrierQueryService = courrierQueryService;
        this.userService = userService;
        this.employeeQueryService = employeeQueryService;
    }

    /**
     * {@code POST  /courriers} : Create a new courrier.
     *
     * @param courrier the courrier to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courrier, or with status {@code 400 (Bad Request)} if the courrier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/courriers")
    public ResponseEntity<Courrier> createCourrier(@RequestBody Courrier courrier) throws URISyntaxException {
        log.debug("REST request to save Courrier : {}", courrier);
        if (courrier.getId() != null) {
            throw new BadRequestAlertException("A new courrier cannot already have an ID", ENTITY_NAME, "idexists");
        }

        addCoordinatorToServices(courrier);

        // set num order
        CourrierCriteria criteria = new CourrierCriteria();
        Optional<Courrier> last = courrierQueryService.findLast(criteria);
        int num = 0;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if (last.isPresent()) {
            String value = last.get().getIdCourrier();
            if (value != null) {
                String[] split = value.split("/");
                num = Integer.parseInt(split[0]);
            }
        }
        String format = String.format("%06d", ++num);
        String numOrder = format + "/" + year;
        courrier.setIdCourrier(numOrder);
        // =====

        Courrier result = courrierService.save(courrier);
        return ResponseEntity.created(new URI("/api/courriers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /courriers} : Updates an existing courrier.
     *
     * @param courrier the courrier to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courrier,
     * or with status {@code 400 (Bad Request)} if the courrier is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courrier couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/courriers")
    public ResponseEntity<Courrier> updateCourrier(@RequestBody Courrier courrier) throws URISyntaxException {
        log.debug("REST request to update Courrier : {}", courrier);
        if (courrier.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        addCoordinatorToServices(courrier);

        Courrier result = courrierService.save(courrier);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courrier.getId().toString()))
            .body(result);
    }

    private void addCoordinatorToServices(@RequestBody Courrier courrier) {
        LeService coordinator = courrier.getCoordinator();
        Set<LeService> services = courrier.getServices();
        if (!services.contains(coordinator)) {
            services.add(coordinator);
        }
    }

    /**
     * {@code GET  /courriers} : get all the courriers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courriers in body.
     */
    @GetMapping("/courriers")
    public ResponseEntity<List<Courrier>> getAllCourriers(CourrierCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Courriers by criteria: {}", criteria);
        Page<Courrier> page = Page.empty();

        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.AGENT_BO) ||
            SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.DIRECTEUR) ||
            SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {

            page = courrierQueryService.findByCriteria(criteria, pageable);

        } else if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.CHEF_DE_SERVICE)) {
            Optional<User> user = userService.getUserWithAuthorities();

            if (user.isPresent()) {
                LongFilter longFilter = new LongFilter();
                longFilter.setEquals(user.get().getId());

                EmployeeCriteria employeeCriteria = new EmployeeCriteria();
                employeeCriteria.setUserId(longFilter);
                List<Employee> byCriteria = employeeQueryService.findByCriteria(employeeCriteria);
                if (!byCriteria.isEmpty()) {

                    if (criteria == null) criteria = new CourrierCriteria();

                    CourrierCriteria criteria2 = new CourrierCriteria();

                    Long serviceId = byCriteria.get(0).getService().getId();

                    // Arrivee and in services
                    criteria.setTypeCourrier((CourrierCriteria.TypeCourrierFilter) new CourrierCriteria.TypeCourrierFilter().setEquals(TypeCourrier.Arrivee));
                    criteria.setServicesId((LongFilter) new LongFilter().setIn(Arrays.asList(serviceId)));

                    // Sortie and in emetteur
                    criteria2.setTypeCourrier((CourrierCriteria.TypeCourrierFilter) new CourrierCriteria.TypeCourrierFilter().setEquals(TypeCourrier.Sortie));
                    criteria2.setEmetteurId((LongFilter) new LongFilter().setEquals(serviceId));

                    page = courrierQueryService.findByCriteria2(criteria, criteria2, pageable);
                }
            }
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/courriers/ChefDeService")
    public ResponseEntity<List<Courrier>> getAllCourriersChefDeService(CourrierCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Courriers by criteria: {}", criteria);
        Page<Courrier> page = courrierQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /courriers/count} : count all the courriers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/courriers/count")
    public ResponseEntity<Long> countCourriers(CourrierCriteria criteria) {
        log.debug("REST request to count Courriers by criteria: {}", criteria);
        return ResponseEntity.ok().body(courrierQueryService.countByCriteria(criteria));
    }

    @GetMapping("/courriers/stats")
    public ResponseEntity<Stats> statsCourriers(CourrierCriteria criteria) {
        log.debug("REST request to count Courriers by criteria: {}", criteria);
        long count = courrierQueryService.countByCriteria(criteria);

        // all courrier de départ
        CourrierCriteria ccDepart = new CourrierCriteria();
        ccDepart.setTypeCourrier((CourrierCriteria.TypeCourrierFilter) new CourrierCriteria.TypeCourrierFilter().setEquals(TypeCourrier.Sortie));
        long ccDepartCount = courrierQueryService.countByCriteria(ccDepart);

        // all courrier d'arriver
        CourrierCriteria ccArrivee = new CourrierCriteria();
        ccArrivee.setTypeCourrier((CourrierCriteria.TypeCourrierFilter) new CourrierCriteria.TypeCourrierFilter().setEquals(TypeCourrier.Arrivee));
        long ccArriveeCount = courrierQueryService.countByCriteria(ccArrivee);

        // all open courrier
        CourrierCriteria ccOpen = new CourrierCriteria();
        ccOpen.setStatus((CourrierCriteria.StatusFilter) new CourrierCriteria.StatusFilter().setEquals(Status.Ouvert));
        long ccOpenCount = courrierQueryService.countByCriteria(ccOpen);

        // all in progress courrier
        CourrierCriteria ccWip = new CourrierCriteria();
        ccWip.setStatus((CourrierCriteria.StatusFilter) new CourrierCriteria.StatusFilter().setEquals(Status.Encours));
        long ccWipCount = courrierQueryService.countByCriteria(ccWip);

        // all traité courrier
        CourrierCriteria ccTraite = new CourrierCriteria();
        ccTraite.setStatus((CourrierCriteria.StatusFilter) new CourrierCriteria.StatusFilter().setEquals(Status.Traite));
        long ccTraiteCount = courrierQueryService.countByCriteria(ccTraite);

        return ResponseEntity.ok().body(new Stats(count, ccDepartCount, ccArriveeCount, ccOpenCount, ccWipCount, ccTraiteCount));
    }

    class Stats {
        long count;
        long ccDepartCount;
        long ccArriveeCount;
        long ccOpenCount;
        long ccWipCount;
        long ccTraiteCount;

        public Stats(long count, long ccDepartCount, long ccArriveeCount, long ccOpenCount, long ccWipCount, long ccTraiteCount) {
            this.count = count;
            this.ccDepartCount = ccDepartCount;
            this.ccArriveeCount = ccArriveeCount;
            this.ccOpenCount = ccOpenCount;
            this.ccWipCount = ccWipCount;
            this.ccTraiteCount = ccTraiteCount;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public long getCcDepartCount() {
            return ccDepartCount;
        }

        public void setCcDepartCount(long ccDepartCount) {
            this.ccDepartCount = ccDepartCount;
        }

        public long getCcArriveeCount() {
            return ccArriveeCount;
        }

        public void setCcArriveeCount(long ccArriveeCount) {
            this.ccArriveeCount = ccArriveeCount;
        }

        public long getCcOpenCount() {
            return ccOpenCount;
        }

        public void setCcOpenCount(long ccOpenCount) {
            this.ccOpenCount = ccOpenCount;
        }

        public long getCcWipCount() {
            return ccWipCount;
        }

        public void setCcWipCount(long ccWipCount) {
            this.ccWipCount = ccWipCount;
        }

        public long getCcTraiteCount() {
            return ccTraiteCount;
        }

        public void setCcTraiteCount(long ccTraiteCount) {
            this.ccTraiteCount = ccTraiteCount;
        }
    }

    /**
     * {@code GET  /courriers/:id} : get the "id" courrier.
     *
     * @param id the id of the courrier to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courrier, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/courriers/{id}")
    public ResponseEntity<Courrier> getCourrier(@PathVariable Long id) {
        log.debug("REST request to get Courrier : {}", id);
        Optional<Courrier> courrier = courrierService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courrier);
    }

    /**
     * {@code DELETE  /courriers/:id} : delete the "id" courrier.
     *
     * @param id the id of the courrier to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/courriers/{id}")
    public ResponseEntity<Void> deleteCourrier(@PathVariable Long id) {
        log.debug("REST request to delete Courrier : {}", id);
        courrierService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
