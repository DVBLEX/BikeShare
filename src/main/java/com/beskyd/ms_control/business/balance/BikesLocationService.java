package com.beskyd.ms_control.business.balance;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.beskyd.ms_control.business.repairreports.entity.RepairReports;
import com.beskyd.ms_control.business.repairreports.repo.RepairReportsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BikesLocationService {

    public static final int STATE_NEW = 41; //In Depot

    public static final int STATE_DONE = 43; //On-street


    private final RepairReportsRepository repo;
    private final BikesLocationInDepotRepository bikesLocationInDepotRepository;
    private final BikesLocationOnStreetRepository bikesLocationOnStreetRepository;


    @Inject
    public BikesLocationService(RepairReportsRepository repo, BikesLocationInDepotRepository bikesLocationInDepotRepository, BikesLocationOnStreetRepository bikesLocationOnStreetRepository) {
        this.repo = repo;
        this.bikesLocationInDepotRepository = bikesLocationInDepotRepository;
        this.bikesLocationOnStreetRepository = bikesLocationOnStreetRepository;
    }

    public List<RepairReports> findAll() {
        return repo.findAll(Sort.by("id").descending());
    }


    public List<RepairReports> findDone() {
        return repo.findByStateId(STATE_DONE);
    }

    public List<RepairReports> setBikesLocationOnStreetRepository() {
        return bikesLocationOnStreetRepository.findDone(STATE_DONE);
    }

    public List<RepairReports> findNew() {
        return repo.findByStateId(STATE_NEW);
    }

    public List<RepairReports> setBikesLocationInDepotRepository() {
        return bikesLocationInDepotRepository.findNew(STATE_NEW);
    }

    public List<RepairReports> findDoneByScheme(String schemeName){
        return repo.findByLocation_Scheme_NameAndState_Id(schemeName, STATE_DONE, Sort.by("id").descending());
    }



    public List<RepairReports> findNewByScheme(String schemeName){
        return repo.findByLocation_Scheme_NameAndState_Id(schemeName, STATE_NEW, Sort.by("id").descending());
    }

    public List<RepairReports> findByLocation_Scheme_NameAndState_Id(String schemeName, int id) {
        return repo.findByLocation_Scheme_NameAndState_Id(schemeName, id, Sort.by("id").descending());
    }
    public List<BikesLocationInDepot> findBikesByScheme(String schemeName){
        return bikesLocationInDepotRepository.findBySchemeName(schemeName, Sort.by("bike_id").descending());
    }

    public List<BikesLocationInDepot> findBikesBySchemeAndNumbers(String schemeName, List<String> numbers){
        return bikesLocationInDepotRepository.findBySchemeNameAndNumberIn(schemeName, numbers, Sort.by("bike_id").descending());
    }

    public List<BikesLocationInDepot> addInDepot() {
        List<RepairReports> reports = repo.findByStateId(STATE_NEW);
        List<BikesLocationInDepot> bikesLocationInDepots = new ArrayList<>();

        for (RepairReports rr : reports) {

            BikesLocationInDepot inDepot = new BikesLocationInDepot();

            inDepot.setBike(rr.getBike());
            inDepot.setScheme(rr.getBike().getScheme());
            inDepot.setReason("Repair");
            inDepot.setIn_storage_since(rr.getReportDate());
            inDepot.setManually_selected(false);
            bikesLocationInDepots.add(inDepot);
        }

        bikesLocationInDepotRepository.saveAll(bikesLocationInDepots);

        return bikesLocationInDepots;
    }

    public List<BikesLocationOnStreet> addOnStreet() {
        List<RepairReports> reports = repo.findByStateId(STATE_DONE);
        List<BikesLocationOnStreet> bikesLocationOnStreets = new ArrayList<>();

        for (RepairReports rr : reports) {

            BikesLocationOnStreet onStreet = new BikesLocationOnStreet();

            onStreet.setBike(rr.getBike());
            onStreet.setScheme(rr.getBike().getScheme());
            onStreet.setReason("Repair");
            onStreet.setLast_in_storage(rr.getRepairDate());
            onStreet.setManually_selected(false);
            bikesLocationOnStreets.add(onStreet);

        }

        bikesLocationOnStreetRepository.saveAll(bikesLocationOnStreets);

        return bikesLocationOnStreets;
    }

   /* public List<BikesLocationOnStreet> findBikesByScheme(String schemeName){
        return bikesLocationOnStreetRepository.findBySchemeName(schemeName, Sort.by("bike_id").descending());
    }

    public List<BikesLocationOnStreet> findBikesBySchemeAndNumbers(String schemeName, List<String> numbers){
        return bikesLocationOnStreetRepository.findBySchemeNameAndNumberIn(schemeName, numbers, Sort.by("bike_id").descending());
    }*/


   /* public List<BikeStations> findAllBikeStations(){
        return bikeStationsRepository.findAll();
    }

    public List<Bikes> findAllBikes(){
        return bikesRepository.findAll();
    }

    public List<BikeStations> findBikeStationsByScheme(String schemeName){
        return bikeStationsRepository.findBySchemeName(schemeName);
    }

    public List<Bikes> findBikesByScheme(String schemeName){
        return bikesRepository.findBySchemeName(schemeName, Sort.by("number").descending());
    }

    public List<Bikes> findBikesBySchemeAndNumbers(String schemeName, List<String> numbers){
        return bikesRepository.findBySchemeNameAndNumberIn(schemeName, numbers, Sort.by("number").descending());
    }*/

}
