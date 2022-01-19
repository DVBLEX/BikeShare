package com.beskyd.ms_control.business.reports.stocks;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsRepository;
import com.beskyd.ms_control.business.distributions.DistributionAssets;
import com.beskyd.ms_control.business.distributions.DistributionAssetsRepository;
import com.beskyd.ms_control.business.reports.responses.StocksResponse;
import com.beskyd.ms_control.business.stockrequests.RequestProductsTypesList;
import com.beskyd.ms_control.business.stockrequests.RequestProductsTypesListRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public final class StocksReportService {
    private final TypeOfAssetsRepository typeOfAssetsRepository;
    private final RequestProductsTypesListRepository requestProductsTypesListRepository;
    private final DistributionAssetsRepository distributionAssetsRepository;

    public List<StocksResponse> getReport(LocalDate startDate, LocalDate endDate) {
        final Timestamp start = Timestamp.valueOf(startDate.atStartOfDay());
        final Timestamp end = Timestamp.valueOf(endDate.atStartOfDay().plusDays(1));
        List<TypeOfAssets> typeOfAssetsList = typeOfAssetsRepository.findAll();
        Map<Integer, Integer> requestedMap = requestProductsTypesListRepository
                .findByRange(start, end)
                .stream()
                .collect(Collectors.groupingBy(RequestProductsTypesList::getIdProdType,
                        Collectors.summingInt(RequestProductsTypesList::getOrderValue)));
        Map<Integer, Integer> distributedMap = distributionAssetsRepository
                .findByRange(start, end)
                .stream()
                .collect(Collectors.groupingBy(DistributionAssets::getTypeOfAssetsId,
                        Collectors.summingInt(DistributionAssets::getQuantity)));

        return typeOfAssetsList.stream().map(toa -> new StocksResponse(toa.getGroupName(),
                requestedMap.getOrDefault(toa.getId(), 0),
                distributedMap.getOrDefault(toa.getId(), 0)))
                .filter(toa -> toa.getDistributed() != 0 || toa.getRequested() != 0)
                .collect(Collectors.toList());
    }
}
