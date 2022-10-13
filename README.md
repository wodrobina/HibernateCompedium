# Results of using FetchMode.LAZY and EAGER

This branch focus on SQL results when we use eager or lazy loaded entities to retrieve from database.
Example is simple one to many relation. Where depending on used `FetchMode` name of the product starts with adequate mode.   
```
+---------+              +--------+
| Product | -- 1...N --> | Review |
+---------+              +--------+
```

## Relation setup
`EAGER` that is used in `EagerProduct`
```sql
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Review> reviews = new HashSet<>();
```

and `LAZY` that is used in `LazyProduct`
```sql
    @OneToMany(fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();
```

This is unidirectional relation to `Review`. Also this annotation will create additional linking table. Tables will look like presented:
```
+---------+      +-----------------------+      +--------+
| Product | ---- | eager_product_reviews | ---- | Review |
+---------+      +-----------------------+      +--------+
```

## Setup
Set variables
```
SPRING_DATASOURCE_URL
APPLICATION_USER
APPLICATION_USER_PASSWORD
```
There are many ways to set them. One of them is to update file `application.properties`
```xml
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${APPLICATION_USER}
spring.datasource.password=${APPLICATION_USER_PASSWORD}
```
Note that I used for my tests Postgresql database. Some drivers may be required to run tests.

## Tests
There are two groups of tests:
- single
- multiple

Single focus on retrieving single element from database while multiple retrieve many objects.
There are differences from fetching single value and multiple ones.

## Short conclusions
### Lazy 
When entity is retrieved from database then only root element is retrieved. Child entity waits until it's needed.
In one of the tests I select root entity and then retrieved child entities. In result we have two queries.
Optimisation here is to wait with second select.
```sql
    select
        lazyproduc0_.id as id1_2_0_,
        lazyproduc0_.name as name2_2_0_ 
    from
        lazy_product lazyproduc0_ 
    where
        lazyproduc0_.id=?
```
```sql
    select
        reviews0_.lazy_product_id as lazy_pro1_3_0_,
        reviews0_.reviews_id as reviews_2_3_0_,
        review1_.id as id1_4_1_,
        review1_.text as text2_4_1_ 
    from
        lazy_product_reviews reviews0_ 
    inner join
        review review1_ 
            on reviews0_.reviews_id=review1_.id 
    where
        reviews0_.lazy_product_id=?
```
### Eager
When entity is retrieved then root element with its child is retrieved instantly when single object is taken.
This select is very optimal but works only with `findById()`.
```sql
    select
        eagerprodu0_.id as id1_0_0_,
        eagerprodu0_.name as name2_0_0_,
        reviews1_.eager_product_id as eager_pr1_1_1_,
        review2_.id as reviews_2_1_1_,
        review2_.id as id1_4_2_,
        review2_.text as text2_4_2_ 
    from
        eager_product eagerprodu0_ 
    left outer join
        eager_product_reviews reviews1_ 
            on eagerprodu0_.id=reviews1_.eager_product_id 
    left outer join
        review review2_ 
            on reviews1_.reviews_id=review2_.id 
    where
        eagerprodu0_.id=?
```
In case of using method like `repositoy.findAll()` then two selects are used. We lost optimisation like it was with `Lazy` 
```sql
    select
        eagerprodu0_.id as id1_0_,
        eagerprodu0_.name as name2_0_ 
    from
        eager_product eagerprodu0_
```
```sql
 select
        reviews0_.eager_product_id as eager_pr1_1_0_,
        reviews0_.reviews_id as reviews_2_1_0_,
        review1_.id as id1_4_1_,
        review1_.text as text2_4_1_ 
    from
        eager_product_reviews reviews0_ 
    inner join
        review review1_ 
            on reviews0_.reviews_id=review1_.id 
    where
        reviews0_.eager_product_id=?
```
 
## Conclusion
When we compare `Lazy` with `Eager` then we clearly see that the only real difference is moment of retrieving child entities.
For first fetch type we wait and for second we fetch at once. This clearly shows that in scenario when root entity require
 child entites then `Lazy` will perform like `Eager`. Optimisation that `Eager` has with `findById()` can done by `@Query()`
if needed. 





