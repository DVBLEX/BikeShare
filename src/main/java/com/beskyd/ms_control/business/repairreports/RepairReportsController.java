package com.beskyd.ms_control.business.repairreports;

import com.beskyd.ms_control.business.audit.OperationsLoggingService;
import com.beskyd.ms_control.business.general.StatesRepository;
import com.beskyd.ms_control.business.general.StatesResponse;
import com.beskyd.ms_control.business.repairreports.entity.*;
import com.beskyd.ms_control.business.repairreports.request.CoordinatesRequest;
import com.beskyd.ms_control.business.repairreports.request.RepairHistoryRequest;
import com.beskyd.ms_control.business.repairreports.response.*;
import com.beskyd.ms_control.business.requests.RoutineReviewsByFiltersRequest;
import com.beskyd.ms_control.business.requests.SaveRoutineReviewRequest;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;
import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/msc-api/repair-reports")
public class RepairReportsController {
    
    private final RepairReportsService repairReportsService;
    private final StatesRepository statesRepository;
    private final AssetsCurrentValuesService acvService;
    private final UserService userService;
    private final RoutineReviewService routineReviewService;
    private final OperationsLoggingService logService;

    private final String SPACE_SYMBOL = " ";
    private final String COMMA_SYMBOL = ",";
    private final int DEFAULT_DAYS_BEFORE = 30;

    public RepairReportsController(RepairReportsService repairReportsService, StatesRepository statesRepository, AssetsCurrentValuesService acvService, UserService userService, 
        RoutineReviewService routineReviewService, OperationsLoggingService logService) {
        this.repairReportsService = repairReportsService;
        this.statesRepository = statesRepository;
        this.acvService = acvService;
        this.userService = userService;
        this.routineReviewService = routineReviewService;
        this.logService = logService;
    }
    
    @GetMapping
    public List<RepairReportsResponse> getAllRepairReports(){
        return RepairReportsResponse.createListFrom(repairReportsService.findAll());
    }

    @GetMapping("/{schemeName}")
    public List<RepairReportsResponse> getRepairReportsByScheme(@PathVariable("schemeName") String schemeName){
        return RepairReportsResponse.createListFrom(repairReportsService.findByScheme(schemeName));
    }
    
    @GetMapping("/done/{schemeName}")
    public List<RepairReportsResponse> getDoneRepairReportsByScheme(@PathVariable("schemeName") String schemeName){
        return RepairReportsResponse.createListFrom(repairReportsService.findDoneByScheme(schemeName));
    }

    @GetMapping("/done")
    public List<RepairReportsResponse> getDoneRepairReports(@RequestParam(required = false, name = "bikeNumber") String bikeNumber,
                                                            @RequestParam(required = false, name = "location") String location,
                                                            @RequestParam("startDate")
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                            @RequestParam("endDate")
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<RepairReports> repairReports = repairReportsService.findDone();
        if (StringUtils.isNotBlank(bikeNumber)) {
            repairReports = repairReports.stream()
                    .filter(r -> (r.getBike() != null) && r.getBike().getNumber().equals(bikeNumber))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(location)) {
            repairReports = repairReports.stream()
                    .filter(r -> r.getLocation().getId().toString().equals(location))
                    .collect(Collectors.toList());
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(DEFAULT_DAYS_BEFORE);
        }
        endDate = endDate.plusDays(1); // to include final day in result set
        Timestamp finalTo = Timestamp.valueOf(endDate.atStartOfDay()); // to use in lambda should be final or effectively final
        Timestamp finalFrom = Timestamp.valueOf(startDate.atStartOfDay());
        repairReports = repairReports.stream()
                .filter(r -> r.getReportDate().after(finalFrom) && r.getReportDate().before(finalTo))
                .collect(Collectors.toList());

        Collections.reverse(repairReports); //for required DESC sorting
        return RepairReportsResponse.createListFrom(repairReports);
    }


    @GetMapping("/bike-stations")
    public List<BikeStationsResponse> getAllBikeStations(){
        return BikeStationsResponse.createListFrom(repairReportsService.findAllBikeStations());
    }
    
