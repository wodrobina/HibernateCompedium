package eu.wodrobina.hibernatecompedium.product;

import org.springframework.data.repository.CrudRepository;

public interface EagerProductRepository extends CrudRepository<EagerProduct, Integer> {
}
