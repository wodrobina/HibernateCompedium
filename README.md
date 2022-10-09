# Results of using FetchMode.LAZY and EAGER

This branch focus on SQL results when we use eager or lazy loaded entities.
Example is simple one to many relation. Where depending on `FetchMode` used name of the product starts with adequate mode.   
```
+─────────+              +--------+
| Product | -- 1...N --> | Review |
+─────────+              +--------+
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









