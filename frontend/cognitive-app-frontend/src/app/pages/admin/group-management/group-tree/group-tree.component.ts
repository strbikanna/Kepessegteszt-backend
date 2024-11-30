import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {UserGroupService} from "../../../../service/user-group/user-group.service";
import {NestedTreeControl} from '@angular/cdk/tree';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {BehaviorSubject, Observable} from "rxjs";
import {TEXTS} from "../../../../utils/app.text_messages";
import {Group} from "../../../../model/user-group";


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
    text = TEXTS.group_management;
    selectedNodeId?: number;

    treeControl = new NestedTreeControl<GroupNode>(node => this.getChildNodes(node));
    dataSource = new MatTreeNestedDataSource<GroupNode>();

    @Output() onGroupIdSelectedForDetails: EventEmitter<number> = new EventEmitter<number>();

    constructor(private groupService: UserGroupService) {
    }

    ngOnInit() {
        this.dataLoading = true;
        this.groupService.getAllOrganizations().subscribe(orgs => {
            this.treeData = orgs
                .sort((a, b) => a.name.localeCompare(b.name))
                .map(org => {
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

    getChildNodes(node: GroupNode): Observable<GroupNode[]> | undefined {
        return node.children;
    }

    loadChildrenOfNode(node: GroupNode) {
        if (node.loaded) {
            return;
        }
        node.loading = true;
        if (node.level === 1) {
            this.groupService.getGroupsInOrganization(node.id).subscribe(groups => {
                const children = this.groupsToChildNodes(groups, node.level);
                node.children?.next(children);
                if (!groups || groups.length === 0) {
                    node.hasChild = false;
                }
                node.loading = false;
                node.loaded = true;
                this.dataSource.data = [...this.treeData];
            });
        } else {
            this.groupService.getGroupsInGroup(node.id).subscribe(groups => {
                const children = this.groupsToChildNodes(groups, node.level);
                node.children?.next(children);
                if (!groups || groups.length === 0) {
                    node.hasChild = false;
                }
                node.loading = false;
                node.loaded = true;
                this.dataSource.data = [...this.treeData];
            });
        }
    }

    expandNode(node: GroupNode) {
        this.loadChildrenOfNode(node);
    }

    selectGroup(groupId: number) {
        this.selectedNodeId = groupId
        this.onGroupIdSelectedForDetails.emit(groupId);
    }

    hasChild = (_: number, node: GroupNode) => node.hasChild;

    iterablesForLevel(level: number): number[] {
        return Array.from({length: level-1}, (v, k) => k + 1);
    }

    private groupsToChildNodes(groups: Group[], nodeLevel: number) {
        return groups
            .sort((a, b) => a.name.localeCompare(b.name))
            .map(group => {
                return {
                    id: group.id,
                    name: group.name,
                    level: nodeLevel + 1,
                    hasChild: true,
                    children: new BehaviorSubject<GroupNode[]>([])
                }
            });
    }

}
