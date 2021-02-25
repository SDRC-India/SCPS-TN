import { Component, OnInit, ElementRef, ViewChild, Input, SimpleChanges } from '@angular/core';
import * as d3Scale from 'd3-scale';
import * as d3Shape from 'd3-shape';
import * as d3Array from 'd3-array';
import * as d3Axis from 'd3-axis';
import * as D3 from 'd3';
declare var $:any;

@Component({
  selector: 'app-dashboard-line-chart',
  templateUrl: './dashboard-line-chart.component.html',
  styleUrls: ['./dashboard-line-chart.component.scss']
})
export class DashboardLineChartComponent implements OnInit {

  @ViewChild('linechart') element: ElementRef;

  private host;
  
  @Input() lineData:any[];

  MultiLineChartData: MultiLineChart[];


  

  data: any[];
  datas: Set<String>

  svg: any;
  margin = { top: 20, right: 15, bottom: 60, left: 50 };
  g: any;
  width: number;
  height: number;
  x;
  y;
  z;
  line;
  private htmlElement: HTMLElement;

  constructor() {}

  formatData()
  {
    this.MultiLineChartData=[];
    this.lineData.forEach(element=>{
      let lineChartArray :LineChart[]=[]
      element.forEach(data => {
        let lineChart:LineChart={
          axis:data.date,
          value:data.value,
          zaxis:data.source,
          class:data.cssClass
        }
        lineChartArray.push(lineChart);
      });
      this.MultiLineChartData.push({id:lineChartArray[0].zaxis,parentAreaName:lineChartArray[0].zaxis,values:lineChartArray});
    })

  }


  ngOnInit() {
    this.htmlElement = this.element.nativeElement;
    this.host = D3.select(this.htmlElement);

    this.initChart();
    this.drawAxis();
    this.drawPath();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.htmlElement = this.element.nativeElement;
    this.host = D3.select(this.htmlElement);

    this.initChart();
    this.drawAxis();
    this.drawPath();
  }

  private initChart(): void {
    this.formatData();
    this.datas = new Set();

    this.MultiLineChartData.map((v) => v.values.map((v) => this.datas.add(v.axis)));
    this.data = Array.from(this.datas);
    this.host.html('');

    // d3.selectAll('#lineChart svg').remove();
    this.svg =this.host.append("svg")
      .attr("width", 640 )
      .attr("height",  280-this.margin.top);

    this.width = 640 - this.margin.left - this.margin.right
    this.height = 280-this.margin.top-this.margin.bottom;

    this.g = this.svg.append("g").attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");

    this.x = d3Scale.scaleBand().range([0, this.width]).padding(1.2);
    this.y = d3Scale.scaleLinear().range([this.height, 0])
    this.z = d3Scale.scaleOrdinal(['#717171']);

    this.line = d3Shape.line()
      .x((d: any) => this.data.indexOf(d.axis) > -1 ? this.x(d.axis) : null)
      .y((d: any) => this.data.indexOf(d.axis) > -1 ? this.y(d.value) : null)


    this.x.domain(this.data);

    this.y.domain([
      0,
      d3Array.max(this.MultiLineChartData, function (c) { return d3Array.max(c.values, function (d) { return parseFloat(d.value.toString()); }); })
    ]);

    this.z.domain(this.MultiLineChartData.map(function (c) { return c.id; }));
  }

  private drawAxis(): void {
    this.g.append("g")
      .attr("class", "axis axis--x")
      .attr("transform", "translate(0," + this.height + ")")
      .call(d3Axis.axisBottom(this.x))
      .selectAll("text")
      .attr("font-size", "9px")
      .attr("transform", "rotate(-35)")
      .attr("dx", "-1.0em")
      .attr("dy", "9")
      .append("text")
      .attr("transform", "rotate(0)")
      .attr("x", this.width)
      .attr("dx", "2.0em")
      .attr("dy", "1.5em")
      .attr("fill", "#000")
      .text("Timperiod");;

    this.g.append("g")
      .attr("class", "axis axis--y")
      .call(d3Axis.axisLeft(this.y))
      .selectAll("text")
      .attr("font-size", "10px")
      .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", "0.71em")
      .attr("fill", "#000")
      .text("Value");
  }

  private drawPath(): void {
    this.g.append("g")
      .attr("class", "grid")
      .attr("transform", "translate(0," + this.height + ")")
      .selectAll("text").remove();

    this.g.append("g")
      .attr("class", "grid")
      .selectAll("text").remove();

    let area = this.g.selectAll(".area")
      .data(this.MultiLineChartData)
      .enter().append("g")
      .attr("class", "area");

    area.append("path")
      .attr("class", "line")
      .attr("d", (d) => this.line(d.values))
      // .attr("class", (d) => d.values[0].class)


    area.selectAll("g.dot")
      .data(this.MultiLineChartData)
      .enter().append("g")
      .attr("class", "dot")
      .selectAll("circle")
      .data(function (d) { return d.values; })
      .enter().append("circle")
      .attr("r", (d, i) => this.data.indexOf(d.axis) > -1 ? 4 : 0)
      .attr('class', function (d) { return "dot" })
      .attr("cx", (d, i) => this.data.indexOf(d.axis) > -1 ? this.x(d.axis) : null)
      .attr("cy", (d, i) => this.data.indexOf(d.axis) > -1 ? this.y(d.value) : null)
      .on("mouseover", function showPopover(d) {
        $(this).popover({
          title: '',
          placement: 'top',
          container: 'body',
          trigger: 'manual',
          html: true,
          content: function () {
            return "<div> Time Period : " + d.axis +
            "</div>" + "Value : " + d.value;
          }
        });
        $(this).popover('show');
      })
      .on("mouseout", function showPopover(d) {
        $('.popover').each(function () {
          $(this).remove();
        });
      });





    // if (this.MultiLineChartData.length == 1) {
    //   area.selectAll("g.value")
    //     .data(this.MultiLineChartData)
    //     .enter().append("g")
    //     .attr("class", "value")
    //     .selectAll("circle")
    //     .data(function (d) { return d.values; })
    //     .enter().append("text")
    //     .attr("x", (d) => this.x(d.axis)-10)
    //     .attr("y", (d) => this.y(d.value))
    //     .attr("dx", "0.3em")
    //     .attr("dy", "1.85em")
    //     .attr("font-size", "8px")
    //     .text(function (d) { return d.value; });
    // }


  }

  defined(): boolean {
    return true;
  }


}
