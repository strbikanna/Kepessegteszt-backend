import {Routes} from '@angular/router';
import {HomeComponent} from "../general/home/home.component";
import {ProfilePageComponent} from "../pages/common/profile/profile-page.component";
import {CognitiveProfilePageComponent} from "../pages/student/cognitive-profile/cognitive-profile-page.component";
import {loggedInGuard} from "../auth/logged-in.guard";
import {NotFoundComponent} from "../general/not-found/not-found.component";
import {GameManagementPageComponent} from "../pages/admin/game-management/game-management-page.component";
import {EditGameFormComponent} from "../pages/admin/game-management/edit-game-form/edit-game-form.component";
import {
    adminCognitiveProfileGuard,
    cognitiveProfileEditGuard,
    appRoutesGuard,
    groupManagementGuard, recommendationGuard, PermissionGuard
} from "../auth/app-routes.guard";
import {RecommendationPageComponent} from "../pages/admin/recommendation/recommendation-page.component";
import {ProfileDataComparisonPageComponent} from "../pages/student/profile-data-comparison/profile-data-comparison-page.component";
import {ResultPageComponent} from "../pages/common/result/result-page/result-page.component";
import {AdminPageComponent} from "../pages/admin/admin-page/admin-page.component";
import {adminAuthGuard} from "../auth/admin-auth.guard";
import {
    AdminCognitiveProfilePageComponent
} from "../pages/admin/admin-cognitive-profile/admin-cognitive-profile-page.component";
import {
    AdminProfileDataComparisonPageComponent
} from "../pages/admin/admin-profile-data-comparison/admin-profile-data-comparison-page.component";
import {GroupManagementComponent} from "../pages/admin/group-management/group-management.component";
import {PrivacyPolicyPageComponent} from "../general/privacy-policy-page/privacy-policy-page.component";
import {DeleteAccountComponent} from "../pages/common/delete-account/delete-account.component";
import {UserRegisterComponent} from "../pages/admin/user-register/user-register.component";
import {AbilityPageComponent} from "../pages/admin/ability-page/ability-page.component";
import {
    CognitiveProfileEditPageComponent
} from "../pages/admin/cognitive-profile-edit-page/cognitive-profile-edit-page.component";
import {GamesComponent} from "../pages/common/games/games.component";
import {ResultInsightPageComponent} from "../pages/common/result-insight-page/result-insight-page.component";
import {Permission} from "./constants";


export const appRoutes: Routes = [
    {
        path: 'admin',
        component: AdminPageComponent,
        canActivate: [PermissionGuard],
        data: {
            permissions: [Permission.MANAGE_USERS_DATA]
        },
        title: 'Felhasználók kezelése'
    },
    {
        path: 'ability',
        component: AbilityPageComponent,
        canActivate: [PermissionGuard],
        title: 'Kognitív képességek',
        data: {
            permissions: [Permission.MANAGE_ABILITIES, Permission.VIEW_ABILITIES]
        }
    },
    {
        path: 'game-management',
        component: GameManagementPageComponent,
        canActivate: [PermissionGuard],
        title: 'Játékok kezelése',
        data: {
            permissions: [Permission.MANAGE_GAMES]
        }
    },
    {
        path: 'edit-game/:id',
        component: EditGameFormComponent,
        canActivate: [PermissionGuard],
        title: 'Játék szerkesztése',
        data: {
            permissions: [Permission.MANAGE_GAMES]
        }
    },
    {
        path: 'edit-game',
        component: EditGameFormComponent,
        canActivate: [PermissionGuard],
        title: 'Játék szerkesztése',
        data: {
            permissions: [Permission.MANAGE_GAMES]
        }
    },
    {
        path: 'games',
        component: GamesComponent,
        canActivate: [PermissionGuard],
        title: 'Játékok',
        data: {
            permissions: [Permission.VIEW_GAMES]
        }
    },
    {
        path: '',
        component: HomeComponent,
        title: 'Cognitive App'
    },
    {
        path: 'profile',
        component: ProfilePageComponent,
        canActivate: [loggedInGuard],
        title: 'Profil'
    },
    {
        path: 'profile-compare',
        component: ProfileDataComparisonPageComponent,
        canActivate: [PermissionGuard],
        title: 'Profil összehasonlítás',
        data: {
            permissions: [Permission.VIEW_OWN_COGNITIVE_PROFILE_COMPARISON]
        }
    },
    {
        path: 'profile-compare-admin',
        component: AdminProfileDataComparisonPageComponent,
        canActivate: [PermissionGuard],
        title: 'Profil összehasonlítás',
        data: {
            permissions: [Permission.VIEW_OTHERS_COGNITIVE_PROFILE_COMPARISON]
        }
    },
    {
        path: 'cognitive-profile',
        component: CognitiveProfilePageComponent,
        canActivate: [PermissionGuard],
        title: 'Kognitív profil',
        data: {
            permissions: [Permission.VIEW_OWN_COGNITIVE_PROFILE]
        }
    },
    {
        path: 'cognitive-profile-admin',
        component: AdminCognitiveProfilePageComponent,
        canActivate: [PermissionGuard],
        title: 'Kognitív profil',
        data: {
            permissions: [Permission.VIEW_OTHERS_COGNITIVE_PROFILES]
        }
    },
    {
        path: 'cognitive-profile-edit',
        component: CognitiveProfileEditPageComponent,
        canActivate: [PermissionGuard],
        title: 'Kognitív profil szerkesztése',
        data: {
            permissions: [Permission.MANAGE_OTHERS_COGNITIVE_PROFILES]
        }
    },
    {
        path: 'recommendation',
        component: RecommendationPageComponent,
        canActivate: [PermissionGuard],
        title: 'Játékok ajánlása',
        data: {
            permissions: [Permission.VIEW_RECOMMENDATIONS, Permission.MANAGE_RECOMMENDATIONS]
        }
    },
    {
        path: 'result',
        component: ResultPageComponent,
        canActivate: [PermissionGuard],
        title: 'Eredmények megtekintése',
        data: {
            permissions: [Permission.VIEW_OWN_RESULTS, Permission.VIEW_OTHERS_RESULTS]
        }
    },
    {
        path: 'result-charts',
        component: ResultInsightPageComponent,
        canActivate: [PermissionGuard],
        title: 'Eredmények elemzése',
        data: {
            permissions: [Permission.VIEW_OWN_RESULTS, Permission.VIEW_OTHERS_RESULTS]
        }
    },
    {
        path: 'group-management',
        component: GroupManagementComponent,
        canActivate: [PermissionGuard],
        title: 'Felhasználói csoportok kezelése',
        data: {
            permissions: [Permission.MANAGE_GROUPS]
        }
    },
    {
        path: 'privacy-policy',
        component: PrivacyPolicyPageComponent,
        title: 'Adatvédelmi irányelvek'
    },
    {
        path: 'delete-account',
        component: DeleteAccountComponent,
        title: 'Felhasználói fiók eltávolítása',
        canActivate: [loggedInGuard]
    },
    {
        path: 'register-user',
        component: UserRegisterComponent,
        title: 'Felhasználók regisztrálása',
        canActivate: [PermissionGuard],
        data: {
            permissions: [Permission.MANAGE_USER_REGISTRATION]
        }
    },
    {
        path: '**', component: NotFoundComponent}
];
