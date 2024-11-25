import {Component, OnInit} from '@angular/core';
import {Organization} from "../../../model/user-group";


@Component({
  selector: 'app-group-management',
  templateUrl: './group-management.component.html',
  styleUrls: ['./group-management.component.scss']
})
export class GroupManagementComponent implements OnInit{
  selectedOrganization?: Organization
  ngOnInit() {
  }
}
