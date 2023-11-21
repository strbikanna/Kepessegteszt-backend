import {Injectable} from '@angular/core';
import {SimpleHttpService} from "../utils/simple-http.service";
import {Observable} from "rxjs";
import {Ability} from "../model/ability.model";

@Injectable({
  providedIn: 'root'
})
export class AbilityService extends SimpleHttpService{

  private path = "/ability";
  getAllAbilities() : Observable<Ability[]>{
    return this.http.get<Ability[]>(`${this.baseUrl}${this.path}/all`);
  }
}
