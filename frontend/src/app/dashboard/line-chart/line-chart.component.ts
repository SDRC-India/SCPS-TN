import { Component, OnInit, ViewEncapsulation, OnChanges, ViewChild, ElementRef, Input } from '@angular/core';
import * as d3 from 'd3';

declare var $ :any;

@Component({
  selector: 'sdrc-line-chart',
  templateUrl: './line-chart.component.html',
  styleUrls: ['./line-chart.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class LineChartComponent implements OnInit, OnChanges{

  @ViewChild('linechart') private chartContainer: ElementRef;
  @Input() private data: any;

  constructor(private hostRef: ElementRef) { }

ngOnInit() {
  if(this.data){
    this.createChart(this.data);
  }
}

ngOnChanges(changes){
  if(this.data && changes.data.previousValue){
    this.createChart(this.data);
  }
}

createChart(data){
  let el = this.chartContainer.nativeElement;
  d3.select(el).selectAll("*").remove();
  var margin = {
    top : 20,
		right : 15,
		bottom : 60,
		left : 50
  }, width = $(this.hostRef.nativeElement).parent().width() - margin.left - margin.right
  if(el.clientWidth > 565)
  var height = 350	- margin.top - margin.bottom;
  else
  var height=250- margin.top - margin.bottom;

var x = d3.scaleBand().range([0, width], 1.0);
var y = d3.scaleLinear().rangeRound(
   [ height, 0 ]);

var xAxis = d3.axisBottom().scale(x).ticks(5);
var yAxis = d3.axisLeft().scale(y)
.ticks(5);

var dataNest = d3.nest().key(function(d) {
 return d.key;
}).entries(data);

var lineFunctionCardinal = d3.line()
 .defined(function(d) {  return d && d.value!= null; })
 .x(function(d) {   
   return x(d.timeperiod)+width/data.length * dataNest.length / 2;
 }).y(function(d) {
   return y(d.value);
 }).curve(d3.curveCardinal);
 
y.domain([ 0, 100 ]);

// Adds the svg canvas
var svg = d3.select(el).append("svg").attr("id",
   "trendsvg").attr("width",
   width + margin.left + margin.right).attr(
   "height",
   height + margin.top + margin.bottom + 70)
   .append("g").attr(
       "transform",
       "translate(" + margin.left + ","
           + (margin.top + 50) + ")").style(
       "fill", "#333");

x.domain(data.map(function(d) {
 return d.timeperiod;
}));
y.domain([ 0, d3.max(data, function(d) {
 return d.value;
}) ]);

svg.append("g").attr("class", "x axis")
.attr(
   "transform", "translate(0," + height + ")")
   .call(xAxis).append("text").attr("x",
       width).attr("y",
       "65").attr("dx", ".71em")																			
   
   .text("Time Period").style({"fill":
       "#333","text-align":"right", "text-anchor": "end",
     "font-weight": "bold",
     "letter-spacing": "1px"
   });
d3.selectAll(".x.axis .tick text").attr("dx", "0").attr("dy",
   "9").style({"text-anchor":
"middle","font-size":"11px","font-weight":"normal"});

 svg.selectAll("text");
 svg.append("g").attr("class", "y axis").call(yAxis)
 .append("text").attr("transform",
     "rotate(-90)").attr("y", -50).attr("x", -height/2).attr(
     "dy", ".71em").text("Value")
     .style({"text-anchor":
     "middle", "fill": "#333",
       "font-weight": "bold",
       "letter-spacing": "1px"
     });
// adding multiple lines in Line chart
for (let index = 0; index < dataNest.length; index++) {

 let series = svg.append(
     "g").attr("class", "series tag"+ dataNest[index].key.split(" ")[0]).attr("id",
     "tag" + dataNest[index].key.split(" ")[0]);

     let path = svg.selectAll(".series#tag"+dataNest[index].key.split(" ")[0])
     .append("path")
     .attr("class", "line tag"+dataNest[index].key.split(" ")[0])
     .attr("id", "tag" + dataNest[index].key.split(" ")[0])
     .attr(
         "d",
         function(d) {
             return lineFunctionCardinal(dataNest[index].values);

           }).style("stroke", function(d) {

      return dataNest[index].values[0].cssClass;
     }).style("stroke-width", "2px").style(
         "fill", "none").style("cursor", function(d){

             return "default";
             }).on("mouseover",
                 function(d) {
               if($(this).attr("id") == "tagP-Average")
                 showPopover.call(this, dataNest[3].values[0]);
             }).on("mouseout", function(d) {
           removePopovers();
         });			;
      
 svg.selectAll(".series#tag"+dataNest[index].key.split(" ")[0]).select(".point").data(function() {
   return dataNest[index].values;
 }).enter().append("circle").attr("id",
     "tag" + dataNest[index].key.split(" ")[0]).attr(
     "class", function(d){
       return dataNest[index].key.split(" ")[0]
       }).attr("cx",
     function(d) {
       return x(d.timeperiod)+width/data.length * dataNest.length / 2;
     }).attr("cy", function(d) {
   return y(d.value);
 }).attr("r",  function(d) {
   if(d.value!=null && d.key == "CL")
     return "3px";
   else
     return "3px";}).style("fill", function(d) {
  return dataNest[index].values[0].cssClass;
 }).style("stroke", "none").style(
     "stroke-width", "2px").style("cursor", "pointer").on("mouseover",
     function(d) {
       // d3.select(this).moveToFront();
       showPopover.call(this, d);
     }).on("mouseout", function(d) {
   removePopovers();
 });			
  
}

svg.append("text").attr("x", width / 2)// author
.attr("y", height + 90).attr("dy", ".3em")
.text("Time Period")
.style({ 
       "fill": "rgb(66, 142, 173)",
       "font-weight": "bold",
       "text-anchor": "middle",
       "font-size": "13px"
 })

function removePopovers() {
 $('.popover').each(function() {
   $(this).remove();
 });
}
function showPopover(d) {
 $(this).popover(
     {
       title : '',
       placement : 'top',
       container : 'body',
       trigger : 'manual',
       html : true,
       animation: false,
       content : function() {
         return "<b class='blue-text'>"
         + d.key + "</b><br>Time Period: <span class='navy-text'>" + d.timeperiod
             + "</span><br/>" + "Data Value: <span class='navy-text'>"
             + d.value +"</span><br/>" +(d.areaName? "Area Name: <span class='navy-text'>"
             + d.areaName +"</span>":"");
       }
     });
 $(this).popover('show');
}

 d3.selection.prototype.moveToFront = function() {
 return this.each(function(){
     this.parentNode.appendChild(this);
  });
 };
  d3.selectAll(".domain, .y.axis .tick line").style({"fill": "none", "stroke": "#333"});
  d3.selectAll("circle.point").moveToFront();
}

}
