<h1 id="start"> Online book store</h1>

Welcome to the online bookstore management system!
This application provides a convenient experience
for both buyers and managers. Customers can register,
view available books, add books to cart, manage cart,
and create an order based on the cart. Managers, in turn,
can easily manage books, categories and customer orders. <br>
Security is arranged using reliable authentication and
authorization functions using JWT tokens

<p align="center">
  <a href="#technologies">Technologies</a> |
  <a href="#getting-started">Getting Started</a> |
  <a href="#domain-models">Domain Models</a> |
  <a href="#user-roles">User Roles</a> |
  <a href="#database-structure">Database Structure</a> |
  <a href="#endpoints">Endpoints</a>
</p>



<h2 id="technologies"> Technologies</h2>
<ul style="list-style: none">
 <li><img src="icons/java.png" alt="" width="25" style="position: relative; top: 5px;"> Java 17</li>
 <li><img src="icons/maven.png" alt="" width="25" style="position: relative; top: 10px;"> Maven</li>
 <li><img src="icons/spring-logo.png" alt="" width="25" style="position: relative; top: 5px;"> Spring framework <i><small>(boot, data, security)</small></i> </li>
 <li><img src="icons/lombok.png" alt="" width="25" style="position: relative; top: 5px;"> Lombok</li>
 <li><img src="icons/mapstruct.png" alt="" width="25" style="position: relative; top: 5px;"> MapStruct</li>
 <li><img src="icons/mysql.png" alt="" width="25" style="position: relative; top: 5px;"> MySql 8</li>
 <li><img src="icons/hibernate-logo.png" alt="" width="25" style="position: relative; top: 5px;"> Hibernate</li>
 <li><img src="icons/cta-icon.svg" alt="" width="25" style="position: relative; top: 5px;"> Liquibase</li>
 <li><img src="icons/junit5-logo.png" alt="" width="25" style="position: relative; top: 5px;"> JUnit5 <i><small>(+ Mockito)</small></i></li>
 <li><img src="icons/docker.png" alt="" width="25" style="position: relative; top: 5px;"> Docker</li>
 <li><img src="icons/Swagger-logo.png" alt="" width="25" style="position: relative; top: 5px;"> Swagger</li>
</ul>



<h2 id="getting-started"> Getting Started</h2>

1. Make sure you have installed

<ul style="list-style: none">
 <li><img src="icons/java.png" alt="" width="25" style="position: relative; top: 5px;"> JDK 17+</li>
 <li><img src="icons/docker.png" alt="" width="25" style="position: relative; top: 5px;"> Docker</li>
</ul>

2. Clone repository

```text
git clone https://github.com/Oleksandr-Tymoshenko/Online-Book-Store.git
```

3. Create .env file with variables <br>
   Look for variables names in file: `.env.sample`

4. Run the following command to build and start the Docker containers

```text
docker-compose up --build
```

5. The application should now be running at `http://localhost:8081`.



<h2 id="domain-models"> Domain Models</h2>

#### User

* Attributes: Contains comprehensive information about registered users, including authentication details and personal
  information.
* Roles: Users are assigned roles, such as admin or regular user, defining their permissions and access levels.

#### Role

* Purpose: Represents the role of a user within the system, playing a crucial role in defining their access and actions.

#### Book

* Attributes: Represents detailed information about a book available in the store, including title, author, price, and
  category.

#### Category

* Purpose: Represents a category to which a book can belong, contributing to effective organization and user navigation.

#### ShoppingCart

* Purpose: Represents a user's shopping cart, capable of containing multiple items (CartItems).

#### CartItem

* Attributes: Represents an item in a user's shopping cart, linked to a specific book.

#### Order

* Purpose: Represents an order placed by a user, encapsulating OrderItems.

#### OrderItem

* Attributes: Represents an item in a user's order, associated with a specific book.


<h2 id="user-roles"> User Roles</h2>

#### Shopper (User)

* Actions: Capable of joining, signing in, exploring books, searching, managing the shopping cart, placing and reviewing
  orders, and accessing past receipts.

#### Manager (Admin)

* Actions: Empowered to arrange books, organize bookshelf sections, manage receipts, and modify their status.


<h2 id="database-structure"> Database structure</h2>

![database structure](icons/bookservicedb.png)

<h2 id="endpoints"> Endpoints</h2>

#### Authorization

| **HTTP method** | **Endpoint**       | **Role** | **Description**                                     |
|:----------------|:-------------------|----------|:----------------------------------------------------|
| POST            | /api/auth/register |          | Register a new user to the system                   |
| POST            | /api/auth/login    |          | Login with email and password. Response - JWT token |

#### Book management

| **HTTP method** | **Endpoint**      | **Role** | **Description**                                                   |
|:----------------|:------------------|----------|:------------------------------------------------------------------|
| GET             | /api/books        | USER     | Get all books per website pages                                   |
| GET             | /api/books/{id}   | USER     | Get the book by its id number                                     |
| GET             | /api/books/search | USER     | Search books by title and author *(titles=values&authors=values)* |
| POST            | /api/books        | ADMIN    | Create a new book                                                 |
| PUT             | /api/books/{id}   | ADMIN    | Update the book by its id number                                  |
| DELETE          | /api/books/{id}   | ADMIN    | Delete the book by its id number *(soft-delete)*                  |

#### Categories management

| **HTTP method** | **Endpoint**               | **Role** | **Description**                                      |
|:----------------|:---------------------------|----------|:-----------------------------------------------------|
| GET             | /api/categories            | USER     | Get all categories per website pages                 |
| GET             | /api/categories/{id}       | USER     | Get the category by its id number                    |
| GET             | /api/categories/{id}/books | USER     | Get list of books by the category by its id number   |
| POST            | /api/categories            | ADMIN    | Create a new category                                |
| PUT             | /api/categories/{id}       | ADMIN    | Update the category by its id number                 |
| DELETE          | /api/categories/{id}       | ADMIN    | Delete the category by its id number *(soft-delete)* |

#### Shopping cart management

| **HTTP method** | **Endpoint**              | **Role** | **Description**                                            |
|:----------------|:--------------------------|----------|:-----------------------------------------------------------|
| GET             | /api/cart                 | USER     | Get shopping cart                                          |
| POST            | /api/cart                 | USER     | Add a new book to shopping cart                            |
| PUT             | /api/cart/cart-items/{id} | USER     | Endpoint for updating quantity of an item in shopping cart |
| DELETE          | /api/cart/cart-items/{id} | USER     | Delete book from shopping cart by id                       |

#### Order management

| **HTTP method** | **Endpoint**                    | **Role** | **Description**                                                           |
|:----------------|:--------------------------------|----------|:--------------------------------------------------------------------------|
| POST            | /api/orders                     | USER     | Place an order based on your shopping cart, then shopping cart is deleted |
| GET             | /api/orders                     | USER     | Get all orders for user                                                   |
| GET             | /api/orders/{id}/items          | USER     | Get all order items by order id                                           |
| GET             | /api/orders/{id}/items/{itemId} | USER     | Get info about order item by order id and item id                         |
| PATCH           | /api/orders/{id}                | ADMIN    | Update order status for order by id                                       |

[//]: # (#### More detailed information about request bodies you can find by endpoint below)

[//]: # (http://aws/link)
___
