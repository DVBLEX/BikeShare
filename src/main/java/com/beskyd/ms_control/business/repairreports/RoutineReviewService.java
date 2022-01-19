package com.beskyd.ms_control.business.repairreports;

import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.repairreports.entity.BikeStations;
import com.beskyd.ms_control.business.repairreports.entity.RoutineReview;
import com.beskyd.ms_control.business.repairreports.repo.BikeStationsRepository;
import com.beskyd.ms_control.business.repairreports.repo.RoutineReviewRepository;
import com.beskyd.ms_control.business.requests.RoutineReviewsByFiltersRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoutineReviewService {

    private static final Double STATION_POSITION_DELTA = 0.0005;

    private EntityManager em;
    private final RoutineReviewRepository repo;
    private final BikeStationsRepository bikeStationsRepo;

    public RoutineReviewService(RoutineReviewRepository repo,BikeStationsRepository bikeStationsRepo, EntityManager em) {
        this.repo = repo;
        this.bikeStationsRepo = bikeStationsRepo;
        this.em = em;
    }
    
    public List<RoutineReview> findAll(){
        return repo.findAll(Sort.by("id").descending());
    }
    
    public List<RoutineReview> findAllBySchemeName(String schemeName){
        return repo.findByStation_Scheme_Name(schemeName, Sort.by("id").descending());
    }

    public List<RoutineReview> findByFilters(String schemeName, RoutineReviewsByFiltersRequest request) {
        List<RoutineReview> content;

        if(request.getPageSize() == 0) {
            content = new ArrayList<>();
        } else {
            content = getQueryFromFilters(schemeName, request)
                    .setMaxResults(request.getPageSize())
                    .setFirstResult((request.getPage() - 1) * request.getPageSize())
                    .getResultList();
        }

        return content;
    }

    public int countByFilters(String schemeName, RoutineReviewsByFiltersRequest request) {
        return getQueryFromFilters(schemeName, request).getResultList().size();
    }

    private TypedQuery<RoutineReview> getQueryFromFilters(String schemeName, RoutineReviewsByFiltersRequest request) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RoutineReview> cq = cb.createQuery(RoutineReview.class);

        var review = cq.from(RoutineReview.class);
        Join<BikeStations, Scheme> joinScheme = review.join("station").join("scheme");

        List<Predicate> predicates = new ArrayList<>();
        if(request.isFilterByBollards()){
            if(request.isWithBollards()) {
                predicates.add(cb.notEqual(review.get("bollardsInactive"), "[]"));
            } else {
                predicates.add(cb.equal(review.get("bollardsInactive"), "[]"));
            }
        }
        if(request.isFilterByGraffiti()){
            predicates.add(request.isWithGraffiti() ? cb.isTrue(review.get("graffiti")) : cb.isFalse(review.get("graffiti")));
        }
        if(request.isFilterByWeeds()){
            predicates.add(request.isWithWeeds() ? cb.isTrue(review.get("weeds")) : cb.isFalse(review.get("weeds")));
        }
        if(request.isFilterByBikes()){
            if(request.isWithBikes()) {
                predicates.add(cb.like(review.get("reports"), "%B%"));
            } else {
                predicates.add(cb.notLike(review.get("reports"), "%B%"));
            }
        }
        if(request.isFilterByStation()){
            if(request.isWithStation()) {
                predicates.add(cb.like(review.get("reports"), "%S%"));
            } else {
                predicates.add(cb.notLike(review.get("reports"), "%S%"));
            }
        }

        List<Order> orderBy = new ArrayList<>();

        orderBy.add(request.isDateAsc() ? cb.asc(review.get("creationDate")) : cb.desc(review.get("creationDate")));

        if(request.isSortByScheme()) {
            orderBy.add(cb.desc(joinScheme.get("name")));
        }

        if(predicates.size() == 0) {
            cq.where(cb.equal(joinScheme.get("name"), schemeName));
        } else {
            cq.where(cb.and(cb.equal(joinScheme.get("name"), schemeName), cb.and(predicates.toArray(new Predicate[0]))));
        }
        cq.orderBy(orderBy);

        return em.createQuery(cq);
    }

    public RoutineReview save(RoutineReview savable) {
        if(savable.getId() == null) {
            savable.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        
        return repo.save(savable);
    }
    
    public BikeStations findStationForCheckIn(double latitude, double longitude) {
        return bikeStationsRepo.findByCoords(latitude, longitude, STATION_POSITION_DELTA);
    }
    
    public List<BikeStations> findBikeStationsByScheme(String schemeName){
        return bikeStationsRepo.findBySchemeName(schemeName);
    }
}
