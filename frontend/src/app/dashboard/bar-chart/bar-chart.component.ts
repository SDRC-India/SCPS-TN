import { Component, OnInit,Input, ElementRef, ViewChild, ViewEncapsulation } from '@angular/core';
import * as d3 from 'd3';
declare var $:any;

@Component({
  selector: 'sdrc-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class BarChartComponent implements OnInit {
  
  @ViewChild('barChart') private chartContainer: ElementRef;
  @Input() private data: Array<any>;

  constructor(private hostRef: ElementRef) { }

  ngOnInit() {
    if (this.data) {
      if (this.data.length) {
        this.createChart(this.data);
      }else {
        let el = this.chartContainer.nativeElement;
        d3.select(el).select("svg").remove();
      }
    }
  }
  
  ngOnChanges(changes){
    if(this.data && changes.data.previousValue){
      if (this.data.length) {
        this.createChart(this.data);
      }else {
        let el = this.chartContainer.nativeElement;
        d3.select(el).select("svg").remove();
      }
    }
  }

  createChart(data){
    let el = this.chartContainer.nativeElement;
    d3.select(el).select("svg").remove();
    var n = data.length, // number of layers
    m = 10 // number of samples per layer
    var layers = data;
    layers.forEach(function(layer, i) {
      layer.forEach(function(el, j) {
        el.y = undefined;
        el.y0 = i;
      });
    });
    
    var margin = {
      top : 20,
      right : 55,
      bottom : 70,   // bottom height
      left : 40
    }, width = $(this.hostRef.nativeElement).parent().width()  - margin.left - margin.right, height = 400 // //
        // height
        - margin.top - margin.bottom;
   
    var x = d3.scaleBand().domain(data[0].map(function(d) {
      return d.axis;
    })).range([0, width]).padding(0.1);
    
    var y = d3.scaleLinear().domain([ 0, 1 ]).rangeRound(
      [ height, 0 ]);
  
    var color = [ "#1a9850", "#91cf60", "#e8b933", "#fc8d59", "#d73027" ];
  
  
    var formatTick = function(d) {
      return d.split(".")[0];
    };
   
    var xAxis = d3.axisBottom().scale(x).tickFormat(formatTick);
    var svg = d3.select(el).append("svg").attr("id",
        "columnbarChart").attr("width",
        width + margin.left + margin.right).attr("height",
        height + margin.top + margin.bottom).append("g")
        .attr(
            "transform",
            "translate(" + margin.left + ","
                + margin.top + ")");
  
    var layer = svg.selectAll(".layer").data(layers).enter()
        .append("g").attr("class", "layer").style("fill",
            function(d, i) {
              return color[i];
            }).attr("id", function(d, i) {
          return i;
        });
  
    var rect = layer.selectAll("rect").data(function(d) {
      return d;
    }).enter().append("rect").attr("x", function(d) {
      return x(d.axis);
    }).attr("y", height).attr("width", x.bandwidth()).attr(
        "height", 0).style("fill", function(d, i) {

          return d.cssColor
    }).on("mouseover", function(d) {
      showPopover.call(this, d);
    
      
  
    }).on("mouseout", function(d) {
      removePopovers();
     
      

    });
    ;
  
    svg.append("g").attr("class", "x axis").attr("transform",
        "translate(0," + height + ")").call(xAxis)
        .selectAll("text").style("text-anchor", "middle")
        .attr("class",  function(d,i){return  "evmtext"+i})
        .attr("dx", "-.2em").attr("dy", ".70em").call(wrap, x.bandwidth());

    svg.append("text").attr("class", "x axis").attr("transform",
        "translate(0," + height + ")").attr(
        "x", width / 2).attr("y", margin.bottom).attr("dx",
        "1em").style("text-anchor", "middle").text(
        "Time Period");
   
    
    var yAxis = d3.axisLeft().scale(y); 
        
    svg.append("g").attr("class", "y axis").call(yAxis)

  
    function transitionGrouped() {
      y.domain([ 0, 1 ]);
  
      rect.transition().duration(500).delay(function(d, i) {
        return i * 10;
      }).attr("x", function(d, i, j) {
        return x(d.axis) + x.bandwidth() / n * d.y0; // function(d)     
      }).attr("width", x.bandwidth() / n - 1).transition().attr(
          "y", function(d) {
            return y(d.value);
          }).attr("height", function(d) {
        return height - y(d.value);
      });
    }
  
    transitionGrouped();
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
              if(d.axis != "")
              return "<div style='color: #257ab6;'>" + d.axis + "</div>" + "Score : "
                  + d.value + "<br>Timeperiod : " + d.timePeriod;
              else
                return "<div style='color: #257ab6;'> Average: " + d.value + "%</div>" ;
            }
          });
      $(this).popover('show');
    }

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
            tspan = text.text(null).append("tspan").attr("x", 0).attr("y", y).attr("dy", dy + "em");


            if(words.length == 1) {
              let chars = words.toString().split("");
              chars.splice((chars.length/2).toFixed(), 0, '-',' ');
              tspan.text(chars.join(""));
              if (tspan.node().getComputedTextLength() > width ) {
                words = chars.join("").split(/\s+/).reverse();
              }
              tspan.text('');

            }


        while (word = words.pop()) {
          cnt++;
          line.push(word);
          tspan.text(line.join(" "));
          if (tspan.node().getComputedTextLength() > width ) {
            
              line.pop();
            
              tspan.text(line.join(" "));	
              line = [word];
             // if(cnt!=1)
              tspan = text.append("tspan").attr("x", 0).attr("y", y).attr("dy", ++lineNumber * lineHeight + dy + "em").text(word);
            
          }
        }
      });
    }
    //NEW CODE FOR DATA VALUE TEXT ON EACH BAR-----------------
    // var e0Arr = [];
    // for (var i = 0; i < data.length; i++) {
    //   e0Arr.push(data[i][0].value);
    //   layer.selectAll("evmtext" + i).data(data[i]).enter()
    //       .append("text").attr(
    //           "x",
    //           function(d) {
    //             return i == 0 ? x(d.axis)
    //                 + x.bandwidth() / 2 - 9
    //                 : x(d.axis) + x.bandwidth()
    //                     / 2 + 12;
    //           }) 
    //       .attr("y", function(d) {
    //         return y(d.value) - 3;
    //       }).style("text-anchor", "start").style("fill",
    //           "#000000").text(function(d) {
    //         return Math.round(d.value);
    //       });
    //   }  
    }

}
