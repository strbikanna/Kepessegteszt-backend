import {Directive, Input, TemplateRef, ViewContainerRef} from '@angular/core';
import {Permission} from "../utils/constants";
import {AuthService} from "./auth.service";

@Directive({
  selector: '[appHasAnyPermission]',
  standalone: true
})
export class HasAnyPermissionDirective {

  @Input() set appHasAnyPermission(permissions: Permission[]) {
    if (this.auth.hasAnyPermission(permissions)) {
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
