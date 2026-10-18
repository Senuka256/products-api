# Week 4 - Tutorial 3

## Warm-up

Rows are considered separately from the starting setup, except row 8 uses `q`
from row 7. If row 1 runs first, the list size in row 5 becomes 3 instead of 2.

| Row | Compiles? | What happens? |
| --- | --- | --- |
| 1 | Yes | Adds Mug to the list. It now has three products. |
| 2 | No | A `List<Product>` cannot hold a String. |
| 3 | No | `get(0)` returns a Product, which cannot be assigned to a String. |
| 4 | Yes | Throws `IndexOutOfBoundsException`: index 5 is outside the list. |
| 5 | Yes | Stores 2 in `n` when using the starting setup. |
| 6 | No | Arrays have no `add()` method. Their length is fixed. |
| 7 | Yes | No entry has key 99, so `q` is null. |
| 8 | Yes | Throws `NullPointerException` because `q` is null. |

The compiler checks types and available methods. It does not know which keys
will be in a map or how many items a list will contain when the code runs.

## Controller and service

ProductService stores the list and assigns ids. ProductController handles HTTP
requests and calls the service. Spring supplies the service through `@Autowired`.
The service has no URLs or HTTP status codes.

The catalogue starts empty. Products are stored in memory and disappear when
the application restarts. New products get ids starting at 1, even if a client
sends an id in the POST body. Deleting an item does not reset the counter.

Ids are compared with `.equals()`. Using `==` on Long objects compares references
and can fail for values outside the usual -128 to 127 cache range.

## Endpoints

| Method | URL | Purpose |
| --- | --- | --- |
| GET | `/products` | Return the catalogue. |
| GET | `/products/{id}` | Find one product. |
| POST | `/products` | Add a product from a JSON body. |
| DELETE | `/products/{id}` | Remove the matching product. |
| GET | `/products/search?name=Lap` | Find names containing `Lap`. |

Search is case sensitive, following `String.contains()`. No matches gives `[]`.

## Missing-product experiment

The controller returns `.orElse(null)` when an id is missing, as shown in the lab.
A real API should return 404 for a missing product. The guide leaves that change
for Lecture 6, so this tutorial keeps the current behaviour.

## HTTP test results

The automated HTTP checks started with an empty catalogue and ran this sequence:

| Request | Status | Body |
| --- | --- | --- |
| GET `/products` | 200 | `[]` |
| POST `/products` with Laptop, 899.99 | 200 | `{"id":1,"name":"Laptop","price":899.99}` |
| POST `/products` with Mouse, 24.99 | 200 | `{"id":2,"name":"Mouse","price":24.99}` |
| GET `/products` | 200 | `[{"id":1,"name":"Laptop","price":899.99},{"id":2,"name":"Mouse","price":24.99}]` |
| GET `/products/1` | 200 | `{"id":1,"name":"Laptop","price":899.99}` |
| DELETE `/products/1` | 200 | Empty body |
| GET `/products` | 200 | `[{"id":2,"name":"Mouse","price":24.99}]` |
| GET `/products/99` | 200 | Empty body |

Adding Laptop stand and Laptop case after the delete gave ids 3 and 4. Searching
for `Lap` returned those two products. Searching for `Keyboard` or lowercase
`lap` returned `[]`. The checks also confirmed that Swagger lists all five
operations.

The full Java 25 test run passed all 10 tests. It includes the earlier Customer
checks and a service test that finds and deletes product 130 using value equality.

## Diagram and manual checks

- [Updated Product diagram](uml/Product.png) ([PlantUML source](uml/Product.puml))
- [Postman collection](postman/Tutorial3.postman_collection.json)

The controller points to the service with a dashed dependency arrow. The service
points to Product with a solid association arrow. Neither arrow points back to
the controller.

To repeat the checks manually, start the app and open
`http://localhost:8080/swagger-ui.html`, or import the collection into Postman.
Run the requests in order on a fresh app run so ids start at 1. The collection
includes status and response checks; it was prepared for Postman, while the
recorded results above came from the automated Java HTTP tests.

Run `mvnw.cmd test` on Windows or `./mvnw test` on macOS/Linux with Java 25 to run
the automated tests.

## Exit ticket

HTTP handling belongs in the controller; product storage and catalogue rules
belong in the service.

The remaining two answers are personal: add one topic you still find unclear
and your own confidence rating from 1 to 5.
