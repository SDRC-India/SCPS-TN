import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'areaFilter'
})
export class AreaFilterPipe implements PipeTransform {

  transform(areas: any, areaLevel: number, parentAreaId: number)  {

    
    if(areas != undefined && areas != null && areaLevel != undefined && areaLevel != null && (!areas.District || (parentAreaId != undefined && parentAreaId != null)) ){      
    

        return areas.filter(area => area.area_Parent_NId == parentAreaId && area.area_Level == areaLevel)   
    }
    else {
      return [];
    }
  }

}
