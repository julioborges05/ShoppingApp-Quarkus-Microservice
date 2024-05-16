# Shopping App

### This project is a shopping app service that has three principal modules explained below:

- Product classes: These classes handle database insertion, update, delete, and find methods from the products.
- User classes: These classes handle database insertion, update, delete, and find methods from the users.
- Cart classes: These classes handle database insertion, update, delete, and find methods from the cart and products in the carts. Therefore, these classes abort the shopping and finish the carts.

---

### Database

On the "db" path, the database Dockerfile is available to create a Docker image with the application database. The "create container" file has a command that initializes a container with this image.