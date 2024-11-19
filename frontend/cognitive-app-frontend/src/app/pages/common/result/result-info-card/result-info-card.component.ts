import {Component, Input, OnInit} from '@angular/core';
import {Result} from "../../../../model/result.model";
import {TEXTS} from "../../../../utils/app.text_messages";
import {FlatTreeControl} from '@angular/cdk/tree';
import {ArrayDataSource} from '@angular/cdk/collections';


@Component({
    selector: 'app-result-info-card',
    templateUrl: './result-info-card.component.html',
    styleUrls: ['./result-info-card.component.scss']
})
export class ResultInfoCardComponent implements OnInit{

    @Input({required: true}) result!: Result;

    protected readonly texts = TEXTS.result.result_info
    protected resultTree: TreeNode[] = [];
    protected configTree: TreeNode[] = [];

    ngOnInit() {
        this.resultTree = this.createTreeFromJson(this.result.result);
        this.configTree = this.createTreeFromJson(this.result.config);
        this.configDataSource = new ArrayDataSource(this.configTree);
        this.resultDataSource = new ArrayDataSource(this.resultTree);
    }

    treeControl = new FlatTreeControl<TreeNode>(
        node => node.level,
        node => this.hasChild(node.level, node)
    );
    hasChild = (_: number, node: TreeNode) => !!node.children && node.children.length > 0;
    resultDataSource!: ArrayDataSource<TreeNode>
    configDataSource!: ArrayDataSource<TreeNode>

    private createTreeFromJson(json: any, level: number = 0): TreeNode[] {
        return Object.keys(json).map(key => {
            if (typeof json[key] === 'object') {
                return {name: key, children: this.createTreeFromJson(json[key], level+1), level: level}
            } else {
                return {name: key, value: json[key], level: level}
            }
        })
    }


}

interface TreeNode {
    level: number;
    name: string;
    value?: any | undefined;
    children?: TreeNode[] | undefined;
}
