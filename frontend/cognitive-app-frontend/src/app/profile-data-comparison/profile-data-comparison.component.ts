import {Component} from '@angular/core';
import {ProfileData} from "../model/profile_data.model";
import {Observable, of} from "rxjs";
import {AbilityType} from "../model/ability.model";

@Component({
    selector: 'app-profile-data-comparison',
    templateUrl: './profile-data-comparison.component.html',
    styleUrls: ['./profile-data-comparison.component.scss']
})
export class ProfileDataComparisonComponent {

    profileData: Observable<ProfileData[]> = of(
        [
            {
                ability: {
                    code: '1',
                    name: 'Ability 1',
                    description: 'Description 1',
                    type: AbilityType.FLOAT
                },
                value: 1.0
            },
            {
                ability: {
                    code: '2',
                    name: 'Ability 2',
                    description: 'Description 2',
                    type: AbilityType.FLOAT                },
                value: 0.8
            },
            {
                ability: {
                    code: '3',
                    name: 'Ability 3',
                    description: 'Description 3',
                    type: AbilityType.FLOAT
                },
                value: 1.6
            }
        ]
    )
    comparisonData: Observable<ProfileData[]> = of(
        [
            {
                ability: {
                    code: '1',
                    name: 'Ability 1',
                    description: 'Description 1',
                    type: AbilityType.FLOAT
                },
                value: 0.8
            },
            {
                ability: {
                    code: '2',
                    name: 'Ability 2',
                    description: 'Description 2',
                    type: AbilityType.FLOAT
                },
                value: 0.7
            },
            {
                ability: {
                    code: '3',
                    name: 'Ability 3',
                    description: 'Description 3',
                    type: AbilityType.FLOAT
                },
                value: 1.5
            }
        ]
    )
    comparisonTitle: 'avg' | 'max' | 'min' = 'avg';

}
