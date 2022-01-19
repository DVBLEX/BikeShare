package com.beskyd.ms_control.business.repairreports;

import com.beskyd.ms_control.business.general.StatesRepository;
import com.beskyd.ms_control.business.repairreports.entity.*;
import com.beskyd.ms_control.business.repairreports.repo.*;
import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RepairReportsService {

    public static final int STATE_NEW = 41;
    public static final int STATE_IN_PROGRESS = 42;
    public static final int STATE_DONE = 43;
    public static final int STATE_PENDING = 44;
    
    private final RepairReportsRepository repo;
    private final BikeStationsRepository bikeStationsRepository;
    private final BikesRepository bikesRepository;
    private final ReportReasonsRepository reportReasonsRepository;
    private final RepairReasonsRepository repairReasonsRepository;
    private final StatesRepository statesRepository;
    private final RepairJobsRepository repairJobsRepository;
    private final RepairReportsOperatorsRepository operatorsRepo;
    private final ReportUsedAssetsRepository usedAssetsRepo;
    private final UserService userService;


    public RepairReportsService(RepairReportsRepository repo, BikeStationsRepository bikeStationsRepository, BikesRepository bikesRepository, RepairReportsOperatorsRepository operatorsRepo,
        ReportReasonsRepository reportReasonsRepository, RepairReasonsRepository repairReasonsRepository, StatesRepository statesRepository, RepairJobsRepository repairJobsRepository,
        ReportUsedAssetsRepository usedAssetsRepo, UserService userService) {
        this.repo = repo;
        this.bikeStationsRepository = bikeStationsRepository;
        this.bikesRepository = bikesRepository;
        this.reportReasonsRepository = reportReasonsRepository;
        this.repairReasonsRepository = repairReasonsRepository;
        this.statesRepository = statesRepository;
        this.repairJobsRepository = repairJobsRepository;
        this.operatorsRepo = operatorsRepo;
        this.usedAssetsRepo = usedAssetsRepo;
        this.userService = userService;
    }
    
    
    public List<RepairReports> findAll(){
        return repo.findAll(Sort.by("id").descending());
    }
    
    public List<BikeStations> findAllBikeStations(){
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
    }
    
    public List<ReportReasons> findAllReportReasons(){
        return reportReasonsRepository.findAll();
    }
    
    public List<RepairReasons> findAllRepairReasons(){
        return repairReasonsRepository.findAll(Sort.by("reason").ascending());
    }
    
    public List<RepairReasons> findRepairReasonsForStation(){
        return repairReasonsRepository.findByForWhat(1, Sort.by("reason").ascending());
    }
    
    public List<RepairReasons> findRepairReasonsForBike(){
        return repairReasonsRepository.findByForWhat(2, Sort.by("reason").ascending());
    }
    
    public List<RepairReports> findByScheme(String schemeName){
        return repo.findByLocation_Scheme_Name(schemeName, Sort.by("id").descending());
    }

    public List<RepairReports> findDone() {
        return repo.findByStateId(STATE_DONE);
    }
    
    public List<RepairReports> findDoneByScheme(String schemeName){
        return repo.findByLocation_Scheme_NameAndState_Id(schemeName, STATE_DONE, Sort.by("id").descending());
    }
    
    public List<RepairJobs> findAllRepairJobs(){
        return repairJobsRepository.findAll(Sort.by("job").ascending());
    }

    public List<RepairReports> findByBike(String schemeName, String bikeNumber){
        return repo.findByBike_Scheme_NameAndBike_Number(schemeName, bikeNumber);
    }
    
    public List<RepairReports> findByStation(String schemeName, String stationLocation){
        return repo.findByLocation_Scheme_NameAndLocation_Location(schemeName, stationLocation);
    }

    public ReportReasons findReportReasonById(int id) {
        return reportReasonsRepository.findById(id).orElseThrow();
    }

    /**
     * Find {@link RepairReports} by {@code id}
     * @param id
     * @return object if found, {@code null} - if not
     */
    public RepairReports findById(int id) {
        return repo.findById(id).orElse(null);
    }

    /**
     * Saves report and adjusts params (state, operators, etc...)
     * if you want to state that request is pending, then set id as -2
     * Don't use it just to persist Report, because most likely state, or date, or smth. will be changed !
     * @param savable
     * @return
     */
    @Transactional
    public RepairReports adjustAndSave(RepairReports savable) {
        var saved = saveAdjusted(savable, false);
        performUsedAssetCheck(saved, savable);
        return saved;
    }

    private void performUsedAssetCheck(RepairReports saved, RepairReports savable) {
        if(saved.getState().getId() != RepairReportsService.STATE_NEW && saved.getState().getId() != RepairReportsService.STATE_PENDING) {
            for(var operator : savable.getOperators()) {

                var usedSpareparts = operator.getUsedSpareparts();
                operator.setUsedSpareparts(null);

                operator.setReport(saved);
                var savedOperator = saveOperatorData(operator);

                usedAssetsRepo.deleteByRepairOperatorId(savedOperator.getId());

                for(var asset : usedSpareparts) {
                    asset.setOperator(savedOperator);
                    saveUsedAssets(asset);
                }
                operator.setUsedSpareparts(usedSpareparts);
            }
        }
    }

    private RepairReports saveAdjusted(RepairReports savable, Boolean complete) {
        if (complete) {
            savable.getOperators().forEach(o -> o.setJobDoneDatetime(savable.getReportDate()));
            return repo.save(savable);
        }
        else
            return repo.save(adjustStateAndId(savable));
    }

    @Transactional
    public RepairReports adjustAndSave(RepairReports savable, Boolean complete) {
        savable.setState(statesRepository.findById(STATE_DONE).orElseThrow());
        savable.setReportDate(Timestamp.valueOf(LocalDateTime.now()));
        savable.setRepairDate(savable.getReportDate());
        var saved = saveAdjusted(savable, complete);
        performUsedAssetCheck(saved, savable);

        return saved;
    }

    public RepairReports adjustStateAndId(RepairReports report) {
        if(report.getId() == null) {
            report.setState(statesRepository.findById(STATE_NEW).orElseThrow());
            report.setReportDate(Timestamp.valueOf(LocalDateTime.now()));
        } else if(report.getId().equals(-2)) {
            report.setId(null);
            report.setState(statesRepository.findById(STATE_PENDING).orElseThrow());
            report.setReportDate(Timestamp.valueOf(LocalDateTime.now()));
        } else if(report.getId() > 0 && report.getState().getId() == STATE_PENDING) {
            report.setState(statesRepository.findById(STATE_NEW).orElseThrow());
            report.setCollectedDate(Timestamp.valueOf(LocalDateTime.now()));
        } else if(((report.getOperators() != null && !report.getOperators().isEmpty())
                || (report.getDepotComments() != null && !report.getDepotComments().isEmpty()))
                && report.getState().getId() == STATE_NEW) {

            report.setState(statesRepository.findById(STATE_IN_PROGRESS).orElseThrow());
        }
        
        return report;
    }
    
    public BikeStations saveBikeStation(BikeStations savable) {
        return bikeStationsRepository.save(savable);
    }
    
    public Bikes saveBike(Bikes savable) {
        return bikesRepository.save(savable);
    }
    
    public RepairReasons saveRepairReason(RepairReasons savable) {
        return repairReasonsRepository.save(savable);
    }
    
    public RepairJobs saveRepairJob(RepairJobs savable) {
        return repairJobsRepository.save(savable);
    }
    
    public void deleteRepairReason(int id) {
        repairReasonsRepository.deleteById(id);
    }
    
    public void deleteRepairJob(int id) {
        repairJobsRepository.deleteById(id);
    }

    public RepairJobs findRepairJobById(int id) {
        return repairJobsRepository.findById(id).orElseThrow();
    }
    
    @Transactional
    public RepairReportsOperators saveOperatorData(RepairReportsOperators savable) {
        return operatorsRepo.save(savable);
    }
    
    @Transactional
    public ReportUsedAssets saveUsedAssets(ReportUsedAssets savable) {
        return usedAssetsRepo.save(savable);
    }
    
    public void deleteByRepairOperatorIdAndTypeId(int operatorId, int typeId) {
        usedAssetsRepo.deleteByRepairOperatorIdAndTypeId(operatorId, typeId);
    }

    public RepairReportsOperators addSelectedOperatorToReportByEmail(RepairReports report, String email, int millisFromJobStart){

        RepairReportsOperators newRepairOperator = report.getOperators().stream().filter(o -> o.getUserName().equals("new")).findFirst().orElseThrow();
        report.getOperators().remove(newRepairOperator);

        User operator = userService.findOne(email);

        newRepairOperator = RepairReportsOperators.builder()
                .id(newRepairOperator.getId())
                .report(report)
                .userName(operator.getFirstName() + " " + operator.getLastName())
                .timeOfWorkMillis(millisFromJobStart)
                .jobsDone(newRepairOperator.getJobsDone())
                .jobDoneDatetime(Timestamp.valueOf(LocalDateTime.now()))
                .usedSpareparts(newRepairOperator.getUsedSpareparts())
                .build();

        report.getOperators().add(newRepairOperator);
        return newRepairOperator;
    }
}
