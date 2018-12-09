## Synopsis
Example of spring boot 2 web service
API is supporting the basic CRUD operations for products:
* Create a new product
* Retrieve a list of all products
* Update a product

Each product should have a name and a list of historical price. Price with the newest date is the current one for the product.
API is supporting the basic CRUD operations for orders:
* Placing an order
* Retrieving all orders within a given time period

Each order have a list of products. It have a unique id, the buyer’s e-mail, and the time
the order was placed. It is possible to change a product’s price, but this should not affect the calculation of the total
order amount for orders which have already been placed.

## Installation

1. `git clone https://github.com/karolgle/products-order.git`
2. `cd products-order`
3. `gradlew build`
3. `gradlew bootJar`
4. `java -jar build/libs/product-order-0.0.1-SNAPSHOT-boot.jar`
5. ...or `gradlew bootRun`

## API Reference

Available endpoints for the application also available in the [Swagger](http://localhost:8080/swagger-ui.html) after application in running: 

### Endpoints

| resource                 | description                       |
|:-------------------------|:----------------------------------|
| <code>GET</code>  `/products`               | returns list of available products with their prices |
| <code>GET</code>  `/products/{name}`        | returns detailed data for product with specific `{name}`|
| <code>POST</code> `/products`               | create new product if not exist |
| <code>GET</code>  `/products/{name}/price`  | returns products `{name}` current price|
| <code>PUT</code>  `/products/{name}/price`  | updates product current price  |
| <code>GET</code>  `/orders`                 | returns list of available orders |
| <code>POST</code> `/orders`                 | create new order |
| <code>GET</code>  `/orders/searches?from={date}&to={date}`| search orders in date range |
| <code>GET</code>  `/orders/searches?from={date}`| search orders placed after date |
| <code>GET</code>  `/orders/searches?to={date}`| search orders placed before date |
| <code>GET</code>  `/orders/{email}/{orderDate}/placed/{date}`| check how order with specific email and order date would look(e.g. total order sum) if it would be placed in other date |

#### cUrl commands

##### Products:
1. `curl -X GET http://localhost:8080/products`
2. `curl -X GET http://localhost:8080/products/{name}`
   * e.g. `curl -X GET http://localhost:8080/products/Product%201`
3. `curl -X POST -H "Content-Type: application/json" -d {jsonString} http://localhost:8080/products`
   * e.g. `curl -X POST -H "Content-Type: application/json" -d "{\"name\":\"Product 3\",\"prices\":[{\"price\":555.55,\"fromDate\":\"1977-01-01T10:30:00\"},{\"price\":200.50,\"fromDate\":\"1978-01-01T10:30:00\"},{\"price\":300.50,\"fromDate\":\"2000-01-01T10:30:00\"}]},{\"name\":\"Product 2\",\"prices\":[{\"id\":6,\"price\":300.50,\"fromDate\":\"1974-01-01T10:30:00\"},{\"price\":15.55,\"fromDate\":\"2018-12-07T10:53:17.901\"},{\"price\":35.55,\"fromDate\":\"1989-01-01T10:30:00\"}]}" http://localhost:8080/products`
4. `curl -X GET http://localhost:8080/products/{name}/price`
      * e.g. `curl -X GET http://localhost:8080/products/Product%201/price`     
5. `curl -X PUT -H "Content-Type: application/json" -d {jsonString} http://localhost:8080/companies/{name}` 
   * e.g. `curl -X PUT -H "Content-Type: application/json" -d "{\"productName\":\"Product 1\",\"price\":{\"price\":755.50,\"fromDate\":\"2009-01-01T10:30:00\"}}" http://localhost:8080/products/Product%201/price`

##### Orders:
1. `curl -X GET "http://localhost:8080/orders?fromDate={date}&toDate={date}"`
   * e.g. `curl -X GET "http://localhost:8080/orders?fromDate=1978-01-01T10:30:00&toDate=2079-01-01T10:30:00"`
2. `curl -X POST -H "Content-Type: application/json" -d {jsonString} http://localhost:8080/orders`
   * e.g. `curl -X POST -H "Content-Type: application/json" -d "{\"email\":\"customer1@test.test\",\"productsToOrder\":[{\"productName\":\"Product 1\",\"quantity\":4},{\"productName\":\"Product 1\",\"quantity\":3},{\"productName\":\"Product 2\",\"quantity\":5}]}" http://localhost:8080/orders`
3. `curl -X GET "/orders/{email}/{orderDate}/placed/{date}"`
   * e.g. `curl -X GET "http://localhost:8080/orders/customer1@test.test/2000-01-01T10:30:00/placed/1989-08-01T09:30:30"`   


## Tests
Run following command:

1. Run `gradlew test`
2. or test API manually using e.g. curl 
3. or swagger

## License

A short snippet describing the license (MIT, Apache, etc.)
       