    @GetMapping("/bike-stations/{schemeName}")
    public List<BikeStationsResponse> getBikeStationsByScheme(@PathVariable("schemeName") String schemeName){
        return BikeStationsResponse.createListFrom(repairReportsService.findBikeStationsByScheme(schemeName));
    }
    
    @PutMapping("/bike-stations/by-geo")
    public BikeStationsResponse getBikeStationsByCoordinates(@RequestBody CoordinatesRequest request, Principal principal) {

        User user = userService.findOne(principal.getName());

        BikeStations bikeStation = routineReviewService.findStationForCheckIn(request.getLatitude(), request.getLongitude());

        if(bikeStation != null) {
            return new BikeStationsResponse(bikeStation);
        } else {
            return null;
        }
    }
    
    @PostMapping("/bike-stations")
    public BikeStationsResponse saveBikeStation(@RequestBody BikeStationsResponse request){
        return new BikeStationsResponse(repairReportsService.saveBikeStation(request.toOriginal()));
    }
    
    @GetMapping("/bikes")
    public List<BikesResponse> getAllBikes(){
        return BikesResponse.createListFrom(repairReportsService.findAllBikes());
    }
    
    @GetMapping("/bikes/{schemeName}")
    public List<BikesResponse> getBikesByScheme(@PathVariable("schemeName") String schemeName){
        return BikesResponse.createListFrom(repairReportsService.findBikesByScheme(schemeName));
    }
    
    @PutMapping("/bikes/{schemeName}")
    public List<BikesResponse> getBikesBySchemeAndNumbers(@PathVariable("schemeName") String schemeName, @RequestBody List<String> numbers){
        return BikesResponse.createListFrom(repairReportsService.findBikesBySchemeAndNumbers(schemeName, numbers));
    }
    
    @PostMapping("/bikes")
    public BikesResponse saveBike(@RequestBody BikesResponse request){
        return new BikesResponse(repairReportsService.saveBike(request.toOriginal()));
    }
    
    @GetMapping("/report-reasons")
    public List<ReportReasonsResponse> getReportReasons(){
        return ReportReasonsResponse.createListFrom(repairReportsService.findAllReportReasons());
    }
    
    @GetMapping("/repair-reasons")
    public List<RepairReasonsResponse> getAllRepairReasons(){
        return RepairReasonsResponse.createListFrom(repairReportsService.findAllRepairReasons());
    }

    @GetMapping("/repair-reasons/station")
    public List<RepairReasonsResponse> getRepairReasonsForStation(){
        return RepairReasonsResponse.createListFrom(repairReportsService.findRepairReasonsForStation());
    }
    
    @GetMapping("/repair-reasons/bike")
    public List<RepairReasonsResponse> getRepairReasonsForBike(){
        return RepairReasonsResponse.createListFrom(repairReportsService.findRepairReasonsForBike());
    }
    
    @PostMapping("/repair-reasons")
    public RepairReasonsResponse saveRepairReason(@RequestBody RepairReasonsResponse request) {
        return new RepairReasonsResponse(repairReportsService.saveRepairReason(request.toOriginal()));
    }
    
    @DeleteMapping("/repair-reasons/{id}")
    public void deleteRepairReason(@PathVariable Integer id) {
        repairReportsService.deleteRepairReason(id);
    }
    
    @GetMapping("/repair-jobs")
    public List<RepairJobsResponse> getRepairJobs(){
        return RepairJobsResponse.createListFrom(repairReportsService.findAllRepairJobs());
    }
    
    @PostMapping("/repair-jobs")
    public RepairJobsResponse saveRepairJob(@RequestBody RepairJobsResponse request) {
        return new RepairJobsResponse(repairReportsService.saveRepairJob(request.toOriginal()));
    }
    
    @DeleteMapping("/repair-jobs/{id}")
    public void deleteRepairJob(@PathVariable Integer id) {
        repairReportsService.deleteRepairJob(id);
    }
    
    @PutMapping("/repair-history")
    public List<RepairReportsResponse> getRepairHistory(@RequestBody RepairHistoryRequest request){
        if(request.getBikeNumber().contains("Station")) {
            return RepairReportsResponse.createListFrom(repairReportsService.findByStation(request.getSchemeName(), request.getLocation()));
        } else {
            return RepairReportsResponse.createListFrom(repairReportsService.findByBike(request.getSchemeName(), request.getBikeNumber()));
        }
    }
    
