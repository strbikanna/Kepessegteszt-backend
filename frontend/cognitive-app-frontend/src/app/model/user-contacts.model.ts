/**
 * User model specifically used for admin page.
 */
export class UserForAdmin {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    roles: string[];
    contacts: UserForAdmin[] | undefined;

    constructor(id: number, username: string, firstName: string, lastName: string, email: string, roles: string[], contacts: UserForAdmin[] | undefined) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
        this.contacts = contacts;
    }
}