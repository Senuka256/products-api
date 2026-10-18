# Week 3 - Tutorial 2

## Product experiments

Prediction: making the fields private without adding getters should give empty
JSON because Jackson cannot read the fields directly.

Results from `/products/1`:

| Product version | Status | Response |
| --- | --- | --- |
| Public fields | 200 | `{"id":1,"name":"Laptop","price":999.99}` |
| Private fields without getters | 200 | `{}` |
| Private fields with getters | 200 | `{"id":1,"name":"Laptop","price":999.99}` |
| `getName()` commented out | 200 | `{"id":1,"price":999.99}` |

With private fields, Jackson reads the values through the public getters.
Without `getName()`, name disappears but id and price still show up.

A 200 response does not mean every field is there. For a bigger class, compare
the JSON fields with what the endpoint should return.

`getName()` is restored in the final code.

## Customer and Address

`/customers/1` returns Ada Lovelace. The address sits inside the customer JSON
and has its own street, city and postcode fields.

Commenting out `getAddress()` removed the whole address. The id, name and email
were still there, and the status was still 200. Restoring the getter brought
the address back.

Both endpoints take the id from the URL. The other values are the sample data
from the lab guide.

## UML diagrams

- [Product](uml/Product.png) ([editable source](uml/Product.puml))
- [Customer and Address](uml/CustomerAddress.png) ([editable source](uml/CustomerAddress.puml))

Customer has an Address, so the arrow points from Customer to Address.

## Run the tests

Use Java 25, then run `mvnw.cmd test` on Windows or `./mvnw test` on macOS/Linux.
The tests check the JSON fields, ids from the URL and the endpoints listed in
Swagger. They start the app on a free port.