    @PostMapping
    public RepairReportsResponse saveNewReport(@RequestBody RepairReportsResponse request, Principal principal) {
        if(request.getId() == null) {
            User user = userService.findOne(principal.getName());
            request.setOnStreetOperator(user.getFirstName() + " " + user.getLastName());
        }
        
        RepairReports savedReport = repairReportsService.adjustAndSave(request.toOriginal());
        
        logService.pushLog(2001, principal.getName(), savedReport, null, savedReport.getId().toString());
        
        return new RepairReportsResponse(savedReport);
    }

    @PostMapping("/{operatorEmail}/{millisFromJobStart}")
    public RepairReportsResponse saveReportJobs(
            @PathVariable("operatorEmail") String operatorEmail,
            @PathVariable("millisFromJobStart") int millisFromJobStart,
            @RequestBody RepairReportsResponse request,
            Principal principal) {
        RepairReports report = request.toOriginal();
        RepairReportsOperators newRepairOperator = repairReportsService.addSelectedOperatorToReportByEmail(report, operatorEmail, millisFromJobStart);

        if(request.getSparepartsToRemove() != null) {
            for(var sparePartToRemove : request.getSparepartsToRemove()) {
                repairReportsService.deleteByRepairOperatorIdAndTypeId(sparePartToRemove.getRepairOperatorId(), sparePartToRemove.getProductType().getId());
                for(var oper : report.getOperators()) {
                    if(sparePartToRemove.getRepairOperatorId().equals(oper.getId())) {

                        var iter = oper.getUsedSpareparts().iterator();
                        while(iter.hasNext()) {
                            var sp = iter.next();
                            if(sp.getProductType().getId().equals(sparePartToRemove.getProductType().getId())) {
                                iter.remove();

                                break;
                            }
                        }
                    }
                }
            }
        }

        RepairReports savedReport = repairReportsService.adjustAndSave(report);

        savedReport.setOperators(report.getOperators());

        RepairReportsResponse response = new RepairReportsResponse(savedReport);
        response.setTheLatestOperator(new RepairReportsOperatorsResponse(newRepairOperator));

        if (request.isCompleteAfter()) {
            response = completeJob(response.getId(), principal);
        }
        return response;
    }


    public RepairReportsResponse completeJob(
            @PathVariable("savedResponseId") int savedReportId,

            Principal principal) {
        RepairReports report = repairReportsService.findById(savedReportId);
        
        report.setState(statesRepository.findById(RepairReportsService.STATE_DONE).orElseThrow());
        report.setRepairDate(Timestamp.valueOf(LocalDateTime.now()));

        repairReportsService.adjustAndSave(report);
        
        //reduce quantity of used spare parts for every operator which worked on this report
        for(var op : report.getOperators()) {
            for(var ua : op.getUsedSpareparts()) {
                acvService.reduceQuantity(ua.getProductType().getId(), report.getLocation().getScheme().getName(), ua.getAmount());
            }
        }

        return new RepairReportsResponse(report);
    }
    
    @DeleteMapping("/used-assets/{operatorId}/{typeId}")
    public void deleteUsedAsset(@PathVariable("operatorId") int operatorId, @PathVariable("typeId") int typeId) {
        repairReportsService.deleteByRepairOperatorIdAndTypeId(operatorId, typeId);
    }
    
    @PostMapping("/from-list/{routineReviewId}")
    public List<Integer> saveReports(@PathVariable("routineReviewId") int routineReviewId, @RequestBody List<RepairReportsResponse> requests, Principal principal) {
        User user = userService.findOne(principal.getName());
        
        List<Integer> ids = new ArrayList<>();
        
        for(RepairReportsResponse r : requests) {
            if(r.getId() == null) {
                r.setOnStreetOperator(user.getFirstName() + " " + user.getLastName());
            }
            
            RepairReports rrSaved = repairReportsService.adjustAndSave(r.toOriginal());
            
            ids.add(rrSaved.getId());
            
            logService.pushLog(2001, principal.getName(), rrSaved.toJSONObject().put("routineReviewId", routineReviewId), null, null, rrSaved.getId().toString());
        }
        
        return ids;
    }
    
