import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timestamp'
})
export class TimestampPipe implements PipeTransform {

  transform(value: string | Date): string  {
    return value.toString().replace('T', ' ').replace('Z', '');
  }

}
