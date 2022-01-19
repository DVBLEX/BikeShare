"use strict";

var SIMPLE_DATE = 'YYYY-MM-DD';
var REPORTS_ENDPOINT = projectRoot + '/msc-api/reports';

app.controller('reportsCtrl', [
    '$scope',
    '$http',
    '$timeout',
    '$rootScope',
    function ($scope, $http) {
        $scope.activeTab = 'general';
        $scope.setActiveTab = function (tabName) {
            $scope.activeTab = tabName;
        }

        $scope.repairReportsLabels = [];
        $scope.repairReportsData = [];
        $scope.routineReviewsLabels = [];
        $scope.routineReviewsData = [];

        $scope.chartOptions = {
            scales: {
                xAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        };

        $scope.gridPageSizes = [20, 50, 100];
        $scope.gridPageSize = $scope.gridPageSizes[0];

        var initialDate = {startDate: moment().subtract(1, 'month'), endDate: moment()};
        var datePickerCommonOptions = {
            locale: {format: 'DD/MM/YYYY'},
            autoApply: true,
            showDropdowns: true
        }

        var datePickerOptionsRepairReport = {
            eventHandlers: {
                'apply.daterangepicker': function (ev, picker) {
                    fillTotalReportChart($scope.datePickerRepairReports.date, 'RepairReports');
                }
            }
        };

        var datePickerOptionsRoutineReviews = {
            eventHandlers: {
                'apply.daterangepicker': function (ev, picker) {
                    fillTotalReportChart($scope.datePickerRoutineReviews.date, 'RoutineReviews');
                }
            }
        };

        var datePickerOptionsGrids = {
            eventHandlers: {
                'apply.daterangepicker': function (ev, picker) {
                    loadGridsData($scope.datePickerGrids.date);
                }
            }
        };

        $scope.datePickerAll = {
            date: initialDate,
            options: datePickerCommonOptions
        }

        $scope.datePickerRepairReports = {
            date: initialDate,
            options: {...datePickerOptionsRepairReport, ...datePickerCommonOptions}
        }

        $scope.datePickerRoutineReviews = {
            date: initialDate,
            options: {...datePickerOptionsRoutineReviews, ...datePickerCommonOptions}
        }

        $scope.datePickerGrids = {
            date: initialDate,
            options: {...datePickerOptionsGrids, ...datePickerCommonOptions}
        }

        $scope.printToPDF = function (elementId) {

            var element = document.getElementById(elementId);

            html2pdf()
                .from(element)
                .set({
                    margin: 10,
                    html2canvas: {scale: 4},
                    jsPDF: {unit: 'mm', format: 'a4', orientation: 'landscape'}
                })
                .toPdf()
                .get('pdf')
                .then(function (pdf) {
                    window.open(pdf.output('bloburl'), '_blank');
                });

        }

        $scope.exportAllToCSV = function () {

            var csvLinks = [];
            $scope.repairReportsCSVData.unshift({"scheme": "Scheme", "count": "Total Repair Reports"});
            $scope.routineReviewsCSVData.unshift({"scheme": "Scheme", "count": "Total Routine Reviews"});
            $scope.stocksCSVData.unshift({"name": "Name", "requested": "Requested", "distributed": "Distributed"});
            $scope.assetsCSVData.unshift({"name": "Name", "acquired": "Acquired", "ordered": "Ordered"});
            csvLinks.push(makeCSVLink($scope.repairReportsCSVData, "Total Repair Reports"));
            csvLinks.push(makeCSVLink($scope.routineReviewsCSVData, "Total Routine Reviews"));
            csvLinks.push(makeCSVLink($scope.stocksCSVData, "Stocks requested distributed"));
            csvLinks.push(makeCSVLink($scope.assetsCSVData, "Assets acquired ordered"));
            csvLinks.forEach(link => {
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
            });
        }

        var makeCSVLink = function (json, name) {
            var csv = JSON2CSV(json);
            var downloadLink = document.createElement("a");
            var blob = new Blob(["\ufeff", csv]);
            downloadLink.href = URL.createObjectURL(blob);
            downloadLink.download = name + ".csv";
            return downloadLink;
        };

        var JSON2CSV = function (objArray) {
            var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
            var str = '';
            var line = '';

            if ($("#labels").is(':checked')) {
                var head = array[0];
                if ($("#quote").is(':checked')) {
                    for (var index in array[0]) {
                        var value = index + "";
                        line += '"' + value.replace(/"/g, '""') + '",';
                    }
                } else {
                    for (var index in array[0]) {
                        line += index + ',';
                    }
                }

                line = line.slice(0, -1);
                str += line + '\r\n';
            }

            for (var i = 0; i < array.length; i++) {
                var line = '';

                if ($("#quote").is(':checked')) {
                    for (var index in array[i]) {
                        var value = array[i][index] + "";
                        line += '"' + value.replace(/"/g, '""') + '",';
                    }
                } else {
                    for (var index in array[i]) {
                        line += array[i][index] + ',';
                    }
                }

                line = line.slice(0, -1);
                str += line + '\r\n';
            }
            return str;
        };

        $scope.setAllDateRange = function () {

            fillTotalReportChart($scope.datePickerAll.date, 'RepairReports');
            fillTotalReportChart($scope.datePickerAll.date, 'RoutineReviews');
            loadGridsData($scope.datePickerAll.date);
        }

        var fillTotalReportChart = function (date, report) {

            $http.get(REPORTS_ENDPOINT + '/total/' + report, {
                params: {
                    startDate: date.startDate.format(SIMPLE_DATE),
                    endDate: date.endDate.format(SIMPLE_DATE)
                }
            }).then(function (response) {
                var schemes = response.data.map(r => r.scheme);
                var counts = response.data.map(r => r['count']);
                if (report === 'RepairReports') {
                    $scope.datePickerRepairReports.date = date;
                    $scope.repairReportsLabels = schemes;
                    $scope.repairReportsData = counts;
                    $scope.repairReportsCSVData = response.data;
                } else if (report === 'RoutineReviews') {
                    $scope.datePickerRoutineReviews.date = date;
                    $scope.routineReviewsLabels = schemes;
                    $scope.routineReviewsData = counts;
                    $scope.routineReviewsCSVData = response.data;
                }
            }, function (errorResponse) {
                console.log('Error: ' + errorResponse);
            });
        };

        fillTotalReportChart(initialDate, 'RepairReports');
        fillTotalReportChart(initialDate, 'RoutineReviews');

        //--grid-related-stuff--
        var stocksColumnDefs = [
            {headerName: "Name", field: "name", filter: "agNumberColumnFilter", width: 200},
            {headerName: "Requested", field: "requested", filter: "agTextColumnFilter", width: 150},
            {headerName: "Distributed", field: "distributed", filter: "agTextColumnFilter", width: 150}
        ];

        $scope.stocksGridOptions = {
            defaultColDef: {
                resizable: true
            },
            columnDefs: stocksColumnDefs,
            rowData: null,
            pagination: true,
            paginationPageSize: 20,
            angularCompileRows: true,
            domLayout: 'autoHeight'
        };

        var assetsColumnDefs = [
            {headerName: "Name", field: "name", filter: "agNumberColumnFilter", width: 200},
            {headerName: "Acquired", field: "acquired", filter: "agTextColumnFilter", width: 150},
            {headerName: "Ordered", field: "ordered", filter: "agTextColumnFilter", width: 150}
        ];

        $scope.assetsGridOptions = {
            defaultColDef: {
                resizable: true
            },
            columnDefs: assetsColumnDefs,
            rowData: null,
            pagination: true,
            paginationPageSize: 20,
            angularCompileRows: true,
            domLayout: 'autoHeight'
        };

        $scope.setGridPageSize = function (pageSize) {
            $scope.stocksGridOptions.api.paginationSetPageSize(pageSize);
            $scope.assetsGridOptions.api.paginationSetPageSize(pageSize);
        }
        //--grid-related-stuff-end---
        var loadGridsData = function (date) {
            $scope.datePickerGrids.date = date;
            $http.get(REPORTS_ENDPOINT + '/stocksRequestedDistributed', {
                params: {
                    startDate: date.startDate.format(SIMPLE_DATE),
                    endDate: date.endDate.format(SIMPLE_DATE)
                }
            }).then(function (response) {
                $scope.stocksGridOptions.api.setRowData(response.data);
                $scope.stocksCSVData = response.data;
            }, function (errorResponse) {
                console.log('Error: ' + errorResponse);
            });

            $http.get(REPORTS_ENDPOINT + '/assetsAcquiredOrdered', {
                params: {
                    startDate: date.startDate.format(SIMPLE_DATE),
                    endDate: date.endDate.format(SIMPLE_DATE)
                }
            }).then(function (response) {
                $scope.assetsGridOptions.api.setRowData(response.data);
                $scope.assetsCSVData = response.data;
            }, function (errorResponse) {
                console.log('Error: ' + errorResponse);
            });
        }
        loadGridsData(initialDate);
    }
]);
