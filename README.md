# Price Comparator Application

## Overview

This application is a lightweight price comparison tool built using a **layered architecture**. It enables users to:

- View best product offers by store and date
- Create optimized shopping lists
- Set and track product price targets
- Explore current discounts and long-term price trends


The architecture includes:
- Controller (Javalin-based)
- Service (business logic and entity linking)
- DAO (provides fast, in-memory data access)
- Models (represent core entities)
- Helper layers (DTOs, composite keys, and utility classes)

## Architecture

### DAO Layer<br>
Each primary entity has a corresponding DAO that uses HashMap for O(1) lookup:

`ProductDAO`: Keyed by `productId` (`String`) holding `name`, `category`, `brand`, `packageQuantity` and `packageUnit`

`StoreCatalogDAO`: Also uses a composite key of `(productId, store, date)` holding `price` and `currency`

`DiscountDAO`: Keyed by a composite `(productId, store, date)` holding `fromDate`, `toDate`, and `discountPercentage`

These mimic database-like joins and access patterns using pure in-memory structures.

### Service Layer<br>
The service layer handles business logic and links related entities by performing in-memory joins, similar to SQL operations, delivering business functionality such as:
- **Product Offers:** For each product, shows all available offers from best to worst across stores
- **Shopping Basket Optimization:**
  * Show best deals for selected basket items
  * If preferred, suggest optimal store to shop at (based on availability and total cost)
- **Price Targets:** Allows users to set and track price goals for products
- **Latest Discounts:** Show most recent available discounts across all products
- **Price Trends:** Analyze how prices change over time based on historical records

### Controller Layer<br>
Built using Javalin, the controller layer exposes API endpoints, delegates requests to the service layer, and manages request/response lifecycles.
## Application Startup
On startup, the following steps are executed in the `Main` class:
1. **CSV files** are loaded and parsed into the DAO layer 
2. **Dependencies** are injected into services and controllers. 
3. The **Javalin server** starts and listens for API requests on port `7070`

## Testing
Unit tests are written using **JUnit** for both the **service** and **DAO** layers to ensure reliability and correctness of the core logic.


## Assumptions
To ensure realistic functionality, minimal assumptions were made. The only enforced assumption is:

For each store, at most one discount listing is posted between two product listing dates.<br>
_Example_: If Lidl lists prices on May 1 and May 8, there can be at most one list of discounts between those dates, applicable to products listed on May 1.
