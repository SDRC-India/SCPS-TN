import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'searchValue'
})
export class SearchValuePipe implements PipeTransform {

  transform(value: any, searchValue,searchKey): any {

     if(searchValue)
    {
      return value?value.filter(d=>(d[searchKey] as string).toLocaleLowerCase().includes(searchValue.toLowerCase())):value
    }

    else
    {
      return value;
     
    }
   
  }
  }
