- [Basic](#Basic)
    * [SELECT](#SELECT)
    * [ORDER BY](#ORDER-BY)
    * [WHERE](#WHERE)
    * [DISTINCT](#DISTINCT)
    * [AND](#AND)
    * [OR](#OR)
    * [IN](#IN)
    * [BETWEEN](#BETWEEN)
    * [LIKE](#LIKE)
    * [LIMIT](#LIMIT)
    * [Alias](#Alias)
    * [INNER JOIN](#INNER-JOIN)
    * [LEFT JOIN](#LEFT-JOIN)
    * [RIGHT JOIN](#RIGHT-JOIN)
    * [CROSS JOIN](#CROSS-JOIN)
    * [Self Join](#Self-Join)
    * [GROUP BY](#GROUP-BY)
    * [HAVING](#HAVING)
    * [ROLLUP](#ROLLUP)
    * [Subquery](#Subquery)
    * [Derived Table](#Derived-Table)
    * [EXISTS](#EXISTS)
    * [CTE](#CTE)
    * [recursive CTE](#recursive-CTE)
    * [UNION](#UNION)
    * [INTERSECT](#INTERSECT)
    * [MINUS](#MINUS)
    * [INSERT](#INSERT)
    * [INSERT INTO SELECT](#INSERT-INTO-SELECT)
    * [INSERT IGNORE](#INSERT-IGNORE)
    * [UPDATE JOIN ](#UPDATE-JOIN)
    * [DELETE](#DELETE)
    * [ON DELETE CASCADE](#ON-DELETE-CASCADE)
    * [DELETE JOIN](#DELETE-JOIN)
    * [REPLACE](#REPLACE)
    * [Prepared Statement](#Prepared-Statement)

---
* refs: 
    * > https://www.mysqltutorial.org/
---

## Basic
### SELECT
* The `SELECT` statement allows you to read data from one or more tables. 
    ```
    SELECT select_list
    FROM table_name;
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

### DISTINCT
* When querying data from a table, you may get duplicate rows. In order to remove these duplicate rows, you use the `DISTINCT` clause in the `SELECT` statement.
    ```
    SELECT DISTINCT
        select_list
    FROM
        table_name;
    ```

### AND
* The `AND` operator is a logical operator that combines two or more Boolean expressions and returns true only if both expressions evaluate to true. The `AND` operator returns false if one of the two expressions evaluate to false.
    ```
    boolean_expression_1 AND boolean_expression_2
    ```

### OR
* The MySQL `OR` operator combines two Boolean expressions and returns true when either condition is true.
    ```
    boolean_expression_1 OR boolean_expression_2
    ```

### IN
* The `IN` operator allows you to determine if a specified value matches any value in a set of values or returned by a subquery.
    ```
    SELECT 
        column1,column2,...
    FROM
        table_name
    WHERE 
        (expr|column_1) IN ('value1','value2',...);
    ```

### BETWEEN
* The `BETWEEN` operator is a logical operator that allows you to specify whether a value in a range or not. 
* The BETWEEN operator is often used in the `WHERE` clause of the `SELECT`, `UPDATE`, and `DELETE` statements.
    ```
    expr [NOT] BETWEEN begin_expr AND end_expr;
    ```

### LIKE
* The `LIKE` operator is a logical operator that tests whether a string contains a specified pattern or not.
    ```
    expression LIKE pattern ESCAPE escape_character
    ```
* MySQL provides two wildcard characters for constructing patterns: percentage `%` and underscore `_` .
    * The percentage (`%`) wildcard matches any string of zero or more characters.
    * The underscore (`_`) wildcard matches any single character.

### LIMIT
* The `LIMIT` clause is used in the `SELECT` statement to constrain the number of rows to return. The `LIMIT` clause accepts one or two arguments. The values of both arguments must be zero or positive integers.
    ```
    SELECT 
        select_list
    FROM
        table_name
    LIMIT [offset,] row_count;
    ```
* the evaluation order of the `LIMIT` clause in the `SELECT` statement
    ```
    FROM -> WHERE -> SELECT -> ORDER BY -> LIMIT
    ```

### Alias
* Sometimes, column names are so technical that make the query’s output very difficult to understand. To give a column a descriptive name, you can use a column alias.
* To assign an alias to a column, you use the AS keyword followed by the alias.
    ```
    SELECT 
    [column_1 | expression] AS descriptive_name
    FROM table_name;
    ```

### INNER JOIN
* The `INNER JOIN` matches each row in one table with every row in other tables and allows you to query rows that contain columns from both tables.
    ```
    SELECT
        select_list
    FROM t1
    INNER JOIN t2 ON join_condition1
    INNER JOIN t3 ON join_condition2
    ...;
    ```

### LEFT JOIN
* The `LEFT JOIN` allows you to query data from two or more tables. Similar to the INNER JOIN clause, the `LEFT JOIN` is an optional clause of the `SELECT` statement, which appears immediately after the `FROM` clause.
    ```
    SELECT 
        select_list
    FROM
        t1
    LEFT JOIN t2 ON 
        join_condition;
    ```

### RIGHT JOIN
* MySQL `RIGHT JOIN` is similar to LEFT JOIN, except that the treatment of the joined tables is reversed.
    ```
    SELECT 
        select_last
    FROM t1
    RIGHT JOIN t2 ON join_condition;
    ```
* the joined columns of both tables have the same name, you can use the `USING` syntax
    ```
    SELECT 
        select_last
    FROM t1
    RIGHT JOIN t2 USING(column_name);
    ```

### CROSS JOIN
* Suppose you join two tables using the `CROSS JOIN` clause. The result set will include all rows from both tables, where each row is the combination of the row in the first table with the row in the second table. In general, if each table has n and m rows respectively, the result set will have nxm rows.
    ```
    SELECT * FROM t1
    CROSS JOIN t2;
    ```

### Self Join
* In the previous tutorials, you have learned how to join a table to the other tables using  `INNER JOIN`, `LEFT JOIN`, `RIGHT JOIN`, or `CROSS JOIN` clause. However, there is a special case that you need to join a table to itself, which is known as a `self join`.

### GROUP BY
* The `GROUP BY` clause groups a set of rows into a set of summary rows by values of columns or expressions. The `GROUP BY` clause returns one row for each group. In other words, it reduces the number of rows in the result set.
* You often use the `GROUP BY` clause with aggregate functions such as `SUM`, `AVG`, `MAX`, `MIN`, and `COUNT`. The aggregate function that appears in the `SELECT` clause provides information about each group.
* FROM -> WHERE -> SELECT -> GROUP BY -> HAVING -> ORDER BY -> LIMIT
    ```
    SELECT 
        c1, c2,..., cn, aggregate_function(ci)
    FROM
        table
    WHERE
        where_conditions
    GROUP BY c1 , c2,...,cn;
    ```

### HAVING
* The `HAVING` clause is used in the `SELECT` statement to specify filter conditions for a group of rows or aggregates.
* The `HAVING` clause is often used with the `GROUP BY` clause to filter groups based on a specified condition. If the `GROUP BY` clause is omitted, the `HAVING` clause behaves like the `WHERE` clause.
    ```
    SELECT 
        select_list
    FROM 
        table_name
    WHERE 
        search_condition
    GROUP BY 
        group_by_expression
    HAVING 
        group_condition;
    ```

### ROLLUP
* Generate multiple grouping sets considering a hierarchy between columns specified in the GROUP BY clause.
    ```
    SELECT 
        select_list
    FROM 
        table_name
    GROUP BY
        c1, c2, c3 WITH ROLLUP;
    ```

### Subquery
* A MySQL subquery is a query nested within another query such as `SELECT`, `INSERT`, `UPDATE` or `DELETE`. 
* A MySQL subquery is called an inner query while the query that contains the subquery is called an outer query. A subquery can be used anywhere that expression is used and must be closed in parentheses.
    ```sql
    SELECT 
        lastName, firstName
    FROM
        employees
    WHERE
        officeCode IN (SELECT 
                officeCode
            FROM
                offices
            WHERE
                country = 'USA');
    ```
    ```sql
    SELECT 
        MAX(items), 
        MIN(items), 
        FLOOR(AVG(items))
    FROM
        (SELECT 
            orderNumber, COUNT(orderNumber) AS items
        FROM
            orderdetails
        GROUP BY orderNumber) AS lineitems;
    ```

### Derived Table
* A derived table is a virtual table returned from a `SELECT` statement. A derived table is similar to a temporary table, but using a derived table in the `SELECT` statement is much simpler than a temporary table because it does not require steps of creating the temporary table.
* The term derived table and subquery is often used interchangeably. When a stand-alone subquery is used in the `FROM` clause of a `SELECT` statement, it is called a derived table.
    ```
    SELECT 
        column_list
    FROM
        (SELECT 
            column_list
        FROM
            table_1) derived_table_name;
    WHERE derived_table_name.c1 > 0;
    ```

### EXISTS
* The `EXISTS` operator is a Boolean operator that returns either true or false. The `EXISTS` operator is often used to test for the existence of rows returned by the subquery.
    ```
    SELECT 
        select_list
    FROM
        a_table
    WHERE
        [NOT] EXISTS(subquery);
    ```

### CTE
* A common table expression is a named temporary result set that exists only within the execution scope of a single SQL statement e.g.,`SELECT`, `INSERT`, `UPDATE`, or `DELETE`.
* Similar to a derived table, a `CTE` is not stored as an object and last only during the execution of a query.
* The structure of a `CTE` includes the name, an optional column list, and a query that defines the `CTE`. After the `CTE` is defined, you can use it as a view in a `SELECT`, `INSERT`, `UPDATE`, `DELETE`, or `CREATE VIEW` statement.
    ```
    WITH cte_name (column_list) AS (
        query
    ) 
    SELECT * FROM cte_name;
    ```

### recursive CTE
* A recursive common table expression is a `CTE` that has a subquery which refers to the `CTE` name itself. 
    ```
    WITH RECURSIVE cte_name AS (
        initial_query  -- anchor member
        UNION ALL
        recursive_query -- recursive member that references to the CTE name
    )
    SELECT * FROM cte_name;
    ```

### UNION
* MySQL `UNION` operator allows you to combine two or more result sets of queries into a single result set. 
    ```
    SELECT column_list
    UNION [DISTINCT | ALL]
    SELECT column_list
    UNION [DISTINCT | ALL]
    SELECT column_list
    ...
    ```

### INTERSECT
* The `INTERSECT` operator is a set operator that returns only distinct rows of two queries or more queries.
    ```
    (SELECT column_list 
    FROM table_1)
    INTERSECT
    (SELECT column_list
    FROM table_2);
    ```

### MINUS
* The `MINUS` operator is one of three set operators in the SQL standard that includes `UNION`, `INTERSECT`, and `MINUS`.
    ```
    SELECT select_list1 
    FROM table_name1
    MINUS 
    SELECT select_list2 
    FROM table_name2;
    ```

### INSERT
* The `INSERT` statement allows you to insert one or more rows into a table.
    ```
    INSERT INTO table(c1,c2,...)
    VALUES (v1,v2,...);
    ```
    ```
    INSERT INTO table(c1,c2,...)
    VALUES 
        (v11,v12,...),
        (v21,v22,...),
            ...
        (vnn,vn2,...);
    ```

### INSERT INTO SELECT
* Besides using row values in the VALUES clause, you can use the result of a SELECT statement as the data source for the INSERT statement.
    ```
    INSERT INTO table_name(column_list)
    SELECT 
        select_list 
    FROM 
        another_table
    WHERE
        condition;
    ```

### INSERT IGNORE
* When you use the `INSERT` statement to add multiple rows to a table and if an error occurs during the processing, MySQL terminates the statement and returns an error. As the result, no rows are inserted into the table.
* However, if you use the `INSERT IGNORE` statement, the rows with invalid data that cause the error are ignored and the rows with valid data are inserted into the table.
    ```
    INSERT IGNORE INTO table(column_list)
    VALUES(value_list),
          (value_list),
        ...
    ```

### UPDATE
* The `UPDATE` statement modifies existing data in a table. You can also use the `UPDATE` statement change values in one or more columns of a single row or multiple rows.
    ```
    UPDATE [LOW_PRIORITY] [IGNORE] table_name 
    SET 
        column_name1 = expr1,
        column_name2 = expr2,
        ...
    [WHERE
        condition];
    ```

### UPDATE JOIN 
* You often use joins to query rows from a table that have (in the case of `INNER JOIN`) or may not have (in the case of `LEFT JOIN`) matching rows in another table. you can use the `JOIN` clauses in the `UPDATE` statement to perform the cross-table update.
    ```
    UPDATE T1, T2,
    [INNER JOIN | LEFT JOIN] T1 ON T1.C1 = T2. C1
    SET T1.C2 = T2.C2, 
        T2.C3 = expr
    WHERE condition
    ```

### DELETE
* To delete data from a table, you use the `DELETE` statement. 
    ```
    DELETE FROM table_name
    WHERE condition;
    ```

### ON DELETE CASCADE
* Sometimes, it is useful to know which table is affected by the `ON DELETE CASCADE` referential action when you delete data from a table. You can query this data from the `referential_constraints` in the `information_schema`  database as follows
    ```
    USE information_schema;

    SELECT 
        table_name
    FROM
        referential_constraints
    WHERE
        constraint_schema = 'database_name'
            AND referenced_table_name = 'parent_table'
            AND delete_rule = 'CASCADE'
    ```

### DELETE JOIN
* MySQL also allows you to use the `INNER JOIN` clause in the `DELETE` statement to delete rows from a table and the matching rows in another table.
    ```
    DELETE T1, T2
    FROM T1
    INNER JOIN T2 ON T1.key = T2.key
    WHERE condition;
    ```

### REPLACE
* The MySQL REPLACE statement is an extension to the SQL Standard. 
    * Step 1. Insert a new row into the table, if a duplicate key error occurs.
    * Step 2. If the insertion fails due to a duplicate-key error occurs:
        * Delete the conflicting row that causes the duplicate key error from the table.
        * Insert the new row into the table again.
    ```
    REPLACE [INTO] table_name(column_list)
    VALUES(value_list);
    ```

### Prepared Statement
