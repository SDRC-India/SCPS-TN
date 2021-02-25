import { Component, OnInit, ElementRef, Input } from '@angular/core';
import * as d3 from 'd3v4';
declare var jquery:any;
declare var $ :any;

@Component({
  selector: 'app-horizontal-bar-chart',
  templateUrl: './horizontal-bar-chart.component.html',
  styleUrls: ['./horizontal-bar-chart.component.scss']
})
export class HorizontalBarChartComponent implements OnInit {


  @Input()
  data: any;

  constructor(private hostRef: ElementRef) { 
  }

  ngOnInit() {
    if(this.data){
      this.createChart(this.data);
    }
  }
  ngOnChanges(changes){
    if(changes.data.currentValue && changes.data.currentValue.length && changes.data.currentValue != changes.data.previousValue){
      this.createChart(changes.data.currentValue);
    }
    
  }

 


//   createBlankChart(data){
//     d3.select("#horizontal-bar").html("")
//     var svg = d3.select("svg").attr("width", $(this.hostRef.nativeElement).parent().width()).attr("height", function(){
//         return 700
  
//     }),
//     margin = {top: 0, right: 20, bottom: 100, left: 80}
//     if($(window).width() > 767){
//       margin = {top: 0, right: 60, bottom: 100, left: 150}
//     }
//     var width = +svg.attr("width") - margin.left - margin.right
//     var height = +svg.attr("height") - margin.top - margin.bottom;
  
//     var tooltip = d3.select("body").append("div").attr("class", "toolTip horizontal-bar-tip");
      
//     var x = d3.scaleLinear().range([0, width]);
//     var y = d3.scaleBand().range([height, 0]);

//     var g = svg.append("g")
//         .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
//     x.domain([0, d3.max(data, function(d) { return 0; })]);
//     y.domain(data.map(function(d) { return d.indicatorName; })).padding(0.3);

//     g.append("g")
//         .attr("class", "x axis")
//        	.attr("transform", "translate(0," + height + ")")
//         .call(d3.axisBottom(x).ticks(5).tickFormat(d3.format(".2f")).tickSizeInner([-height]));
//         // .text("Value (in numbers)");
// // 
//     g.append("g")
//         .attr("class", "y axis")
//         .call(d3.axisLeft(y));
    
//     g.append("text")
//       .style("font-size", "30px")
//       .style("font-weight", "bold").style("letter-spacing", "2px")
//       .style("fill", "#a53737").style("transform", "translate("+ (width/2 - 135) + "px, "+ height/2 +"px)")
//       .style("left", "50px")
//       .text("No Data Available")

//   }

  createChart(data){
    d3.select("#horizontal-bar").html("")
    var svg = d3.select("#horizontal-bar").attr("width", $(this.hostRef.nativeElement).parent().width()).attr("height", function(){
      if(data.length * 40 > 600){
        return 650
      }
      else{
        return 450
      }
    }),
    margin = {top: 0, right: 20, bottom: 100, left: 80}
    if($(window).width() > 767){
      margin = {top: 0, right: 60, bottom: 100, left: 350}
    }
    
    var width = +svg.attr("width") - margin.left - margin.right
    var height = +svg.attr("height") - margin.top - margin.bottom;
  
    var tooltip = d3.select("body").append("div").attr("class", "toolTip horizontal-bar-tip");
      
    var x = d3.scaleLinear().range([0, width]);
    var y = d3.scaleBand().range([height, 0]);

    var g = svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
    // x.domain([0, d3.max(data, function(d) { return d.value; })]);
    x.domain([0, 100]);
    y.domain(data.map(function(d) { return d.indicatorName; })).padding(0.5);

    g.append("g")
        .attr("class", "x axis")
       	.attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x).ticks(10))
        .append("text").attr("x",
       width-30).attr("y",
       "35").attr("dx", ".1em")																			
   .style("fill", "#333")
   .style("font-weight", "bold")
   .style("font-size", "13px")
   .text("Percentage")
