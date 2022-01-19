package com.beskyd.ms_control.business.audit;

import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserDoesNotExistException;
import com.beskyd.ms_control.business.usermanagement.UserService;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OperationsLoggingService {

    private static final String VALUE_KEY = "value";
    
    private final SystemOperationsRepository operationsRepository;
    private final OperationsLogRepository operationsLogRepository;
    private final UserService userService;
    
    @PersistenceContext
    private EntityManager em;

    @Inject
    public OperationsLoggingService(SystemOperationsRepository operationsRepository, OperationsLogRepository operationsLogRepository, UserService userService) {
        this.userService = userService;
        this.operationsRepository = operationsRepository;
        this.operationsLogRepository = operationsLogRepository;
    }
    
    public List<SystemOperations> findAllSystemOperations(){
        return operationsRepository.findAll();
    }
    
    public List<OperationsLog> findAllLogs(){
        return operationsLogRepository.findAll(Sort.by("id").descending());
    }
    
    public List<OperationsLog> findLogsByFilters(List<String> userEmails, List<String> groups){
        return findLogsByFilters(userEmails, groups, null, null, null);
    }
    
    public List<OperationsLog> findLogsByFilters(List<String> userEmails, List<String> groups, Long startDateMilis, Long endDateMilis){
        return findLogsByFilters(userEmails, groups, startDateMilis, endDateMilis, null);
    }
    
    /**
     * Searches for logs with passed {@code userEmails}, {@code groups} (action groups) and {@code orderNumber}
     * {@code userEmails} and {@code groups} should not be null, but may be empty
     * @param userEmails
     * @param groups
     * @param orderNumber
     * @return
     */
    public List<OperationsLog> findLogsByFilters(List<String> userEmails, List<String> groups, Long startDateMilis, Long endDateMilis, String orderNumber){
        if(userEmails == null || groups == null) {
            throw new NullPointerException();
        }
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OperationsLog> cq = cb.createQuery(OperationsLog.class);
        
        List<Predicate> params = new ArrayList<>();
        
        Root<OperationsLog> row = cq.from(OperationsLog.class);
        In<String> names = cb.in(row.get("userEmail"));
        for(String email : userEmails) {
            User user = userService.findOne(email);
            names.value(user.getFirstName() + " " + user.getLastName());
        }
        if(!userEmails.isEmpty()) {
            params.add(names);
        }
        
        Join<OperationsLog, SystemOperations> operations = row.join("operation");
        In<String> actionGroups = cb.in(operations.get("actionGroup"));
        for(String group : groups) {
            actionGroups.value(group);
        }
        if(!groups.isEmpty()) {
            params.add(actionGroups);
        }  
        if(startDateMilis != null && startDateMilis > 0) {
            params.add(cb.greaterThanOrEqualTo(row.get("timeStamp"), new Date(startDateMilis)));
        }
        if(endDateMilis != null && endDateMilis > 0) {
            params.add(cb.lessThanOrEqualTo(row.get("timeStamp"), new Date(endDateMilis)));
        }
        
        cq.select(row);
        
        if(orderNumber == null) {
            cq.where(params.toArray(new Predicate[0]));
        } else {
            //cq.where(emails, actionGroups, cb.like(row.get("dataObject"), pattern));//its not clear how to search by orderId
            Predicate allParams = cb.and(params.toArray(new Predicate[0]));
            
            final String dataObjectKey = "dataObject";
            
            Predicate predicateWithOrder = cb.and(allParams, cb.or(cb.like(row.get(dataObjectKey), "%OrderId\":" + orderNumber + "%"), 
                                                                   cb.like(row.get(dataObjectKey), "%OrdersIds\":[%" + orderNumber + "%]%"),
                                                                   cb.like(row.get(dataObjectKey), "%Id\":" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%id\":" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%Id\":\"" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%id\":\"" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%Name\":" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%name\":" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%Name\":\"" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%name\":\"" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%Id:" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%id:" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%Id: " + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%id: " + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%Name:" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%name:" + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%Name: " + orderNumber + "%"),
                                                                   cb.like(row.get(dataObjectKey), "%name: " + orderNumber + "%")));
            
            cq.where(predicateWithOrder);
        }
        cq.orderBy(cb.desc(row.get("id")));
        
        return em.createQuery(cq).getResultList();
    }
    
    /**
     * find {@link OperationsLog} by pages
     * @param pageNum zero-based page index
     * @param pageSize page size
     * @return
     */
    public List<OperationsLog> findByPages(int pageNum, int pageSize){
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("id").descending());
        Page<OperationsLog> page = operationsLogRepository.findAll(pageable);
        
        return page.getContent();
    }
    
    /**
     * Find {@link OperationsLog} by id.
     * @param id
     * @return OperationsLog object if present, null - if not
     */
    public OperationsLog findLogById(Integer id) {
        return operationsLogRepository.findById(id).orElse(null);
    }
    
    /**
     * Creates and saves new log row into DB
     * @param actionCode
     * @param userEmail
     * @param value may by null; will be put to "value" dataObject's field of pushed log row
     * @return id of saved log
     */
    public Integer pushLog(int actionCode, String userEmail, String value) {
        JSONObject dataObject = new JSONObject();
        
        if(value != null) {
            dataObject.put(VALUE_KEY, value);
        }
        
        return pushLog(actionCode, userEmail, dataObject);
    }
    
    /**
     * Creates and saves new log row into DB
     * Sets id-data name as 'id'
     * @param actionCode
     * @param userEmail
     * @param newBody may be null;
     * @param oldBody may be null;
     * @param id of changed data
     * @return id of saved log
     */
    public Integer pushLog(int actionCode, String userEmail, JsonAware newBody, JsonAware oldBody, String id) {
        return pushLog(actionCode, userEmail, newBody, oldBody, id, "id");
    }
    
    /**
     * Creates and saves new log row into DB
     * @param actionCode
     * @param userEmail
     * @param newBody may be null;
     * @param oldBody may be null;
     * @param id of changed data
     * @param idName name of identifier data
     * @return id of saved log
     */
    public Integer pushLog(int actionCode, String userEmail, JSONObject newBody, JSONObject oldBody, String id, String idName) {
        JSONObject dataObject = new JSONObject();
        
        if(newBody != null) {
            JSONObject newValue = new JSONObject();
            newValue.put(VALUE_KEY, newBody);
            
            dataObject.put("newValue", newValue);
        }
        
        if(oldBody != null) {
            JSONObject oldValue = new JSONObject();
            oldValue.put(VALUE_KEY, oldBody);
            
            dataObject.put("oldValue", oldValue);
        }
        dataObject.put(idName, id);
        return pushLog(actionCode, userEmail, dataObject);
        
    }
    
    /**
     * Creates and saves new log row into DB
     * @param actionCode
     * @param userEmail
     * @param newBody may be null;
     * @param oldBody may be null;
     * @param id of changed data
     * @param idName name of identifier data
     * @return id of saved log
     */
    public Integer pushLog(int actionCode, String userEmail, JsonAware newBody, JsonAware oldBody, String id, String idName) {
        JSONObject dataObject = new JSONObject();
        
        if(newBody != null) {
            JSONObject newValue = new JSONObject();
            newValue.put(VALUE_KEY, newBody.toJSONObject());
            
            dataObject.put("newValue", newValue);
        }
        
        if(oldBody != null) {
            JSONObject oldValue = new JSONObject();
            oldValue.put(VALUE_KEY, oldBody.toJSONObject());
            
            dataObject.put("oldValue", oldValue);
        }
        dataObject.put(idName, id);
        return pushLog(actionCode, userEmail, dataObject);
        
    }
    
    /**
     * Creates and saves new log row into DB
     * @param actionCode
     * @param userEmail
     * @param dataObject may be null;
     * @return id of saved log
     * @throws UserDoesNotExistException if User with this userEmail doesn't exist
     * @throws SystemOperationDoesNotExistException if SystemOperation with this actionCode doesn't exist
     */
    public Integer pushLog(int actionCode, String userEmail, JSONObject dataObject) {
        User user = userService.findOne(userEmail);

        String userName = "-";

        if(user != null) {
            userName = user.getFirstName() + " " + user.getLastName();
        }
        
        Optional<SystemOperations> sysOperationOpt = operationsRepository.findById(actionCode);
        if(sysOperationOpt.isEmpty()) {
            throw new SystemOperationDoesNotExistException(actionCode);
        }
               
        OperationsLog log = new OperationsLog(sysOperationOpt.get(), userName, dataObject);
        log.setTimeStamp(Timestamp.valueOf(LocalDateTime.now()));
        
        return operationsLogRepository.save(log).getId();
    }
    
    /**
     * Returns a list of all system operations groups.
     * Uses this native query: select distinct action_group from system_operations
     * @return list of strings
     */
    public List<String> getSystemOperationsGroupList(){
        return em.createNativeQuery("select distinct action_group from system_operations").getResultList();
    }
    
}
