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
