import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timestamp'
})
export class TimestampPipe implements PipeTransform {

  transform(value: Date): string  {
    return `${value.toLocaleDateString()} ${value.toLocaleTimeString()}`;
  }

}
