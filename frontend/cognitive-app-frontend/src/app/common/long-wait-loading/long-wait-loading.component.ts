import { Component } from '@angular/core';
import {TEXTS} from "../../text/app.text_messages";
import {combineLatest, interval, map, Observable, take, timer} from "rxjs";
import {AsyncPipe} from "@angular/common";

@Component({
  selector: 'app-long-wait-loading',
  standalone: true,
  imports: [
    AsyncPipe
  ],
  templateUrl: './long-wait-loading.component.html',
  styleUrl: './long-wait-loading.component.scss'
})
export class LongWaitLoadingComponent {
  private messages = TEXTS.loading_messages
  protected currentMessage = this.messages[0]

  readonly message$: Observable<string> = timer(0, 5600).pipe(
      map(i => this.messages[i]),
      take(this.messages.length)
  );
  readonly dots$ = interval(800).pipe(map(i => ' .  '.repeat((i % 4))));

  readonly display$ = combineLatest([this.message$, this.dots$]).pipe(
      map(([msg, dots]) => `${msg} ${dots}`)
  );

}
