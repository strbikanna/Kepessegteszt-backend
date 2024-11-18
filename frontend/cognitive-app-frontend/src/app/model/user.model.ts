export interface User{
  username: string,
  firstName: string,
  lastName: string,
  roles: string[],
  birthDate?: Date,
  address?: {
    city: string,
    street: string,
    houseNumber: string,
    zip: string
  },
  gender?: string,
}

