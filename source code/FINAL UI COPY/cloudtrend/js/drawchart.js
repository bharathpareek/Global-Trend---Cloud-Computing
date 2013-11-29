function displayLineChart(paramsTitle, paramsViewDate, paramsPageViews, paramsChartArea)
{
	var intArray = new Array();
	for(var i=0; i<paramsPageViews.length; i++)
	{
		intArray.push(parseInt(paramsPageViews[i]))
	}

	var s1 = intArray ;
	var ticks = paramsViewDate;

	var plot1 = $.jqplot (paramsChartArea, [s1],{
		title: 'Wikipedia Trend for ' + paramsTitle,
		seriesDefaults: {
			pointLabels: { show: true },
			rendererOptions: { smooth: true }
		},
		axes: {
			xaxis: {
				renderer: $.jqplot.CategoryAxisRenderer,
				ticks: ticks
			},
		yaxis: {
				label:'#views',
				pad: 1.05,
				tickOptions: {formatString: '%d'}
			}
		},
    highlighter: {
      show: false
    },
    cursor: {
      show: true,
      tooltipLocation:'sw'
    }
	});
}

function displayCompareLineChart(paramsViewDate, paramsCompareOne, paramsComparetwo, paramsTitle)
{
	var intArrayOne = new Array();
	for(var i=0; i<paramsCompareOne.length; i++)
	{
		intArrayOne.push(parseInt(paramsCompareOne[i]))
	}

	var intArrayTwo = new Array();
	for(var i=0; i<paramsComparetwo.length; i++)
	{
		intArrayTwo.push(parseInt(paramsComparetwo[i]))
	}

	var s1 = intArrayOne ;
	var s2 = intArrayTwo ;
	var ticks = paramsViewDate;

	var plot1 = $.jqplot ('compareLineChart', [s1,s2],{
		title: 'Comparison of Wikipedia Trends for ' + paramsTitle[0] + ' and ' + paramsTitle[1],
		seriesDefaults: {
			pointLabels: { show: true },
			rendererOptions: { smooth: true }
		},
		axes: {
			xaxis: {
				renderer: $.jqplot.CategoryAxisRenderer,
				ticks: ticks
			},
		yaxis: {
				label:'#views',
				pad: 1.05,
				tickOptions: {formatString: '%d'}
			}
		},
    highlighter: {
      show: false
    },
    cursor: {
      show: true,
      tooltipLocation:'sw'
    }
	});
}


function displaychart(){ 
var s1 = [262234, 172362, 558363, 576198, 512269, 404645];
    var s2 = [191496, 210345, 409231, 435682, 431791, 282810];
    // Can specify a custom tick Array.
    // Ticks should match up one for each y value (category) in the series.
    var ticks = ['12 AM', '4AM', '10AM', '1PM', '6PM', '10PM'];
     
    var plot1 = $.jqplot('chart1', [s1, s2], {
title: 'Comparison of WikiPages Views on October 14th 2012',
        // The "seriesDefaults" option is an options object that will
        // be applied to all series in the chart.
        seriesDefaults:{
            renderer:$.jqplot.BarRenderer,
            rendererOptions: {fillToZero: true}
        },
        // Custom labels for the series are specified with the "label"
        // option on the series option.  Here a series option object
        // is specified for each series.
        series:[
            {label:'German'},
            {label:'Russian'}
        ],
        // Show the legend and put it outside the grid, but inside the
        // plot container, shrinking the grid to accomodate the legend.
        // A value of "outside" would not shrink the grid and allow
        // the legend to overflow the container.
        legend: {
            show: true,
            placement: 'outsideGrid'
        },
        axes: {
            // Use a category axis on the x axis and use our custom ticks.
            xaxis: {
			label:'Time',
                renderer: $.jqplot.CategoryAxisRenderer,
                ticks: ticks
            },
            // Pad the y axis just a little so bars can get close to, but
            // not touch, the grid boundaries.  1.2 is the default padding.
            yaxis: {
			label:'#views',
                pad: 1.05,
                tickOptions: {formatString: '%d'}
            }
        }
    });
}


