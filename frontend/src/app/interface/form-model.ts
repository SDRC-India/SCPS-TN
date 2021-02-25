export class User {
    id = 0;
    name = '';
    addresses: Address[];
}

export class Address {
    street = '';
    city = '';
    state = '';
    zip = '';
}

export const users: User[] = [
    {
        id: 1,
        name: 'Ratikanta',
        addresses: [
            { street: 'Satti Choura', city: 'Cuttack', state: 'Odisha', zip: '94801' },
            { street: '456 Maple', city: 'Bhubaneswar', state: 'Odisha', zip: '23226' },
        ]
    },
    {
        id: 2,
        name: 'Laxman',
        addresses: [
            { street: 'Tamando', city: 'Bhubaneswar', state: 'Odisha', zip: '04501' },
        ]
    },
    {
        id: 3,
        name: 'Naseem',
        addresses: [
            { street: 'Food Street', city: 'Bhubaneswar', state: 'Odisha', zip: '04501' }
        ]
    },
];

export const states = ['Odisha', 'Chhattisgarh', 'Uttar Pradesh'];
