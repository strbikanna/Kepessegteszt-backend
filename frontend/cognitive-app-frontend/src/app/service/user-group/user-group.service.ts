import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {Observable} from "rxjs";
import {Group, Organization} from "../../model/user-group";
import {User} from "../../model/user.model";

@Injectable({
    providedIn: 'root'
})
export class UserGroupService {

    constructor(private http: HttpClient, private httpService: SimpleHttpService) {
    }

    getAllOrganizations(): Observable<Organization[]> {
        return this.http.get<Organization[]>(`${this.httpService.baseUrl}/user_group/org`);
    }

    getAllGroups(): Observable<Group[]> {
        return this.http.get<Group[]>(`${this.httpService.baseUrl}/user_group/group`);
    }

    searchOrganization(name: string): Observable<Organization[]> {
        return this.http.get<Organization[]>(`${this.httpService.baseUrl}/user_group/search/org?name=${name}`);
    }

    searchGroup(name: string): Observable<Group[]> {
        return this.http.get<Group[]>(`${this.httpService.baseUrl}/user_group/search/group?name=${name}`);
    }

    createOrganization(organization: Organization): Observable<Organization> {
        return this.http.post<Organization>(`${this.httpService.baseUrl}/user_group/organization`, organization);
    }

    createGroup(group: Group): Observable<Group> {
        return this.http.post<Group>(`${this.httpService.baseUrl}/user_group/group`, group);
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

    getGroupOrOrgMembers(groupId: number): Observable<User[]> {
        return this.http.get<User[]>(`${this.httpService.baseUrl}/user_group/members/${groupId}`);
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

    searchMembers(groupIds: number[], name: string): Observable<User[]> {
        return this.http.post<User[]>(`${this.httpService.baseUrl}/user_group/members/search?name=${name}`, groupIds);
    }

    getGroupsInOrganization(orgId: number): Observable<Group[]> {
        return this.http.get<Group[]>(`${this.httpService.baseUrl}/user_group/child_groups/org?id=${orgId}`);
    }

    getGroupsInGroup(groupId: number): Observable<Group[]> {
        return this.http.get<Group[]>(`${this.httpService.baseUrl}/user_group/child_groups/group?id=${groupId}`);
    }

}
