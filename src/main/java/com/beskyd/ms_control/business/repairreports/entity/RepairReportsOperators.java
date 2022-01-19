package com.beskyd.ms_control.business.repairreports.entity;

import com.beskyd.ms_control.config.addLogic.JsonAware;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = "usedSpareparts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RepairReportsOperators implements JsonAware{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "report_id")
    private RepairReports report;
    
    private String userName;
    
    private Integer timeOfWorkMillis;

    private String jobsDone;

    private Timestamp jobDoneDatetime;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "operator", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonManagedReference
    //@Fetch(value = FetchMode.SUBSELECT)
    private Set<ReportUsedAssets> usedSpareparts;


    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("report", report.toJSONObject());
        jo.put("userName", userName);
        jo.put("timeOfWorkMillis", timeOfWorkMillis);
        jo.put("jobDoneDatetime", jobDoneDatetime);
        jo.put("jobsDone", jobsDone);
        jo.put("usedSpareparts", usedSpareparts);
        return jo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jobDoneDatetime, jobsDone, timeOfWorkMillis, userName);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RepairReportsOperators other = (RepairReportsOperators) obj;
        return Objects.equals(id, other.id) && Objects.equals(jobDoneDatetime, other.jobDoneDatetime) && Objects.equals(jobsDone, other.jobsDone) && Objects.equals(report,
                other.report) && Objects.equals(timeOfWorkMillis, other.timeOfWorkMillis) && Objects.equals(userName, other.userName);
    }

}
