<%@ page language="java" contentType="text/html; UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" name="viewport"
          content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Reports</title>


    <script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

    <%@ include file="/resources/views/html-headers/header.html" %>

    <script type="application/javascript"
            src="/msc/resources/logic/reports/reports.js"></script>

    <script type="application/javascript"
            src="/msc/resources/logic/reports/repair-history.js"></script>

    <script type="application/javascript"
            src="/msc/resources/directives/confirm-modal/confirm-modal.js"></script>

    <script type="application/javascript"
            src="/msc/resources/directives/content-modal/content-modal.js"></script>
    <script src="/msc/resources/components/html2pdf/html2pdf.min.js"></script>

</head>
<body data-ng-app="mainModule"
      style="height: 100%;"
      data-ng-controller="reportsCtrl">

<%@ include file="/resources/views/navbar.html" %>

<div class="container pt-2 pb-2"
     style="background-color: white; min-height: 100%;">

    <ul class="nav nav-tabs mb-1" role="tablist" data-ng-cloak>
        <li class="nav-item">
            <a
                class="nav-link" data-ng-class="{'active': activeTab === 'general'}"
                id="tab-general" href="#" data-toggle="tab" role="tab"
                aria-selected="{{activeTab === 'general' ? 'true' : 'false'}}"
                data-ng-click="setActiveTab('general')">
                General
            </a>
        </li>
        <li class="nav-item">
            <a
                class="nav-link" data-ng-class="{'active': activeTab === 'purchases'}"
                id="tab-purchases" href="#" data-toggle="tab" role="tab"
                aria-selected="{{activeTab === 'purchases' ? 'true' : 'false'}}"
                data-ng-click="setActiveTab('purchases')">
                Purchases
            </a>
        </li>
        <li class="nav-item">
            <a
                class="nav-link" data-ng-class="{'active': activeTab === 'repair-history'}"
                id="tab-repair-history" href="#" data-toggle="tab" role="tab"
                aria-selected="{{activeTab === 'repair-history' ? 'true' : 'false'}}"
                data-ng-click="setActiveTab('repair-history')">
                Repair History
            </a>
        </li>
    </ul>
    <div id="content" data-ng-show="activeTab === 'general'">
        <div class="pl-3 row" data-html2canvas-ignore="true">
            <button id="print" class="mt-2 mb-2 mr-2 btn btn-primary d-inline-block" data-ng-click="printToPDF('content')">
                Print current page
            </button>
            <button class="mt-2 mb-2 mr-2 btn btn-info d-inline-block"
                    data-ng-click="exportAllToCSV()">
                Export all to CSV
            </button>

            <input date-range-picker class="mt-2 form-control form-control-md msc-daterange-width"
                   type="text" ng-model="datePickerAll.date" options="datePickerAll.options"/>
            <button class="m-2 btn btn-info d-inline-block" data-ng-click="setAllDateRange()">
                Set All
            </button>
        </div>
        <hr>
        <div class="col-md-12">
            <div class="row justify-content-md-around">
                <div class="col-md-5 m-1 border">
                    <div class="d-lg-flex justify-content-between">
                        <h6 class="font-weight-bold mt-2">Total Repair Reports per period</h6>
                        <input date-range-picker
                               class="mt-2 form-control form-control-md msc-daterange-width"
                               type="text" ng-model="datePickerRepairReports.date"
                               options="datePickerRepairReports.options"/>
                    </div>
                    <canvas id="base" class="chart-horizontal-bar"
                            chart-data="repairReportsData"
                            chart-labels="repairReportsLabels"
                            chart-options="chartOptions">
                    </canvas>
                </div>
                <div class="col-md-5 m-1 border">
                    <div class="d-lg-flex justify-content-between">
                        <h6 class="font-weight-bold mt-2">Total Routine Reviews per period</h6>
                        <input date-range-picker
                               class="mt-2 form-control form-control-md msc-daterange-width"
                               type="text" ng-model="datePickerRoutineReviews.date"
                               options="datePickerRoutineReviews.options"/>
                    </div>
                    <canvas id="base2" class="chart-horizontal-bar"
                            chart-data="routineReviewsData"
                            chart-labels="routineReviewsLabels"
                            chart-options="chartOptions">
                    </canvas>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12 m-1 border" id="grids">
                    <div class="row">
                        <h6 class="ml-3 mt-2 font-weight-bold">Stocks requested/distributed | Assets
                            acquired/ordered</h6>
                    </div>
                    <div class="d-sm-flex justify-content-between">
                        <button class="mt-2 mb-2 btn btn-primary d-inline-block"
                                data-ng-click="printToPDF('grids')"
                                data-html2canvas-ignore="true">
                            Print
                        </button>

                        <div class="mt-2 mb-2 d-sm-flex" style="width: 400px">
                            <input date-range-picker
                                   class="mr-1 mb-1 form-control form-control-md msc-daterange-width"
                                   type="text" ng-model="datePickerGrids.date"
                                   options="datePickerGrids.options"/>
                            <div class="m-1"><span class="">Show </span><select class="form-control form-control-sm"
                                                                                data-ng-change="setGridPageSize(gridPageSize)"
                                                                                data-ng-model="gridPageSize"
                                                                                data-ng-options="size for size in gridPageSizes"
                                                                                style="width: auto; display: inline-block;"></select>
                                <span class=""> entries</span>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div ag-grid="stocksGridOptions" class="ag-theme-balham col-md-6"></div>
                        <div ag-grid="assetsGridOptions" class="ag-theme-balham col-md-6"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div data-ng-if="activeTab === 'repair-history'" data-ng-controller="repairHistoryCtrl">

        <div class="row mb-2 mt-2">
                <div class="col-md-12 btn-group msc-inline-block" role="group" aria-label="Schemes" data-ng-cloak>
                    <button type="button" class="btn btn-secondary" data-ng-repeat="scheme in schemes"
                            data-ng-click="setCurrentScheme(scheme)" data-ng-disabled="scheme.name === currentScheme.name || !repairReports">{{scheme.name}}</button>
                </div>
        </div>

        <div class="row comb-2 mt-1 d-flex flex-row align-items-center">
                
                <div class="col-lg-2 d-flex align-items-center position-relative">
                    <label class="pt-2 pr-2">Bike: </label>
                    <searchable-select name="'bike-number-select'" cleaned-string="cleanStringBikeNumber" set-model="selectBikeNumber"
                                       array="bikes" search-by-field="'number'"
                                       is-disabled="!bikes" is-valid="true">
                    </searchable-select>
                    <div data-ng-if="showCleanBtnBikeNumber" class="position-absolute right-distance-20px">
                        <button data-ng-click="clearBikeNumber()"
                                class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
                            <i class="material-icons md-18 text-white">clear</i>
                        </button>
                    </div>
                </div>

                <div class="col-lg-4 pl-3 pl-lg-0 d-flex align-items-center position-relative">
                    <label class="mt-2 pr-2">Station: </label>
                    <searchable-select name="'station-select'" cleaned-string="cleanStringStation" set-model="selectStation"
                                       array="bikeStations" search-by-field="'name'"
                                       is-disabled="!bikeStations" is-valid="true">
                    </searchable-select>
                    <div data-ng-if="showCleanBtnStation" class="position-absolute right-distance-20px">
                        <button data-ng-click="clearStation()"
                                class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
                            <i class="material-icons md-18 text-white">clear</i>
                        </button>
                    </div>
                </div>

                <div class="col-lg-3 pl-3 pl-lg-0 d-flex align-items-center">
                    <label for="datepickerHistory" class="pt-2 pr-2">Date: </label>
                    <input data-ng-click="setDate()" id="datepickerHistory" date-range-picker
                           class="form-control form-control-md"
                           type="text" ng-model="datePickerRepairHistory.date"
                           options="datePickerRepairHistory.options"/>
                </div>

                <div class="col-lg-3 mt-2 mt-lg-0">
                    <button class="btn btn-info d-inline-block mr-1" data-ng-click="selectRepairHistory()">
                        Select
                    </button>

                    <button class="btn btn-dark d-inline-block" data-ng-disabled="!repairReports"
                            data-ng-click="exportRepairHistoryToCSV()">
                        Export to CSV
                    </button>
                </div>

        </div>

        <div class="row">
            <div class="col-12">
                <div data-ng-if="showGrid" ag-grid="reportsGridOptions" class="ag-theme-balham mt-2"></div>

            </div>
        </div>

    </div>


</div>
<%@ include file="/resources/views/footer.html" %>
</body>
</html>
