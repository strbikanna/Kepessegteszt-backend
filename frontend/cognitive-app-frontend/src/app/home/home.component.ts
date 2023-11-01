import {Component} from '@angular/core';
import {TEXTS} from "../utils/app.text_messages";
import {UserInfo} from "../auth/userInfo";
import {Role} from "../utils/constants";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {

  texts = TEXTS.home
  user = UserInfo.currentUser


    getFeatureMessageForUser(){
    if(this.user === undefined){
        return this.texts.login
    }
    if(this.user.roles.includes(Role.SCIENTIST)){
        return this.texts.features_scientist
    }
    if(this.user.roles.includes(Role.TEACHER )){
        return this.texts.features_teacher
    }
    if(this.user.roles.includes(Role.PARENT)){
        return this.texts.features_parent
    }
    if(this.user.roles.includes(Role.STUDENT)){
        return this.texts.features_student
    }
    else return ''
  }

}
