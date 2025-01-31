/**
 * User model specifically used for admin page. Only used to get contacts!
 */
export class AuthUser {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    roles: string[];
    contacts: AuthUser[] | undefined;

    constructor(id: number, username: string, firstName: string, lastName: string, email: string, roles: string[], contacts: AuthUser[] | undefined) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
        this.contacts = contacts;
    }
}