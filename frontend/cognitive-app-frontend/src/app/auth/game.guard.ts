import {CanActivateFn, Router} from '@angular/router';
import {GameInfo} from "./gameInfo";
import {inject} from "@angular/core";
import {UserInfo} from "./userInfo";

/**
 * Guards playground to be only accessible with game token
 * @param route
 * @param state
 */
export const gameGuard: CanActivateFn = (route, state) => {
  const router = inject(Router)
  if(GameInfo.accessToken && GameInfo.currentGameId && UserInfo.currentUser){
    return true;
  }
  console.log('Access denied to playground, redirecting to games.')
  return router.parseUrl('/games')
};
