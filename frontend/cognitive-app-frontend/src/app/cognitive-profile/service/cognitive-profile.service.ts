import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {CognitiveProfile} from "../../model/cognitive_profile.model";
import {Ability, AbilityType} from "../../model/ability.model";

@Injectable({
  providedIn: 'root'
})
export class CognitiveProfileService {

  constructor(private http: HttpClient) { }

    /**
     * returns the last three profile snapshot
     */
    getLatestProfiles(): Observable<CognitiveProfile[]>{
        return of([{
            timestamp: new Date(2023, 2, 3),
            profileItems: new Map<Ability, number>(
                [
                    [{code: "GF", name: "Fluid Reasoning", description: "", type: AbilityType.FLOAT}, 0.1],
                    [{code: "Ga", name: "Auditive memory", description: "", type: AbilityType.FLOAT}, 0.7],
                    [{code: "Gq", name: "Quantitativ Ability", description: "", type: AbilityType.FLOAT}, 0.9],
                    [{code: "Gh", name: "Fluid Reasoning Second", description: "", type: AbilityType.FLOAT}, 0.5],
                    [{code: "Gh", name: "Fluid Reasoning T", description: "", type: AbilityType.FLOAT}, 0.5],
                ]
            )
        },
            {
                timestamp: new Date(2023, 4,9),
                profileItems: new Map<Ability, number>(
                    [
                        [{code: "GF", name: "Fluid Reasoning", description: "", type: AbilityType.FLOAT}, 0.4],
                        [{code: "Ga", name: "Auditive memory", description: "", type: AbilityType.FLOAT}, 1],
                        [{code: "Gq", name: "Quantitativ Ability", description: "", type: AbilityType.FLOAT}, 0.95],
                        [{code: "Gh", name: "Fluid Reasoning Second", description: "", type: AbilityType.FLOAT}, 0.7],
                        [{code: "Gh", name: "Fluid Reasoning T", description: "", type: AbilityType.FLOAT}, 0.6],

                    ]
                )
            },
            {
                timestamp: new Date(2023, 8,11),
                profileItems: new Map<Ability, number>(
                    [
                        [{code: "GF", name: "Fluid Reasoning", description: "", type: AbilityType.FLOAT}, 0.8],
                        [{code: "Ga", name: "Auditive memory", description: "", type: AbilityType.FLOAT}, 1.2],
                        [{code: "Gq", name: "Quantitativ Ability", description: "", type: AbilityType.FLOAT}, 1],
                        [{code: "Gh", name: "Fluid Reasoning Second", description: "", type: AbilityType.FLOAT}, 1],
                        [{code: "Gh", name: "Fluid Reasoning T", description: "", type: AbilityType.FLOAT}, 0.7],

                    ]
                )
            },
        ])
    }

  /**
   * returns the actual cognitive profile of the user logged in
   */
  getCurrentProfile(): Observable<CognitiveProfile>{
    return of({
      timestamp: new Date(),
      profileItems: new Map<Ability, number>(
          [
              [{code: "GF", name: "Fluid Reasoning", description: "", type: AbilityType.FLOAT}, 5],
              [{code: "Ga", name: "Auditive memory", description: "", type: AbilityType.FLOAT}, 9],
              [{code: "Gq", name: "Quantitativ Ability", description: "", type: AbilityType.FLOAT}, 2],
          ]
      )
    })
  }

  /**
   * returns all saved cognitive profiles of the user logged in between the given dates
   */
  getProfilesBetween(start: Date, end: Date): Observable<CognitiveProfile[]>{
      return of([{
          timestamp: start,
          profileItems: new Map<Ability, number>(
              [
                  [{code: "Ga", name: "Auditive memory", description: "", type: AbilityType.FLOAT}, 0.7],
                  [{code: "Gq", name: "Quantitativ Ability", description: "", type: AbilityType.FLOAT}, 0.9],
                  [{code: "Gh", name: "Fluid Reasoning Second", description: "", type: AbilityType.FLOAT}, 0.5],
                  [{code: "Gh", name: "Fluid Reasoning T", description: "", type: AbilityType.FLOAT}, 0.5],
              ]
          )
      },
          {
              timestamp: new Date(2023, 4,9),
              profileItems: new Map<Ability, number>(
                  [
                      [{code: "GF", name: "Fluid Reasoning", description: "", type: AbilityType.FLOAT}, 0.4],
                      [{code: "Ga", name: "Auditive memory", description: "", type: AbilityType.FLOAT}, 1],
                      [{code: "Gq", name: "Quantitativ Ability", description: "", type: AbilityType.FLOAT}, 0.95],
                      [{code: "Gh", name: "Fluid Reasoning Second", description: "", type: AbilityType.FLOAT}, 0.8],
                      [{code: "Gh", name: "Fluid Reasoning T", description: "", type: AbilityType.FLOAT}, 0.6],

                  ]
              )
          },
          {
              timestamp: end,
              profileItems: new Map<Ability, number>(
                  [
                      [{code: "GF", name: "Fluid Reasoning", description: "", type: AbilityType.FLOAT}, 0.8],
                      [{code: "Ga", name: "Auditive memory", description: "", type: AbilityType.FLOAT}, 1.2],
                      [{code: "Gq", name: "Quantitativ Ability", description: "", type: AbilityType.FLOAT}, 1],
                      [{code: "Gh", name: "Fluid Reasoning Second", description: "", type: AbilityType.FLOAT}, 1.1],
                      [{code: "Gh", name: "Fluid Reasoning T", description: "", type: AbilityType.FLOAT}, 0.7],

                  ]
              )
          },
      ])
  }
}
