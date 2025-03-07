import {Address} from "./user.model";

export interface Organization {
    id: number;
    name: string;
    address: Address;
    canWrite: boolean;
}

export interface Group {
    id: number;
    name: string;
    organization: Organization;
    childGroupIds: number[];
    canWrite: boolean;
}