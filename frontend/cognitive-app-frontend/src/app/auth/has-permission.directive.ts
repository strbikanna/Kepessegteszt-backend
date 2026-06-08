import {Directive, Input, TemplateRef, ViewContainerRef} from '@angular/core';
import {Permission} from "../utils/constants";
import {AuthService} from "./auth.service";

@Directive({
  selector: '[appHasPermission]',
  standalone: true
})
export class HasPermissionDirective {

  @Input() set appHasPermission(permission: Permission) {
    if (this.auth.hasPermission(permission)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }

  constructor(
      private templateRef: TemplateRef<any>,
      private viewContainer: ViewContainerRef,
      private auth: AuthService
  ) {}

}
