import {Injectable} from '@angular/core';
import {SimpleHttpService} from "../../utils/simple-http.service";
import {Observable} from "rxjs";
import {Ability} from "../../model/ability.model";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class AbilityService {

  private path = "/ability";
  constructor(private helper: SimpleHttpService, private http: HttpClient) {
  }
  getAllAbilities() : Observable<Ability[]>{
    return this.http.get<Ability[]>(`${this.helper.baseUrl}${this.path}/all`);
  }
}
