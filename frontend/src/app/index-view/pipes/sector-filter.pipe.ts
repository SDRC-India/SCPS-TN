import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'sectorFilter'
})
export class SectorFilterPipe implements PipeTransform {

  transform(sectorList: any, parentSectorId: number)  {

    
    if(sectorList != undefined && sectorList != null && (!sectorList.District || (parentSectorId != undefined && parentSectorId != null)) ){      
    

        return sectorList.filter(sector => sector.ic_Parent_NId == parentSectorId)   
    }
    else {
      return [];
    }
  }

}
