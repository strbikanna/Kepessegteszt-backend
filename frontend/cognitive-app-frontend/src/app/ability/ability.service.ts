import {Injectable} from '@angular/core';
import {SimpleHttpService} from "../utils/simple-http.service";
import {Observable, of} from "rxjs";
import {Ability, AbilityType} from "../model/ability.model";

@Injectable({
  providedIn: 'root'
})
export class AbilityService extends SimpleHttpService{

  getAllAbilities() : Observable<Ability[]>{
    return this.mockData;
  }

  private mockData: Observable<Ability[]> = of([
    {
      code: "Gf",
        name: "Geschicklichkeit",
        description: "Geschicklichkeit1",
      type: AbilityType.ENUM,
    },
    {
      code: "Gf",
        name: "Geschicklichkeit2",
        description: "Geschicklichkeit",
      type: AbilityType.ENUM,
    },
    ])
}