    @PostMapping("/collect")
    public RepairReportsResponse collectReport(@RequestBody RepairReportsResponse request) {
        RepairReports report = request.toOriginal();

        report.setState(statesRepository.findById(RepairReportsService.STATE_PENDING).orElseThrow());
        repairReportsService.adjustAndSave(report);

        return new RepairReportsResponse(report);
    }

    @GetMapping("/routine-review/{schemeName}")
    public List<RoutineReviewResponse> getAllRoutineReviews(@PathVariable("schemeName") String schemeName){
        return RoutineReviewResponse.createListFrom(routineReviewService.findAllBySchemeName(schemeName));
    }

    @PutMapping("/routine-review/{schemeName}/count")
    public int countRoutineReviewsByFilters(@PathVariable("schemeName") String schemeName,
                                                                  @RequestBody RoutineReviewsByFiltersRequest request){
        return routineReviewService.countByFilters(schemeName, request);
    }

    @PutMapping("/routine-review/{schemeName}")
    public List<RoutineReviewResponse> getRoutineReviewsByFilters(@PathVariable("schemeName") String schemeName,
                                                                  @RequestBody RoutineReviewsByFiltersRequest request){
        return RoutineReviewResponse.createListFrom(routineReviewService.findByFilters(schemeName, request));
    }
    
    @PostMapping("/routine-review/{suppressLogs}")
    public RoutineReviewResponse saveRoutineReview(@PathVariable("suppressLogs") boolean suppressLogs, @RequestBody RoutineReviewResponse request, Principal principal) {
        User user = userService.findOne(principal.getName());
        request.setOperator(user.getFirstName() + " " + user.getLastName());
        
        RoutineReview rrSaved = routineReviewService.save(request.toOriginal());
        
        if(!suppressLogs) {
            logService.pushLog(2002, principal.getName(), rrSaved, null, rrSaved.getId().toString());
        }
        
        return new RoutineReviewResponse(rrSaved);
    }

    @PostMapping("/create-form")
    public RepairReports createRepairReportFromForm(@RequestBody SaveRoutineReviewRequest.ReportRequest request, Principal principal) {
        var reportOriginal = initRepairReportFromRequest(request, principal);
        repairReportsService.adjustAndSave(reportOriginal);
        return reportOriginal;
    }

    @PostMapping("/create-completed-report")
    public RepairReports createCompletedRepairReport(@RequestBody SaveRoutineReviewRequest.ReportRequest request, Principal principal) {
        var reportOriginal = initRepairReportFromRequest(request, principal);
        repairReportsService.adjustAndSave(reportOriginal);

        if (request.isPendingCollection()) {
            collectReport(new RepairReportsResponse(reportOriginal));
            reportOriginal.setState(statesRepository.findById(RepairReportsService.STATE_NEW).orElseThrow());
            reportOriginal.setRoutineReview(false);
            reportOriginal.setCollectedDate(reportOriginal.getReportDate());
        }

        RepairReportsOperators operator = new RepairReportsOperators();
        operator.setUserName("new");
        operator.setUsedSpareparts(new HashSet<>());
        operator.setJobsDone(getJobsDoneAsStringFromRelatedRepairReasons(reportOriginal));
        Set<RepairReportsOperators> operators = new HashSet<>();
        operators.add(operator);
        reportOriginal.setOperators(operators);

        RepairReportsResponse response = new RepairReportsResponse();
        response.setId(reportOriginal.getId());
        response.setState(new StatesResponse(reportOriginal.getState()));
        response.setLocation(new BikeStationsResponse(reportOriginal.getLocation()));
        response.setBike(reportOriginal.getBike() != null ? new BikesResponse(reportOriginal.getBike()) : null);
        response.setStationItself(reportOriginal.getStationItself());
        response.setReportReason(new ReportReasonsResponse(reportOriginal.getReportReason()));
        response.setReportDate(reportOriginal.getReportDate());
        response.setOnStreetOperator(reportOriginal.getOnStreetOperator());
        response.setStreetComments(reportOriginal.getStreetComments());
        response.setOnStreetRepair(true);
        response.setOperators(RepairReportsOperatorsResponse.createSetFrom(operators));
        response.setCompleteAfter(true);
        response.setBollardNumbers(request.getBollardNumbers());
        response.setBollardComments(request.getBollardComments());
        response.setRepairReason(RepairReasonsResponse.createSetFrom(reportOriginal.getRepairReason()));


        response = saveReportJobs(principal.getName(), 0, response, principal);
        return response.toOriginal();
    }