// 
    g.append("g")
        .attr("class", "y axis")
        .call(d3.axisLeft(y))
        .selectAll("text")
        .attr("class", "indicatorName")
        .call(wrap, margin.left-5);;

    var bars = g.selectAll(".bar")
        .data(data)
      .enter().append("g")
      bars.append("rect")
        .attr("class", "bar horizontal-chart-bars")
        .attr("x", 1)
        .attr("height", y.bandwidth()
        // function(){
        //   if(y.bandwidth() < 40){
        //     return y.bandwidth()
        //   }
        //   else{
        //     return 40;
        //   }
        //   }
        )
        .attr("y", function(d) { return y(d.indicatorName); })
        .attr("width", function(d) { 
          if(d.value)
            return x(d.value);
          else
            return width;
         })

        .on("mousemove", function(d){
            tooltip
              .style("left", d3.event.pageX - 50 + "px")
              .style("top", d3.event.pageY - 70 + "px")
              .style("display", "inline-block")
              .html('AreaName: '+(d.indicatorName) + "<br>" + "Value: " + (d.value ? d.value: "Not Available"));
        })
        // .on("click", (d) => {
        // if(d.value)
        // this.getTrendChart(d)})
        .on("mouseout", function(d){ tooltip.style("display", "none");})
        .style("fill", (d) => {
          if(d.value){
            return '#0275d8'
          }
          else{
            return '#DDD'
          }
        })
        .style("cursor", (d) => {
          if(d.value){
            return 'pointer'
          }
          else{
            return 'default'
          }
        });
        bars.append("text")
          .attr("class", "label")
          //y position of the label is halfway down the bar
          .attr("y", function (d) {
              return y(d.indicatorName) + y.bandwidth() / 2 + 4;
          })
          //x position is 3 pixels to the right of the bar
          .attr("x", function (d) {
            return x(d.value)+ 5;
          })
          .attr("transform", (d) => {
            if(d.value == null){
              return "translate(30, 0)"
            }
          })
          .style("fill", (d) => {
              return "#999"
          })
          .style("font-size", (d) => {
            if(d.value == null){
              return "10px"
            }
          }).style("letter-spacing", (d) => {
            if(d.value == null){
              return "2px"
            }
          })
          
          .text(function (d) {
            if(d.value || d.value == 0)
              return d.value;
            else{
              return "Not Available"
            }
        });
    //     bars.append("text").attr("class", "not-avail")
    //     .attr("y", function (d) {
    //       return y(d.areaName) + y.bandwidth() / 2 + 4;
    //   })
    //   .attr("x", function (d) {
    //     return x(d.value)/2;
    // })

    //============Text wrap function in x-axis of column chart=====================
    function wrap(text, width) {
      text.each(function() {
        var text = d3.select(this),
            words = text.text().split(/\s+/).reverse(),
            word,
            cnt=0,
            line = [],
            lineNumber = 0,
            lineHeight = 1.1, 
            y = text.attr("y"),
            dy = parseFloat(text.attr("dy")),
            tspan = text.text(null).append("tspan").attr("x", -8).attr("y", y - 10).attr("dy", (dy) + "em").style("font-size", "12px");
        while (word = words.pop()) {
          cnt++;
          line.push(word);
          tspan.text(line.join(" "));
          if (tspan.node().getComputedTextLength() > width) {
            line.pop();
            
            tspan.text(line.join(" "));	
            line = [word];
            if(cnt!=1)
            tspan = text.append("tspan").attr("x", -8).attr("y", y -10 ).attr("dy", ++lineNumber * lineHeight + dy + "em").style("font-size", "12px").text(word);
          }
        }
      });
    }
  }

  // getTrendChart(d){
  //   this.dashboardService.getTrendChartData(d.areaNid).subscribe(res => {
  //     this.dashboardService.trendChartData = res;
  //     setTimeout(()=>{
  //       this.dashboardService.dragElement(document.getElementById("trend-chart-container"))
  //     }, 1000)
  //   })
  // }
}
