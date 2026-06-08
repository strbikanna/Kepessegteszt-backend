import {Injectable} from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {map, Observable} from "rxjs";
import {Group, Organization} from "../../model/user/user-group";
import {Address, User} from "../../model/user/user.model";
import {UserInfo} from "../../auth/userInfo";
import {UserGroup} from "../../model/user/user_group.model";
import {log} from "echarts/types/src/util/log";

@Injectable({
    providedIn: 'root'
})
export class UserGroupService {

    constructor(private http: HttpClient, private httpService: SimpleHttpService) {
    }

    getAllOrganizations(): Observable<Organization[]> {
        return this.http.get<Organization[]>(`${this.httpService.baseUrl}/user_group/org`).pipe(
            map(groups => groups.map(this.convertOrg))
        );
    }

    getById(id: number): Observable<Organization | Group> {
        return this.http.get<Organization | Group>(`${this.httpService.baseUrl}/user_group?id=${id}`).pipe(
            map(group => this.convertOrgOrGroup(group))
        );
    }

    getAllGroups(): Observable<Group[]> {
        return this.http.get<Group[]>(`${this.httpService.baseUrl}/user_group/group`).pipe(
            map(groups => groups.map(this.convertGroup))
        );
    }

    searchOrganization(name: string): Observable<Organization[]> {
        return this.http.get<Organization[]>(`${this.httpService.baseUrl}/user_group/search/org?name=${name}`).pipe(
            map(groups => groups.map(this.convertOrg))
        );
    }

    searchUserGroup(name: string): Observable<UserGroup[]> {
        return this.http.get<Group[]>(`${this.httpService.baseUrl}/user_group/search/all?name=${name}`).pipe(
            map(groups => groups.map(this.convertOrgOrGroup))
        );
    }

    createOrganization(organization: {name: string, address: Address}): Observable<Organization> {
        return this.http.post<Organization>(`${this.httpService.baseUrl}/user_group/organization`, organization).pipe(
            map(org => this.convertOrg(org))
        );
    }

    createGroup(group: {name: string, organization: Organization}, parentGroupId?: number): Observable<Group> {
        let url = `${this.httpService.baseUrl}/user_group/group`;
        if (parentGroupId !== undefined) {
            url += `?parentGroupId=${parentGroupId}`;
        }
        return this.http.post<Group>(url, group).pipe(
            map(group => this.convertGroup(group))
        );
    }

    addMemberToGroup(groupId: number, username: string) {
        const params = new HttpParams().set('username', username).set('groupId', groupId.toString());
        return this.http.patch(`${this.httpService.baseUrl}/user/group`, null, {params: params});
    }

    addMemberToOrganization(groupId: number, username: string) {
        const params = new HttpParams().set('username', username).set('groupId', groupId.toString());
        return this.http.patch(`${this.httpService.baseUrl}/user/org`, null, {params: params});
    }

    removeMemberFromGroup(groupId: number, username: string) {
        const params = new HttpParams().set('username', username).set('groupId', groupId.toString());
        return this.http.delete(`${this.httpService.baseUrl}/user_group/member`, {params: params});
    }

    getGroupOrOrgMembers(groupId: number, pageIndex: number): Observable<User[]> {
        return this.http.get<User[]>(`${this.httpService.baseUrl}/user_group/members/${groupId}?pageIndex=${pageIndex}`);
    }

    getGroupOrOrgAdmins(groupId: number): Observable<User[]> {
        return this.http.get<User[]>(`${this.httpService.baseUrl}/user_group/admins/${groupId}`);
    }

    addAdminToGroup(groupId: number, username: string) {
        const params = new HttpParams().set('username', username).set('groupId', groupId.toString());
        return this.http.patch(`${this.httpService.baseUrl}/user_group/admins`, null, {params: params});
    }

    removeAdminFromGroup(groupId: number, username: string) {
        const params = new HttpParams().set('username', username).set('groupId', groupId.toString());
        return this.http.delete(`${this.httpService.baseUrl}/user_group/admins`, {params: params});
    }


    getGroupsInOrganization(orgId: number): Observable<Group[]> {
        return this.http.get<Group[]>(`${this.httpService.baseUrl}/user_group/child_groups/org?id=${orgId}`).pipe(
            map(groups => groups.map(this.convertGroup))
        );
    }

    getGroupsInGroup(groupId: number): Observable<Group[]> {
        return this.http.get<Group[]>(`${this.httpService.baseUrl}/user_group/child_groups/group?id=${groupId}`).pipe(
            map(groups => groups.map(this.convertGroup))
        );
    }

    private convertGroup(group: any): Group{
        const user = UserInfo.currentUser
        group.canWrite = group.adminUsernames.includes(user.username) || UserInfo.isAdmin()
        return group
    }
    private convertOrg(group: any): Organization{
        const user = UserInfo.currentUser
        group.canWrite = group.adminUsernames.includes(user.username) || UserInfo.isAdmin()
        return group
    }

    private convertOrgOrGroup(group: any): Group | Organization {
        const user = UserInfo.currentUser
        group.canWrite = group.adminUsernames.includes(user.username) || UserInfo.isAdmin()
        return group
    }

}
