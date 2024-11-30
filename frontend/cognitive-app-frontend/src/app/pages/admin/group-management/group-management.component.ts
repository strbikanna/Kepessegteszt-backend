import {Component, inject, OnInit} from '@angular/core';
import {Group, Organization} from "../../../model/user-group";
import {TEXTS} from "../../../utils/app.text_messages";
import {UserGroupService} from "../../../service/user-group/user-group.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {Address, User} from "../../../model/user.model";
import {Observable, of} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {AddUserToGroupDialogComponent} from "./add-user-to-group-dialog/add-user-to-group-dialog.component";
import {CreateGroupDialogComponent} from "./create-group-dialog/create-group-dialog.component";
import {CreateOrgDialogComponent} from "./create-org-dialog/create-org-dialog.component";
import {UserInfo} from "../../../auth/userInfo";


@Component({
    selector: 'app-group-management',
    templateUrl: './group-management.component.html',
    styleUrls: ['./group-management.component.scss']
})
export class GroupManagementComponent implements OnInit {
    text = TEXTS.group_management;
    selectedOrganization?: Organization
    selectedGroup?: Group
    selected: boolean = false
    showCannotAccessData: boolean = false

    adminsOfGroup: Observable<User[]> = of([])
    membersOfGroup: Observable<User[]> = of([])

    selectedIdKey: string = 'selectedId'

    constructor(private service: UserGroupService, private router: Router, private route: ActivatedRoute, private location: Location) {
    }

    dialog = inject(MatDialog);

    openAddUserDialog(addAdmin: boolean) {
        this.dialog.open(AddUserToGroupDialogComponent, {
            data: {
                title: addAdmin ? this.text.add_admin : this.text.add_member,
                onOk: addAdmin ? (user: User) => this.addAdmin(user.username)
                    : (user: User) => this.addMember(user.username),
                onCancel: () => {
                }
            },
        });
    }

    openCreateGroupDialog() {
        this.dialog.open(CreateGroupDialogComponent, {
            data: {
                parentGroupName: this.selectedGroup?.name ?? '',
                organization: this.selectedGroup?.organization ?? this.selectedOrganization,
                onOk: (group: { name: string; organization: Organization }) => this.createGroup(group),
                onCancel: () => {
                }
            },
        });
    }

    openCreateOrganizationDialog() {
        this.dialog.open(CreateOrgDialogComponent, {
            data: {
                onOk: (organization: { name: string; address: Address }) => this.createOrganization(organization),
                onCancel: () => {
                }
            },
        });
    }

    ngOnInit() {
        const id = this.route.snapshot.queryParams[this.selectedIdKey]
        if (id) {
            this.onGroupIdSelected(id)
        }
    }

    onGroupIdSelected(id: number) {
        this.selectedOrganization = undefined
        this.selectedGroup = undefined
        this.selected = false
        this.updateUrlParams(id)
        this.loadGroup(id)
    }

    removeMember(username: string) {
        const id = this.selectedGroup?.id ?? this.selectedOrganization?.id
        if (!!id) {
            this.service.removeMemberFromGroup(id, username).subscribe(
                () => this.loadMembersOfGroup(id)
            )
        }
    }

    removeAdmin(username: string) {
        const id = this.selectedGroup?.id ?? this.selectedOrganization?.id
        if (!!id) {
            this.service.removeAdminFromGroup(id, username).subscribe(
                () => {
                    this.loadAdminsOfGroup(id)
                    this.loadMembersOfGroup(id)
                })
        }
    }

    addMember(username: string) {
        let id = this.selectedGroup?.id
        if (!!id) {
            this.service.addMemberToGroup(id, username).subscribe(
                () => this.loadMembersOfGroup(id!!)
            )
        }else{
            id = this.selectedOrganization?.id
            if(!!id){
                this.service.addMemberToOrganization(id, username).subscribe(
                    () => this.loadMembersOfGroup(id!!)
                )
            }
        }
    }

    addAdmin(username: string) {
        const id = this.selectedGroup?.id ?? this.selectedOrganization?.id
        if (!!id) {
            this.service.addAdminToGroup(id, username).subscribe(
                () => {
                    this.loadAdminsOfGroup(id)
                    this.loadMembersOfGroup(id)
                }
            )
        }
    }
    canCreateOrg(): boolean {
        return UserInfo.isAdmin()
    }

    private createGroup(group: { name: string; organization: Organization }) {
        this.service.createGroup(group, this.selectedGroup?.id).subscribe(group => {
            this.onGroupIdSelected(group.id)
        })
    }

    private createOrganization(organization: { name: string; address: Address }) {
        this.service.createOrganization(organization).subscribe(organization => {
            this.onGroupIdSelected(organization.id)
        })
    }

    private loadGroup(id: number) {
        this.service.getById(id).subscribe(group => {
            if ('organization' in group) {
                this.selectedGroup = group as Group
            } else {
                this.selectedOrganization = group as Organization
            }
            if(!group.canWrite){
                this.showCannotAccessData = true
                return
            }
            this.showCannotAccessData = false
            this.selected = true
            this.loadMembersOfGroup(id)
            this.loadAdminsOfGroup(id)
        });

    }

    private loadMembersOfGroup(id: number) {
        this.membersOfGroup = this.service.getGroupOrOrgMembers(id)
    }

    private loadAdminsOfGroup(id: number) {
        this.adminsOfGroup = this.service.getGroupOrOrgAdmins(id)
    }

    private updateUrlParams(id: number) {
        const params = {
            selectedId: id,
        };
        const urlTree = this.router.createUrlTree(['/group-management'], {
            relativeTo: this.route,
            queryParams: params,
            queryParamsHandling: 'merge',
        });
        this.location.go(urlTree.toString());
    }
}
