import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'trueFalse'
})
export class TrueFalsePipe implements PipeTransform {

  transform(value: any): string {
    if(typeof value === 'boolean'){
        return value ? 'IGEN' : 'NEM';
    }
    return value;
  }

}