    private String getJobsDoneAsStringFromRelatedRepairReasons(RepairReports report) {
        StringBuilder result = new StringBuilder();
        for (RepairReasons reason: report.getRepairReason()) {
            result.append(repairReportsService.findRepairJobById(reason.getId()).getJob());
            result.append(COMMA_SYMBOL);
            result.append(SPACE_SYMBOL);
        }
        result.deleteCharAt(result.lastIndexOf(COMMA_SYMBOL));
        result.deleteCharAt(result.lastIndexOf(SPACE_SYMBOL));
        return result.toString();
    }

    private RepairReports initRepairReportFromRequest(SaveRoutineReviewRequest.ReportRequest request, Principal principal) {
        User user = userService.findOne(principal.getName());

        var reportOriginal = RepairReports.builder()
                .location(request.getLocation().toOriginal())
                .bike(request.getBike() != null ? request.getBike().toOriginal() : null)
                .reportReason(request.isVandalism() ? repairReportsService.findReportReasonById(3) : repairReportsService.findReportReasonById(2))
                .repairReason(RepairReasonsResponse.toOriginals(request.getRepairReason()))
                .streetComments(request.getStreetComments())
                .bollardNumbers(request.getBollardNumbers())
                .bollardComments(request.getBollardComments())
                .routineReview(false)
                .stationItself(request.getBike() == null)
                .onStreetRepair(request.isOnStreetRepair())
                .onStreetOperator(user.getFirstName() + " " + user.getLastName())
                .build();

        if(request.isPendingCollection()) {
            reportOriginal.setId(-2);
        }

        return reportOriginal;
    }

    @PostMapping("/routine-review/with-reports")
    public void saveRoutineReviewWithReports(@RequestBody SaveRoutineReviewRequest request, Principal principal) {
        User user = userService.findOne(principal.getName());
        request.getReview().setOperator(user.getFirstName() + " " + user.getLastName());

        RoutineReview reviewSaved = routineReviewService.save(request.getReview().toOriginal());

        var stationId = new StringBuilder();
        var bikesIds = new StringBuilder("B: ");

        for(SaveRoutineReviewRequest.ReportRequest r : request.getReports()) {
            r.setOperator(user.getFirstName() + " " + user.getLastName());

            var reportOriginal = RepairReports.builder()
                    .location(r.getLocation().toOriginal())
                    .bike(r.getBike() != null ? r.getBike().toOriginal() : null)
                    .reportReason(r.isVandalism() ? repairReportsService.findReportReasonById(3) : repairReportsService.findReportReasonById(2))
                    .repairReason(RepairReasonsResponse.toOriginals(r.getRepairReason()))
                    .streetComments(r.getStreetComments())
                    .bollardNumbers(r.getBollardNumbers())
                    .bollardComments(r.getBollardComments())
                    .routineReview(true)
                    .stationItself(r.getBike() == null)
                    .build();

            if(r.isPendingCollection()) {
                reportOriginal.setId(-2);
            }

            RepairReports reportSaved = repairReportsService.adjustAndSave(reportOriginal);

            if(reportSaved.getStationItself()) {
                stationId.append("S: ").append(reportSaved.getId()).append("; ");
            } else {
                bikesIds.append(reportSaved.getId()).append(", ");
            }

            logService.pushLog(2001, principal.getName(), reportSaved.toJSONObject().put("routineReviewId", reportSaved), null, null, reportSaved.getId().toString());
        }

        if(bikesIds.length() > 3) { //means, that we have recorded at least one id
            bikesIds.delete(bikesIds.length() - 2, bikesIds.length());
        }

        var allIds = new StringBuilder();
        if(stationId.length() > 0) {
            allIds.append(stationId);
        }
        if(bikesIds.length() > 3) {
            allIds.append(bikesIds);
        }

        reviewSaved.setReports(allIds.toString());

        routineReviewService.save(reviewSaved);


        logService.pushLog(2002, principal.getName(), reviewSaved, null, reviewSaved.getId().toString());
    }
}