function displayBarChart(paramsTitle, paramsViewDate, paramsPageViews){ 
	var intArray = new Array();
	for(var i=0; i<paramsPageViews.length; i++)
	{
		intArray.push(parseInt(paramsPageViews[i]))
	}

	var s1 = intArray;
	var ticks = paramsViewDate;

    	var plot1 = $.jqplot('chart3', [s1], {
		title: 'Wikipedia Trend for ' + paramsTitle,
		animate: !$.jqplot.use_excanvas,
		   
		seriesDefaults:{
			renderer:$.jqplot.BarRenderer,
			pointLabels: { show: true },
			rendererOptions: {fillToZero: true}
		},
        axes: {
            xaxis: {
                renderer: $.jqplot.CategoryAxisRenderer,
                ticks: ticks
            },
            
            yaxis: {
			label:'#views',
			min:0,
                pad: 1.05,
                tickOptions: {formatString: '%d'}
            }
        }
    });
}


function displayTrendBarChart(paramsTitle, paramsViewDate, paramsPageViews, paramsChartTitle){ 
	var intArray = new Array();
	for(var i=0; i<paramsPageViews.length; i++)
	{
		intArray.push(parseInt(paramsPageViews[i]))
	}

	var s1 = intArray;
	var ticks = paramsViewDate;

    	var plot1 = $.jqplot('barChartTrend', [s1], {
		title: 'Wikipedia Trend for ' + paramsChartTitle,
		animate: !$.jqplot.use_excanvas,
		   
		seriesDefaults:{
			renderer:$.jqplot.BarRenderer,
			pointLabels: { show: true },
			rendererOptions: {fillToZero: true}
		},
        
		axes: {
			xaxis: {
                renderer: $.jqplot.CategoryAxisRenderer,
                ticks: ticks
            },
            
            yaxis: {
			label:'#views',
			min:0,
                pad: 1.05,
                tickOptions: {formatString: '%d'}
            }
        }
    });
}

function displayCompareChart(paramsViewDate, paramsCompareOne, paramsComparetwo, paramsTitle)
{
	var intArrayOne = new Array();
	for(var i=0; i<paramsCompareOne.length; i++)
	{
		intArrayOne.push(parseInt(paramsCompareOne[i]))
	}

	var intArrayTwo = new Array();
	for(var i=0; i<paramsComparetwo.length; i++)
	{
		intArrayTwo.push(parseInt(paramsComparetwo[i]))
	}

	var s1 = intArrayOne ;
	var s2 = intArrayTwo ;
	var ticks = paramsViewDate;
    
	var plot1 = $.jqplot('compareChart', [s1, s2], {
		title: 'Comparison of Wikipedia Trends for ' + paramsTitle[0] + ' and ' + paramsTitle[1],
        seriesDefaults:{
			renderer:$.jqplot.BarRenderer,
			pointLabels: { show: true },
			rendererOptions: {fillToZero: true}
        },
       
		series:[
            {label:paramsTitle[0]},
            {label:paramsTitle[1]}
        ],
       
		legend: {
            show: true,
            placement: 'outsideGrid'
        },
        
		axes: {
			xaxis: {
				renderer: $.jqplot.CategoryAxisRenderer,
				ticks: ticks
			},

            yaxis: {
				label:'#views',
				pad: 1.05,
				tickOptions: {formatString: '%d'}
			}
		}
	});
}

function hello(){alert("hello world 11");}
function displayPieChart(paramsTitle, paramsPageViews)
{
//alert("pie chart called");
//alert(paramsTitle);
//alert(paramsPageViews);
	var s1 = paramsTitle;
	var s2 = paramsPageViews;
	var s3 = [];
	
	for (var i=0;i<s1.length;i++)
	{ 
		var s4 = [s1[i],parseInt(s2[i])];
		s3.push(s4);
	}

	var data = s3;
	var plot1 = jQuery.jqplot ('pieChart', [data], 
	{ 
		seriesDefaults: {
			renderer: jQuery.jqplot.PieRenderer, 
			rendererOptions: { showDataLabels: true }
		}, 
      
		legend: { show:true, location: 'e' }
	});
}