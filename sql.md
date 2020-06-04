- [SELECT](#SELECT)
- [ORDER BY](#ORDER-BY)
- [WHERE](#WHERE)

---

---

### SELECT
* The `SELECT` statement allows you to read data from one or more tables. 
    ```
    SELECT select_list
    FROM table_name;
    ```
* Using the MySQL SELECT statement to retrieve data from a single column
    ```sql
    SELECT lastName
    FROM employees;
    ```
* Using the MySQL SELECT statement to query data from multiple columns
    ```sql
    SELECT 
        lastname, 
        firstname, 
        jobtitle
    FROM
        employees;
    ```
* Using the MySQL SELECT statement to retrieve data from all columns
    ```sql
    SELECT * 
    FROM employees;
    ```

### ORDER BY
* To sort the result set, you add the ORDER BY clause to the SELECT statement. 
    ```
    SELECT 
        select_list
    FROM 
        table_name
    ORDER BY 
        column1 [ASC|DESC], 
        column2 [ASC|DESC],
        ...;
    ```
* Using MySQL ORDER BY clause to sort values in one column 
    ```sql
    SELECT
        contactLastname,
        contactFirstname
    FROM
        customers
    ORDER BY
        contactLastname;
    ```
    ```sql
    SELECT
        contactLastname,
        contactFirstname
    FROM
        customers
    ORDER BY
        contactLastname DESC;
    ```
* Using MySQL ORDER BY clause to sort values in multiple columns
    ```sql
    SELECT
        contactLastname,
        contactFirstname
    FROM
        customers
    ORDER BY
        contactLastname DESC,
        contactFirstname ASC;
    ```
* Using MySQL ORDER BY to sort a result set by an expression
    ```sql
    SELECT 
        orderNumber, 
        orderlinenumber, 
        quantityOrdered * priceEach
    FROM
        orderdetails
    ORDER BY 
    quantityOrdered * priceEach DESC;
    ```
    ```sql
    SELECT 
        orderNumber,
        orderLineNumber,
        quantityOrdered * priceEach AS subtotal
    FROM
        orderdetails
    ORDER BY subtotal DESC;
    ```

#### Using MySQL ORDER BY to sort data using a custom list
* The `ORDER BY` clause allows you to sort data using a custom list by using the `FIELD()` function.
    ```sql
    SELECT 
        orderNumber, 
        status
    FROM
        orders
    ORDER BY 
        FIELD(status,
            'In Process',
            'On Hold',
            'Cancelled',
            'Resolved',
            'Disputed',
            'Shipped');
    ```
    * returns the index of the status in the list `In Process`, `On Hold`, 'Cancelled', 'Resolved', 'Disputed', 'Shipped'.
    * For example, if the status is In Process, the function will return 1. If the status is On Hold, the function will return 2, and so on.

### WHERE
* The `WHERE` clause allows you to specify a search condition for the rows returned by a query. 
    ```
    SELECT 
        select_list
    FROM
        table_name
    WHERE
        search_condition;
    ```
* Using MySQL WHERE clause with equal operator
    ```sql
    SELECT 
        lastname, 
        firstname, 
        jobtitle
    FROM
        employees
    WHERE
        jobtitle = 'Sales Rep';
    ```
* Using MySQL WHERE clause with AND operator
    ```sql
    SELECT 
        lastname, 
        firstname, 
        jobtitle,
        officeCode
    FROM
        employees
    WHERE
        jobtitle = 'Sales Rep' AND 
        officeCode = 1;
    ```
* Using MySQL WHERE clause with OR operator
    ```sql
    SELECT 
        lastName, 
        firstName, 
        jobTitle, 
        officeCode
    FROM
        employees
    WHERE
        jobtitle = 'Sales Rep' OR 
        officeCode = 1
    ORDER BY 
        officeCode , 
        jobTitle;
    ```
* Using MySQL WHERE with BETWEEN operator
    * The `BETWEEN` operator returns TRUE if a value is in a range of values
        ```sql
        SELECT 
            firstName, 
            lastName, 
            officeCode
        FROM
            employees
        WHERE
            officeCode BETWEEN 1 AND 3
        ORDER BY officeCode;
        ```
*  Using MySQL WHERE with the LIKE operator example
    * The `LIKE` operator evaluates to TRUE if a value matches a specified pattern. To form a pattern, you use `%` and `_` wildcards. The `%` wildcard matches any string of zero or more characters while the `_` wildcard matches any single character.
        ```sql
        SELECT 
            firstName, 
            lastName
        FROM
            employees
        WHERE
            lastName LIKE '%son'
        ORDER BY firstName;
        ```
* Using MySQL WHERE clause with the IN operator
    * The IN operator returns TRUE if a value matches any value in a list.
        ```sql
        SELECT 
            firstName, 
            lastName, 
            officeCode
        FROM
            employees
        WHERE
            officeCode IN (1 , 2, 3)
        ORDER BY 
            officeCode;
        ```
* Using MySQL WHERE clause with the IS NULL operator
    * The IS NULL operator returns TRUE if a value is NULL.
    ```sql
    SELECT 
        lastName, 
        firstName, 
        reportsTo
    FROM
        employees
    WHERE
        reportsTo IS NULL;
    ```
* Using MySQL WHERE clause with comparison operators

| Operator | Description |
| --- | --- |
| `=` | Equal to. You can use it with almost any data types. |
| `<> or !=` | Not equal to |
| `<` | Less than. You typically use it with numeric and date/time data types. |
| `>` | Greater than. |
| `<=` | Less than or equal to |
| `>=` | Greater than or equal to |

    ```sql
    SELECT 
        lastname, 
        firstname, 
        jobtitle
    FROM
        employees
    WHERE
        jobtitle <> 'Sales Rep';
    ```
    ```sql
    SELECT 
        lastname, 
        firstname, 
        officeCode
    FROM
        employees
    WHERE 
        officecode > 5;
    ```
    ```sql
    SELECT 
        lastname, 
        firstname, 
        officeCode
    FROM
        employees
    WHERE 
        officecode <= 4;
    ```
