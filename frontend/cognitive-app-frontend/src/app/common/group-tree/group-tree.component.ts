import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {UserGroupService} from "../../service/user-group/user-group.service";
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import {BehaviorSubject, Observable} from "rxjs";


interface GroupNode {
    id: number;
    name: string;
    level: number;
    children?: BehaviorSubject<GroupNode[]>;
    loading?: boolean;
    hasChild: boolean;
    loaded?: boolean;
}

@Component({
    selector: 'app-group-tree',
    templateUrl: './group-tree.component.html',
    styleUrls: ['./group-tree.component.scss']
})
export class GroupTreeComponent implements OnInit {

    treeData: GroupNode[] = [];
    dataLoading = false;

    treeControl = new NestedTreeControl<GroupNode>(node => this.getChildNodes(node));
    dataSource = new MatTreeNestedDataSource<GroupNode>();

    @Output() onGroupIdSelected: EventEmitter<number> = new EventEmitter<number>();

    constructor(private groupService: UserGroupService) {
    }

    ngOnInit() {
        this.dataLoading = true;
        this.groupService.getAllOrganizations().subscribe(orgs => {
            this.treeData = orgs.map(org => {
                return {
                    id: org.id,
                    name: org.name,
                    level: 1,
                    loading: false,
                    hasChild: true,
                    children: new BehaviorSubject<GroupNode[]>([])
                }
            });
            this.dataSource.data = this.treeData;
            this.dataLoading = false;
        });
    }

    getChildNodes(node: GroupNode): Observable<GroupNode[]>| undefined{
        console.log("get child nodes", node);
        return node.children;
    }

    loadChildrenOfNode(node: GroupNode) {
        if (node.loaded) {
            console.log("children already loaded");
            return;
        }
        node.loading = true;
        if (node.level === 1) {
            this.groupService.getGroupsInOrganization(node.id).subscribe(groups => {
                const children =groups.map(group => {
                    return {
                        id: group.id,
                        name: group.name,
                        level: node.level+1,
                        hasChild: true,
                        children: new BehaviorSubject<GroupNode[]>([])
                    }
                });
                node.children?.next(children);
                if(!groups || groups.length === 0) {
                    node.hasChild = false;
                }
                node.loading = false;
                node.loaded = true;
                console.log("children loaded", node);
                this.dataSource.data = [...this.treeData];
            });
        } else {
            this.groupService.getGroupsInGroup(node.id).subscribe(groups => {
                const children = groups.map(group => {
                    return {
                        id: group.id,
                        name: group.name,
                        level: node.level + 1,
                        hasChild: true,
                        children: new BehaviorSubject<GroupNode[]>([])
                    }
                });
                node.children?.next(children);
                if(!groups || groups.length === 0) {
                    node.hasChild = false;
                }
                node.loading = false;
                node.loaded = true;
                this.dataSource.data = [...this.treeData];
            });
        }
    }

    expandNode(node: GroupNode) {
        console.log("expand node")
        this.loadChildrenOfNode(node);
        console.log(this.treeControl.isExpanded(node));
    }

    selectGroup(groupId: number) {
        this.onGroupIdSelected.emit(groupId);
    }
    hasChild = (_: number, node: GroupNode) => node.hasChild;

    iterablesForLevel(level: number): number[] {
        return Array.from({length: level}, (v, k) => k + 1);
    }

}
