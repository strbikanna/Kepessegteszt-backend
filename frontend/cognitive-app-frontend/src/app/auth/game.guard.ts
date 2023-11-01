import {CanActivateFn, Router} from '@angular/router';
import {GameInfo} from "./gameInfo";
import {inject} from "@angular/core";

export const gameGuard: CanActivateFn = (route, state) => {
  // const router = inject(Router)
  // const idParam = route.paramMap.get('game_id')
  // if(idParam && GameInfo.accessToken && parseInt(idParam) === GameInfo.currentGameId){
  //   return true;
  // }
  // console.log('Access denied to playground, redirecting to games.')
  // return router.parseUrl('/games')
  return true
};
