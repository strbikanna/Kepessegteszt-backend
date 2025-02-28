export interface User{
  username: string,
  firstName: string,
  lastName: string,
  roles: string[],
  birthDate?: Date,
  address?: Address,
  gender?: string,
}

export interface Address{
    city: string,
    street: string,
    houseNumber: string,
    zip: string
}

