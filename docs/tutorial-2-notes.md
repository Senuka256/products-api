# Tutorial 2 notes

## Product experiments

Before changing the fields to private, the prediction was that Jackson would no
longer be able to read them and the JSON would be empty.

HTTP checks confirmed these responses for `/products/1`:

| Product version | Status | Response |
| --- | --- | --- |
| Public fields | 200 | `{"id":1,"name":"Laptop","price":999.99}` |
| Private fields without getters | 200 | `{}` |
| Private fields with getters | 200 | `{"id":1,"name":"Laptop","price":999.99}` |
| `getName()` commented out | 200 | `{"id":1,"price":999.99}` |

Jackson uses the public getters to read the private fields, so removing
`getName()` hides the name but leaves the other properties available.

To catch this in a larger class, check the response fields against the expected
JSON rather than relying on a 200 status or looking for an error log.

The getter was restored after the experiment.

## Customer and Address

`/customers/1` returns Ada Lovelace with an Address containing street, city and
postcode. The address is an object inside the customer JSON, not a string.

Commenting out `getAddress()` removed the entire address block. The response
still had id, name and email, with status 200. Restoring the getter brought back
the nested address and all three of its fields.

Both controllers use the id from the URL. They return fixed example data for
the other fields, as required for this tutorial.

## UML diagrams

- [Product](uml/Product.png) ([editable source](uml/Product.puml))
- [Customer and Address](uml/CustomerAddress.png) ([editable source](uml/CustomerAddress.puml))

The arrow points from Customer to Address because a Customer has an Address.

## Running the checks

Use Java 25, then run `mvnw.cmd test` on Windows or `./mvnw test` on macOS/Linux.
The endpoint tests start the application on an available port and check the JSON
fields, URL ids and Swagger paths.
