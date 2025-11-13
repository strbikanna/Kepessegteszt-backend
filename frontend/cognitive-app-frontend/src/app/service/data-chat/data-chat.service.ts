import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {map, Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DataChatService {

  private path = "/assistant/question"
  constructor(private http: HttpClient, private httpBase: SimpleHttpService) { }

    sendQuestion(question: string): Observable<string> {
        return this.http.post(this.httpBase.baseUrl + this.path, question).pipe(
            map((response: any) => response.response)
        )
    }
}
