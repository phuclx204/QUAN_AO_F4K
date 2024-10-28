package org.example.quan_ao_f4k.repository.general;

import org.example.quan_ao_f4k.model.general.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long>,
		JpaSpecificationExecutor<Favorite> {
}
