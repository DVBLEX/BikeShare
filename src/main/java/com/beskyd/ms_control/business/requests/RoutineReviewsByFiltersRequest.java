package com.beskyd.ms_control.business.requests;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class RoutineReviewsByFiltersRequest {
    private int page;
    private int pageSize;
    private boolean dateAsc;
    private boolean sortByScheme;
    private boolean withBollards;
    private boolean withGraffiti;
    private boolean withWeeds;
    private boolean withBikes;
    private boolean withStation;
    private boolean filterByBollards;
    private boolean filterByGraffiti;
    private boolean filterByWeeds;
    private boolean filterByBikes;
    private boolean filterByStation;
}
